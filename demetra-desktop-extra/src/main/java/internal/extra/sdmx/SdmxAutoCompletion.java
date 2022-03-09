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

import internal.favicon.FaviconSupport;
import internal.favicon.GoogleSupplier;
import sdmxdl.Dataflow;
import sdmxdl.DataflowRef;
import sdmxdl.Dimension;
import sdmxdl.LanguagePriorityList;
import sdmxdl.web.SdmxWebManager;
import sdmxdl.web.SdmxWebSource;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ec.util.completion.AutoCompletionSource;
import static ec.util.completion.AutoCompletionSource.Behavior.ASYNC;
import static ec.util.completion.AutoCompletionSource.Behavior.NONE;
import static ec.util.completion.AutoCompletionSource.Behavior.SYNC;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.completion.swing.CustomListCellRenderer;
import internal.favicon.FaviconkitSupplier;
import internal.util.DialectLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import nbbrd.io.WrappedIOException;
import org.openide.util.ImageUtilities;
import sdmxdl.Connection;
import sdmxdl.ext.spi.Dialect;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class SdmxAutoCompletion {

    public AutoCompletionSource onDialects() {
        return ExtAutoCompletionSource
                .builder(o -> new DialectLoader().get())
                .behavior(AutoCompletionSource.Behavior.SYNC)
                .postProcessor(SdmxAutoCompletion::filterAndSortDialects)
                .valueToString(Dialect::getName)
                .build();
    }

    private List<Dialect> filterAndSortDialects(List<Dialect> allValues, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return allValues.stream()
                .filter(o -> filter.test(o.getDescription()) || filter.test(o.getName()))
                .sorted(Comparator.comparing(Dialect::getDescription))
                .collect(Collectors.toList());
    }

    public ListCellRenderer getDialectRenderer() {
        return CustomListCellRenderer.of(Dialect::getName, Dialect::getDescription);
    }

    public AutoCompletionSource onSources(SdmxWebManager manager) {
        return ExtAutoCompletionSource
                .builder(term -> getAllSources(manager))
                .behavior(AutoCompletionSource.Behavior.SYNC)
                .postProcessor((values, term) -> filterAndSortSources(values, term, manager.getLanguages()))
                .valueToString(SdmxWebSource::getName)
                .build();
    }

    private List<SdmxWebSource> getAllSources(SdmxWebManager manager) {
        return manager
                .getSources()
                .values()
                .stream()
                .filter(source -> !source.isAlias())
                .collect(Collectors.toList());
    }

    public static ImageIcon getDefaultIcon() {
        return ImageUtilities.loadImageIcon("demetra/desktop/extra/sdmx/sdmx-logo.png", false);
    }

    public static final FaviconSupport FAVICONS = FaviconSupport
            .builder()
            .supplier(new GoogleSupplier())
            .supplier(new FaviconkitSupplier())
            .executor(Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setPriority(Thread.MIN_PRIORITY).build()))
            .fallback(getDefaultIcon())
            .cache(new HashMap<>())
//            .cache(IOCacheFactoryLoader.get().ofTtl(Duration.ofHours(1)))
            .build();

    public ListCellRenderer getSourceRenderer(SdmxWebManager manager) {
        return new CustomListCellRenderer<SdmxWebSource>() {
            @Override
            protected String getValueAsString(SdmxWebSource value) {
                return getNameAndDescription(value, manager.getLanguages());
            }

            @Override
            protected Icon toIcon(String term, JList list, SdmxWebSource value, int index, boolean isSelected, boolean cellHasFocus) {
                return FAVICONS.get(value.getWebsite(), list::repaint);
            }
        };
    }

    private String getNameAndDescription(SdmxWebSource o, LanguagePriorityList langs) {
        return o.getName() + ": " + langs.select(o.getDescriptions());
    }

    public AutoCompletionSource onFlows(SdmxWebManager manager, Supplier<String> source, ConcurrentMap cache) {
        return ExtAutoCompletionSource
                .builder(o -> loadFlows(manager, source))
                .behavior(o -> canLoadFlows(source) ? ASYNC : NONE)
                .postProcessor(SdmxAutoCompletion::filterAndSortFlows)
                .valueToString(o -> o.getRef().toString())
                .cache(cache, o -> getFlowCacheKey(source, manager.getLanguages()), SYNC)
                .build();
    }

    public ListCellRenderer getFlowsRenderer() {
        return CustomListCellRenderer.of(Dataflow::getLabel, o -> o.getRef().toString());
    }

    public AutoCompletionSource onDimensions(SdmxWebManager manager, Supplier<String> source, Supplier<String> flow, ConcurrentMap cache) {
        return ExtAutoCompletionSource
                .builder(o -> loadDimensions(manager, source, flow))
                .behavior(o -> canLoadDimensions(source, flow) ? ASYNC : NONE)
                .postProcessor(SdmxAutoCompletion::filterAndSortDimensions)
                .valueToString(Dimension::getId)
                .cache(cache, o -> getDimensionCacheKey(source, flow, manager.getLanguages()), SYNC)
                .build();
    }

    public ListCellRenderer getDimensionsRenderer() {
        return CustomListCellRenderer.of(Dimension::getId, Dimension::getLabel);
    }

    public String getDefaultDimensionsAsString(SdmxWebManager manager, Supplier<String> source, Supplier<String> flow, ConcurrentMap cache, CharSequence delimiter) throws Exception {
        String key = getDimensionCacheKey(source, flow, manager.getLanguages());
        List<Dimension> result = (List<Dimension>) cache.get(key);
        if (result == null) {
            result = loadDimensions(manager, source, flow);
            cache.put(key, result);
        }
        return result.stream()
                .sorted(Comparator.comparingInt(Dimension::getPosition))
                .map(Dimension::getId)
                .collect(Collectors.joining(delimiter));
    }

    private List<SdmxWebSource> filterAndSortSources(List<SdmxWebSource> allValues, String term, LanguagePriorityList langs) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return allValues
                .stream()
                .filter(source -> filterSource(source, filter, langs))
                .collect(Collectors.toList());
    }

    private static boolean filterSource(SdmxWebSource source, Predicate<String> filter, LanguagePriorityList langs) {
        return filter.test(langs.select(source.getDescriptions()))
                || filter.test(source.getName())
                || source.getAliases().stream().anyMatch(filter);
    }

    private boolean canLoadFlows(Supplier<String> source) {
        return !Strings.isNullOrEmpty(source.get());
    }

    private List<Dataflow> loadFlows(SdmxWebManager manager, Supplier<String> source) throws IOException {
        try (Connection c = manager.getConnection(source.get())) {
            return new ArrayList<>(c.getFlows());
        } catch (RuntimeException ex) {
            throw WrappedIOException.wrap(ex);
        }
    }

    private List<Dataflow> filterAndSortFlows(List<Dataflow> values, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return values.stream()
                .filter(o -> filter.test(o.getLabel()) || filter.test(o.getRef().getId()))
                .sorted(Comparator.comparing(Dataflow::getLabel))
                .collect(Collectors.toList());
    }

    private String getFlowCacheKey(Supplier<String> source, LanguagePriorityList languages) {
        return source.get() + languages.toString();
    }

    private boolean canLoadDimensions(Supplier<String> source, Supplier<String> flow) {
        return canLoadFlows(source) && !Strings.isNullOrEmpty(flow.get());
    }

    private List<Dimension> loadDimensions(SdmxWebManager manager, Supplier<String> source, Supplier<String> flow) throws IOException {
        try (Connection c = manager.getConnection(source.get())) {
            return new ArrayList<>(c.getStructure(DataflowRef.parse(flow.get())).getDimensions());
        } catch (RuntimeException ex) {
            throw WrappedIOException.wrap(ex);
        }
    }

    private List<Dimension> filterAndSortDimensions(List<Dimension> values, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return values.stream()
                .filter(o -> filter.test(o.getId()) || filter.test(o.getLabel()) || filter.test(String.valueOf(o.getPosition())))
                .sorted(Comparator.comparing(Dimension::getId))
                .collect(Collectors.toList());
    }

    private String getDimensionCacheKey(Supplier<String> source, Supplier<String> flow, LanguagePriorityList languages) {
        return source.get() + "/" + flow.get() + languages.toString();
    }
}
