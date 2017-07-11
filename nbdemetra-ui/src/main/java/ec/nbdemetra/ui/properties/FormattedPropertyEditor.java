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

import static ec.nbdemetra.ui.properties.Util.attr;
import static internal.JTextComponents.enableDecimalMappingOnNumpad;
import static internal.JTextComponents.enableValidationFeedback;
import static internal.JTextComponents.peekValue;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;
import java.text.ParseException;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultFormatterFactory;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Philippe Charles
 */
public class FormattedPropertyEditor extends AbstractExPropertyEditor {

    public static final String FORMATTER_ATTRIBUTE = "formatter";

    private PropertyEnv currentEnv = null;

    @Override
    public void attachEnv(PropertyEnv env) {
        super.attachEnv(env);
        currentEnv = env;
    }

    @Override
    public String getAsText() {
        return attr(currentEnv, FORMATTER_ATTRIBUTE, JFormattedTextField.AbstractFormatter.class)
                .map(o -> {
                    try {
                        return o.valueToString(getValue());
                    } catch (ParseException ex) {
                        return null;
                    }
                })
                .orElseGet(super::getAsText);
    }

    @Override
    protected InplaceEditor createInplaceEditor() {
        return new FormattedInplaceEditor();
    }

    private static final class FormattedInplaceEditor extends AbstractInplaceEditor {

        private final JFormattedTextField component = new JFormattedTextField();

        {
            component.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "FIRE_ACTION_PERFORMED");
            component.getActionMap().put("FIRE_ACTION_PERFORMED", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isValid(component.getText())) {
                        fireActionPerformed(COMMAND_SUCCESS);
                    }
                }
            });
            enableValidationFeedback(component, this::isValid);
            enableDecimalMappingOnNumpad(component);
        }

        private boolean isValid(String input) {
            try {
                return component.getFormatter().stringToValue(input) != null;
            } catch (ParseException ex) {
                return false;
            }
        }

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            JFormattedTextField.AbstractFormatterFactory format = attr(env, FORMATTER_ATTRIBUTE, JFormattedTextField.AbstractFormatter.class)
                    .map(DefaultFormatterFactory::new)
                    .orElseGet(DefaultFormatterFactory::new);
            component.setFormatterFactory(format);
            super.connect(propertyEditor, env);
        }

        @Override
        public JComponent getComponent() {
            return component;
        }

        @Override
        public Object getValue() {
            return peekValue(component).orElseGet(component::getValue);
        }

        @Override
        public void setValue(Object o) {
            component.setValue(o);
        }
    }
}
