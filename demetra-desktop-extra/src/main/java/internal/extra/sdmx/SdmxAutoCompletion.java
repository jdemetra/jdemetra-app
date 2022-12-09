/*
 * Copyright 2017 National Bank of Belgium
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
package internal.extra.sdmx;

import sdmxdl.Dataflow;
import sdmxdl.DataflowRef;
import sdmxdl.Dimension;
import sdmxdl.LanguagePriorityList;
import sdmxdl.web.SdmxWebManager;
import sdmxdl.web.SdmxWebSource;
import demetra.desktop.TsManager;
import demetra.tsp.extra.sdmx.web.SdmxWebProvider;
import ec.util.completion.AutoCompletionSource;
import static ec.util.completion.AutoCompletionSource.Behavior.ASYNC;
import static ec.util.completion.AutoCompletionSource.Behavior.NONE;
import static ec.util.completion.AutoCompletionSource.Behavior.SYNC;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.completion.swing.CustomListCellRenderer;
import java.io.IOException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toList;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import nbbrd.desktop.favicon.DomainName;
import nbbrd.desktop.favicon.FaviconRef;
import nbbrd.desktop.favicon.FaviconSupport;
import nbbrd.desktop.favicon.URLConnectionFactory;
import org.openide.util.ImageUtilities;
import sdmxdl.Attribute;
import sdmxdl.Connection;
import sdmxdl.SdmxManager;
import sdmxdl.SdmxSource;
import sdmxdl.ext.Registry;
import sdmxdl.ext.spi.Dialect;
import sdmxdl.web.Network;

/**
 *
 * @author Philippe Charles
 */
public abstract class SdmxAutoCompletion {

    public abstract AutoCompletionSource getSource();

    public abstract ListCellRenderer getRenderer();

    public static SdmxAutoCompletion onDialect(Registry registry) {
        return new DialectCompletion(registry);
    }

    public static SdmxAutoCompletion onWebSource(SdmxWebManager manager) {
        return new WebSourceCompletion(manager);
    }

    public static <S extends SdmxSource> SdmxAutoCompletion onDataflow(SdmxManager<S> manager, Supplier<S> source, ConcurrentMap cache) {
        return new DataflowCompletion<>(manager, source, cache);
    }

    public static <S extends SdmxSource> SdmxAutoCompletion onDimension(SdmxManager<S> manager, Supplier<S> source, Supplier<DataflowRef> flowRef, ConcurrentMap cache) {
        return new DimensionCompletion<>(manager, source, flowRef, cache);
    }

    public static <S extends SdmxSource> SdmxAutoCompletion onAttribute(SdmxManager<S> manager, Supplier<S> source, Supplier<DataflowRef> flowRef, ConcurrentMap cache) {
        return new AttributeCompletion<>(manager, source, flowRef, cache);
    }

    @lombok.AllArgsConstructor
    private static final class DialectCompletion extends SdmxAutoCompletion {

        @lombok.NonNull
        private final Registry registry;

        @Override
        public AutoCompletionSource getSource() {
            return ExtAutoCompletionSource
                    .builder(this::load)
                    .behavior(SYNC)
                    .postProcessor(this::filterAndSort)
                    .valueToString(Dialect::getName)
                    .build();
        }

        @Override
        public ListCellRenderer getRenderer() {
            return CustomListCellRenderer.of(Dialect::getName, Dialect::getDescription);
        }

        private List<Dialect> load(String term) {
            return registry.getDialects();
        }

        private List<Dialect> filterAndSort(List<Dialect> list, String term) {
            return list.stream().filter(getFilter(term)).sorted(getSorter(term)).collect(toList());
        }

        private Predicate<Dialect> getFilter(String term) {
            Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
            return value -> filter.test(value.getDescription()) || filter.test(value.getName());
        }

        private Comparator<Dialect> getSorter(String term) {
            return Comparator.comparing(Dialect::getDescription);
        }
    }

    @lombok.AllArgsConstructor
    private static final class WebSourceCompletion extends SdmxAutoCompletion {

        @lombok.NonNull
        private final SdmxWebManager manager;

        @Override
        public AutoCompletionSource getSource() {
            return ExtAutoCompletionSource
                    .builder(this::load)
                    .behavior(SYNC)
                    .postProcessor(this::filterAndSort)
                    .valueToString(SdmxWebSource::getName)
                    .build();
        }

        @Override
        public ListCellRenderer getRenderer() {
            return new CustomListCellRenderer<SdmxWebSource>() {
                @Override
                protected String getValueAsString(SdmxWebSource value) {
                    return value.getName() + ": " + manager.getLanguages().select(value.getDescriptions());
                }

                @Override
                protected Icon toIcon(String term, JList list, SdmxWebSource value, int index, boolean isSelected, boolean cellHasFocus) {
                    return getFavicon(value.getWebsite(), list::repaint);
                }
            };
        }

        private List<SdmxWebSource> load(String term) {
            return manager
                    .getSources()
                    .values()
                    .stream()
                    .filter(source -> !source.isAlias())
                    .collect(toList());
        }

        private List<SdmxWebSource> filterAndSort(List<SdmxWebSource> list, String term) {
            return list.stream().filter(getFilter(term)).collect(toList());
        }

        private Predicate<SdmxWebSource> getFilter(String term) {
            Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
            LanguagePriorityList langs = manager.getLanguages();
            return value -> filter.test(langs.select(value.getDescriptions()))
                    || filter.test(value.getName())
                    || value.getAliases().stream().anyMatch(filter);
        }
    }

    @lombok.AllArgsConstructor
    private static final class DataflowCompletion<S extends SdmxSource> extends SdmxAutoCompletion {

        @lombok.NonNull
        private final SdmxManager<S> manager;

        @lombok.NonNull
        private final Supplier<S> source;

        @lombok.NonNull
        private final ConcurrentMap cache;

        @Override
        public AutoCompletionSource getSource() {
            return ExtAutoCompletionSource
                    .builder(this::load)
                    .behavior(this::getBehavior)
                    .postProcessor(this::filterAndSort)
                    .valueToString(o -> o.getRef().toString())
                    .cache(cache, this::getCacheKey, SYNC)
                    .build();
        }

        @Override
        public ListCellRenderer getRenderer() {
            return CustomListCellRenderer.<Dataflow>of(flow -> flow.getRef() + "<br><i>" + flow.getName(), flow -> flow.getRef().toString());
        }

        private List<Dataflow> load(String term) throws Exception {
            try ( Connection c = manager.getConnection(source.get())) {
                return new ArrayList<>(c.getFlows());
            }
        }

        private AutoCompletionSource.Behavior getBehavior(String term) {
            return source.get() != null ? ASYNC : NONE;
        }

        private List<Dataflow> filterAndSort(List<Dataflow> values, String term) {
            Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
            return values.stream()
                    .filter(o -> filter.test(o.getName()) || filter.test(o.getRef().getId()) || filter.test(o.getDescription()))
                    .sorted(Comparator.comparing(Dataflow::getName))
                    .collect(toList());
        }

        private String getCacheKey(String term) {
            return "Dataflow" + source.get() + manager.getLanguages();
        }
    }

    @lombok.AllArgsConstructor
    private static final class DimensionCompletion<S extends SdmxSource> extends SdmxAutoCompletion {

        @lombok.NonNull
        private final SdmxManager<S> manager;

        @lombok.NonNull
        private final Supplier<S> source;

        @lombok.NonNull
        private final Supplier<DataflowRef> flowRef;

        @lombok.NonNull
        private final ConcurrentMap cache;

        @Override
        public AutoCompletionSource getSource() {
            return ExtAutoCompletionSource
                    .builder(this::load)
                    .behavior(this::getBehavior)
                    .postProcessor(this::filterAndSort)
                    .valueToString(Dimension::getId)
                    .cache(cache, this::getCacheKey, SYNC)
                    .build();
        }

        @Override
        public ListCellRenderer getRenderer() {
            return CustomListCellRenderer.of(Dimension::getId, Dimension::getLabel);
        }

        private List<Dimension> load(String term) throws Exception {
            try ( Connection c = manager.getConnection(source.get())) {
                return new ArrayList<>(c.getStructure(flowRef.get()).getDimensions());
            }
        }

        private AutoCompletionSource.Behavior getBehavior(String term) {
            return source.get() != null && flowRef.get() != null ? ASYNC : NONE;
        }

        private List<Dimension> filterAndSort(List<Dimension> values, String term) {
            Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
            return values.stream()
                    .filter(o -> filter.test(o.getId()) || filter.test(o.getLabel()) || filter.test(String.valueOf(o.getPosition())))
                    .sorted(Comparator.comparing(Dimension::getId))
                    .collect(toList());
        }

        private String getCacheKey(String term) {
            return "Dimension" + source.get() + flowRef.get() + manager.getLanguages();
        }
    }

    @lombok.AllArgsConstructor
    private static final class AttributeCompletion<S extends SdmxSource> extends SdmxAutoCompletion {

        @lombok.NonNull
        private final SdmxManager<S> manager;

        @lombok.NonNull
        private final Supplier<S> source;

        @lombok.NonNull
        private final Supplier<DataflowRef> flowRef;

        @lombok.NonNull
        private final ConcurrentMap cache;

        @Override
        public AutoCompletionSource getSource() {
            return ExtAutoCompletionSource
                    .builder(o -> load(o))
                    .behavior(this::getBehavior)
                    .postProcessor(this::filterAndSort)
                    .valueToString(Attribute::getId)
                    .cache(cache, this::getCacheKey, SYNC)
                    .build();
        }

        @Override
        public ListCellRenderer getRenderer() {
            return CustomListCellRenderer.of(Attribute::getId, Attribute::getLabel);
        }

        private List<Attribute> load(String term) throws Exception {
            try ( Connection c = manager.getConnection(source.get())) {
                return new ArrayList<>(c.getStructure(flowRef.get()).getAttributes());
            }
        }

        private AutoCompletionSource.Behavior getBehavior(String term) {
            return source.get() != null && flowRef.get() != null ? ASYNC : NONE;
        }

        private List<Attribute> filterAndSort(List<Attribute> values, String term) {
            Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
            return values.stream()
                    .filter(o -> filter.test(o.getId()) || filter.test(o.getLabel()))
                    .sorted(Comparator.comparing(Attribute::getId))
                    .collect(toList());
        }

        private String getCacheKey(String term) {
            return "Attribute" + source.get() + flowRef.get() + manager.getLanguages();
        }
    }

    public static ImageIcon getDefaultIcon() {
        return ImageUtilities.loadImageIcon("demetra/desktop/extra/sdmx/sdmx-logo.png", false);
    }

    public static Icon getFavicon(URL website) {
        return website != null
                ? FAVICONS.getOrDefault(FaviconRef.of(DomainName.of(website), 16), getDefaultIcon())
                : getDefaultIcon();
    }

    public static Icon getFavicon(URL website, Runnable callback) {
        return website != null
                ? FAVICONS.getOrDefault(FaviconRef.of(DomainName.of(website), 16), callback, getDefaultIcon())
                : getDefaultIcon();
    }

    public static final FaviconSupport FAVICONS = FaviconSupport
            .ofServiceLoader()
            .toBuilder()
            .client(new ClientOverCustomNetwork())
            .cache(new HashMap<>())
            //            .cache(IOCacheFactoryLoader.get().ofTtl(Duration.ofHours(1)))
            .build();

    private static final class ClientOverCustomNetwork implements URLConnectionFactory {

        @Override
        public URLConnection openConnection(URL url) throws IOException {
            Network network = getNetwork();
            Proxy proxy = selectProxy(network, url);
            URLConnection result = network.getURLConnectionFactory().openConnection(url, proxy);
            applyHttps(result, network);
            return result;
        }

        private void applyHttps(URLConnection result, Network network) {
            if (result instanceof HttpsURLConnection) {
                HttpsURLConnection https = (HttpsURLConnection) result;
                https.setHostnameVerifier(network.getHostnameVerifier());
                https.setSSLSocketFactory(network.getSSLSocketFactory());
            }
        }

        private Proxy selectProxy(Network network, URL url) throws IOException {
            try {
                return network.getProxySelector().select(url.toURI()).stream().findFirst().orElse(Proxy.NO_PROXY);
            } catch (URISyntaxException ex) {
                throw new IOException(ex);
            }
        }
    }

    private static Network getNetwork() {
        return TsManager
                .get()
                .getProvider(SdmxWebProvider.class)
                .map(SdmxWebProvider::getSdmxManager)
                .map(SdmxWebManager::getNetwork)
                .orElse(Network.getDefault());
    }
}
