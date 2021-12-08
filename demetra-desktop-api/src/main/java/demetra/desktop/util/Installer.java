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

import demetra.timeseries.TsProvider;
import demetra.desktop.TsManager;
import demetra.desktop.workspace.WorkspaceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Installer extends ModuleInstall{

    final static Logger LOGGER = LoggerFactory.getLogger(Installer.class);

    public static final InstallerStep STEP = InstallerStep.all(
            new AppVersionStep(),
            new ProvidersV3Step(),
            new TsVariableStep());

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

//        final Preferences prefs = prefs();
//        final Parser<File[]> pathsParser = Parsers.onJAXB(PathsBean.class).andThen(o -> o.paths != null ? o.paths : new File[0])::parse;
//        final Formatter<File[]> pathsFormatter = Formatters.onJAXB(PathsBean.class, false).compose(PathsBean::create)::format;

        ProvidersV3Step() {
            super(TsProvider.class);
        }

        private void register(Iterable<? extends TsProvider> providers) {
//            Preferences pathsNode = prefs.node("paths");
            for (TsProvider o : providers) {
                TsManager.getDefault().register(o);
//                if (o instanceof FileLoader) {
//                    tryGet(pathsNode, o.getSource(), pathsParser)
//                            .ifPresent(((FileLoader) o)::setPaths);
//                }
            }
//            TsManager.getDefault().register(new PocProvider());
        }

        private void unregister(Iterable<? extends TsProvider> providers) {
//            Preferences pathsNode = prefs.node("paths");
            for (TsProvider o : providers) {
//                if (o instanceof FileLoader) {
//                    tryPut(pathsNode, o.getSource(), pathsFormatter, ((FileLoader) o).getPaths());
//                }
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
//            try {
//                prefs.flush();
//            } catch (BackingStoreException ex) {
//                LOGGER.warn("Can't flush storage", ex);
//            }
            TsManager.getDefault().close();
        }

//        @XmlRootElement(name = "paths")
//        @XmlJavaTypeAdapter(value = FileXmlAdapter.class, type = File.class)
//        static class PathsBean {
//
//            @XmlElement(name = "path")
//            public File[] paths;
//
//            static PathsBean create(File[] o) {
//                PathsBean result = new PathsBean();
//                result.paths = o;
//                return result;
//            }
//        }
    }

//    private static final class ProvidersV2Step extends InstallerStep.LookupStep<TsProvider> {
//
//        final Preferences prefs = prefs();
//        final Parser<File[]> pathsParser = Parsers.onJAXB(PathsBean.class).andThen(o -> o.paths != null ? o.paths : new File[0])::parse;
//        final Formatter<File[]> pathsFormatter = Formatters.onJAXB(PathsBean.class, false).compose(PathsBean::create)::format;
//
//        ProvidersV2Step() {
//            super(TsProvider.class);
//        }
//
//        private void register(Iterable<? extends TsProvider> providers) {
//            Preferences pathsNode = prefs.node("paths");
//            for (TsProvider o : providers) {
//                TsManager.getDefault().register(o);
//                if (o instanceof FileLoader) {
//                    tryGet(pathsNode, o.getSource(), pathsParser)
//                            .ifPresent(((FileLoader) o)::setPaths);
//                }
//            }
//        }
//
//        private void unregister(Iterable<? extends TsProvider> providers) {
//            Preferences pathsNode = prefs.node("paths");
//            for (TsProvider o : providers) {
//                if (o instanceof FileLoader) {
//                    tryPut(pathsNode, o.getSource(), pathsFormatter, ((FileLoader) o).getPaths());
//                }
//                TsManager.getDefault().unregister(o);
//            }
//        }
//
//        private static <X> List<X> except(List<X> l, List<X> r) {
//            List<X> result = new ArrayList(l);
//            result.removeAll(r);
//            return result;
//        }
//
//        private static String toString(Stream<? extends TsProvider> providers) {
//            return providers
//                    .map(o -> o.getSource() + "(" + o.getClass().getName() + ")")
//                    .collect(Collectors.joining(", "));
//        }
//
//        @Override
//        protected void onResultChanged(Lookup.Result<TsProvider> lookup) {
//            List<TsProvider> old = TsManager.getDefault().getProviders().collect(Collectors.toList());
//            List<TsProvider> current = new ArrayList<>(lookup.allInstances());
//
//            unregister(except(old, current));
//            register(except(current, old));
//        }
//
//        @Override
//        protected void onRestore(Lookup.Result<TsProvider> lookup) {
//            register(lookup.allInstances());
//            LOGGER.debug("Loaded providers: [{}]", toString(TsManager.getDefault().getProviders()));
//        }
//
//        @Override
//        protected void onClose(Lookup.Result<TsProvider> lookup) {
//            unregister(TsManager.getDefault().getProviders().collect(Collectors.toList()));
//            try {
//                prefs.flush();
//            } catch (BackingStoreException ex) {
//                LOGGER.warn("Can't flush storage", ex);
//            }
//        }
//
//        @XmlRootElement(name = "paths")
//        @XmlJavaTypeAdapter(value = FileXmlAdapter.class, type = File.class)
//        static class PathsBean {
//
//            @XmlElement(name = "path")
//            public File[] paths;
//
//            static PathsBean create(File[] o) {
//                PathsBean result = new PathsBean();
//                result.paths = o;
//                return result;
//            }
//        }
//    }
    
    private static final class TsVariableStep extends InstallerStep {

        @Override
        public void restore() {
//            TsVariable.register();
//            DynamicTsVariable.register();
        }
    }
}