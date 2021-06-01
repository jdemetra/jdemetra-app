package demetra.ui;

import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import demetra.timeseries.TsMoniker;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.util.WeakListeners;

/**
 *
 */
public interface NextTsManager {

    @OnAnyThread
    @NonNull
    Ts getTs(@NonNull TsMoniker moniker, @NonNull TsInformationType type);

    @OnAnyThread
    @NonNull
    TsCollection getTsCollection(@NonNull TsMoniker moniker, @NonNull TsInformationType type);

    @OnEDT
    default void addWeakListener(@NonNull TsListener listener) {
        addListener(WeakListeners.create(TsListener.class, listener, this));
    }

    @OnEDT
    void addListener(@NonNull TsListener listener);

    @OnEDT
    void removeListener(@NonNull TsListener listener);

    @OnAnyThread
    @NonNull
    default Ts loadTs(@NonNull Ts ts, @NonNull TsInformationType type) {
        if (ts.getType().encompass(type)) {
            return ts;
        }
        if (!ts.getMoniker().isProvided()) {
            return ts;
        } 
        return getTs(ts.getMoniker(), type);
    }

    @OnAnyThread
    @NonNull
    default TsCollection loadTsCollection(@NonNull TsCollection col, @NonNull TsInformationType type) {
        if (col.getType().encompass(type)) {
            return col;
        }
        if (!col.getMoniker().isProvided()) {
            return col.stream().map(ts -> loadTs(ts, type)).collect(TsCollection.toTsCollection());
        } 
        return getTsCollection(col.getMoniker(), type);
    }
}
