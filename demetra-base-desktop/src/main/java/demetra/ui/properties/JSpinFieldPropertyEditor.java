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
package demetra.ui.properties;

import com.toedter.components.JSpinField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Philippe Charles
 */
public final class JSpinFieldPropertyEditor extends AbstractExPropertyEditor {

    public static final String MAX_ATTRIBUTE = "max";
    public static final String MIN_ATTRIBUTE = "min";

    @Override
    protected InplaceEditor createInplaceEditor() {
        return new JSpinFieldInplaceEditor();
    }

    private static final class JSpinFieldInplaceEditor extends AbstractInplaceEditor {

        final JSpinField component = new JSpinField() {
            {
                textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "FIRE_ACTION_PERFORMED");
                textField.getActionMap().put("FIRE_ACTION_PERFORMED", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (textField.getText().equals(Integer.toString(value))) {
                            fireActionPerformed(COMMAND_SUCCESS);
                        }
                    }
                });
                spinner.setBorder(null);
            }
        };

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            component.setMaximum(attr(env, MAX_ATTRIBUTE, Integer.class).orElse(Integer.MAX_VALUE));
            component.setMinimum(attr(env, MIN_ATTRIBUTE, Integer.class).orElse(Integer.MIN_VALUE));
            super.connect(propertyEditor, env);
        }

        @Override
        public JComponent getComponent() {
            return component;
        }

        @Override
        public Object getValue() {
            return component.getValue();
        }

        @Override
        public void setValue(Object o) {
            component.setValue((Integer) o);
        }
    }
}
