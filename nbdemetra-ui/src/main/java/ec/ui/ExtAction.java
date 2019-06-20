/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.ui;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import org.checkerframework.checker.nullness.qual.NonNull;
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
        c.addPropertyChangeListener(evt -> {
            for (String o : binder.propertyNames()) {
                if (o.equals(evt.getPropertyName())) {
                    binder.set(ExtAction.this);
                    break;
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

    @NonNull
    public static <C extends AbstractButton> C hideWhenDisabled(@NonNull C c) {
        c.setVisible(c.isEnabled());
        c.addPropertyChangeListener(HIDE);
        return c;
    }

    private static final PropertyChangeListener HIDE = evt -> {
        if ("enabled".equals(evt.getPropertyName()) && evt.getSource() instanceof JComponent) {
            ((JComponent) evt.getSource()).setVisible((Boolean) evt.getNewValue());
        }
    };
}
