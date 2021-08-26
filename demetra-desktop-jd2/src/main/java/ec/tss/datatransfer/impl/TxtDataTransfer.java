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
package ec.tss.datatransfer.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.ImmutableList;
import demetra.bridge.TsConverter;
import demetra.math.matrices.MatrixType;
import demetra.ui.Config;
import demetra.ui.ConfigEditor;
import demetra.ui.beans.BeanHandler;
import ec.nbdemetra.ui.DemetraUiIcon;
import demetra.ui.properties.PropertySheetDialogBuilder;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.tss.Ts;
import ec.tss.TsInformationType;
import ec.tss.TsStatus;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IParser;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tss.tsproviders.utils.Parsers;
import ec.tstoolkit.maths.matrices.Matrix;
import java.awt.datatransfer.DataFlavor;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.ServiceProvider;
import demetra.ui.datatransfer.DataTransferSpi;
import nbbrd.io.text.BooleanProperty;
import org.openide.util.lookup.ServiceProviders;
import demetra.ui.properties.BeanEditor;
import demetra.ui.Converter;
import demetra.ui.Persistable;
import demetra.ui.actions.Configurable;
import demetra.ui.beans.BeanConfigurator;

/**
 * @author Jean Palate
 */
@ServiceProviders({
    @ServiceProvider(service = DataTransferSpi.class, position = 1000)
})
public final class TxtDataTransfer implements DataTransferSpi, Configurable, Persistable, ConfigEditor {

    private static final char DELIMITOR = '\t';
    private static final String NEWLINE = StandardSystemProperty.LINE_SEPARATOR.value();
    private static final int MINDATES = 2;
    // PROPERTIES
    private final NumberFormat numberFormat;
    private final DateFormat dateFormat;
    private final BeanConfigurator<InternalConfig, TxtDataTransfer> configurator;
    private InternalConfig config;

    public TxtDataTransfer() {
        this.numberFormat = NumberFormat.getNumberInstance();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.configurator = new BeanConfigurator<>(new InternalConfigHandler(), new InternalConfigConverter(), new InternalConfigEditor());
        this.config = new InternalConfig();
    }

    @Override
    public int getPosition() {
        return 1000;
    }

    //<editor-fold defaultstate="collapsed" desc="INamedService">
    @Override
    public String getName() {
        return "TXT";
    }

    @Override
    public String getDisplayName() {
        return "Tab-delimited values";
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TssTransferHandler">
    @Override
    public DataFlavor getDataFlavor() {
        return DataFlavor.stringFlavor;
    }

    @Override
    public boolean canExportTsCollection(demetra.timeseries.TsCollection col) {
        return config.exportTimeSeries && !col.isEmpty();
    }

    @Override
    public Object exportTsCollection(demetra.timeseries.TsCollection col) throws IOException {
        ec.tss.TsCollection tmp = TsConverter.fromTsCollection(col);
        tmp.load(TsInformationType.Data);
        return tsCollectionToString(tmp);
    }

    @Override
    public boolean canImportTsCollection(Object obj) {
        return config.importTimeSeries && obj instanceof String;
    }

    @Override
    public demetra.timeseries.TsCollection importTsCollection(Object obj) throws IOException {
        ec.tss.TsCollection result = tsCollectionFromString((String) obj);
        if (result != null) {
            return TsConverter.toTsCollection(result);
        }
        throw new IOException("Cannot parse collection");
    }

    @Override
    public boolean canExportMatrix(MatrixType matrix) {
        return config.exportMatrix && !matrix.isEmpty();
    }

    @Override
    public Object exportMatrix(MatrixType matrix) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < matrix.getRowsCount(); i++) {
            result.append(numberFormat.format(matrix.get(i, 0)));
            for (int j = 1; j < matrix.getColumnsCount(); j++) {
                result.append(DELIMITOR).append(numberFormat.format(matrix.get(i, j)));
            }
            result.append(NEWLINE);
        }
        return result.toString();
    }

    @Override
    public boolean canImportMatrix(Object obj) {
        return false;
    }

    @Override
    public MatrixType importMatrix(Object obj) throws IOException, ClassCastException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canExportTable(demetra.util.Table<?> table) {
        return config.exportTable && !table.isEmpty();
    }

    @Override
    public Object exportTable(demetra.util.Table<?> table) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < table.getRowsCount(); i++) {
            result.append(valueToString(table.get(i, 0)));
            for (int j = 1; j < table.getColumnsCount(); j++) {
                result.append(DELIMITOR).append(valueToString(table.get(i, j)));
            }
            result.append(NEWLINE);
        }
        return result.toString();
    }

    @Override
    public boolean canImportTable(Object obj) {
        return false;
    }

    @Override
    public demetra.util.Table<?> importTable(Object obj) throws IOException, ClassCastException {
        throw new UnsupportedOperationException("Not supported yet.");
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

    @Override
    public void configure() {
        Configurable.configure(this, this);
    }
    //</editor-fold>

    private String valueToString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Date) {
            return dateFormat.format((Date) value);
        }
        if (value instanceof Number) {
            return numberFormat.format(value);
        }
        return value.toString();
    }

    public String tsCollectionToString(demetra.timeseries.TsCollection col) throws IOException {
        return tsCollectionToString(TsConverter.fromTsCollection(col));
    }

    //writes the collection of ts in a tab delimited txt format into a string
    public String tsCollectionToString(ec.tss.TsCollection col) throws IOException {
        if (col.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        TsCollectionAnalyser analyser = new TsCollectionAnalyser();
        analyser.set(col, config.beginPeriod);
        int nbdates = analyser.dates.length;
        int nseries = analyser.titles.length;
        if (config.vertical) // une série par colonne
        {
            // écriture des titres des séries
            if (config.showTitle) {
                if (config.showDates) {
                    result.append(DELIMITOR);
                }
                for (int i = 0; i < nseries; i++) {
                    result.append(MultiLineNameUtil.join(analyser.titles[i]));
                    if (i == nseries - 1) {
                        result.append(NEWLINE);
                    } else {
                        result.append(DELIMITOR);
                    }
                }
            }

            for (int i = 0; i < nbdates; i++) {
                if (config.showDates) {
                    result.append(dateFormat.format(analyser.dates[i])).append(DELIMITOR);
                }
                for (int j = 0; j < nseries; j++) {
                    double val = analyser.data.get(i, j);
                    if (!Double.isNaN(val)) {
                        result.append(numberFormat.format(val));
                    }
                    if (j == nseries - 1) {
                        result.append(NEWLINE);
                    } else {
                        result.append(DELIMITOR);
                    }
                }
            }

        } // une série par ligne
        else {
            if (config.showDates) {
                if (config.showTitle) {
                    result.append(DELIMITOR);
                }
                for (int i = 0; i < nbdates; i++) {
                    result.append(dateFormat.format(analyser.dates[i]));
                    result.append(DELIMITOR);
                    if (i == nbdates - 1) {
                        result.append(NEWLINE);
                    } else {
                        result.append(DELIMITOR);
                    }
                }
            }
            for (int i = 0; i < nseries; i++) {
                if (config.showTitle) {
                    result.append(MultiLineNameUtil.join(analyser.titles[i]));
                    result.append(DELIMITOR);
                }
                for (int j = 0; j < nbdates; j++) {
                    double val = analyser.data.get(j, i);
                    if (!Double.isNaN(val)) {
                        result.append(numberFormat.format(val));
                    }
                    if (j == nbdates - 1) {
                        result.append(NEWLINE);
                    } else {
                        result.append(DELIMITOR);
                    }
                }
            }
        }
        return result.toString();
    }

    public ec.tss.TsCollection tsCollectionFromString(String text) throws IOException {
        IParser<Date> periodParser = FALLBACK_PARSER.get();
        IParser<Number> valueParser = Parsers.onNumberFormat(numberFormat);

        ec.tss.TsCollection result = null;
        try {
            int rows = 0;
            int cols = 0;
            boolean datesAreVertical = true;

            String[] rowarray = text.split("\\r?\\n");
            rows = rowarray.length;
            for (int i = 0; i < rows; i++) {
                String[] colarray = rowarray[i].split("\\t");
                if (cols < colarray.length) {
                    cols = colarray.length;
                }
                datesAreVertical = periodParser.parseValue(colarray[0]).isPresent();
            }

            if (cols < 1 || rows < 1) {
                return null;
            }
            Matrix datamatrix = new Matrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    datamatrix.set(i, j, Double.NaN);
                }
            }
            Date[] dates = new Date[(datesAreVertical ? rows : cols)];
            String[] titles = new String[(datesAreVertical ? cols : rows)];

            rowarray = text.split("\\r?\\n");
            for (int i = 0; i < rowarray.length; i++) {
                String[] colarray = rowarray[i].split("\\t");
                for (int j = 0; j < colarray.length; j++) {
                    if (((j == 0 && datesAreVertical) || (i == 0 && !datesAreVertical))
                            && periodParser.parseValue(colarray[j]).isPresent()) {
                        dates[(datesAreVertical ? i : j)] = periodParser.parse(colarray[j]);
                    } else if ((j == 0 && !datesAreVertical)
                            || (i == 0 && datesAreVertical)) {
                        titles[(datesAreVertical ? j : i)] = colarray[j];
                    } else {
                        Number value = valueParser.parse(colarray[j]);
                        if (value != null) {
                            datamatrix.set(i, j, value.doubleValue());
                        }
                    }
                }
            }
            int ndates = 0;
            for (Date date : dates) {
                if (date != null) {
                    ++ndates;
                }
            }

            if (ndates < MINDATES) {
                return null;
            }
            TsCollectionAnalyser analyser = new TsCollectionAnalyser();

            analyser.data = (datesAreVertical ? datamatrix : datamatrix.transpose());
            analyser.dates = dates;
            analyser.titles = titles;
            result = TsConverter.fromTsCollection(analyser.create());

            for (Ts s : result) {
                if (s.hasData() == TsStatus.Valid) {
                    s.set(s.getTsData());//.cleanExtremities());
                }
            }
        } catch (Exception ex) {
            throw new IOException("Problem while retrieving data", ex);
        }
        return result;
    }
    private static final ThreadLocal<IParser<Date>> FALLBACK_PARSER = new ThreadLocal<IParser<Date>>() {
        @Override
        protected IParser<Date> initialValue() {
            ImmutableList.Builder<Parsers.Parser<Date>> list = ImmutableList.builder();
            for (String o : FALLBACK_FORMATS) {
                list.add(new DataFormat(Locale.ROOT, o, null).dateParser());
            }
            return Parsers.firstNotNull(list.build());
        }
    };
    // fallback formats; order matters!
    private static final String[] FALLBACK_FORMATS = {
        "yyyy-MM-dd",
        "yyyy MM dd",
        "yyyy.MM.dd",
        "yyyy-MMM-dd",
        "yyyy MMM dd",
        "yyyy.MMM.dd",
        "dd-MM-yyyy",
        "dd MM yyyy",
        "dd.MM.yyyy",
        "dd/MM/yyyy",
        "dd-MM-yy",
        "dd MM yy",
        "dd.MM.yy",
        "dd/MM/yy",
        "dd-MMM-yy",
        "dd MMM yy",
        "dd.MMM.yy",
        "dd/MMM/yy",
        "dd-MMM-yyyy",
        "dd MMM yyyy",
        "dd.MMM.yyyy",
        "dd/MMM/yyyy",
        "yyyy-MM-dd hh:mm:ss",
        "yyyy MM dd hh:mm:ss",
        "yyyy.MM.dd hh:mm:ss",
        "yyyy/MM/dd hh:mm:ss",
        "yyyy-MMM-dd hh:mm:ss",
        "yyyy MMM dd hh:mm:ss",
        "yyyy.MMM.dd hh:mm:ss",
        "yyyy/MMM/dd hh:mm:ss",
        "dd-MM-yyyy hh:mm:ss",
        "dd MM yyyy hh:mm:ss",
        "dd.MM.yyyy hh:mm:ss",
        "dd/MM/yyyy hh:mm:ss",
        "dd-MMM-yyyy hh:mm:ss",
        "dd MMM yyyy hh:mm:ss",
        "dd.MMM.yyyy hh:mm:ss",
        "dd/MMM/yyyy hh:mm:ss"};

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
        public boolean exportTable = true;

    }

    private static final class InternalConfigHandler implements BeanHandler<InternalConfig, TxtDataTransfer> {

        @Override
        public InternalConfig loadBean(TxtDataTransfer resource) {
            return resource.config;
        }

        @Override
        public void storeBean(TxtDataTransfer resource, InternalConfig bean) {
            resource.config = bean;
        }
    }

    private static final class InternalConfigEditor implements BeanEditor {

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
//            b.withBoolean().selectField(bean, "importMatrix").display("Import enabled").add();
            b.withBoolean().selectField(bean, "exportMatrix").display("Allow export").add();
            sheet.put(b.build());

            b.reset("table").display("Table");
            b.withBoolean().selectField(bean, "exportTable").display("Allow export").add();
            sheet.put(b.build());

            return new PropertySheetDialogBuilder()
                    .title("Configure Tab-delimited values")
                    .icon(DemetraUiIcon.CLIPBOARD_PASTE_DOCUMENT_TEXT_16)
                    .editSheet(sheet);
        }
    }

    private static final class InternalConfigConverter implements Converter<InternalConfig, Config> {

        private static final String DOMAIN = "ec.tss.datatransfer.TssTransferHandler", NAME = "TXT", VERSION = "";
        private static final BooleanProperty VERTICAL = BooleanProperty.of("vertical", true);
        private static final BooleanProperty SHOW_DATES = BooleanProperty.of("showDates", true);
        private static final BooleanProperty SHOW_TITLE = BooleanProperty.of("showTitle", true);
        private static final BooleanProperty BEGIN_PERIOD = BooleanProperty.of("beginPeriod", true);
        private static final BooleanProperty IMPORT_TS = BooleanProperty.of("importEnabled", true);
        private static final BooleanProperty EXPORT_TS = BooleanProperty.of("exportEnabled", true);
        private static final BooleanProperty IMPORT_MATRIX = BooleanProperty.of("importMatrix", true);
        private static final BooleanProperty EXPORT_MATRIX = BooleanProperty.of("exportMatrix", true);
        private static final BooleanProperty EXPORT_TABLE = BooleanProperty.of("exportTable", true);

        @Override
        public Config doForward(InternalConfig a) {
            Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
            VERTICAL.set(b::parameter, a.vertical);
            SHOW_DATES.set(b::parameter, a.showDates);
            SHOW_TITLE.set(b::parameter, a.showTitle);
            BEGIN_PERIOD.set(b::parameter, a.beginPeriod);
            IMPORT_TS.set(b::parameter, a.importTimeSeries);
            EXPORT_TS.set(b::parameter, a.exportTimeSeries);
            IMPORT_MATRIX.set(b::parameter, a.importMatrix);
            EXPORT_MATRIX.set(b::parameter, a.exportMatrix);
            EXPORT_TABLE.set(b::parameter, a.exportTable);
            return b.build();
        }

        @Override
        public InternalConfig doBackward(Config config) {
            Preconditions.checkArgument(DOMAIN.equals(config.getDomain()));
            Preconditions.checkArgument(NAME.equals(config.getName()));
            InternalConfig result = new InternalConfig();
            result.vertical = VERTICAL.get(config::getParameter);
            result.showDates = SHOW_DATES.get(config::getParameter);
            result.showTitle = SHOW_TITLE.get(config::getParameter);
            result.beginPeriod = BEGIN_PERIOD.get(config::getParameter);
            result.importTimeSeries = IMPORT_TS.get(config::getParameter);
            result.exportTimeSeries = EXPORT_TS.get(config::getParameter);
            result.importMatrix = IMPORT_MATRIX.get(config::getParameter);
            result.exportMatrix = EXPORT_MATRIX.get(config::getParameter);
            result.exportTable = EXPORT_TABLE.get(config::getParameter);
            return result;
        }
    }
}
