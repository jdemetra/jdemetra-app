/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.awt;

import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
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
        for (KeyStroke o : keyStrokes) {
            im.put(o, actionMapKey);
        }
    }

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
                Set<KeyStroke> tmp = Maps.filterValues(keyStrokes, Predicates.equalTo(getActionMapKey(o))).keySet();
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

    private static Ordering<KeyStroke> orderingUsingKeyTextLength() {
        return new Ordering<KeyStroke>() {
            @Override
            public int compare(KeyStroke left, KeyStroke right) {
                return Ints.compare(KeyEvent.getKeyText(left.getKeyCode()).length(), KeyEvent.getKeyText(right.getKeyCode()).length());
            }
        };
    }
}
