package demetra.ui;

import demetra.timeseries.TsMoniker;
import demetra.ui.util.NetBeansServiceBackend;
import javax.swing.Icon;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ServiceDefinition(
        quantifier = Quantifier.OPTIONAL,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface TsMonikerUISpi {

    @Nullable
    Icon getIconOrNull(@NonNull TsMoniker moniker);
}
