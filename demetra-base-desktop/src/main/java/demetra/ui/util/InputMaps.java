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
import javax.annotation.Nonnull;
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

    @Nonnull
    public static InputMap getRoot(@Nonnull InputMap inputMap) {
        InputMap result = inputMap;
        while (result.getParent() != null) {
            result = result.getParent();
        }
        return result;
    }

    @Nonnull
    public static Map<KeyStroke, Object> asMap(@Nonnull InputMap inputMap, boolean includeParentKeys) {
        return asKeySet(inputMap, includeParentKeys).stream().collect(Collectors.toMap(o -> o, o -> inputMap.get(o)));
    }

    public static void copyEntries(@Nonnull InputMap source, boolean includeParentKeys, @Nonnull InputMap destination) {
        asMap(source, includeParentKeys).forEach((k, v) -> destination.put(k, v));
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    @Nonnull
    private static Set<KeyStroke> asKeySet(final @Nonnull InputMap inputMap, final boolean includeParentKeys) {
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
