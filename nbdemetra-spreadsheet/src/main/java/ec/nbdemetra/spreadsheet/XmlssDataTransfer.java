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

import ec.nbdemetra.spreadsheet.SpreadSheetTssTransferSupport.AbstractBean;
import ec.nbdemetra.spreadsheet.SpreadSheetTssTransferSupport.AbstractBeanEditor;
import ec.nbdemetra.spreadsheet.SpreadSheetTssTransferSupport.AbstractConverter;
import ec.nbdemetra.spreadsheet.SpreadSheetTssTransferSupport.Resource;
import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.IConfigurable;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.xmlss.XmlssBookFactory;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;
import org.openide.util.lookup.ServiceProvider;
import demetra.ui.DataTransferSpi;

/**
 * XML Spreadsheet (XMLSS).
 *
 * @see http://msdn.microsoft.com/en-us/library/aa140066(v=office.10).aspx
 * @author Jean Palate
 */
@ServiceProvider(service = DataTransferSpi.class, position = 1000, supersedes = {"ec.tss.datatransfer.impl.XmlssTssTransferHandler"})
public final class XmlssDataTransfer implements DataTransferSpi, IConfigurable {

    private final DataFlavor dataFlavor;
    @lombok.experimental.Delegate
    private final SpreadSheetTssTransferSupport support;
    private final Configurator<XmlssDataTransfer> configurator;
    private XmlssBean config;

    public XmlssDataTransfer() {
        this.dataFlavor = createDataFlavor();
        this.support = new SpreadSheetTssTransferSupport(new ResourceImpl(() -> config));
        this.configurator = new XmlssBeanHandler().toConfigurator(new XmlssConverter(), new XmlssBeanEditor());
        this.config = new XmlssBean();
    }

    @Override
    public String getName() {
        return "XMLSS";
    }

    @Override
    public String getDisplayName() {
        return "XML Spreadsheet (XMLSS)";
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
    private static final class ResourceImpl implements Resource {

        private final XmlssBookFactory bookFactory;
        private final Supplier<? extends AbstractBean> configSupplier;

        public ResourceImpl(Supplier<? extends AbstractBean> configSupplier) {
            this.bookFactory = new XmlssBookFactory();
            this.configSupplier = configSupplier;
        }

        @Override
        public AbstractBean getInternalConfig() {
            return configSupplier.get();
        }

        @Override
        public Book toBook(Object input) throws IOException {
            byte[] bytes = (byte[]) input;
            try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
                return bookFactory.load(stream);
            }
        }

        @Override
        public Object fromBook(Book book) throws IOException {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                bookFactory.store(stream, book);
                return stream.toByteArray();
            }
        }

        @Override
        public boolean isInstance(Object obj) {
            return obj instanceof byte[];
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

    public static final class XmlssBean extends AbstractBean {
    }

    private static final class XmlssBeanHandler extends BeanHandler<XmlssBean, XmlssDataTransfer> {

        @Override
        public XmlssBean loadBean(XmlssDataTransfer resource) {
            return resource.config;
        }

        @Override
        public void storeBean(XmlssDataTransfer resource, XmlssBean bean) {
            resource.config = bean;
        }
    }

    private static final class XmlssBeanEditor extends AbstractBeanEditor {

        @Override
        protected String getTitle() {
            return "Configure XML Spreadsheet (XMLSS)";
        }
    }

    private static final class XmlssConverter extends AbstractConverter<XmlssBean> {

        @Override
        protected Config.Builder newBuilder() {
            return Config.builder("ec.tss.datatransfer.TssTransferHandler", "XMLSS", "");
        }

        @Override
        protected XmlssBean newBean() {
            return new XmlssBean();
        }
    }
    //</editor-fold>
}
