/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

/**
 *
 * @author Philippe Charles
 */
public abstract class ExtAction extends AbstractAction {

    @Deprecated
    public ExtAction() {
    }

    @Deprecated
    public ExtAction(String name) {
        super(name);
    }

    @Deprecated
    public ExtAction(String name, Icon icon) {
        super(name, icon);
    }

    protected void bind(Component c, final IActionBinder binder) {
        binder.set(ExtAction.this);
        c.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                for (String o : binder.propertyNames()) {
                    if (o.equals(evt.getPropertyName())) {
                        binder.set(ExtAction.this);
                        break;
                    }
                }
            }
        });
    }

    @Deprecated
    protected interface IActionBinder {

        String[] propertyNames();

        void set(Action action);
    }

    @Deprecated
    protected static abstract class Enable implements IActionBinder {

        final String[] propertyNames;

        public Enable(String... propertyNames) {
            this.propertyNames = propertyNames;
        }

        @Override
        public String[] propertyNames() {
            return propertyNames;
        }

        @Override
        public void set(Action action) {
            action.setEnabled(canEnable());
        }

        abstract protected boolean canEnable();
    }

    @Deprecated
    protected static abstract class Select implements IActionBinder {

        final String[] propertyNames;

        public Select(String... propertyNames) {
            this.propertyNames = propertyNames;
        }

        @Override
        public String[] propertyNames() {
            return propertyNames;
        }

        @Override
        public void set(Action action) {
            action.putValue(Action.SELECTED_KEY, canSelect());
        }

        abstract protected boolean canSelect();
    }

    public static <C extends AbstractButton> C hideWhenDisabled(C c) {
        c.setVisible(c.isEnabled());
        c.addPropertyChangeListener(HIDE);
        return c;
    }
    //
    private static final PropertyChangeListener HIDE = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("enabled".equals(evt.getPropertyName()) && evt.getSource() instanceof JComponent) {
                ((JComponent) evt.getSource()).setVisible((Boolean) evt.getNewValue());
            }
        }
    };
}
