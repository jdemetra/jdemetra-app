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
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
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

    public static void copyEntries(@Nonnull ActionMap source, boolean includeParentKeys, @Nonnull ActionMap destination) {
        for (Map.Entry<Object, Action> o : asMap(source, includeParentKeys).entrySet()) {
            destination.put(o.getKey(), o.getValue());
        }
    }

    public static void performAction(@Nonnull ActionMap actionMap, @Nonnull String actionName, @Nonnull MouseEvent e) {
        Action action = actionMap.get(actionName);
        if (action != null && action.isEnabled()) {
            action.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, actionName));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
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
    //</editor-fold>
}
