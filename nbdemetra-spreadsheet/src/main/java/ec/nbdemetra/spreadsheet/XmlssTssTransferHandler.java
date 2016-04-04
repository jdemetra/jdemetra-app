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

import ec.nbdemetra.ui.BeanHandler;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.IConfigurable;
import ec.tss.datatransfer.TssTransferHandler;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.xmlss.XmlssBookFactory;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.openide.util.lookup.ServiceProvider;

/**
 * XML Spreadsheet (XMLSS).
 *
 * @see http://msdn.microsoft.com/en-us/library/aa140066(v=office.10).aspx
 * @author Jean Palate
 */
@ServiceProvider(service = TssTransferHandler.class, position = 1000, supersedes = {"ec.tss.datatransfer.impl.XmlssTssTransferHandler"})
public final class XmlssTssTransferHandler extends SpreadSheetTssTransferHandler<byte[]> implements IConfigurable {

    private final DataFlavor dataFlavor;
    private final XmlssBookFactory bookFactory;
    private final Configurator<XmlssTssTransferHandler> configurator;
    private XmlssBean config;

    public XmlssTssTransferHandler() {
        this.dataFlavor = createDataFlavor();
        this.bookFactory = new XmlssBookFactory();
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
    @Override
    protected AbstractBean getInternalConfig() {
        return config;
    }

    @Override
    protected Book toBook(byte[] input) throws IOException {
        // FIXME: there is a bug here with some chars at the end of the stream
        try (ByteArrayInputStream stream = new ByteArrayInputStream(input, 0, input.length - 2)) {
            return bookFactory.load(stream);
        }
    }

    @Override
    protected byte[] fromBook(Book book) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            bookFactory.store(stream, book);
            return stream.toByteArray();
        }
    }

    @Override
    protected boolean isInstance(Object obj) {
        return obj instanceof byte[];
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

    private static final class XmlssBeanHandler extends BeanHandler<XmlssBean, XmlssTssTransferHandler> {

        @Override
        public XmlssBean loadBean(XmlssTssTransferHandler resource) {
            return resource.config;
        }

        @Override
        public void storeBean(XmlssTssTransferHandler resource, XmlssBean bean) {
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
            return Config.builder(TssTransferHandler.class.getName(), "XMLSS", "");
        }

        @Override
        protected XmlssBean newBean() {
            return new XmlssBean();
        }
    }
    //</editor-fold>
}
