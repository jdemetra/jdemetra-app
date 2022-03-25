package internal.desktop.spreadsheet.datatransfer;

import demetra.desktop.Config;
import demetra.desktop.Converter;
import demetra.timeseries.TsUnit;
import demetra.timeseries.util.ObsGathering;
import demetra.tsprovider.grid.GridReader;
import demetra.tsprovider.grid.GridReaderHandler;
import demetra.tsprovider.util.ObsFormat;
import demetra.tsprovider.util.ObsFormatHandler;
import demetra.tsprovider.util.ObsGatheringHandler;
import demetra.tsprovider.util.PropertyHandler;
import static demetra.tsprovider.util.PropertyHandler.*;

@lombok.RequiredArgsConstructor
final class SpreadSheetDataTransferConverter implements Converter<SpreadSheetDataTransferBean, Config> {

    private final SpreadSheetDataTransferBeanHandler handler
            = SpreadSheetDataTransferBeanHandler
                    .builder()
                    .importTs(onBoolean("importEnabled", true))
                    .tsReader(onGridReader("tsReader", GridReader.DEFAULT))
                    .exportTs(onBoolean("exportEnabled", true))
                    .importMatrix(onBoolean("importMatrix", true))
                    .exportMatrix(onBoolean("exportMatrix", true))
                    .importTable(onBoolean("importTable", true))
                    .exportTable(onBoolean("exportTable", true))
                    .build();

    private static PropertyHandler<GridReader> onGridReader(String name, GridReader defaultValue) {
        return GridReaderHandler
                .builder()
                .format(onObsFormat("format", defaultValue.getFormat()))
                .gathering(onObsGathering("gathering", defaultValue.getGathering()))
                .layout(onEnum("layout", defaultValue.getLayout()))
                .namePattern(onString("namePattern", defaultValue.getNamePattern()))
                .nameSeparator(onString("nameSeparator", defaultValue.getNameSeparator()))
                .build()
                .withPrefix(name + ".");
    }

    private static PropertyHandler<ObsFormat> onObsFormat(String name, ObsFormat defaultValue) {
        return ObsFormatHandler
                .builder()
                .locale(onLocale("locale", defaultValue.getLocale()))
                .dateTimePattern(onString("dateTimePattern", defaultValue.getDateTimePattern()))
                .numberPattern(onString("numberPattern", defaultValue.getNumberPattern()))
                .ignoreNumberGrouping(onBoolean("ignoreNumberGrouping", defaultValue.isIgnoreNumberGrouping()))
                .build()
                .withPrefix(name + ".");
    }

    private static PropertyHandler<ObsGathering> onObsGathering(String name, ObsGathering defaultValue) {
        return ObsGatheringHandler
                .builder()
                .unit(of("unit", defaultValue.getUnit(), TsUnit::parse, TsUnit::toString))
                .aggregationType(onEnum("aggregationType", defaultValue.getAggregationType()))
                .allowPartialAggregation(onBoolean("allowPartialAggregation", defaultValue.isAllowPartialAggregation()))
                .includeMissingValues(onBoolean("includeMissingValues", defaultValue.isIncludeMissingValues()))
                .build()
                .withPrefix(name + ".");
    }

    @lombok.NonNull
    private final String domain;

    @lombok.NonNull
    private final String name;

    @lombok.NonNull
    private final String version;

    @Override
    public Config doForward(SpreadSheetDataTransferBean bean) {
        Config.Builder b = Config.builder(domain, name, version);
        handler.set(b::parameter, bean);
        return b.build();
    }

    @Override
    public SpreadSheetDataTransferBean doBackward(Config config) {
        return handler.get(config::getParameter);
    }
}
