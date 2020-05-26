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
import ec.nbdemetra.core.GlobalService;
import ec.util.various.swing.OnEDT;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.util.Lookup;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
public interface TsAction {

    @NonNull
    static TsAction getDefault() {
        return Lookup.getDefault().lookup(TsAction.class);
    }

    static final String NO_ACTION = "";

    @NonNull
    List<? extends NamedService> getTsActions();

    @OnEDT
    default void open(@NonNull Ts ts) {
        openWith(ts, null);
    }

    @OnEDT
    void openWith(@NonNull Ts ts, @Nullable String actionName);
}
