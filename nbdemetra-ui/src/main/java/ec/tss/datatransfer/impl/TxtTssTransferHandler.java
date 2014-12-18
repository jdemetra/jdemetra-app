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

import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.ImmutableList;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsInformationType;
import ec.tss.TsStatus;
import ec.tss.datatransfer.TssTransferHandler;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.tss.tsproviders.utils.Parsers;
import ec.tss.tsproviders.utils.Parsers.Parser;
import ec.tstoolkit.data.Table;
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
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Jean Palate
 */
@ServiceProvider(service = TssTransferHandler.class, position = 2000)
public class TxtTssTransferHandler extends TssTransferHandler implements IConfigurable {

    private static final char DELIMITOR = '\t';
    private static final String NEWLINE = StandardSystemProperty.LINE_SEPARATOR.value();
    private static final int MINDATES = 2;
    // PROPERTIES
    private final NumberFormat numberFormat;
    private final DateFormat dateFormat;
    private final Configurator<TxtTssTransferHandler> configurator;
    private InternalConfig config;

    public TxtTssTransferHandler() {
        this.numberFormat = NumberFormat.getNumberInstance();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.configurator = new InternalConfigHandler().toConfigurator(new InternalConfigConverter(), new InternalConfigEditor());
        this.config = new InternalConfig();
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
    public boolean canExportTsCollection(TsCollection col) {
        return config.exportTimeSeries && !col.isEmpty();
    }

    @Override
    public Object exportTsCollection(TsCollection col) throws IOException {
        col.load(TsInformationType.Data);
        return tsCollectionToString(col);
    }

    @Override
    public boolean canImportTsCollection(Object obj) {
        return config.importTimeSeries && obj instanceof String;
    }

    @Override
    public TsCollection importTsCollection(Object obj) throws IOException {
        return tsCollectionFromString((String) obj);
    }

    @Override
    public boolean canExportMatrix(Matrix matrix) {
        return config.exportMatrix && !matrix.isEmpty();
    }

    @Override
    public Object exportMatrix(Matrix matrix) throws IOException {
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
    public boolean canExportTable(Table<?> table) {
        return config.exportTable && !table.isEmpty();
    }

    @Override
    public Object exportTable(Table<?> table) throws IOException {
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

    private String valueToString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Date) {
            return dateFormat.format((Date) value);
        }
        if (value instanceof Number) {
            return numberFormat.format((Number) value);
        }
        return value.toString();
    }

    //writes the collection of ts in a tab delimited txt format into a string
    public String tsCollectionToString(TsCollection col) throws IOException {
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
                    result.append(analyser.titles[i]);
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
                    result.append(analyser.titles[i]);
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

    public TsCollection tsCollectionFromString(String text) throws IOException {
        Parser<Date> periodParser = FALLBACK_PARSER.get();
        Parser<Number> valueParser = Parsers.onNumberFormat(numberFormat);

        TsCollection result = null;
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
                datesAreVertical = periodParser.tryParse(colarray[0]).isPresent();
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
                            && periodParser.tryParse(colarray[j]).isPresent()) {
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
            for (int i = 0; i < dates.length; ++i) {
                if (dates[i] != null) {
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
            result = analyser.create();

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
    private static final ThreadLocal<Parsers.Parser<Date>> FALLBACK_PARSER = new ThreadLocal<Parsers.Parser<Date>>() {
        @Override
        protected Parsers.Parser<Date> initialValue() {
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

    private static final class InternalConfigHandler extends BeanHandler<InternalConfig, TxtTssTransferHandler> {

        @Override
        public InternalConfig loadBean(TxtTssTransferHandler resource) {
            return resource.config;
        }

        @Override
        public void storeBean(TxtTssTransferHandler resource, InternalConfig bean) {
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
//            b.withBoolean().selectField(bean, "importMatrix").display("Import enabled").add();
            b.withBoolean().selectField(bean, "exportMatrix").display("Allow export").add();
            sheet.put(b.build());

            b.reset("table").display("Table");
            b.withBoolean().selectField(bean, "exportTable").display("Allow export").add();
            sheet.put(b.build());

            return OpenIdePropertySheetBeanEditor.editSheet(sheet, "Configure Tab-delimited values",
                    ImageUtilities.icon2Image(DemetraUiIcon.CLIPBOARD_PASTE_DOCUMENT_TEXT_16));
        }
    }

    private static final class InternalConfigConverter extends Converter<InternalConfig, Config> {

        private static final String DOMAIN = TssTransferHandler.class.getName(), NAME = "TXT", VERSION = "";
        private static final IParam<Config, Boolean> VERTICAL = Params.onBoolean(true, "vertical");
        private static final IParam<Config, Boolean> SHOW_DATES = Params.onBoolean(true, "showDates");
        private static final IParam<Config, Boolean> SHOW_TITLE = Params.onBoolean(true, "showTitle");
        private static final IParam<Config, Boolean> BEGIN_PERIOD = Params.onBoolean(true, "beginPeriod");
        private static final IParam<Config, Boolean> IMPORT_TS = Params.onBoolean(true, "importEnabled");
        private static final IParam<Config, Boolean> EXPORT_TS = Params.onBoolean(true, "exportEnabled");
        private static final IParam<Config, Boolean> IMPORT_MATRIX = Params.onBoolean(true, "importMatrix");
        private static final IParam<Config, Boolean> EXPORT_MATRIX = Params.onBoolean(true, "exportMatrix");
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
            result.exportTable = EXPORT_TABLE.get(config);
            return result;
        }
    }
}
