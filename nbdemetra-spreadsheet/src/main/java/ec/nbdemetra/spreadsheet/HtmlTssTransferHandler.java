/*
 * Copyright 2015 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or â€“ as soon they will be approved 
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

import ec.nbdemetra.spreadsheet.SpreadSheetTssTransferSupport.AbstractBean;
import ec.nbdemetra.spreadsheet.SpreadSheetTssTransferSupport.AbstractBeanEditor;
import ec.nbdemetra.spreadsheet.SpreadSheetTssTransferSupport.AbstractConverter;
import ec.nbdemetra.spreadsheet.SpreadSheetTssTransferSupport.Resource;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.IConfigurable;
import ec.tss.TsCollection;
import ec.tss.datatransfer.TssTransferHandler;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.html.HtmlBookFactory;
import java.awt.datatransfer.DataFlavor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = TssTransferHandler.class, position = 1500)
public final class HtmlTssTransferHandler extends TssTransferHandler implements IConfigurable {

    private final DataFlavor dataFlavor;
    private final SpreadSheetTssTransferSupport support;
    private final Configurator<HtmlTssTransferHandler> configurator;
    private HtmlBean config;

    public HtmlTssTransferHandler() {
        this.dataFlavor = createDataFlavor();
        this.support = new SpreadSheetTssTransferSupport(new ResourceImpl(() -> config));
        this.configurator = new HtmlBeanHandler().toConfigurator(new HtmlConverter(), new HtmlBeanEditor());
        this.config = new HtmlBean();
    }

    @Override
    public String getName() {
        return "HTML";
    }

    @Override
    public String getDisplayName() {
        return "HTML tables";
    }

    @Override
    public DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    @Override
    public boolean canExportTsCollection(TsCollection col) {
        return support.canExportTsCollection(col);
    }

    @Override
    public Object exportTsCollection(TsCollection col) throws IOException {
        return support.exportTsCollection(col);
    }

    @Override
    public boolean canImportTsCollection(Object obj) {
        return support.canImportTsCollection(obj);
    }

    @Override
    public TsCollection importTsCollection(Object obj) throws IOException {
        return support.importTsCollection(obj);
    }

    @Override
    public boolean canExportMatrix(Matrix matrix) {
        return support.canExportMatrix(matrix);
    }

    @Override
    public Object exportMatrix(Matrix matrix) throws IOException {
        return support.exportMatrix(matrix);
    }

    @Override
    public boolean canImportTable(Object obj) {
        return support.canImportTable(obj);
    }

    @Override
    public Table<?> importTable(Object obj) throws IOException, ClassCastException {
        return support.importTable(obj);
    }

    @Override
    public boolean canExportTable(Table<?> table) {
        return support.canExportTable(table);
    }

    @Override
    public Object exportTable(Table<?> table) throws IOException {
        return support.exportTable(table);
    }

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

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final class ResourceImpl implements Resource {

        private final HtmlBookFactory bookFactory;
        private final Supplier<? extends AbstractBean> configSupplier;

        public ResourceImpl(Supplier<? extends AbstractBean> configSupplier) {
            this.bookFactory = new HtmlBookFactory();
            this.configSupplier = configSupplier;
        }

        @Override
        public AbstractBean getInternalConfig() {
            return configSupplier.get();
        }

        @Override
        public Book toBook(Object input) throws IOException {
            try (ByteArrayInputStream stream = new ByteArrayInputStream(((String) input).getBytes())) {
                return bookFactory.load(stream);
            }
        }

        @Override
        public Object fromBook(Book book) throws IOException {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                bookFactory.store(stream, book);
                return new String(stream.toByteArray(), StandardCharsets.UTF_8);
            }
        }

        @Override
        public boolean isInstance(Object obj) {
            return obj instanceof String;
        }
    }

    private static DataFlavor createDataFlavor() {
        try {
            return new DataFlavor("text/html;class=java.lang.String");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static final class HtmlBean extends AbstractBean {
    }

    private static final class HtmlBeanHandler extends BeanHandler<HtmlBean, HtmlTssTransferHandler> {

        @Override
        public HtmlBean loadBean(HtmlTssTransferHandler resource) {
            return resource.config;
        }

        @Override
        public void storeBean(HtmlTssTransferHandler resource, HtmlBean bean) {
            resource.config = bean;
        }
    }

    private static final class HtmlBeanEditor extends AbstractBeanEditor {

        @Override
        protected String getTitle() {
            return "Configure HTML tables";
        }
    }

    private static final class HtmlConverter extends AbstractConverter<HtmlBean> {

        @Override
        protected Config.Builder newBuilder() {
            return Config.builder(TssTransferHandler.class.getName(), "HTML", "");
        }

        @Override
        protected HtmlBean newBean() {
            return new HtmlBean();
        }
    }
    //</editor-fold>
}
