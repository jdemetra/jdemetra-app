/*
 * Copyright 2015 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved
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
package demetra.desktop.components.parts;

import demetra.desktop.TsManager;
import demetra.desktop.design.SwingProperty;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Philippe Charles
 */
public interface HasTs {

    @SwingProperty
    String TS_PROPERTY = "ts";

    @Nullable
    Ts getTs();

    void setTs(@Nullable Ts ts);

    default void replaceTs(@NonNull Ts ts) {
        Ts current = getTs();
        if (current != null && current.getMoniker().equals(ts.getMoniker())) {
            setTs(ts);
        }
    }

    default void loadAsync(TsInformationType info) {
        Ts ts = getTs();
        if (ts != null && !ts.getType().encompass(info)) {
            TsManager.getDefault().loadAsync(ts, info, this::replaceTs);
        }
    }
    
}
