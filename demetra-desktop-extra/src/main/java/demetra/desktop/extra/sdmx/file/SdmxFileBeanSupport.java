package demetra.desktop.extra.sdmx.file;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.ui.properties.FileLoaderFileFilter;
import demetra.desktop.util.Caches;
import demetra.tsp.extra.sdmx.file.SdmxFileBean;
import demetra.tsp.extra.sdmx.file.SdmxFileProvider;
import internal.extra.sdmx.SdmxAutoCompletion;
import static internal.tsp.extra.sdmx.SdmxCubeItems.resolveFileSet;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import sdmxdl.DataflowRef;
import sdmxdl.Dimension;
import sdmxdl.ext.Registry;
import sdmxdl.file.SdmxFileSource;

@lombok.experimental.UtilityClass
class SdmxFileBeanSupport {

    @NbBundle.Messages({
        "bean.cache.description=Mechanism used to improve performance."})
    public static Sheet newSheet(SdmxFileBean bean, SdmxFileProvider provider) {
        ConcurrentMap autoCompletionCache = Caches.ttlCacheAsMap(Duration.ofMinutes(1));

        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        result.put(withSource(b.reset("Source"), bean, provider).build());
        result.put(withOptions(b.reset("Options"), bean, provider, autoCompletionCache).build());
        return result;
    }

    @NbBundle.Messages({
        "bean.file.display=Data file",
        "bean.file.description=The path to the sdmx data file.",})
    private static NodePropertySetBuilder withSource(NodePropertySetBuilder b, SdmxFileBean bean, SdmxFileProvider provider) {
        b.withFile()
                .select("file", bean::getFile, bean::setFile)
                .display(Bundle.bean_file_display())
                .description(Bundle.bean_file_description())
                .filterForSwing(new FileLoaderFileFilter(provider))
                .paths(provider.getPaths())
                .directories(false)
                .add();
        return b;
    }

    @NbBundle.Messages({
        "bean.structureFile.display=Structure file",
        "bean.structureFile.description=The path to the sdmx structure file.",
        "bean.dialect.display=Dialect",
        "bean.dialect.description=The name of the dialect used to parse the sdmx data file.",
        "bean.dimensions.display=Dataflow dimensions",
        "bean.dimensions.description=An optional comma-separated list of dimensions that defines the order used to hierarchise time series.",
        "bean.labelAttribute.display=Series label attribute",
        "bean.labelAttribute.description=An optional attribute that carries the label of time series."
    })
    private static NodePropertySetBuilder withOptions(NodePropertySetBuilder b, SdmxFileBean bean, SdmxFileProvider provider, ConcurrentMap autoCompletionCache) {
        b.withFile()
                .select("structureFile", bean::getStructureFile, bean::setStructureFile)
                .display(Bundle.bean_structureFile_display())
                .description(Bundle.bean_structureFile_description())
                .filterForSwing(new FileLoaderFileFilter(provider))
                .paths(provider.getPaths())
                .directories(false)
                .add();

        SdmxAutoCompletion dialect = SdmxAutoCompletion.onDialect(Registry.ofServiceLoader());

        b.withAutoCompletion()
                .select("dialect", bean::getDialect, bean::setDialect)
                .source(dialect.getSource())
                .cellRenderer(dialect.getRenderer())
                .display(Bundle.bean_dialect_display())
                .description(Bundle.bean_dialect_description())
                .add();

        Supplier<SdmxFileSource> toSource = () -> getFileSource(bean, provider).orElse(null);
        Supplier<DataflowRef> toFlow = () -> getFileSource(bean, provider).map(SdmxFileSource::asDataflowRef).orElse(null);

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

    private static Optional<SdmxFileSource> getFileSource(SdmxFileBean bean, SdmxFileProvider provider) {
        try {
            return Optional.of(resolveFileSet(provider, bean));
        } catch (FileNotFoundException ex) {
            return Optional.empty();
        }
    }
}
