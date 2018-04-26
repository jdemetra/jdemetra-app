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
package ec.nbdemetra.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import demetra.ui.TsManager;
import ec.tss.DynamicTsVariable;
import ec.tss.ITsProvider;
import ec.tss.sa.ISaDiagnosticsFactory;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.ISaProcessingFactory;
import ec.tss.sa.SaManager;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.utils.ByteArrayConverter;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.IParser;
import ec.tss.tsproviders.utils.Parsers;
import ec.tstoolkit.timeseries.regression.TsVariable;
import ec.tstoolkit.utilities.FileXmlAdapter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Installer {

    final static Logger LOGGER = LoggerFactory.getLogger(Installer.class);

    public static final InstallerStep STEP = InstallerStep.all(
            new AppVersionStep(),
            new ByteArrayConverterStep(),
            new ProvidersStep(),
            new SaFactoriesStep(),
            new SaDiagnosticsStep(),
            new SaOutputStep(),
            new TsVariableStep());

    private static final class AppVersionStep extends InstallerStep {

        @Override
        public void restore() {
            Properties p = new Properties();
            try {
                p.load(Installer.class.getResourceAsStream("/META-INF/maven/eu.europa.ec.joinup.sat/nbdemetra-ui/pom.properties"));
                System.setProperty("netbeans.buildnumber", p.getProperty("version"));
                p.clear();
            } catch (IOException ex) {
                LOGGER.warn("While loading version", ex);
            }
        }
    }

    private static final class ByteArrayConverterStep extends InstallerStep {

        @Override
        public void restore() {
            try {
                ByteArrayConverter.setInstance(new SnappyConverter());
                LOGGER.info("Using Snappy byte array converter");
            } catch (Exception ex) {
                LOGGER.warn("While loading Snappy byte array converter", ex);
            }
        }
    }

    private static final class ProvidersStep extends InstallerStep.LookupStep<ITsProvider> {

        final Preferences prefs = prefs();
        final IParser<File[]> pathsParser = Parsers.onJAXB(PathsBean.class).andThen(o -> o.paths != null ? o.paths : new File[0]);
        final IFormatter<File[]> pathsFormatter = Formatters.onJAXB(PathsBean.class, false).compose(PathsBean::create);

        ProvidersStep() {
            super(ITsProvider.class);
        }

        private void register(Iterable<? extends ITsProvider> providers) {
            Preferences pathsNode = prefs.node("paths");
            for (ITsProvider o : providers) {
                TsManager.getDefault().register(o);
                if (o instanceof IFileLoader) {
                    tryGet(pathsNode, o.getSource(), pathsParser)
                            .ifPresent(bean -> ((IFileLoader) o).setPaths(bean));
                }
            }
        }

        private void unregister(Iterable<? extends ITsProvider> providers) {
            Preferences pathsNode = prefs.node("paths");
            for (ITsProvider o : providers) {
                if (o instanceof IFileLoader) {
                    tryPut(pathsNode, o.getSource(), pathsFormatter, ((IFileLoader) o).getPaths());
                }
                TsManager.getDefault().unregister(o);
            }
        }

        private static <X> List<X> except(List<X> l, List<X> r) {
            List<X> result = new ArrayList(l);
            result.removeAll(r);
            return result;
        }

        private static String toString(Iterable<? extends ITsProvider> providers) {
            return Streams.stream(providers)
                    .map(o -> o.getSource() + "(" + o.getClass().getName() + ")")
                    .collect(Collectors.joining(", "));
        }

        @Override
        protected void onResultChanged(Lookup.Result<ITsProvider> lookup) {
            List<ITsProvider> old = Lists.newArrayList(TsManager.getDefault().all());
            List<ITsProvider> current = Lists.newArrayList(lookup.allInstances());

            unregister(except(old, current));
            register(except(current, old));
        }

        @Override
        protected void onRestore(Lookup.Result<ITsProvider> lookup) {
            register(lookup.allInstances());
            LOGGER.debug("Loaded providers: [{}]", toString(TsManager.getDefault().all()));
        }

        @Override
        protected void onClose(Lookup.Result<ITsProvider> lookup) {
            unregister(TsManager.getDefault().all());
            try {
                prefs.flush();
            } catch (BackingStoreException ex) {
                LOGGER.warn("Can't flush storage", ex);
            }
            TsManager.getDefault().close();
        }

        @XmlRootElement(name = "paths")
        @XmlJavaTypeAdapter(value = FileXmlAdapter.class, type = File.class)
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

    private static final class SaFactoriesStep extends InstallerStep.LookupStep<ISaProcessingFactory> {

        SaFactoriesStep() {
            super(ISaProcessingFactory.class);
        }

        @Override
        protected void onResultChanged(Lookup.Result<ISaProcessingFactory> lookup) {
            for (ISaProcessingFactory o : lookup.allInstances()) {
                // TODO
            }
        }

        @Override
        protected void onRestore(Lookup.Result<ISaProcessingFactory> lookup) {
            for (ISaProcessingFactory cur : lookup.allInstances()) {
                SaManager.instance.add(cur);
            }
            //LOGGER.debug("Loaded sa factories: [{}]", Strings.join(SaManager.instance.get..., ", "));
        }

        @Override
        protected void onClose(Lookup.Result<ISaProcessingFactory> lookup) {
            //closeProvidersByName(TsFactory.instance.getProviders());
        }
    }

    private static final class SaDiagnosticsStep extends InstallerStep.LookupStep<ISaDiagnosticsFactory> {

        SaDiagnosticsStep() {
            super(ISaDiagnosticsFactory.class);
        }

        @Override
        protected void onResultChanged(Lookup.Result<ISaDiagnosticsFactory> lookup) {
            for (ISaDiagnosticsFactory o : lookup.allInstances()) {
                // TODO
            }
        }

        @Override
        protected void onRestore(Lookup.Result<ISaDiagnosticsFactory> lookup) {
            for (ISaDiagnosticsFactory cur : lookup.allInstances()) {
                SaManager.instance.add(cur);
            }
            //LOGGER.debug("Loaded sa diagnostics: [{}]", Strings.join(SaManager.instance.get..., ", "));
        }

        @Override
        protected void onClose(Lookup.Result<ISaDiagnosticsFactory> lookup) {
            // TODO
        }
    }

    private static final class SaOutputStep extends InstallerStep.LookupStep<ISaOutputFactory> {

        SaOutputStep() {
            super(ISaOutputFactory.class);
        }

        @Override
        protected void onResultChanged(Lookup.Result<ISaOutputFactory> lookup) {
            for (ISaOutputFactory o : lookup.allInstances()) {
                // TODO
            }
        }

        @Override
        protected void onRestore(Lookup.Result<ISaOutputFactory> lookup) {
            for (ISaOutputFactory cur : lookup.allInstances()) {
                SaManager.instance.add(cur);
            }
            //LOGGER.debug("Loaded sa output: [{}]", Strings.join(SaManager.instance.get..., ", "));
        }

        @Override
        protected void onClose(Lookup.Result<ISaOutputFactory> lookup) {
            // TODO
        }
    }

    private static final class TsVariableStep extends InstallerStep {

        @Override
        public void restore() {
            TsVariable.register();
            DynamicTsVariable.register();
        }
    }
}
