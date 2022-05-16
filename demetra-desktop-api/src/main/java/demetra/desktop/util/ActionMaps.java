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
package demetra.desktop.util;

import ec.util.chart.swing.Charts;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Philippe Charles
 */
public final class ActionMaps {

    private ActionMaps() {
        // static class
    }

    @NonNull
    public static ActionMap getRoot(@NonNull ActionMap actionMap) {
        ActionMap result = actionMap;
        while (result.getParent() != null) {
            result = result.getParent();
        }
        return result;
    }

    @NonNull
    public static Map<Object, Action> asMap(@NonNull ActionMap actionMap, boolean includeParentKeys) {
        return asKeySet(actionMap, includeParentKeys).stream().collect(Collectors.toMap(o -> o, actionMap::get));
    }

    public static void copyEntries(@NonNull ActionMap source, boolean includeParentKeys, @NonNull ActionMap destination) {
        asMap(source, includeParentKeys).forEach(destination::put);
    }

    public static void performAction(@NonNull ActionMap actionMap, @NonNull String actionName, @NonNull MouseEvent e) {
        Action action = actionMap.get(actionName);
        if (action != null && action.isEnabled()) {
            action.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, actionName));
        }
    }

    public static void onDoubleClick(@NonNull ActionMap actionMap, @NonNull String actionName, @NonNull JComponent component) {
        component.addMouseListener(new OnDoubleClick(actionMap, actionName));
    }

    @lombok.AllArgsConstructor
    private static final class OnDoubleClick extends MouseAdapter {

        @lombok.NonNull
        private final ActionMap actionMap;

        @lombok.NonNull
        private final String action;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!Charts.isPopup(e) && Charts.isDoubleClick(e)) {
                ActionMaps.performAction(actionMap, action, e);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    @NonNull
    private static Set<Object> asKeySet(final @NonNull ActionMap actionMap, final boolean includeParentKeys) {
        return new AbstractSet<Object>() {
            @Override
            public Iterator<Object> iterator() {
                Object[] keys = includeParentKeys ? actionMap.allKeys() : actionMap.keys();
                return keys != null ? Arrays.asList(keys).iterator() : Collections.emptyIterator();
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
    //</editor-fold>
}
