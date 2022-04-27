package demetra.desktop.extra.sdmx.web;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.tsproviders.TsProviderProperties;
import demetra.desktop.util.Caches;
import demetra.tsp.extra.sdmx.web.SdmxWebBean;
import demetra.tsp.extra.sdmx.web.SdmxWebProvider;
import internal.extra.sdmx.SdmxAutoCompletion;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import sdmxdl.DataflowRef;
import sdmxdl.Dimension;
import sdmxdl.web.SdmxWebSource;

@lombok.experimental.UtilityClass
class SdmxWebBeanSupport {

    @NbBundle.Messages({
        "bean.cache.description=Mechanism used to improve performance."})
    public static Sheet newSheet(SdmxWebBean bean, SdmxWebProvider provider) {
        ConcurrentMap autoCompletionCache = Caches.ttlCacheAsMap(Duration.ofMinutes(1));

        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        result.put(withSource(b.reset("Source"), bean, provider, autoCompletionCache).build());
        result.put(withOptions(b.reset("Options"), bean, provider, autoCompletionCache).build());
        result.put(withCache(b.reset("Cache").description(Bundle.bean_cache_description()), bean).build());
        return result;
    }

    @NbBundle.Messages({
        "bean.source.display=Provider",
        "bean.source.description=The identifier of the service that provides data.",
        "bean.flow.display=Dataflow",
        "bean.flow.description=The identifier of a specific dataflow.",})
    private static NodePropertySetBuilder withSource(NodePropertySetBuilder b, SdmxWebBean bean, SdmxWebProvider provider, ConcurrentMap autoCompletionCache) {
        b.withAutoCompletion()
                .select("source", bean::getSource, bean::setSource)
                .servicePath(SdmxWebSource.class.getName())
                .display(Bundle.bean_source_display())
                .description(Bundle.bean_source_description())
                .add();

        Supplier<SdmxWebSource> toSource = () -> getWebSourceOrNull(bean, provider);

        SdmxAutoCompletion dataflow = SdmxAutoCompletion.onDataflow(provider.getSdmxManager(), toSource, autoCompletionCache);

        b.withAutoCompletion()
                .select("flow", bean::getFlow, bean::setFlow)
                .source(dataflow.getSource())
                .cellRenderer(dataflow.getRenderer())
                .display(Bundle.bean_flow_display())
                .description(Bundle.bean_flow_description())
                .add();

        return b;
    }

    @NbBundle.Messages({
        "bean.dimensions.display=Dataflow dimensions",
        "bean.dimensions.description=An optional comma-separated list of dimensions that defines the order used to hierarchise time series.",
        "bean.labelAttribute.display=Series label attribute",
        "bean.labelAttribute.description=An optional attribute that carries the label of time series."
    })
    private static NodePropertySetBuilder withOptions(NodePropertySetBuilder b, SdmxWebBean bean, SdmxWebProvider provider, ConcurrentMap autoCompletionCache) {
        Supplier<SdmxWebSource> toSource = () -> getWebSourceOrNull(bean, provider);
        Supplier<DataflowRef> toFlow = () -> getDataflowRefOrNull(bean);

        SdmxAutoCompletion dimension = SdmxAutoCompletion.onDimension(provider.getSdmxManager(), toSource, toFlow, autoCompletionCache);

        b.withAutoCompletion()
                .select(bean, "dimensions", List.class,
                        Joiner.on(',')::join, Splitter.on(',').trimResults().omitEmptyStrings()::splitToList)
                .source(dimension.getSource())
                .cellRenderer(dimension.getRenderer())
                .separator(",")
                .defaultValueSupplier(() -> dimension.getSource().getValues("").stream().map(Dimension.class::cast).sorted(Comparator.comparingInt(Dimension::getPosition)).map(Dimension::getId).collect(Collectors.joining(",")))
                .display(Bundle.bean_dimensions_display())
                .description(Bundle.bean_dimensions_description())
                .add();

        SdmxAutoCompletion attribute = SdmxAutoCompletion.onAttribute(provider.getSdmxManager(), toSource, toFlow, autoCompletionCache);

        b.withAutoCompletion()
                .select("labelAttribute", bean::getLabelAttribute, bean::setLabelAttribute)
                .source(attribute.getSource())
                .cellRenderer(attribute.getRenderer())
                .display(Bundle.bean_labelAttribute_display())
                .description(Bundle.bean_labelAttribute_description())
                .add();

        return b;
    }

    private static NodePropertySetBuilder withCache(NodePropertySetBuilder b, SdmxWebBean bean) {
        TsProviderProperties.addBulkCube(b, bean::getCache, bean::setCache);
        return b;
    }

    private static SdmxWebSource getWebSourceOrNull(SdmxWebBean bean, SdmxWebProvider provider) {
        return provider.getSdmxManager().getSources().get(bean.getSource());
    }

    private static DataflowRef getDataflowRefOrNull(SdmxWebBean bean) {
        try {
            return DataflowRef.parse(bean.getFlow());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
