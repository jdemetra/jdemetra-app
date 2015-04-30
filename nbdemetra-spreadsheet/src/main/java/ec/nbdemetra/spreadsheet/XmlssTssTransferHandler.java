/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.spreadsheet;

import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.datatransfer.TssTransferHandler;
import ec.tss.tsproviders.spreadsheet.engine.SpreadSheetCollection;
import ec.tss.tsproviders.spreadsheet.engine.SpreadSheetParser;
import ec.tss.tsproviders.spreadsheet.engine.SpreadSheetSeries;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.tss.tsproviders.utils.Parsers;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.Cell;
import ec.util.spreadsheet.helpers.ArrayBook;
import ec.util.spreadsheet.helpers.ArraySheet;
import ec.util.spreadsheet.xmlss.XmlssBookFactory;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.annotation.Nonnull;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 * XML Spreadsheet (XMLSS).
 *
 * @see http://msdn.microsoft.com/en-us/library/aa140066(v=office.10).aspx
 * @author Jean Palate
 */
@ServiceProvider(service = TssTransferHandler.class, position = 1000, supersedes = {"ec.tss.datatransfer.impl.XmlssTssTransferHandler"})
public final class XmlssTssTransferHandler extends TssTransferHandler implements IConfigurable {

    private final DataFlavor dataFlavor;
    private final NumberFormat numberFormat;
    private final DateFormat dateFormat;
    private final XmlssBookFactory bookFactory;
    private final Configurator<XmlssTssTransferHandler> configurator;
    private InternalConfig config;

    public XmlssTssTransferHandler() {
        this.dataFlavor = createDataFlavor();
        this.numberFormat = NumberFormat.getNumberInstance(Locale.ROOT);
        numberFormat.setMaximumFractionDigits(9);
        numberFormat.setMaximumIntegerDigits(12);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.bookFactory = new XmlssBookFactory();
        this.configurator = createConfigurator();
        this.config = new InternalConfig();
    }

    //<editor-fold defaultstate="collapsed" desc="INamedService">
    @Override
    public String getName() {
        return "XMLSS";
    }

    @Override
    public String getDisplayName() {
        return "XML Spreadsheet (XMLSS)";
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TssTransferHandler">
    @Override
    public DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    @Override
    public boolean canExportTsCollection(TsCollection col) {
        return config.exportTimeSeries;
    }

    @Override
    public Object exportTsCollection(TsCollection col) throws IOException {
        col.load(TsInformationType.Data);

        TsCollectionAnalyser analyser = new TsCollectionAnalyser();
        analyser.set(col, config.beginPeriod);

        if (analyser.data != null && analyser.dates != null && analyser.titles != null) {

            ArraySheet.Builder builder = ArraySheet.builder().name("dnd");

            if (config.showTitle) {
                builder.row(0, config.showDates ? 1 : 0, analyser.titles);
            }

            if (config.showDates) {
                builder.column(config.showTitle ? 1 : 0, 0, analyser.dates);
            }

            builder.table(config.showTitle ? 1 : 0, config.showDates ? 1 : 0, asSheet("", analyser.data));

            ArraySheet sheet = builder.build();

            if (!config.vertical) {
                sheet = sheet.inv();
            }

            return bookToByteArray(sheet.toBook());
        }

        return bookToByteArray(ArrayBook.builder().build());
    }

    @Override
    public boolean canImportTsCollection(Object obj) {
        return config.importTimeSeries && obj instanceof byte[];
    }

    @Override
    public TsCollection importTsCollection(Object obj) throws IOException {
        try (Book book = byteArrayToBook((byte[]) obj)) {
            TsCollection result = TsFactory.instance.createTsCollection();
            for (SpreadSheetCollection o : SpreadSheetParser.getDefault().parse(book, Parsers.onDateFormat(dateFormat), Parsers.onNumberFormat(numberFormat), TsFrequency.Undefined, TsAggregationType.None, true).collections.values()) {
                for (SpreadSheetSeries s : o.series) {
                    if (s.data.isPresent()) {
                        result.add(TsFactory.instance.createTs(s.seriesName, null, s.data.get()));
                    }
                }
            }
            return result;
        }
    }

    @Override
    public boolean canExportMatrix(Matrix matrix) {
        return config.exportMatrix && !matrix.isEmpty();
    }

    @Override
    public Object exportMatrix(Matrix matrix) throws IOException {
        return bookToByteArray(newArraySheet("dnd", matrix).toBook());
    }

    @Override
    public boolean canImportTable(Object obj) {
        return config.importTable && obj instanceof byte[];
    }

    @Override
    public Table<?> importTable(Object obj) throws IOException, ClassCastException {
        try (Book book = byteArrayToBook((byte[]) obj)) {
            if (book.getSheetCount() == 0) {
                return null;
            }
            ec.util.spreadsheet.Sheet sheet = book.getSheet(0);
            Table<Object> result = new Table<>(sheet.getRowCount(), sheet.getColumnCount());
            for (int i = 0; i < sheet.getRowCount(); i++) {
                for (int j = 0; j < sheet.getColumnCount(); j++) {
                    Cell cell = sheet.getCell(i, j);
                    if (cell != null) {
                        if (cell.isDate()) {
                            result.set(i, j, cell.getDate());
                        } else if (cell.isNumber()) {
                            result.set(i, j, cell.getNumber());
                        } else if (cell.isString()) {
                            result.set(i, j, cell.getString());
                        }
                    }
                }
            }
            return result;
        }
    }

    @Override
    public boolean canExportTable(Table<?> table) {
        return config.exportTable;
    }

    @Override
    public Object exportTable(Table<?> table) throws IOException {
        return bookToByteArray(newArraySheet("dnd", table).toBook());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IConfigurable">
    @Override
    public Config getConfig() {
        return configurator.getConfig(this);
    }

    @Override
    public void setConfig(Config config) {
        configurator.setConfig(this, config);
    }

    @Override
    public Config editConfig(Config config) {
        return configurator.editConfig(config);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private byte[] bookToByteArray(Book book) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            bookFactory.store(stream, book);
            return stream.toByteArray();
        }
    }

    private Book byteArrayToBook(byte[] bytes) throws IOException {
        // FIXME: there is a bug here with some chars at the end of the stream
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes, 0, bytes.length - 2)) {
            return bookFactory.load(stream);
        }
    }

    private static DataFlavor createDataFlavor() {
        DataFlavor result = null;
        try {
            result = SystemFlavorMap.decodeDataFlavor("XML Spreadsheet");
        } catch (ClassNotFoundException ex) {
        }
        if (result == null) {
            result = new DataFlavor("xml/x;class=\"[B\"", "XML Spreadsheet");
            SystemFlavorMap map = (SystemFlavorMap) SystemFlavorMap.getDefaultFlavorMap();
            map.addUnencodedNativeForFlavor(result, "XML Spreadsheet");
            map.addFlavorForUnencodedNative("XML Spreadsheet", result);
            return result;
        }
        return result;
    }

    private static ArraySheet newArraySheet(String name, Matrix matrix) {
        ArraySheet.Builder result = ArraySheet.builder(matrix.getRowsCount(), matrix.getColumnsCount()).name(name);
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            for (int j = 0; j < matrix.getColumnsCount(); j++) {
                result.value(i, j, matrix.get(i, j));
            }
        }
        return result.build();
    }

    private static ArraySheet newArraySheet(String name, Table<?> table) {
        ArraySheet.Builder result = ArraySheet.builder(table.getRowsCount(), table.getColumnsCount()).name(name);
        for (int i = 0; i < table.getRowsCount(); i++) {
            for (int j = 0; j < table.getColumnsCount(); j++) {
                result.value(i, j, table.get(i, j));
            }
        }
        return result.build();
    }

    private static ec.util.spreadsheet.Sheet asSheet(final String name, final Matrix matrix) {
        return new ec.util.spreadsheet.Sheet() {

            private final DoubleCell cell = new DoubleCell();

            @Override
            public int getRowCount() {
                return matrix.getRowsCount();
            }

            @Override
            public int getColumnCount() {
                return matrix.getColumnsCount();
            }

            @Override
            public Object getCellValue(int rowIdx, int columnIdx) throws IndexOutOfBoundsException {
                return matrix.get(rowIdx, columnIdx);
            }

            @Override
            public Cell getCell(int rowIdx, int columnIdx) throws IndexOutOfBoundsException {
                return cell.withValue(matrix.get(rowIdx, columnIdx));
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    private static final class DoubleCell extends Cell {

        private double value = Double.NaN;

        @Nonnull
        public DoubleCell withValue(double value) {
            this.value = value;
            return this;
        }

        @Override
        public boolean isNumber() {
            return true;
        }

        @Override
        public Number getNumber() {
            return value;
        }
    }

    public static final class InternalConfig {

        /**
         * true : one series per column, false : one series per line
         */
        public boolean vertical = true;
        /**
         * show or not the dates
         */
        public boolean showDates = true;
        /**
         * show or not the titles of the series
         */
        public boolean showTitle = true;
        /**
         * true to set the dates at the beginning of the period, false for the
         * end of the period
         */
        public boolean beginPeriod = true;
        public boolean importTimeSeries = true;
        public boolean exportTimeSeries = true;
        public boolean importMatrix = true;
        public boolean exportMatrix = true;
        public boolean importTable = true;
        public boolean exportTable = true;
    }

    private static Configurator<XmlssTssTransferHandler> createConfigurator() {
        return new InternalConfigHandler().toConfigurator(new InternalConfigConverter(), new InternalConfigEditor());
    }

    private static final class InternalConfigHandler extends BeanHandler<InternalConfig, XmlssTssTransferHandler> {

        @Override
        public InternalConfig loadBean(XmlssTssTransferHandler resource) {
            return resource.config;
        }

        @Override
        public void storeBean(XmlssTssTransferHandler resource, InternalConfig bean) {
            resource.config = bean;
        }
    }

    private static final class InternalConfigEditor implements IBeanEditor {

        @Override
        public boolean editBean(Object bean) throws IntrospectionException {
            Sheet sheet = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.reset("tscollection").display("Time Series");
            b.withBoolean().selectField(bean, "importTimeSeries").display("Allow import").add();
            b.withBoolean().selectField(bean, "exportTimeSeries").display("Allow export").add();
            b.withBoolean().selectField(bean, "vertical").display("Vertical alignment").add();
            b.withBoolean().selectField(bean, "showDates").display("Include date headers").add();
            b.withBoolean().selectField(bean, "showTitle").display("Include title headers").add();
            b.withBoolean().selectField(bean, "beginPeriod").display("Begin period").add();
            sheet.put(b.build());

            b.reset("matrix").display("Matrix");
//            b.withBoolean().selectField(this, "importMatrix").display("Allow import").add();
            b.withBoolean().selectField(bean, "exportMatrix").display("Allow export").add();
            sheet.put(b.build());

            b.reset("table").display("Table");
            b.withBoolean().selectField(bean, "importTable").display("Allow import").add();
            b.withBoolean().selectField(bean, "exportTable").display("Allow export").add();

            sheet.put(b.build());
            return OpenIdePropertySheetBeanEditor.editSheet(sheet, "Configure XML Spreadsheet (XMLSS)",
                    ImageUtilities.icon2Image(DemetraUiIcon.CLIPBOARD_PASTE_DOCUMENT_TEXT_16));
        }
    }

    private static final class InternalConfigConverter extends Converter<InternalConfig, Config> {

        private static final String DOMAIN = TssTransferHandler.class.getName(), NAME = "XMLSS", VERSION = "";
        private static final IParam<Config, Boolean> VERTICAL = Params.onBoolean(true, "vertical");
        private static final IParam<Config, Boolean> SHOW_DATES = Params.onBoolean(true, "showDates");
        private static final IParam<Config, Boolean> SHOW_TITLE = Params.onBoolean(true, "showTitle");
        private static final IParam<Config, Boolean> BEGIN_PERIOD = Params.onBoolean(true, "beginPeriod");
        private static final IParam<Config, Boolean> IMPORT_TS = Params.onBoolean(true, "importEnabled");
        private static final IParam<Config, Boolean> EXPORT_TS = Params.onBoolean(true, "exportEnabled");
        private static final IParam<Config, Boolean> IMPORT_MATRIX = Params.onBoolean(true, "importMatrix");
        private static final IParam<Config, Boolean> EXPORT_MATRIX = Params.onBoolean(true, "exportMatrix");
        private static final IParam<Config, Boolean> IMPORT_TABLE = Params.onBoolean(true, "importTable");
        private static final IParam<Config, Boolean> EXPORT_TABLE = Params.onBoolean(true, "exportTable");

        @Override
        protected Config doForward(InternalConfig a) {
            Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
            VERTICAL.set(b, a.vertical);
            SHOW_DATES.set(b, a.showDates);
            SHOW_TITLE.set(b, a.showTitle);
            BEGIN_PERIOD.set(b, a.beginPeriod);
            IMPORT_TS.set(b, a.importTimeSeries);
            EXPORT_TS.set(b, a.exportTimeSeries);
            IMPORT_MATRIX.set(b, a.importMatrix);
            EXPORT_MATRIX.set(b, a.exportMatrix);
            IMPORT_TABLE.set(b, a.importTable);
            EXPORT_TABLE.set(b, a.exportTable);
            return b.build();
        }

        @Override
        protected InternalConfig doBackward(Config config) {
            Preconditions.checkArgument(DOMAIN.equals(config.getDomain()));
            Preconditions.checkArgument(NAME.equals(config.getName()));
            InternalConfig result = new InternalConfig();
            result.vertical = VERTICAL.get(config);
            result.showDates = SHOW_DATES.get(config);
            result.showTitle = SHOW_TITLE.get(config);
            result.beginPeriod = BEGIN_PERIOD.get(config);
            result.importTimeSeries = IMPORT_TS.get(config);
            result.exportTimeSeries = EXPORT_TS.get(config);
            result.importMatrix = IMPORT_MATRIX.get(config);
            result.exportMatrix = EXPORT_MATRIX.get(config);
            result.importTable = IMPORT_TABLE.get(config);
            result.exportTable = EXPORT_TABLE.get(config);
            return result;
        }
    }
    //</editor-fold>
}
