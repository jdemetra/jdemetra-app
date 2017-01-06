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
package ec.nbdemetra.ui.properties;

import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Philippe Charles
 */
public class ComboBoxPropertyEditor extends AbstractExPropertyEditor {

    public static final String VALUES_ATTRIBUTE = "values";

    @Override
    public InplaceEditor createInplaceEditor() {
        return new ComboBoxInplaceEditor();
    }

    private static final class ComboBoxInplaceEditor extends AbstractInplaceEditor {

        final JComboBox component = new JComboBox();
        final KeyStroke[] keyStrokes = new KeyStroke[]{
            KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),
            KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),
            KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true),
            KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true),
            KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0, false),
            KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0, false),
            KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0, true),
            KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0, true)
        };

        {
            component.addActionListener(event -> fireActionPerformed(COMMAND_SUCCESS));
        }

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            Object values = env.getFeatureDescriptor().getValue(VALUES_ATTRIBUTE);
            DefaultComboBoxModel cbModel = (DefaultComboBoxModel) component.getModel();
            cbModel.removeAllElements();
            if (values instanceof Object[]) {
                for (Object o : (Object[]) values) {
                    cbModel.addElement(o);
                }
            }
            super.connect(propertyEditor, env);
        }

        @Override
        public JComponent getComponent() {
            return component;
        }

        @Override
        public Object getValue() {
            return component.getSelectedItem();
        }

        @Override
        public void setValue(Object o) {
            component.setSelectedItem(o);
        }

        @Override
        public KeyStroke[] getKeyStrokes() {
            return keyStrokes;
        }
    }
}
