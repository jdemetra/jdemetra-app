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

import com.google.common.base.Converter;
import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import demetra.ui.TsManager;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.DemetraUiIcon;
import demetra.ui.properties.PropertySheetDialogBuilder;
import demetra.ui.properties.IBeanEditor;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.tsproviders.spreadsheet.engine.SpreadSheetFactory;
import ec.tss.tsproviders.spreadsheet.engine.TsExportOptions;
import ec.tss.tsproviders.spreadsheet.engine.TsImportOptions;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.helpers.ArraySheet;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.stream.Collectors;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

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
                return TsCollection
                        .builder()
                        .data(info.items
                                .stream()
                                .filter(TsInformation::hasData)
                                .map(o -> TsManager.toTs(o.name, o.data))
                                .collect(Collectors.toList()))
                        .build();
            }
            return TsCollection.EMPTY;
        }
    }

    public boolean canExportMatrix(Matrix matrix) {
        return resource.getInternalConfig().exportMatrix && !matrix.isEmpty();
    }

    public Object exportMatrix(Matrix matrix) throws IOException {
        return resource.fromBook(resource.getFactory().fromMatrix(matrix).toBook());
    }

    public boolean canImportTable(Object obj) {
        return resource.getInternalConfig().importTable && resource.isInstance(obj);
    }

    public Table<?> importTable(Object obj) throws IOException, ClassCastException {
        try (Book book = resource.toBook(obj)) {
            return book.getSheetCount() > 0
                    ? resource.getFactory().toTable(book.getSheet(0))
                    : new Table<>(0, 0);
        }
    }

    public boolean canExportTable(Table<?> table) {
        return resource.getInternalConfig().exportTable;
    }

    public Object exportTable(Table<?> table) throws IOException {
        return resource.fromBook(resource.getFactory().fromTable(table).toBook());
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

    public abstract static class AbstractBeanEditor implements IBeanEditor {

        abstract protected String getTitle();

        protected Image getImage() {
            return ImageUtilities.icon2Image(DemetraUiIcon.CLIPBOARD_PASTE_DOCUMENT_TEXT_16);
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

    public abstract static class AbstractConverter<T extends AbstractBean> extends Converter<T, Config> {

        private static final IParam<Config, Boolean> IMPORT_TS = Params.onBoolean(true, "importEnabled");
        private static final IParam<Config, Boolean> EXPORT_TS = Params.onBoolean(true, "exportEnabled");
        private static final IParam<Config, DataFormat> DATAFORMAT = Params.onDataFormat(DataFormat.DEFAULT, "locale", "datePattern", "numberPattern");
        private static final IParam<Config, TsFrequency> FREQUENCY = Params.onEnum(TsFrequency.Undefined, "frequency");
        private static final IParam<Config, TsAggregationType> AGGREGATION_TYPE = Params.onEnum(TsAggregationType.None, "aggregationType");
        private static final IParam<Config, Boolean> CLEAN_MISSING = Params.onBoolean(true, "cleanMissing");
        private static final IParam<Config, Boolean> VERTICAL = Params.onBoolean(true, "vertical");
        private static final IParam<Config, Boolean> SHOW_DATES = Params.onBoolean(true, "showDates");
        private static final IParam<Config, Boolean> SHOW_TITLE = Params.onBoolean(true, "showTitle");
        private static final IParam<Config, Boolean> BEGIN_PERIOD = Params.onBoolean(true, "beginPeriod");
        private static final IParam<Config, Boolean> IMPORT_MATRIX = Params.onBoolean(true, "importMatrix");
        private static final IParam<Config, Boolean> EXPORT_MATRIX = Params.onBoolean(true, "exportMatrix");
        private static final IParam<Config, Boolean> IMPORT_TABLE = Params.onBoolean(true, "importTable");
        private static final IParam<Config, Boolean> EXPORT_TABLE = Params.onBoolean(true, "exportTable");

        abstract protected Config.Builder newBuilder();

        abstract protected T newBean();

        @Override
        protected Config doForward(T bean) {
            Config.Builder b = newBuilder();
            IMPORT_TS.set(b, bean.importTs);
            DATAFORMAT.set(b, bean.dataFormat);
            FREQUENCY.set(b, bean.frequency);
            AGGREGATION_TYPE.set(b, bean.aggregationType);
            CLEAN_MISSING.set(b, bean.cleanMissing);
            EXPORT_TS.set(b, bean.exportTs);
            VERTICAL.set(b, bean.vertical);
            SHOW_DATES.set(b, bean.showDates);
            SHOW_TITLE.set(b, bean.showTitle);
            BEGIN_PERIOD.set(b, bean.beginPeriod);
            IMPORT_MATRIX.set(b, bean.importMatrix);
            EXPORT_MATRIX.set(b, bean.exportMatrix);
            IMPORT_TABLE.set(b, bean.importTable);
            EXPORT_TABLE.set(b, bean.exportTable);
            return b.build();
        }

        @Override
        protected T doBackward(Config config) {
            T result = newBean();
            result.importTs = IMPORT_TS.get(config);
            result.dataFormat = DATAFORMAT.get(config);
            result.frequency = FREQUENCY.get(config);
            result.aggregationType = AGGREGATION_TYPE.get(config);
            result.cleanMissing = CLEAN_MISSING.get(config);
            result.exportTs = EXPORT_TS.get(config);
            result.vertical = VERTICAL.get(config);
            result.showDates = SHOW_DATES.get(config);
            result.showTitle = SHOW_TITLE.get(config);
            result.beginPeriod = BEGIN_PERIOD.get(config);
            result.importMatrix = IMPORT_MATRIX.get(config);
            result.exportMatrix = EXPORT_MATRIX.get(config);
            result.importTable = IMPORT_TABLE.get(config);
            result.exportTable = EXPORT_TABLE.get(config);
            return result;
        }
    }
}
