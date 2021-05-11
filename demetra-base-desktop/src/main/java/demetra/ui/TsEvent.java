package demetra.ui;

import demetra.timeseries.TsMoniker;
import java.util.EventObject;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 */
public final class TsEvent extends EventObject {

    @NonNull
    @lombok.Getter
    private final TsMoniker moniker;
    
    public TsEvent(@NonNull NextTsManager source, @NonNull TsMoniker moniker) {
        super(source);
        this.moniker = Objects.requireNonNull(moniker);
    }

    @Override
    public NextTsManager getSource() {
        return (NextTsManager) super.getSource();
    }
}
