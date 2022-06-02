/*
 * Copyright 2018 National Bank of Belgium
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
package demetra.desktop;

import demetra.desktop.design.GlobalService;
import demetra.desktop.util.CollectionSupplier;
import demetra.desktop.util.LazyGlobalService;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import ec.util.various.swing.OnEDT;
import nbbrd.design.MightBePromoted;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.util.Exceptions;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
public final class TsActionManager {

    @NonNull
    public static TsActionManager get() {
        return LazyGlobalService.get(TsActionManager.class, TsActionManager::new);
    }

    private TsActionManager() {
    }

    public static final String NO_ACTION = "";

    private final CollectionSupplier<TsActionOpenSpi> openActions = TsActionOpenSpiLoader::get;
    private final CollectionSupplier<TsActionSaveSpi> saveActions = TsActionSaveSpiLoader::get;

    @NonNull
    public Collection<? extends NamedService> getOpenActions() {
        return openActions.get();
    }

    @NonNull
    public Collection<? extends NamedService> getSaveActions() {
        return saveActions.get();
    }

    @OnEDT
    public void openWith(@NonNull Ts data, @Nullable String actionName) {
        Objects.requireNonNull(data);

        if (NO_ACTION.equals(actionName)) {
            return;
        }

        Optional<? extends TsActionOpenSpi> action = getByName(openActions, actionName);
        if (action.isPresent()) {
            try {
                action.get().open(data);
            } catch (RuntimeException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            // TODO: report missing action ?
        }
    }

    @OnEDT
    public void saveWith(@NonNull List<TsCollection> data, @Nullable String actionName) {
        Objects.requireNonNull(data);

        if (NO_ACTION.equals(actionName)) {
            return;
        }

        Optional<? extends TsActionSaveSpi> action = getByName(saveActions, actionName);
        if (action.isPresent()) {
            try {
                action.get().save(data);
            } catch (RuntimeException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            // TODO: report missing action ?
        }
    }

    private static <X extends NamedService> Optional<X> getByName(CollectionSupplier<X> list, String name) {
        return list.stream()
                .map(o -> (X) o)
                .filter(TsActionManager.byName(name))
                .findFirst();
    }

    @MightBePromoted
    private static Predicate<NamedService> byName(String name) {
        return service -> service.getName().equals(name);
    }
}
