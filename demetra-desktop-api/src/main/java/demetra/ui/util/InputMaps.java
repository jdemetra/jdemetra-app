/*
 * Copyright 2013 National Bank of Belgium
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
package demetra.ui.util;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

/**
 *
 * @author Philippe Charles
 */
public final class InputMaps {

    private InputMaps() {
        // static class
    }

    @NonNull
    public static InputMap getRoot(@NonNull InputMap inputMap) {
        InputMap result = inputMap;
        while (result.getParent() != null) {
            result = result.getParent();
        }
        return result;
    }

    @NonNull
    public static Map<KeyStroke, Object> asMap(@NonNull InputMap inputMap, boolean includeParentKeys) {
        return asKeySet(inputMap, includeParentKeys).stream().collect(Collectors.toMap(o -> o, inputMap::get));
    }

    public static void copyEntries(@NonNull InputMap source, boolean includeParentKeys, @NonNull InputMap destination) {
        asMap(source, includeParentKeys).forEach(destination::put);
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    @NonNull
    private static Set<KeyStroke> asKeySet(final @NonNull InputMap inputMap, final boolean includeParentKeys) {
        return new AbstractSet<KeyStroke>() {
            @Override
            public Iterator<KeyStroke> iterator() {
                KeyStroke[] keys = includeParentKeys ? inputMap.allKeys() : inputMap.keys();
                return keys != null ? Arrays.asList(keys).iterator() : Collections.emptyIterator();
            }

            @Override
            public int size() {
                int result = inputMap.size();
                if (includeParentKeys) {
                    InputMap cursor = inputMap;
                    while ((cursor = cursor.getParent()) != null) {
                        result += cursor.size();
                    }
                }
                return result;
            }
        };
    }
    //</editor-fold>
}
