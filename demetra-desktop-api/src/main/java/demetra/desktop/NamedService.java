/*
 * Copyright 2013 National Bank of Belgium
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

import ec.util.various.swing.OnEDT;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.nodes.Sheet;

import java.awt.*;

/**
 * Generic UI service definition.
 *
 * @author Philippe Charles
 */
public interface NamedService {

    @OnEDT
    @NonNull
    String getName();

    @OnEDT
    @NonNull
    default String getDisplayName() {
        return getName();
    }

    @OnEDT
    @Nullable
    default Image getIcon(int type, boolean opened) {
        return null;
    }

    @OnEDT
    @NonNull
    default Sheet createSheet() {
        return new Sheet();
    }
}
