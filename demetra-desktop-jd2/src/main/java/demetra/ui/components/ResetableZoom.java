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
package demetra.ui.components;

import java.util.Optional;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.ActionMap;

/**
 *
 * @author Philippe Charles
 */
public interface ResetableZoom {

    static final String RESET_ZOOM_ACTION = "resetZoom";

    void resetZoom();

    @NonNull
    static ResetableZoom of(@NonNull Supplier<? extends ActionMap> actionMap) {
        return () -> Optional.ofNullable(actionMap.get().get(RESET_ZOOM_ACTION)).ifPresent(o -> o.actionPerformed(null));
    }
}
