package demetra.desktop.tsproviders;

import demetra.data.AggregationType;
import demetra.desktop.properties.DhmsPropertyEditor;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.ui.properties.FileLoaderFileFilter;
import demetra.timeseries.TsUnit;
import demetra.timeseries.calendars.RegularFrequency;
import demetra.timeseries.util.ObsGathering;
import demetra.tsprovider.FileBean;
import demetra.tsprovider.FileLoader;
import demetra.tsprovider.cube.BulkCube;
import demetra.tsprovider.cube.TableAsCube;
import demetra.tsprovider.util.ObsFormat;
import ec.util.completion.AutoCompletionSource;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.ListCellRenderer;
import nbbrd.design.MightBePromoted;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.util.NbBundle;

@lombok.experimental.UtilityClass
public class TsProviderProperties {

    @NbBundle.Messages({
        "bean.file.display=File",
        "bean.file.description=The path to the file."})
    public static void addFile(@NonNull NodePropertySetBuilder b, @NonNull FileLoader loader, @NonNull FileBean bean) {
        b.withFile()
                .select("file", bean::getFile, bean::setFile)
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .display(loader.getFileDescription())
                .description(Bundle.bean_file_description())
                .add();
    }

    @NbBundle.Messages({
        "bean.cacheDepth.display=Depth",
        "bean.cacheDepth.description=The data retrieval depth. It is always more performant to get one big chunk of data instead of several smaller parts. The downside of it is the increase of memory usage. Setting this value to zero disables the cache.",
        "bean.cacheTtl.display=Time to live",
        "bean.cacheTtl.description=The lifetime of the data stored in the cache. Setting this value to zero disables the cache."})
    public static void addBulkCube(@NonNull NodePropertySetBuilder b, @NonNull Supplier<BulkCube> loader, @NonNull Consumer<BulkCube> storer) {
        b.withInt()
                .select("depth", () -> loader.get().getDepth(), depth -> storer.accept(loader.get().toBuilder().depth(depth).build()))
                .display(Bundle.bean_cacheDepth_display())
                .description(Bundle.bean_cacheDepth_description())
                .min(0)
                .add();

        b.with(long.class)
                .select("ttl", () -> loader.get().getTtl().toMillis(), ttl -> storer.accept(loader.get().toBuilder().ttl(Duration.ofMillis(ttl)).build()))
                .editor(DhmsPropertyEditor.class)
                .display(Bundle.bean_cacheTtl_display())
                .description(Bundle.bean_cacheTtl_description())
                .add();
    }

    @NbBundle.Messages({
        "bean.obsFormat.display=Observation format",
        "bean.obsFormat.description=The format used to parse dates and numbers from character strings."})
    public static void addObsFormat(@NonNull NodePropertySetBuilder b, @NonNull Supplier<ObsFormat> loader, @NonNull Consumer<ObsFormat> storer) {
        b.with(ObsFormat.class)
                .select("obsFormat", loader, storer)
                .display(Bundle.bean_obsFormat_display())
                .description(Bundle.bean_obsFormat_description())
                .add();
    }

    @NbBundle.Messages({
        "bean.frequency.display=Frequency",
        "bean.frequency.description=.",
        "bean.aggregationType.display=Aggregation type",
        "bean.aggregationType.description=.",
        "bean.cleanMissing.display=Clean missing",
        "bean.cleanMissing.description=Erases the Missing values of the series.",
        "bean.allowPartial.display=Partial aggregation",
        "bean.allowPartial.description=Allow partial aggregation (only with average and sum aggregation)."
    })
    public static void addObsGathering(@NonNull NodePropertySetBuilder b, @NonNull Supplier<ObsGathering> loader, @NonNull Consumer<ObsGathering> storer) {
        b.withEnum(RegularFrequency.class)
                .select("frequency",
                        () -> RegularFrequency.parse(loader.get().getUnit().getAnnualFrequency()),
                        o -> storer.accept(loader.get().toBuilder().unit(o.toTsUnit()).build())
                )
                .display(Bundle.bean_frequency_display())
                .description(Bundle.bean_frequency_description())
                .add();

        b.withEnum(AggregationType.class)
                .select("aggregation",
                        () -> loader.get().getAggregationType(),
                        o -> storer.accept(loader.get().toBuilder().aggregationType(o).build())
                )
                .display(Bundle.bean_aggregationType_display())
                .description(Bundle.bean_aggregationType_description())
                .add();

        b.withBoolean()
                .select("missing",
                        () -> !loader.get().isIncludeMissingValues(),
                        o -> storer.accept(loader.get().toBuilder().includeMissingValues(!o).build())
                )
                .display(Bundle.bean_cleanMissing_display())
                .description(Bundle.bean_cleanMissing_description())
                .add();

        b.withBoolean()
                .select("partial",
                        () -> loader.get().isAllowPartialAggregation(),
                        o -> storer.accept(loader.get().toBuilder().allowPartialAggregation(o).build()))
                .display(Bundle.bean_allowPartial_display())
                .description(Bundle.bean_allowPartial_description())
                .add();
    }

    @NbBundle.Messages({
        "bean.dimensions.display=Dimension columns",
        "bean.dimensions.description=A comma-separated list of column names that defines the dimensions of the table.",
        "bean.timeDimension.display=Time dimension column",
        "bean.timeDimension.description=A column name that defines the time of an observation.",
        "bean.measure.display=Measure column",
        "bean.measure.description=A column name that defines the measure of an observation.",
        "bean.version.display=Version column",
        "bean.version.description=An optional column name that defines the version of an observation.",
        "bean.label.display=Label column",
        "bean.label.description=An optional column name that defines the series label.",})
    public static void addTableAsCubeStructure(
            @NonNull NodePropertySetBuilder b,
            @NonNull Supplier<TableAsCube> loader,
            @NonNull Consumer<TableAsCube> storer,
            AutoCompletionSource columns,
            ListCellRenderer columnRenderer
    ) {
        b.withAutoCompletion()
                .select("dimensions", newGetter(() -> loader.get().getDimensions()), newSetter(o -> storer.accept(setDimensions(loader.get(), o))))
                .source(columns)
                .separator(",")
                .defaultValueSupplier(() -> columns.getValues("").stream().map(columns::toString).collect(Collectors.joining(",")))
                .cellRenderer(columnRenderer)
                .display(Bundle.bean_dimensions_display())
                .description(Bundle.bean_dimensions_description())
                .add();

        b.withAutoCompletion()
                .select("timeDimension", () -> loader.get().getTimeDimension(), o -> storer.accept(setTimeDimension(loader.get(), o)))
                .source(columns)
                .cellRenderer(columnRenderer)
                .display(Bundle.bean_timeDimension_display())
                .description(Bundle.bean_timeDimension_description())
                .add();

        b.withAutoCompletion()
                .select("measure", () -> loader.get().getMeasure(), o -> storer.accept(setMeasure(loader.get(), o)))
                .source(columns)
                .cellRenderer(columnRenderer)
                .display(Bundle.bean_measure_display())
                .description(Bundle.bean_measure_description())
                .add();

        b.withAutoCompletion()
                .select("version", () -> loader.get().getVersion(), o -> storer.accept(setVersion(loader.get(), o)))
                .source(columns)
                .cellRenderer(columnRenderer)
                .display(Bundle.bean_version_display())
                .description(Bundle.bean_version_description())
                .add();

        b.withAutoCompletion()
                .select("label", () -> loader.get().getLabel(), o -> storer.accept(setLabel(loader.get(), o)))
                .source(columns)
                .cellRenderer(columnRenderer)
                .display(Bundle.bean_label_display())
                .description(Bundle.bean_label_description())
                .add();
    }

    public static void addTableAsCubeParsing(
            @NonNull NodePropertySetBuilder b,
            @NonNull Supplier<TableAsCube> loader,
            @NonNull Consumer<TableAsCube> storer
    ) {
        addObsFormat(b, () -> loader.get().getFormat(), o -> storer.accept(loader.get().toBuilder().format(o).build()));
        addObsGathering(b, () -> loader.get().getGathering(), o -> storer.accept(loader.get().toBuilder().gathering(o).build()));
    }

    @MightBePromoted
    private static final Formatter<List<String>> LIST_FORMATTER = Formatter.onStringList(stream -> stream.collect(Collectors.joining(",")));

    @MightBePromoted
    private static Supplier<String> newGetter(Supplier<List<String>> getter) {
        return () -> LIST_FORMATTER.formatAsString(getter.get());
    }

    @MightBePromoted
    private static final Parser<List<String>> LIST_PARSER = Parser.onStringList(text -> Stream.of(text.toString().split(",", -1)));

    private static Consumer<String> newSetter(Consumer<List<String>> setter) {
        return list -> setter.accept(LIST_PARSER.parse(list));
    }

    private static TableAsCube setDimensions(TableAsCube bean, List<String> dimensions) {
        return bean.toBuilder().clearDimensions().dimensions(dimensions).build();
    }

    private static TableAsCube setTimeDimension(TableAsCube bean, String timeDimension) {
        return (bean.toBuilder().timeDimension(timeDimension).build());
    }

    private static TableAsCube setMeasure(TableAsCube bean, String measure) {
        return (bean.toBuilder().measure(measure).build());
    }

    private static TableAsCube setVersion(TableAsCube bean, String version) {
        return (bean.toBuilder().version(version).build());
    }

    private static TableAsCube setLabel(TableAsCube bean, String label) {
        return (bean.toBuilder().label(label).build());
    }
}
