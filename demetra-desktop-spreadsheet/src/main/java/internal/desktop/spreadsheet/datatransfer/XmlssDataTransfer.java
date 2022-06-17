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
package internal.desktop.spreadsheet.datatransfer;

import demetra.desktop.beans.BeanHandler;
import demetra.desktop.Config;
import demetra.desktop.ConfigEditor;
import demetra.desktop.DemetraIcons;
import ec.util.spreadsheet.xmlss.XmlssBookFactory;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import org.openide.util.lookup.ServiceProvider;
import demetra.desktop.Persistable;
import demetra.desktop.actions.Configurable;
import demetra.desktop.datatransfer.DataTransferSpi;
import demetra.desktop.beans.BeanConfigurator;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProviders;
import internal.desktop.spreadsheet.datatransfer.SpreadSheetDataTransferSupport.RawDataType;

/**
 * XML Spreadsheet (XMLSS).
 *
 * @see http://msdn.microsoft.com/en-us/library/aa140066(v=office.10).aspx
 * @author Jean Palate
 */
@ServiceProviders({
    @ServiceProvider(service = DataTransferSpi.class, position = 1000)
})
public final class XmlssDataTransfer implements DataTransferSpi, Configurable, Persistable, ConfigEditor {

    private final DataFlavor dataFlavor;
    @lombok.experimental.Delegate
    private final SpreadSheetDataTransferSupport support;
    private final BeanConfigurator<SpreadSheetDataTransferBean, XmlssDataTransfer> configurator;
    private SpreadSheetDataTransferBean config;

    public XmlssDataTransfer() {
        this.dataFlavor = createDataFlavor();
        this.support = new SpreadSheetDataTransferSupport(new XmlssBookFactory(), () -> config, RawDataType.BYTES);
        this.configurator = new BeanConfigurator<>(
                new XmlssBeanHandler(),
                new SpreadSheetDataTransferConverter("ec.tss.datatransfer.TssTransferHandler", "XMLSS", ""),
                new SpreadSheetDataTransferEditor("Configure XML Spreadsheet (XMLSS)", ImageUtilities.icon2Image(DemetraIcons.CLIPBOARD_PASTE_DOCUMENT_TEXT_16))
        );
        this.config = new SpreadSheetDataTransferBean();
    }

    @Override
    public int getPosition() {
        return 1000;
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

    @Override
    public void configure() {
        Configurable.configure(this, this);
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

    private static final class XmlssBeanHandler implements BeanHandler<SpreadSheetDataTransferBean, XmlssDataTransfer> {

        @Override
        public SpreadSheetDataTransferBean load(XmlssDataTransfer resource) {
            return resource.config;
        }

        @Override
        public void store(XmlssDataTransfer resource, SpreadSheetDataTransferBean bean) {
            resource.config = bean;
        }
    }
}
