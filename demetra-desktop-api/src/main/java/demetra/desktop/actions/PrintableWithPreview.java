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
package demetra.desktop.actions;

import demetra.desktop.design.SwingAction;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.util.Optional;
import java.util.function.Supplier;

/**
 *
 * @author Philippe Charles
 */
public interface PrintableWithPreview {

    @SwingAction
    String PRINT_ACTION = "print";

    void printWithPreview();

    @NonNull
    static PrintableWithPreview of(@NonNull Supplier<? extends ActionMap> actionMap) {
        return () -> Optional.ofNullable(actionMap.get().get(PRINT_ACTION)).ifPresent(o -> o.actionPerformed(null));
    }
}
