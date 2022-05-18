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
package demetra.desktop.util;

import demetra.desktop.DemetraBehaviour;
import demetra.desktop.DemetraUI;
import demetra.desktop.Persistable;
import demetra.timeseries.TsProvider;
import demetra.desktop.TsManager;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import demetra.desktop.ui.mru.MruProvidersStep;
import demetra.desktop.ui.mru.MruWorkspacesStep;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.tsprovider.FileLoader;
import ec.util.chart.swing.Charts;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import nbbrd.io.xml.bind.Jaxb;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Installer extends ModuleInstall {

    final static Logger LOGGER = LoggerFactory.getLogger(Installer.class);

    public static final InstallerStep STEP = InstallerStep.all(
            new AppVersionStep(),
            new ProvidersV3Step(),
            new ProviderBuddyStep(),
            new TsVariableStep(),
            new MruProvidersStep(),
            new MruWorkspacesStep(),
            new JFreeChartStep(),
            new DemetraOptionsStep()
    );

    @Override
    public void restored() {
        super.restored();
        STEP.restore();
    }

    @Override
    public void close() {
        STEP.close();
        super.close();
    }

    @Override
    public boolean closing() {
        return WorkspaceFactory.getInstance().closeWorkspace(true);
    }

    private static final class AppVersionStep extends InstallerStep {

        @Override
        public void restore() {
            Properties p = new Properties();
            try {
                p.load(Installer.class.getResourceAsStream("/META-INF/maven/eu.europa.ec.joinup.sat/demetra-desktop-api/pom.properties"));
                System.setProperty("netbeans.buildnumber", p.getProperty("version"));
                p.clear();
            } catch (IOException ex) {
                LOGGER.warn("While loading version", ex);
            }
        }
    }

    private static final class ProvidersV3Step extends InstallerStep.LookupStep<TsProvider> {

        final Preferences prefs = prefs();
        final Parser<File[]> pathsParser = Jaxb.Parser.of(PathsBean.class).asParser().andThen(o -> o.paths != null ? o.paths : new File[0]);
        final Formatter<File[]> pathsFormatter = Jaxb.Formatter.of(PathsBean.class).asFormatter().compose(PathsBean::create);

        ProvidersV3Step() {
            super(TsProvider.class);
        }

        private void register(Iterable<? extends TsProvider> providers) {
            Preferences pathsNode = prefs.node("paths");
            for (TsProvider o : providers) {
                TsManager.getDefault().register(o);
                if (o instanceof FileLoader) {
                    tryGet(pathsNode, o.getSource(), pathsParser)
                            .ifPresent(((FileLoader) o)::setPaths);
                }
            }
//            TsManager.getDefault().register(new PocProvider());
        }

        private void unregister(Iterable<? extends TsProvider> providers) {
            Preferences pathsNode = prefs.node("paths");
            for (TsProvider o : providers) {
                if (o instanceof FileLoader) {
                    tryPut(pathsNode, o.getSource(), pathsFormatter, ((FileLoader) o).getPaths());
                }
                TsManager.getDefault().unregister(o);
            }
        }

        private static <X> List<X> except(List<X> l, List<X> r) {
            List<X> result = new ArrayList(l);
            result.removeAll(r);
            return result;
        }

        private static String toString(Stream<? extends TsProvider> providers) {
            return providers
                    .map(o -> o.getSource() + "(" + o.getClass().getName() + ")")
                    .collect(Collectors.joining(", "));
        }

        @Override
        protected void onResultChanged(Lookup.Result<TsProvider> lookup) {
            List<TsProvider> old = TsManager.getDefault().getProviders().collect(Collectors.toList());
            List<TsProvider> current = new ArrayList<>(lookup.allInstances());

            unregister(except(old, current));
            register(except(current, old));
        }

        @Override
        protected void onRestore(Lookup.Result<TsProvider> lookup) {
            register(lookup.allInstances());
            LOGGER.debug("Loaded providers: [{}]", toString(TsManager.getDefault().getProviders()));
        }

        @Override
        protected void onClose(Lookup.Result<TsProvider> lookup) {
            unregister(TsManager.getDefault().getProviders().collect(Collectors.toList()));
            try {
                prefs.flush();
            } catch (BackingStoreException ex) {
                LOGGER.warn("Can't flush storage", ex);
            }
            TsManager.getDefault().close();
        }

        @XmlRootElement(name = "paths")
        static class PathsBean {

            @XmlElement(name = "path")
            public File[] paths;

            static PathsBean create(File[] o) {
                PathsBean result = new PathsBean();
                result.paths = o;
                return result;
            }
        }
    }

    private static final class TsVariableStep extends InstallerStep {

        @Override
        public void restore() {
//            TsVariable.register();
//            DynamicTsVariable.register();
        }
    }

    private static final class ProviderBuddyStep extends InstallerStep.LookupStep<DataSourceProviderBuddy> {

        final Preferences prefs = prefs();

        public ProviderBuddyStep() {
            super(DataSourceProviderBuddy.class);
        }

        @Override
        protected void onRestore(Lookup.Result<DataSourceProviderBuddy> lookup) {
            for (DataSourceProviderBuddy buddy : lookup.allInstances()) {
                if (buddy instanceof Persistable persistable) {
                    tryGet(prefs, buddy.getProviderName(), XmlConfig.xmlParser()).ifPresent(persistable::setConfig);
                }
            }
        }

        @Override
        protected void onResultChanged(Lookup.Result<DataSourceProviderBuddy> lookup) {
            onRestore(lookup);
        }

        @Override
        protected void onClose(Lookup.Result<DataSourceProviderBuddy> lookup) {
            for (DataSourceProviderBuddy buddy : lookup.allInstances()) {
                if (buddy instanceof Persistable persistable) {
                    tryPut(prefs, buddy.getProviderName(), XmlConfig.xmlFormatter(false), persistable.getConfig());
                }
            }
        }
    }

    private static final class JFreeChartStep extends InstallerStep {

        @Override
        public void restore() {
            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            BarRenderer.setDefaultBarPainter(new StandardBarPainter());
            LOGGER.info("ChartPanel buffer " + (Charts.USE_CHART_PANEL_BUFFER ? "enabled" : "disabled"));
        }
    }

    private static final class DemetraOptionsStep extends InstallerStep {

        final Preferences prefs = prefs().node("options");

        private static final String UI = "ui", BEHAVIOUR = "behaviour";

        @Override
        public void restore() {
            DemetraUI ui = DemetraUI.getDefault();
            tryGet(prefs.node(UI)).ifPresent(ui::setConfig);
            DemetraBehaviour behaviour = DemetraBehaviour.getDefault();
            tryGet(prefs.node(BEHAVIOUR)).ifPresent(behaviour::setConfig);
        }

        @Override
        public void close() {
            DemetraUI ui = DemetraUI.getDefault();
            put(prefs.node(UI), ui.getConfig());
            DemetraBehaviour behaviour = DemetraBehaviour.getDefault();
            put(prefs.node(BEHAVIOUR), behaviour.getConfig());
        }
    }
}
