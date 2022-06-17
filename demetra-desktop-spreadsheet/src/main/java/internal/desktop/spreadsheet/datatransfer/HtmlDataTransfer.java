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
package internal.desktop.spreadsheet.datatransfer;

import demetra.desktop.beans.BeanHandler;
import demetra.desktop.Config;
import demetra.desktop.ConfigEditor;
import demetra.desktop.DemetraIcons;
import java.awt.datatransfer.DataFlavor;
import org.openide.util.lookup.ServiceProvider;
import demetra.desktop.Persistable;
import demetra.desktop.actions.Configurable;
import demetra.desktop.datatransfer.DataTransferSpi;
import demetra.desktop.beans.BeanConfigurator;
import ec.util.spreadsheet.html.HtmlBookFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author Philippe Charles
 */
@ServiceProviders({
    @ServiceProvider(service = DataTransferSpi.class, position = 1000)
})
public final class HtmlDataTransfer implements DataTransferSpi, Configurable, Persistable, ConfigEditor {

    private final DataFlavor dataFlavor;
    @lombok.experimental.Delegate
    private final SpreadSheetDataTransferSupport support;
    private final BeanConfigurator<SpreadSheetDataTransferBean, HtmlDataTransfer> configurator;
    private SpreadSheetDataTransferBean config;

    public HtmlDataTransfer() {
        this.dataFlavor = createDataFlavor();
        this.support = new SpreadSheetDataTransferSupport(new HtmlBookFactory(), () -> config, SpreadSheetDataTransferSupport.RawDataType.TEXT);
        this.configurator = new BeanConfigurator<>(
                new HtmlBeanHandler(),
                new SpreadSheetDataTransferConverter("ec.tss.datatransfer.TssTransferHandler", "HTML", ""),
                new SpreadSheetDataTransferEditor("Configure HTML tables", ImageUtilities.icon2Image(DemetraIcons.CLIPBOARD_PASTE_DOCUMENT_TEXT_16))
        );
        this.config = new SpreadSheetDataTransferBean();
    }

    @Override
    public int getPosition() {
        return 1000;
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

    @Override
    public void configure() {
        Configurable.configure(this, this);
    }

    private static DataFlavor createDataFlavor() {
        try {
            return new DataFlavor("text/html;class=java.lang.String");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static final class HtmlBeanHandler implements BeanHandler<SpreadSheetDataTransferBean, HtmlDataTransfer> {

        @Override
        public SpreadSheetDataTransferBean load(HtmlDataTransfer resource) {
            return resource.config;
        }

        @Override
        public void store(HtmlDataTransfer resource, SpreadSheetDataTransferBean bean) {
            resource.config = bean;
        }
    }
}
