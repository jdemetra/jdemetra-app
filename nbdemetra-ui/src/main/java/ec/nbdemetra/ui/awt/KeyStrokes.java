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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Maps;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import javax.swing.InputMap;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

/**
 *
 * @author Philippe Charles
 */
public final class KeyStrokes {

    private KeyStrokes() {
        // static class
    }

    public static final ImmutableList<KeyStroke> COPY;
    public static final ImmutableList<KeyStroke> PASTE;
    public static final ImmutableList<KeyStroke> SELECT_ALL;
    public static final ImmutableList<KeyStroke> DELETE;
    public static final ImmutableList<KeyStroke> OPEN;
    public static final ImmutableList<KeyStroke> CLEAR;

    public static void putAll(InputMap im, Collection<? extends KeyStroke> keyStrokes, Object actionMapKey) {
        keyStrokes.stream().forEach(o -> im.put(o, actionMapKey));
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private enum ActionType {

        COPY, PASTE, SELECT_ALL, DELETE, OPEN, CLEAR;
    }

    static {
        ImmutableListMultimap<ActionType, KeyStroke> keyStrokes = new TextFieldKeyStrokeSupplier().get();
        COPY = keyStrokes.get(ActionType.COPY);
        PASTE = keyStrokes.get(ActionType.PASTE);
        SELECT_ALL = keyStrokes.get(ActionType.SELECT_ALL);
        DELETE = keyStrokes.get(ActionType.DELETE);
        OPEN = keyStrokes.get(ActionType.OPEN);
        CLEAR = keyStrokes.get(ActionType.CLEAR);
    }

    private static abstract class AbstractSupplier implements Supplier<ImmutableListMultimap<ActionType, KeyStroke>> {

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
        public ImmutableListMultimap<ActionType, KeyStroke> get() {
            Map<KeyStroke, Object> keyStrokes = InputMaps.asMap(getInputMap(), true);
            ImmutableListMultimap.Builder<ActionType, KeyStroke> result = ImmutableListMultimap.builder();
            for (ActionType o : ActionType.values()) {
                Object actionMapKey = getActionMapKey(o);
                Set<KeyStroke> tmp = Maps.filterValues(keyStrokes, x -> Objects.equals(x, actionMapKey)).keySet();
                if (!tmp.isEmpty()) {
                    result.putAll(o, tmp);
                } else {
                    result.put(o, getFallback(o));
                }
            }
            return result.orderValuesBy(orderingUsingKeyTextLength()).build();
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
        return (l, r) -> Integer.compare(KeyEvent.getKeyText(l.getKeyCode()).length(), KeyEvent.getKeyText(r.getKeyCode()).length());
    }
    //</editor-fold>
}
