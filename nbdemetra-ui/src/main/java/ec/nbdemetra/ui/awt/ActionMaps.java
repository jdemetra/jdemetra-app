package ec.nbdemetra.ui.awt;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.swing.Action;
import javax.swing.ActionMap;

/**
 *
 * @author Philippe Charles
 */
public final class ActionMaps {

    private ActionMaps() {
        // static class
    }

    @Nonnull
    public static ActionMap getRoot(@Nonnull ActionMap actionMap) {
        ActionMap result = actionMap;
        while (result.getParent() != null) {
            result = result.getParent();
        }
        return result;
    }

    @Nonnull
    public static Map<Object, Action> asMap(@Nonnull ActionMap actionMap, boolean includeParentKeys) {
        return Maps.asMap(asKeySet(actionMap, includeParentKeys), asKeyToValueFunction(actionMap));
    }

    @Nonnull
    private static Set<Object> asKeySet(final @Nonnull ActionMap actionMap, final boolean includeParentKeys) {
        return new AbstractSet<Object>() {
            @Override
            public Iterator<Object> iterator() {
                Object[] keys = includeParentKeys ? actionMap.allKeys() : actionMap.keys();
                return keys != null ? Iterators.forArray(keys) : Iterators.<Object>emptyIterator();
            }

            @Override
            public int size() {
                int result = actionMap.size();
                if (includeParentKeys) {
                    ActionMap cursor = actionMap;
                    while ((cursor = cursor.getParent()) != null) {
                        result += cursor.size();
                    }
                }
                return result;
            }
        };
    }

    @Nonnull
    private static Function<Object, Action> asKeyToValueFunction(@Nonnull final ActionMap actionMap) {
        return new Function<Object, Action>() {
            @Override
            public Action apply(Object input) {
                return actionMap.get(input);
            }
        };
    }
}
