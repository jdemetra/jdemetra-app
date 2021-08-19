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
package demetra.ui;

import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import ec.util.various.swing.OnEDT;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import nbbrd.design.MightBePromoted;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.util.Exceptions;
import demetra.ui.util.CollectionSupplier;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
public final class TsActions {

    private static final TsActions INSTANCE = new TsActions();

    @NonNull
    public static TsActions getDefault() {
        return INSTANCE;
    }

    public static final String NO_ACTION = "";

    private final CollectionSupplier<TsActionsOpenSpi> openActions = TsActionsOpenSpiLoader::get;
    private final CollectionSupplier<TsActionsSaveSpi> saveActions = TsActionsSaveSpiLoader::get;

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

        Optional<? extends TsActionsOpenSpi> action = getByName(openActions, actionName);
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

        Optional<? extends TsActionsSaveSpi> action = getByName(saveActions, actionName);
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
                .filter(TsActions.byName(name))
                .findFirst();
    }

    @MightBePromoted
    private static Predicate<NamedService> byName(String name) {
        return service -> service.getName().equals(name);
    }
}
