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

import demetra.util.List2;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;

/**
 *
 * @author Philippe Charles
 */
public final class KeyStrokes {

    private KeyStrokes() {
        // static class
    }

    public static final List<KeyStroke> COPY;
    public static final List<KeyStroke> PASTE;
    public static final List<KeyStroke> SELECT_ALL;
    public static final List<KeyStroke> DELETE;
    public static final List<KeyStroke> OPEN;
    public static final List<KeyStroke> CLEAR;

    public static void putAll(InputMap im, Collection<? extends KeyStroke> keyStrokes, Object actionMapKey) {
        keyStrokes.stream().forEach(o -> im.put(o, actionMapKey));
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private enum ActionType {

        COPY, PASTE, SELECT_ALL, DELETE, OPEN, CLEAR
    }

    static {
        Map<ActionType, List<KeyStroke>> keyStrokes = new TextFieldKeyStrokeSupplier().get();
        COPY = keyStrokes.get(ActionType.COPY);
        PASTE = keyStrokes.get(ActionType.PASTE);
        SELECT_ALL = keyStrokes.get(ActionType.SELECT_ALL);
        DELETE = keyStrokes.get(ActionType.DELETE);
        OPEN = keyStrokes.get(ActionType.OPEN);
        CLEAR = keyStrokes.get(ActionType.CLEAR);
    }

    private static abstract class AbstractSupplier implements Supplier<Map<ActionType, List<KeyStroke>>> {

        abstract protected InputMap getInputMap();

        abstract protected Object getActionMapKey(ActionType type);

        protected KeyStroke getFallback(ActionType key) {
            int platformKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            switch (key) {
                case COPY:
                    return KeyStroke.getKeyStroke(KeyEvent.VK_C, platformKeyMask);
                case DELETE:
                    return KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, platformKeyMask);
                case PASTE:
                    return KeyStroke.getKeyStroke(KeyEvent.VK_V, platformKeyMask);
                case SELECT_ALL:
                    return KeyStroke.getKeyStroke(KeyEvent.VK_A, platformKeyMask);
                case OPEN:
                    return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
                case CLEAR:
                    return KeyStroke.getKeyStroke(KeyEvent.VK_L, platformKeyMask);
            }
            return null;
        }

        @Override
        public Map<ActionType, List<KeyStroke>> get() {
            Map<KeyStroke, Object> keyStrokes = InputMaps.asMap(getInputMap(), true);
            Map<ActionType, List<KeyStroke>> result = new HashMap<>();
            for (ActionType o : ActionType.values()) {
                Object actionMapKey = getActionMapKey(o);
                List<KeyStroke> tmp = keyStrokes
                        .entrySet()
                        .stream()
                        .filter(x -> Objects.equals(x.getValue(), actionMapKey))
                        .map(Map.Entry::getKey)
                        .distinct()
                        .sorted(orderingUsingKeyTextLength())
                        .collect(List2.toUnmodifiableList());
                result.put(o, !tmp.isEmpty() ? tmp : Collections.singletonList(getFallback(o)));
            }
            return result;
        }
    }

    private static class ListKeyStrokeSupplier extends AbstractSupplier {

        @Override
        protected InputMap getInputMap() {
            return new JList().getInputMap();
        }

        @Override
        protected Object getActionMapKey(ActionType type) {
            switch (type) {
                case COPY:
                    return "copy";
                case PASTE:
                    return "paste";
                case SELECT_ALL:
                    return "selectAll";
            }
            return null;
        }
    }

    private static class TextFieldKeyStrokeSupplier extends AbstractSupplier {

        @Override
        protected InputMap getInputMap() {
            return new JTextField().getInputMap();
        }

        @Override
        protected Object getActionMapKey(ActionType type) {
            switch (type) {
                case COPY:
                    return DefaultEditorKit.copyAction;
                case DELETE:
                    return DefaultEditorKit.deleteNextCharAction;
                case PASTE:
                    return DefaultEditorKit.pasteAction;
                case SELECT_ALL:
                    return DefaultEditorKit.selectAllAction;
            }
            return null;
        }
    }

    private static Comparator<KeyStroke> orderingUsingKeyTextLength() {
        return Comparator.comparingInt(l -> KeyEvent.getKeyText(l.getKeyCode()).length());
    }
    //</editor-fold>
}
