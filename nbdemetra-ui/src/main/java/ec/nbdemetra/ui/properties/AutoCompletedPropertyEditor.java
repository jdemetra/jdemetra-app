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
import ec.util.completion.AutoCompletionSource;
import java.beans.PropertyEditor;
import javax.swing.JComponent;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public class AutoCompletedPropertyEditor extends AbstractExPropertyEditor {

    public static final String VALUES_ATTRIBUTE = "source";
    public static final String SEPARATOR_ATTRIBUTE = "separator";

    @Override
    protected InplaceEditor createInplaceEditor() {
        return new AbstractInplaceEditor() {
            final AutoCompletedComboBox<String> component = new AutoCompletedComboBox<String>() {
                @Override
                public String getValue() {
                    return textComponent.getText();
                }

                @Override
                public void setValue(String value) {
                    textComponent.setText(value);
                }
            };

            @Override
            public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
                component.setAutoCompletion(attr(env, VALUES_ATTRIBUTE, AutoCompletionSource.class).orElse(null));
                component.setSeparator(attr(env, SEPARATOR_ATTRIBUTE, String.class).orElse(null));
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
                component.setValue((String) o);
            }
        };
    }
}
