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
package demetra.desktop.datatransfer.ts;

import com.google.common.base.Preconditions;
import com.google.common.base.StandardSystemProperty;
import demetra.desktop.Config;
import demetra.desktop.ConfigEditor;
import demetra.desktop.Converter;
import demetra.desktop.DemetraIcons;
import demetra.desktop.Persistable;
import demetra.desktop.actions.Configurable;
import demetra.desktop.beans.BeanConfigurator;
import demetra.desktop.beans.BeanEditor;
import demetra.desktop.beans.BeanHandler;
import demetra.desktop.datatransfer.DataTransferSpi;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import demetra.timeseries.TsFactory;
import demetra.timeseries.TsInformationType;
import demetra.util.MultiLineNameUtil;
import java.awt.datatransfer.DataFlavor;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import jdplus.math.matrices.FastMatrix;
import nbbrd.io.text.BooleanProperty;
import nbbrd.io.text.Parser;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

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
    private final DateTimeFormatter dateFormat;
    private final BeanConfigurator<InternalConfig, TxtDataTransfer> configurator;
    private InternalConfig config;

    public TxtDataTransfer() {
        this.numberFormat = NumberFormat.getNumberInstance();
        this.dateFormat = DateTimeFormatter.ISO_DATE;
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
        TsCollection loaded = col.load(TsInformationType.Data, TsFactory.getDefault());
        return tsCollectionToString(loaded);
    }

    @Override
    public boolean canImportTsCollection(Object obj) {
        return config.importTimeSeries && obj instanceof String;
    }

    @Override
    public demetra.timeseries.TsCollection importTsCollection(Object obj) throws IOException {
        demetra.timeseries.TsCollection col = tsCollectionFromString((String) obj);
        if (col == null) {
            throw new IOException("Cannot parse collection");
        }
        return col;
    }

    @Override
    public boolean canExportMatrix(demetra.math.matrices.Matrix matrix) {
        return config.exportMatrix && !matrix.isEmpty();
    }

    @Override
    public Object exportMatrix(demetra.math.matrices.Matrix matrix) throws IOException {
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
    public demetra.math.matrices.Matrix importMatrix(Object obj) throws IOException, ClassCastException {
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
        if (value instanceof LocalDate) {
            return ((LocalDate) value).format(dateFormat);
        }
        if (value instanceof Number) {
            return numberFormat.format(value);
        }
        return value.toString();
    }

    public String tsCollectionToString(demetra.timeseries.TsCollection col) throws IOException {
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

    public TsCollection tsCollectionFromString(String text) throws IOException {
        Parser<Number> valueParser = Parser.onNumberFormat(numberFormat);

        try {
            int nrows = 0;
            int ncols = 0;

            String[] rowarray = text.split("\\r?\\n");
            List<String[]> rows = new ArrayList<>();
            nrows = rowarray.length;
            for (int i = 0; i < nrows; i++) {
                String[] colarray = rowarray[i].split("\\t");
                rows.add(colarray);
                if (ncols < colarray.length) {
                    ncols = colarray.length;
                }
            }
            if (ncols < 1 || nrows < 1) {
                return null;
            }
            // Search for the orientation, the titles and the dates
            // if vertical, m(1,0) is a date. Otherwise m(0,1)
            boolean datesAreVertical = null != parseDate(rows.get(1)[0]);
            boolean hasTitles = null == parseDate(rows.get(0)[0]);
            boolean datesAreHorizontal = null != parseDate(rows.get(0)[1]);
            if (!datesAreVertical && !datesAreHorizontal) {
                return null;
            }
            LocalDate[] dates;
            String[] titles;
            FastMatrix data;
            int nr=(hasTitles || datesAreHorizontal) ? nrows-1 : nrows;
            int nc=(hasTitles || datesAreVertical) ? ncols-1 : ncols;
            if (datesAreVertical) {
                titles = new String[ncols - 1];
                if (hasTitles) {
                    for (int i = 0; i < titles.length; ++i) {
                        titles[i] = rows.get(0)[i + 1];
                    }
                    data = FastMatrix.make(nrows - 1, ncols - 1);
                } else {
                    data = FastMatrix.make(nrows, ncols - 1);
                    for (int i = 0; i < titles.length; ++i) {
                        titles[i] = "s" + (i + 1);
                    }
                }
                dates = new LocalDate[data.getRowsCount()];
                for (int i = 0, j = hasTitles ? 1 : 0; i < dates.length; ++i, ++j) {
                    dates[i] = parseDate(rows.get(j)[0]);
                }
            } else {
                titles = new String[nrows - 1];
                if (hasTitles) {
                    for (int i = 0; i < titles.length; ++i) {
                        titles[i] = rows.get(i + 1)[0];
                    }
                    data = FastMatrix.make(ncols - 1, nrows - 1);
                } else {
                    data = FastMatrix.make(ncols, nrows - 1);
                    for (int i = 0; i < titles.length; ++i) {
                        titles[i] = "s" + (i + 1);
                    }
                }
                dates = new LocalDate[data.getRowsCount()];
                for (int i = 0, j = hasTitles ? 1 : 0; i < dates.length; ++i, ++j) {
                    dates[i] = parseDate(rows.get(0)[j]);
                }
            }
            data.set(Double.NaN);

            for (int i = 0, j = (datesAreHorizontal || hasTitles) ? 1 : 0; i < nr; ++i, ++j) {
                String[] cols = rows.get(j);
                for (int k = 0, l = (datesAreVertical || hasTitles) ? 1 : 0; k < nc; ++k, ++l) {
                    Number value = valueParser.parse(cols[l]);
                    if (value != null) {
                        if (datesAreVertical) 
                        data.set(i, k, value.doubleValue());
                        else
                        data.set(k,i, value.doubleValue());
                    }
                }
            }
            int ndates = 0;
            for (LocalDate date : dates) {
                if (date != null) {
                    ++ndates;
                }
            }

            if (ndates < MINDATES) {
                return null;
            }
            TsCollectionAnalyser analyser = new TsCollectionAnalyser();

            analyser.data = data;
            analyser.dates = dates;
            analyser.titles = titles;
            List<Ts> result = analyser.create();

            List<Ts> nresult = new ArrayList<>();
            for (Ts s : result) {
                TsData d = s.getData();
                TsData nd = d.cleanExtremities();
                if (d != nd) {
                    nresult.add(s.toBuilder().data(nd).build());
                } else {
                    nresult.add(s);
                }
            }
            return TsCollection.of(nresult);
        } catch (Exception ex) {
            throw new IOException("Problem while retrieving data", ex);
        }
    }

    // TODO: use parsers
    private static LocalDate parseDate(String sd) {
        try {
            return LocalDate.parse(sd, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException ex) {
        }
        for (int i = 0; i < FALLBACK_FORMATS.length; ++i) {
            try {
                return LocalDate.parse(sd, DateTimeFormatter.ofPattern(FALLBACK_FORMATS[i], Locale.getDefault()));
            } catch (DateTimeParseException ex) {
            }
        }
        return null;
    }

//    private static final ThreadLocal<Parser<LocalDate>> FALLBACK_PARSER = new ThreadLocal<Parser<LocalDate>>() {
//        @Override
//        protected Parser<LocalDate> initialValue() {
//            ImmutableList.Builder<Parser<Date>> list = ImmutableList.builder();
//            for (String o : FALLBACK_FORMATS) {
//                DateFormat fmt;
//                list.add(Parser.onDateFormat(DateFormat(o)));
//            }
//            return Parsers  ..firstNotNull(list.build());
//        }
//    };
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
        public InternalConfig load(TxtDataTransfer resource) {
            return resource.config;
        }

        @Override
        public void store(TxtDataTransfer resource, InternalConfig bean) {
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
                    .icon(DemetraIcons.CLIPBOARD_PASTE_DOCUMENT_TEXT_16)
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
