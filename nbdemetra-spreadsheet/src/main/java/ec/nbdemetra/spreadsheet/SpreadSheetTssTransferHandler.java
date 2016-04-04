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
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsFactory;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.datatransfer.TssTransferHandler;
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
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Philippe Charles
 */
abstract class SpreadSheetTssTransferHandler<T> extends TssTransferHandler {

    abstract protected AbstractBean getInternalConfig();

    abstract protected Book toBook(T input) throws IOException;

    abstract protected T fromBook(Book book) throws IOException;

    abstract protected boolean isInstance(Object obj);
    
    protected SpreadSheetFactory getFactory() {
        return SpreadSheetFactory.getDefault();
    }

    @Override
    public boolean canExportTsCollection(TsCollection col) {
        return getInternalConfig().exportTs;
    }

    @Override
    public Object exportTsCollection(TsCollection col) throws IOException {
        TsCollectionInformation info = new TsCollectionInformation(col, TsInformationType.Data);
        ArraySheet sheet = getFactory().fromTsCollectionInfo(info, getInternalConfig().getTsExportOptions());
        return fromBook(sheet.toBook());
    }

    @Override
    public boolean canImportTsCollection(Object obj) {
        return getInternalConfig().importTs && isInstance(obj);
    }

    @Override
    public TsCollection importTsCollection(Object obj) throws IOException {
        try (Book book = toBook((T) obj)) {
            TsCollection result = TsFactory.instance.createTsCollection();
            if (book.getSheetCount() > 0) {
                TsCollectionInformation info = getFactory().toTsCollectionInfo(book.getSheet(0), getInternalConfig().getTsImportOptions());
                for (TsInformation o : info.items) {
                    if (o.hasData()) {
                        result.add(TsFactory.instance.createTs(o.name, null, o.data));
                    }
                }
            }
            return result;
        }
    }

    @Override
    public boolean canExportMatrix(Matrix matrix) {
        return getInternalConfig().exportMatrix && !matrix.isEmpty();
    }

    @Override
    public Object exportMatrix(Matrix matrix) throws IOException {
        return fromBook(getFactory().fromMatrix(matrix).toBook());
    }

    @Override
    public boolean canImportTable(Object obj) {
        return getInternalConfig().importTable && isInstance(obj);
    }

    @Override
    public Table<?> importTable(Object obj) throws IOException, ClassCastException {
        try (Book book = toBook((T) obj)) {
            return book.getSheetCount() > 0
                    ? getFactory().toTable(book.getSheet(0))
                    : new Table<>(0, 0);
        }
    }

    @Override
    public boolean canExportTable(Table<?> table) {
        return getInternalConfig().exportTable;
    }

    @Override
    public Object exportTable(Table<?> table) throws IOException {
        return fromBook(getFactory().fromTable(table).toBook());
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
            return OpenIdePropertySheetBeanEditor.editSheet(getSheet(config), getTitle(), getImage());
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
