/*
 * Copyright 2013 National Bank of Belgium
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
package ec.nbdemetra.ui;

import demetra.ui.Config;
import demetra.bridge.TsConverter;
import demetra.tsprovider.DataSourceLoader;
import demetra.ui.DemetraOptions;
import ec.nbdemetra.core.InstallerStep;
import ec.nbdemetra.sa.output.INbOutputFactory;
import ec.nbdemetra.ui.interchange.InterchangeBroker;
import ec.nbdemetra.ui.mru.MruProvidersStep;
import ec.nbdemetra.ui.mru.MruWorkspacesStep;
import ec.nbdemetra.ui.sa.SaDiagnosticsFactoryBuddy;
import ec.nbdemetra.ui.star.StarHelper;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.Parsers;
import ec.util.chart.swing.Charts;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import demetra.ui.Persistable;
import demetra.ui.TsManager;
import demetra.ui.datatransfer.DataTransfer;
import demetra.ui.datatransfer.DataTransferSpi;
import java.util.stream.Collectors;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;

public final class Installer extends ModuleInstall {

    private final static Logger LOGGER = LoggerFactory.getLogger(Installer.class);

    private final InstallerStep step = InstallerStep.all(
            new JFreeChartStep(),
            new FormattersStep(),
            new MruProvidersStep(),
            new MruWorkspacesStep(),
            new StarHelper(),
            new DemetraUIStep(),
            new PersistOpenedDataSourcesStep(),
            new InterchangeStep(),
            new ProviderBuddiesStep(),
            new DiagnosticsBuddiesStep(),
            new OutputBuddiesStep());

    @Override
    public void restored() {
        ec.nbdemetra.core.Installer.STEP.restore();
        super.restored();
        step.restore();
    }

    @Override
    public void close() {
        step.close();
        super.close();
        ec.nbdemetra.core.Installer.STEP.close();
    }

    @Override
    public boolean closing() {
        return WorkspaceFactory.getInstance().closeWorkspace(true);
    }

    //<editor-fold defaultstate="collapsed" desc="Steps implementation">
    private static final class JFreeChartStep extends InstallerStep {

        @Override
        public void restore() {
            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            BarRenderer.setDefaultBarPainter(new StandardBarPainter());
            LOGGER.info("ChartPanel buffer " + (Charts.USE_CHART_PANEL_BUFFER ? "enabled" : "disabled"));
        }
    }

    private static final class FormattersStep extends InstallerStep.LookupStep<DataTransferSpi> {

        FormattersStep() {
            super(DataTransferSpi.class);
        }

        @Override
        protected void onResultChanged(Lookup.Result<DataTransferSpi> lookup) {
            // TODO: loadConfig
        }

        @Override
        protected void onRestore(Lookup.Result<DataTransferSpi> lookup) {
            loadConfig(lookup.allInstances(), prefs());
        }

        @Override
        protected void onClose(Lookup.Result<DataTransferSpi> lookup) {
            storeConfig(DataTransfer.getDefault().getProviders(), prefs());
        }
    }

    private static final class DemetraUIStep extends InstallerStep {

        @Override
        public void restore() {
            DemetraUI ui = DemetraUI.getDefault();
            loadConfig(Collections.singleton(ui), prefs());
        }

        @Override
        public void close() {
            DemetraUI ui = DemetraUI.getDefault();
            storeConfig(Collections.singleton(ui), prefs());
        }
    }

    private static final class PersistOpenedDataSourcesStep extends InstallerStep {

        @Override
        public void restore() {
            if (DemetraOptions.getDefault().isPersistOpenedDataSources()) {
                Preferences prefs = prefs();
                Parser<DataSourcesBean> parser = Parsers.onJAXB(DataSourcesBean.class)::parse;
                TsManager.getDefault().getProviders()
                        .filter(DataSourceLoader.class::isInstance)
                        .map(DataSourceLoader.class::cast)
                        .forEach(o -> {
                            Optional<DataSourcesBean> value = tryGet(prefs, o.getSource(), parser);
                            if (value.isPresent()) {
                                for (DataSource dataSource : value.get()) {
                                    o.open(TsConverter.toDataSource(dataSource));
                                }
                            }
                        });
            }
        }

        @Override
        public void close() {
            if (DemetraOptions.getDefault().isPersistOpenedDataSources()) {
                Preferences prefs = prefs();
                Formatter<DataSourcesBean> formatter = Formatters.onJAXB(DataSourcesBean.class, false)::format;
                TsManager.getDefault().getProviders()
                        .filter(DataSourceLoader.class::isInstance)
                        .map(DataSourceLoader.class::cast)
                        .forEach(o -> {
                            DataSourcesBean value = new DataSourcesBean();
                            value.dataSources = o.getDataSources().stream().map(TsConverter::fromDataSource).collect(Collectors.toList());
                            tryPut(prefs, o.getSource(), formatter, value);
                        });
                try {
                    prefs.flush();
                } catch (BackingStoreException ex) {
                    LOGGER.warn("Can't flush storage", ex);
                }
            }
        }

        @XmlRootElement(name = "dataSources")
        static class DataSourcesBean implements Iterable<DataSource> {

            @XmlElement(name = "dataSource")
            public List<DataSource> dataSources;

            @Override
            public Iterator<DataSource> iterator() {
                return dataSources != null ? dataSources.iterator() : Collections.emptyIterator();
            }
        }
    }

    private static final class InterchangeStep extends ConfigStep<InterchangeBroker> {

        InterchangeStep() {
            super(InterchangeBroker.class);
        }
    }

    private static final class ProviderBuddiesStep extends ConfigStep<IDataSourceProviderBuddy> {

        ProviderBuddiesStep() {
            super(IDataSourceProviderBuddy.class);
        }
    }

    private static final class DiagnosticsBuddiesStep extends ConfigStep<SaDiagnosticsFactoryBuddy> {

        DiagnosticsBuddiesStep() {
            super(SaDiagnosticsFactoryBuddy.class);
        }
    }

    private static final class OutputBuddiesStep extends ConfigStep<INbOutputFactory> {

        OutputBuddiesStep() {
            super(INbOutputFactory.class);
        }
    }

    private static class ConfigStep<T> extends InstallerStep.LookupStep<T> {

        public ConfigStep(Class<T> clazz) {
            super(clazz);
        }

        @Override
        protected void onResultChanged(Lookup.Result<T> lookup) {
            // TODO: loadConfig
        }

        @Override
        protected void onRestore(Lookup.Result<T> lookup) {
            loadConfig(lookup.allInstances(), prefs());
        }

        @Override
        protected void onClose(Lookup.Result<T> lookup) {
            Collection<? extends T> instances = lookup != null ? lookup.allInstances() : Lookup.getDefault().lookupAll(getLookupClass());
            storeConfig(instances, prefs());
        }
    }
    //</editor-fold>

    public static void loadConfig(Collection<?> list, Preferences root) {
        loadConfig(list.stream(), root);
    }

    public static void loadConfig(Stream<?> stream, Preferences root) {
        Parser<Config> parser = XmlConfig.xmlParser()::parse;
        stream
                .filter(Persistable.class::isInstance)
                .map(Persistable.class::cast)
                .forEach(o -> {
                    Config current = o.getConfig();
                    try {
                        if (root.nodeExists(current.getDomain())) {
                            Preferences domain = root.node(current.getDomain());
                            Optional<Config> config = InstallerStep.tryGet(domain, current.getName(), parser);
                            if (config.isPresent()) {
                                o.setConfig(config.get());
                            }
                        }
                    } catch (BackingStoreException ex) {
                        // do nothing?
                    }
                });
    }

    public static void storeConfig(Collection<?> list, Preferences root) {
        storeConfig(list.stream(), root);
    }

    private static void storeConfig(Stream<?> stream, Preferences root) {
        Formatter<Config> formatter = XmlConfig.xmlFormatter(false)::format;
        stream
                .filter(Persistable.class::isInstance)
                .map(Persistable.class::cast)
                .forEach(o -> {
                    Config current = o.getConfig();
                    Preferences domain = root.node(current.getDomain());
                    InstallerStep.tryPut(domain, current.getName(), formatter, current);
                });
        try {
            root.flush();
        } catch (BackingStoreException ex) {
            LOGGER.warn("Can't flush storage", ex);
        }
    }
}
