/*
 * Copyright 2017 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package internal;

import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsEvent;
import ec.tss.TsInformationType;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import javax.swing.SwingUtilities;

/**
 *
 * @author Philippe Charles
 */
public final class TsEventHelper<T> {

    // TODO: replace by None when code can deal with it
    public static final TsInformationType SHOULD_BE_NONE = TsInformationType.Definition;

    public static TsEventHelper<Ts> onTs(Runnable updaterOnEDT) {
        AtomicReference<Ts> safeTs = new AtomicReference<>();
        return new TsEventHelper<>(safeTs, o -> isUpdated(safeTs.get(), o), updaterOnEDT);
    }

    public static TsEventHelper<TsCollection> onTsCollection(Runnable updaterOnEDT) {
        AtomicReference<TsCollection> safeCol = new AtomicReference<>();
        return new TsEventHelper<>(safeCol, o -> isUpdated(safeCol.get(), o), updaterOnEDT);
    }

    private final AtomicReference<T> safeRef;
    private final Predicate<TsEvent> filter;
    private final Runnable componentUpdater;
    private final AtomicBoolean dirty;

    private TsEventHelper(AtomicReference<T> safeRef, Predicate<TsEvent> filterOnAnyThread, Runnable updaterOnEDT) {
        this.safeRef = safeRef;
        this.filter = filterOnAnyThread;
        this.componentUpdater = updaterOnEDT;
        this.dirty = new AtomicBoolean(false);
    }

    @OnAnyThread
    public void setObserved(T value) {
        safeRef.set(value);
    }

    @OnAnyThread
    public void process(Observable o, Object arg) {
        if (isUpdateRequired(o, arg)) {
            dirty.set(true);
            SwingUtilities.invokeLater(() -> {
                if (dirty.getAndSet(false)) {
                    updateComponent();
                }
            });
        }
    }

    @OnAnyThread
    private boolean isUpdateRequired(Observable o, Object arg) {
        return arg instanceof TsEvent && filter.test((TsEvent) arg);
    }

    @OnEDT
    private void updateComponent() {
        componentUpdater.run();
    }

    @OnAnyThread
    private static boolean isUpdated(Ts observed, TsEvent evt) {
        return observed != null
                && ((evt.isSeries() && evt.ts.equals(observed))
                || (evt.isCollection() && evt.tscollection.contains(observed)));
    }

    @OnAnyThread
    private static boolean isUpdated(TsCollection observed, TsEvent evt) {
        return observed != null
                && ((evt.isCollection() && observed.equals(evt.tscollection))
                || (evt.isSeries() && observed.contains(evt.ts)));
    }
}
