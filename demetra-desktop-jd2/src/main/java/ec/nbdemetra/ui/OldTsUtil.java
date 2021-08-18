package ec.nbdemetra.ui;

import demetra.bridge.TsConverter;
import demetra.timeseries.Ts;
import ec.tss.tsproviders.utils.OptionalTsData;
import ec.tstoolkit.timeseries.simplets.TsData;
import org.checkerframework.checker.nullness.qual.NonNull;

public class OldTsUtil {

    public static demetra.timeseries.@NonNull Ts toTs(@NonNull String name, @NonNull TsData data) {
        return Ts.builder().name(name).data(TsConverter.toTsData(OptionalTsData.present(data))).build();
    }

    public static demetra.timeseries.@NonNull Ts toTs(@NonNull TsData data) {
        return Ts.builder().data(TsConverter.toTsData(OptionalTsData.present(data))).build();
    }
}
