/*
 * Copyright 2015 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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

import demetra.bridge.TsConverter;
import demetra.math.matrices.MatrixType;
import demetra.timeseries.TsCollection;
import demetra.desktop.Config;
import demetra.desktop.DemetraIcons;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.DataFormatParam;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.tsproviders.spreadsheet.engine.SpreadSheetFactory;
import ec.tss.tsproviders.spreadsheet.engine.TsExportOptions;
import ec.tss.tsproviders.spreadsheet.engine.TsImportOptions;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.helpers.ArraySheet;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.IOException;
import nbbrd.io.text.BooleanProperty;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import nbbrd.io.text.Property;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import demetra.desktop.beans.BeanEditor;
import demetra.desktop.Converter;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
final class SpreadSheetTssTransferSupport {

    public interface Resource {

        AbstractBean getInternalConfig();

        Book toBook(Object input) throws IOException;

        Object fromBook(Book book) throws IOException;

        boolean isInstance(Object obj);

        default SpreadSheetFactory getFactory() {
            return SpreadSheetFactory.getDefault();
        }
    }

    private final Resource resource;

    public SpreadSheetTssTransferSupport(Resource resource) {
        this.resource = resource;
    }

    public boolean canExportTsCollection(TsCollection col) {
        return resource.getInternalConfig().exportTs;
    }

    public Object exportTsCollection(TsCollection col) throws IOException {
        TsCollectionInformation info = new TsCollectionInformation(TsConverter.fromTsCollection(col), TsInformationType.Data);
        ArraySheet sheet = resource.getFactory().fromTsCollectionInfo(info, resource.getInternalConfig().getTsExportOptions());
        return resource.fromBook(sheet.toBook());
    }

    public boolean canImportTsCollection(Object obj) {
        return resource.getInternalConfig().importTs && resource.isInstance(obj);
    }

    public TsCollection importTsCollection(Object obj) throws IOException {
        try (Book book = resource.toBook(obj)) {
            if (book.getSheetCount() > 0) {
                TsCollectionInformation info = resource.getFactory().toTsCollectionInfo(book.getSheet(0), resource.getInternalConfig().getTsImportOptions());
                return info.items
                        .stream()
                        .filter(TsInformation::hasData)
                        .map(tsInfo -> TsConverter.toTsBuilder(tsInfo).build())
                        .collect(TsCollection.toTsCollection());
            }
            return TsCollection.EMPTY;
        }
    }

    public boolean canImportMatrix(Object obj) {
        return false;
    }

    public MatrixType importMatrix(Object obj) throws IOException, ClassCastException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canExportMatrix(MatrixType matrix) {
        return resource.getInternalConfig().exportMatrix && !matrix.isEmpty();
    }

    public Object exportMatrix(MatrixType matrix) throws IOException {
        return resource.fromBook(resource.getFactory().fromMatrix(TsConverter.fromMatrix(matrix)).toBook());
    }

    public boolean canImportTable(Object obj) {
        return resource.getInternalConfig().importTable && resource.isInstance(obj);
    }

    public demetra.util.Table<?> importTable(Object obj) throws IOException, ClassCastException {
        try (Book book = resource.toBook(obj)) {
            return book.getSheetCount() > 0
                    ? TsConverter.toTable(resource.getFactory().toTable(book.getSheet(0)))
                    : new demetra.util.Table<>(0, 0);
        }
    }

    public boolean canExportTable(demetra.util.Table<?> table) {
        return resource.getInternalConfig().exportTable;
    }

    public Object exportTable(demetra.util.Table<?> table) throws IOException {
        return resource.fromBook(resource.getFactory().fromTable(TsConverter.fromTable(table)).toBook());
    }

    public abstract static class AbstractBean {

        public boolean importTs = true;
        public DataFormat dataFormat = DataFormat.DEFAULT;
        public TsFrequency frequency = TsFrequency.Undefined;
        public TsAggregationType aggregationType = TsAggregationType.None;
        public boolean cleanMissing = true;

        public boolean exportTs = true;
        public boolean vertical = true;
        public boolean showDates = true;
        public boolean showTitle = true;
        public boolean beginPeriod = true;

        public boolean importMatrix = true;
        public boolean exportMatrix = true;

        public boolean importTable = true;
        public boolean exportTable = true;

        private TsImportOptions getTsImportOptions() {
            return TsImportOptions.create(dataFormat, frequency, aggregationType, cleanMissing);
        }

        private TsExportOptions getTsExportOptions() {
            return TsExportOptions.create(vertical, showDates, showTitle, beginPeriod);
        }
    }

    public abstract static class AbstractBeanEditor implements BeanEditor {

        abstract protected String getTitle();

        protected Image getImage() {
            return ImageUtilities.icon2Image(DemetraIcons.CLIPBOARD_PASTE_DOCUMENT_TEXT_16);
        }

        @NbBundle.Messages({
            "bean.importTs.display=Allow import",
            "bean.importTs.description=Enable/disable import of time series.",
            "bean.exportTs.display=Allow export",
            "bean.exportTs.description=Enable/disable export of time series."
        })
        protected Sheet getSheet(AbstractBean bean) {
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.reset("tsimport").display("Time series import");
            b.withBoolean()
                    .selectField(bean, "importTs")
                    .display(Bundle.bean_importTs_display())
                    .description(Bundle.bean_importTs_description())
                    .add();
            b.with(DataFormat.class)
                    .selectField(bean, "dataFormat")
                    .display(Bundle.bean_dataFormat_display())
                    .description(Bundle.bean_dataFormat_description())
                    .add();
            b.withEnum(TsFrequency.class)
                    .selectField(bean, "frequency")
                    .display(Bundle.bean_frequency_display())
                    .description(Bundle.bean_frequency_description())
                    .add();
            b.withEnum(TsAggregationType.class)
                    .selectField(bean, "aggregationType")
                    .display(Bundle.bean_aggregationType_display())
                    .description(Bundle.bean_aggregationType_description())
                    .add();
            b.withBoolean()
                    .selectField(bean, "cleanMissing")
                    .display(Bundle.bean_cleanMissing_display())
                    .description(Bundle.bean_cleanMissing_description())
                    .add();
            result.put(b.build());

            b.reset("tsexport").display("Time series export");
            b.withBoolean()
                    .selectField(bean, "exportTs")
                    .display(Bundle.bean_exportTs_display())
                    .description(Bundle.bean_exportTs_description())
                    .add();
            b.withBoolean().selectField(bean, "vertical").display("Vertical alignment").add();
            b.withBoolean().selectField(bean, "showDates").display("Include date headers").add();
            b.withBoolean().selectField(bean, "showTitle").display("Include title headers").add();
            b.withBoolean().selectField(bean, "beginPeriod").display("Begin period").add();
            result.put(b.build());

            b.reset("other").display("Other");
//            b.withBoolean().selectField(this, "importMatrix").display("Allow import").add();
            b.withBoolean().selectField(bean, "exportMatrix").display("Allow matrix export").add();
            b.withBoolean().selectField(bean, "importTable").display("Allow table import").add();
            b.withBoolean().selectField(bean, "exportTable").display("Allow table export").add();
            result.put(b.build());

            return result;
        }

        @Override
        final public boolean editBean(Object bean) throws IntrospectionException {
            AbstractBean config = (AbstractBean) bean;
            return new PropertySheetDialogBuilder().title(getTitle()).icon(getImage()).editSheet(getSheet(config));
        }
    }

    public abstract static class AbstractConverter<T extends AbstractBean> implements Converter<T, Config> {

        private static final BooleanProperty IMPORT_TS = BooleanProperty.of("importEnabled", true);
        private static final BooleanProperty EXPORT_TS = BooleanProperty.of("exportEnabled", true);
        private static final Config.Converter<DataFormat> DATAFORMAT = new DataFormatParam(DataFormat.DEFAULT, "locale", "datePattern", "numberPattern");
        private static final Property<TsFrequency> FREQUENCY = Property.of("frequency", TsFrequency.Undefined, Parser.onEnum(TsFrequency.class), Formatter.onEnum());
        private static final Property<TsAggregationType> AGGREGATION_TYPE = Property.of("aggregationType", TsAggregationType.None, Parser.onEnum(TsAggregationType.class), Formatter.onEnum());
        private static final BooleanProperty CLEAN_MISSING = BooleanProperty.of("cleanMissing", true);
        private static final BooleanProperty VERTICAL = BooleanProperty.of("vertical", true);
        private static final BooleanProperty SHOW_DATES = BooleanProperty.of("showDates", true);
        private static final BooleanProperty SHOW_TITLE = BooleanProperty.of("showTitle", true);
        private static final BooleanProperty BEGIN_PERIOD = BooleanProperty.of("beginPeriod", true);
        private static final BooleanProperty IMPORT_MATRIX = BooleanProperty.of("importMatrix", true);
        private static final BooleanProperty EXPORT_MATRIX = BooleanProperty.of("exportMatrix", true);
        private static final BooleanProperty IMPORT_TABLE = BooleanProperty.of("importTable", true);
        private static final BooleanProperty EXPORT_TABLE = BooleanProperty.of("exportTable", true);

        abstract protected Config.Builder newBuilder();

        abstract protected T newBean();

        @Override
        public Config doForward(T bean) {
            Config.Builder b = newBuilder();
            IMPORT_TS.set(b::parameter, bean.importTs);
            DATAFORMAT.set(b, bean.dataFormat);
            FREQUENCY.set(b::parameter, bean.frequency);
            AGGREGATION_TYPE.set(b::parameter, bean.aggregationType);
            CLEAN_MISSING.set(b::parameter, bean.cleanMissing);
            EXPORT_TS.set(b::parameter, bean.exportTs);
            VERTICAL.set(b::parameter, bean.vertical);
            SHOW_DATES.set(b::parameter, bean.showDates);
            SHOW_TITLE.set(b::parameter, bean.showTitle);
            BEGIN_PERIOD.set(b::parameter, bean.beginPeriod);
            IMPORT_MATRIX.set(b::parameter, bean.importMatrix);
            EXPORT_MATRIX.set(b::parameter, bean.exportMatrix);
            IMPORT_TABLE.set(b::parameter, bean.importTable);
            EXPORT_TABLE.set(b::parameter, bean.exportTable);
            return b.build();
        }

        @Override
        public T doBackward(Config config) {
            T result = newBean();
            result.importTs = IMPORT_TS.get(config::getParameter);
            result.dataFormat = DATAFORMAT.get(config);
            result.frequency = FREQUENCY.get(config::getParameter);
            result.aggregationType = AGGREGATION_TYPE.get(config::getParameter);
            result.cleanMissing = CLEAN_MISSING.get(config::getParameter);
            result.exportTs = EXPORT_TS.get(config::getParameter);
            result.vertical = VERTICAL.get(config::getParameter);
            result.showDates = SHOW_DATES.get(config::getParameter);
            result.showTitle = SHOW_TITLE.get(config::getParameter);
            result.beginPeriod = BEGIN_PERIOD.get(config::getParameter);
            result.importMatrix = IMPORT_MATRIX.get(config::getParameter);
            result.exportMatrix = EXPORT_MATRIX.get(config::getParameter);
            result.importTable = IMPORT_TABLE.get(config::getParameter);
            result.exportTable = EXPORT_TABLE.get(config::getParameter);
            return result;
        }
    }
}
