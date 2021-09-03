package demetra.desktop;

import demetra.desktop.util.NetBeansServiceBackend;
import demetra.timeseries.TsMoniker;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import java.awt.*;

@ServiceDefinition(
        quantifier = Quantifier.OPTIONAL,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface IconManagerSpi {

    @Nullable
    Icon getIconOrNull(@NonNull TsMoniker moniker);

    @Nullable
    Image getImageOrNull(@NonNull TsMoniker moniker, int type, boolean opened);
}
