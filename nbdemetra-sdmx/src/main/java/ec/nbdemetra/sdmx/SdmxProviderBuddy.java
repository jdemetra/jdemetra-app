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
package ec.nbdemetra.sdmx;

import demetra.ui.TsManager;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.Configurator;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.properties.FileLoaderFileFilter;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.AbstractDataSourceProviderBuddy;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.sdmx.SdmxBean;
import ec.tss.tsproviders.sdmx.SdmxProvider;
import ec.tss.tsproviders.sdmx.engine.CunningPlanFactory;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = IDataSourceProviderBuddy.class)
public class SdmxProviderBuddy extends AbstractDataSourceProviderBuddy implements IConfigurable {

    private final Configurator<SdmxProviderBuddy> configurator = createConfigurator();

    @Override
    public String getProviderName() {
        return SdmxProvider.SOURCE;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/sdmx/document-code.png", true);
    }

    @Override
    protected List<Set> createSheetSets(Object bean) throws IntrospectionException {
        return bean instanceof SdmxBean
                ? createSheetSets((SdmxBean) bean)
                : super.createSheetSets(bean);
    }

    private List<Set> createSheetSets(SdmxBean bean) {
        List<Set> result = new ArrayList<>();

        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Source");

        TsManager.getDefault()
                .lookup(IFileLoader.class, SdmxProvider.SOURCE)
                .ifPresent(o -> addFileProperty(b, bean, o));
        result.add(b.build());

        b.reset("Options");
        b.withAutoCompletion()
                .select(bean, "factory")
                .source(CunningPlanFactory.NAME)
                .display("Factory")
                .description("The factory used to extract the data.")
                .add();
        b.with(String.class)
                .select(bean, "titleAttribute")
                .display("Title attribute")
                .description("The attribute used as a time series title.")
                .add();
        result.add(b.build());

        return result;
    }

    @Override
    public Config getConfig() {
        return configurator.getConfig(this);
    }

    @Override
    public void setConfig(Config config) throws IllegalArgumentException {
        configurator.setConfig(this, config);
    }

    @Override
    public Config editConfig(Config config) throws IllegalArgumentException {
        return configurator.editConfig(config);
    }

    private static Configurator<SdmxProviderBuddy> createConfigurator() {
        return new SdmxBuddyConfigHandler().toConfigurator(new SdmxBuddyConfigConverter(), new SdmxBuddyConfigEditor());
    }

    private static void addFileProperty(NodePropertySetBuilder b, SdmxBean bean, IFileLoader loader) {
        b.withFile()
                .select(bean, "file")
                .display("Sdmx file")
                .description("The path to the sdmx file.")
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();
    }
}
