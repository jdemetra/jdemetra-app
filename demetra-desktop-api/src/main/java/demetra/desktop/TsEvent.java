package demetra.desktop;

import demetra.timeseries.TsMoniker;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.EventObject;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 */
public final class TsEvent extends EventObject {

//    @NonNull
//    @lombok.Getter
//    private final TsMoniker moniker;
//
    @NonNull
    @lombok.Getter
    private final Predicate<TsMoniker> related;
    
//    public TsEvent(@NonNull TsManager source, @NonNull TsMoniker moniker, @NonNull Predicate<TsMoniker> related) {
//        super(source);
//        this.moniker = Objects.requireNonNull(moniker);
//        this.related = Objects.requireNonNull(related);
//    }
//
    public TsEvent(@NonNull TsManager source, @NonNull Predicate<TsMoniker> related) {
        super(source);
         this.related = Objects.requireNonNull(related);
    }

    @Override
    public TsManager getSource() {
        return (TsManager) super.getSource();
    }
}
