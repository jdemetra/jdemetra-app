package demetra.ui;

import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import demetra.timeseries.TsMoniker;
import ec.util.various.swing.OnEDT;
import java.util.EventListener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.util.WeakListeners;

/**
 *
 */
public interface NextTsManager extends AutoCloseable {

    @Override
    void close();

    @Nullable
    Ts lookupTs2(@NonNull TsMoniker moniker);

    @NonNull
    TsCollection lookupTsCollection2(@Nullable String name, @NonNull TsMoniker moniker, @NonNull TsInformationType type);

    @OnEDT
    default void addWeakUpdateListener(@NonNull UpdateListener listener) {
        addUpdateListener(WeakListeners.create(NextTsManager.UpdateListener.class, listener, this));
    }

    @OnEDT
    void addUpdateListener(@NonNull UpdateListener listener);

    @OnEDT
    void removeUpdateListener(@NonNull UpdateListener listener);

    interface UpdateListener extends EventListener {

        @OnEDT
        void accept(@NonNull NextTsManager manager, @NonNull TsMoniker moniker);
    }
}
