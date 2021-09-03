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
package demetra.desktop.interchange;

import demetra.desktop.Config;
import ec.util.various.swing.OnEDT;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Defines the ability of a component to import a previous state/configuration.
 *
 * @author charphi
 */
public interface Importable {

    /**
     * Returns the domain of configs that can be imported.
     *
     * @return a non-null domain
     */
    @OnEDT
    @NonNull
    String getDomain();

    /**
     * Imports a previous state of a component from a config object.
     *
     * @param config a non-null config
     * @throws IllegalArgumentException if the config cannot be parsed
     */
    @OnEDT
    void importConfig(@NonNull Config config) throws IllegalArgumentException;
}
