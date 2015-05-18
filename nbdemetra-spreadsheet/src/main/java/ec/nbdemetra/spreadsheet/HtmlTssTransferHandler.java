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

import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.IConfigurable;
import ec.tss.datatransfer.TssTransferHandler;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.html.HtmlBookFactory;
import java.awt.datatransfer.DataFlavor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = TssTransferHandler.class, position = 1500)
public final class HtmlTssTransferHandler extends SpreadSheetTssTransferHandler<String> implements IConfigurable {

    private final DataFlavor dataFlavor;
    private final HtmlBookFactory bookFactory;
    private final Configurator<HtmlTssTransferHandler> configurator;
    private HtmlBean config;

    public HtmlTssTransferHandler() {
        this.dataFlavor = createDataFlavor();
        this.bookFactory = new HtmlBookFactory();
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
    @Override
    protected AbstractBean getInternalConfig() {
        return config;
    }

    @Override
    protected Book toBook(String input) throws IOException {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes())) {
            return bookFactory.load(stream);
        }
    }

    @Override
    protected String fromBook(Book book) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            bookFactory.store(stream, book);
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    @Override
    protected boolean isInstance(Object obj) {
        return obj instanceof String;
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
