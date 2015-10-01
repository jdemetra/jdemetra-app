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
package ec.nbdemetra.ui.awt;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
        return Maps.asMap(asKeySet(inputMap, includeParentKeys), asKeyToValueFunction(inputMap));
    }

    public static void copyEntries(@Nonnull InputMap source, boolean includeParentKeys, @Nonnull InputMap destination) {
        for (Map.Entry<KeyStroke, Object> o : asMap(source, includeParentKeys).entrySet()) {
            destination.put(o.getKey(), o.getValue());
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    @Nonnull
    private static Set<KeyStroke> asKeySet(final @Nonnull InputMap inputMap, final boolean includeParentKeys) {
        return new AbstractSet<KeyStroke>() {
            @Override
            public Iterator<KeyStroke> iterator() {
                KeyStroke[] keys = includeParentKeys ? inputMap.allKeys() : inputMap.keys();
                return keys != null ? Iterators.forArray(keys) : Iterators.<KeyStroke>emptyIterator();
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

    @Nonnull
    private static Function<KeyStroke, Object> asKeyToValueFunction(@Nonnull final InputMap inputMap) {
        return new Function<KeyStroke, Object>() {
            @Override
            public Object apply(KeyStroke input) {
                return inputMap.get(input);
            }
        };
    }
    //</editor-fold>
}
