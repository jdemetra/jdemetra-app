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
}
