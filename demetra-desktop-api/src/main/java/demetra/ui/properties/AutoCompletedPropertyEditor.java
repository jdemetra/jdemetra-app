/*
 * Copyright 2015 National Bank of Belgium
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
package demetra.ui.properties;

import demetra.ui.completion.AutoCompletion;
import internal.ui.properties.AutoCompletedComponent;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.swing.JAutoCompletion;
import ec.util.various.swing.TextPrompt;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyEditor;
import java.util.concurrent.Callable;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Philippe Charles
 */
public final class AutoCompletedPropertyEditor extends AbstractExPropertyEditor {

    public static final String SERVICE_PATH_ATTRIBUTE = "servicePath";
    public static final String AUTO_FOCUS_ATTRIBUTE = "autoFocus";
    public static final String DELAY_ATTRIBUTE = "delay";
    public static final String MIN_LENGTH_ATTRIBUTE = "minLength";
    public static final String SEPARATOR_ATTRIBUTE = "separator";
    public static final String SOURCE_ATTRIBUTE = "source";
    public static final String CELL_RENDERER_ATTRIBUTE = "cellRenderer";
    public static final String PROMPT_TEXT_ATTRIBUTE = "promptText";
    public static final String DEFAULT_VALUE_SUPPLIER_ATTRIBUTE = "defaultValueSupplier";

    private final AutoCompletedComponent customEditor;
    private PropertyEnv currentEnv;

    public AutoCompletedPropertyEditor() {
        this.customEditor = new AutoCompletedComponent();
        this.currentEnv = null;

        customEditor.setPreferredSize(new Dimension(300, 180));
        customEditor.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case AutoCompletedComponent.VALUE_PROPERTY:
                    setValue(customEditor.getValue());
                    break;
                case AutoCompletedComponent.RUNNING_PROPERTY:
                    currentEnv.setState(customEditor.isRunning() ? PropertyEnv.STATE_INVALID : PropertyEnv.STATE_VALID);
                    break;
            }
        });
    }

    @Override
    protected InplaceEditor createInplaceEditor() {
        return new AC3InplaceEditor();
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        super.attachEnv(env);
        currentEnv = env;
    }

    @Override
    public String getAsText() {
        return attr(currentEnv, SEPARATOR_ATTRIBUTE, String.class)
                .map(s -> super.getAsText().replace(s, " \u27A1 "))
                .orElseGet(super::getAsText);
    }

    @Override
    public boolean supportsCustomEditor() {
        return attr(currentEnv, SEPARATOR_ATTRIBUTE, String.class).isPresent();
    }

    @Override
    public Component getCustomEditor() {
        customEditor.setValue((String) getValue());
        customEditor.setAutoCompletion(o -> applyAutoCompletion(currentEnv, o));
        attr(currentEnv, SEPARATOR_ATTRIBUTE, String.class).ifPresent(customEditor::setSeparator);
        attr(currentEnv, DEFAULT_VALUE_SUPPLIER_ATTRIBUTE, Callable.class).ifPresent(customEditor::setDefaultValueSupplier);
        return customEditor;
    }

    private static void applyAutoCompletion(PropertyEnv env, JTextField component) {
        JAutoCompletion completion = attr(env, SERVICE_PATH_ATTRIBUTE, String.class)
                .map(servicePath -> AutoCompletion.getDefault().bind(servicePath, component))
                .orElseGet(() -> new JAutoCompletion(component));
        attr(env, AUTO_FOCUS_ATTRIBUTE, Boolean.class).ifPresent(completion::setAutoFocus);
        attr(env, DELAY_ATTRIBUTE, Integer.class).ifPresent(completion::setDelay);
        attr(env, MIN_LENGTH_ATTRIBUTE, Integer.class).ifPresent(completion::setMinLength);
        attr(env, SEPARATOR_ATTRIBUTE, String.class).ifPresent(completion::setSeparator);
        attr(env, SOURCE_ATTRIBUTE, AutoCompletionSource.class).ifPresent(completion::setSource);
        attr(env, CELL_RENDERER_ATTRIBUTE, ListCellRenderer.class).ifPresent(completion.getList()::setCellRenderer);
        attr(env, PROMPT_TEXT_ATTRIBUTE, String.class).ifPresent(o -> new TextPrompt(o, component).setEnabled(false));
    }

    private static final class AC3InplaceEditor extends AbstractInplaceEditor {

        private JTextField component;

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            component = new JTextField();
            applyAutoCompletion(env, component);
            super.connect(propertyEditor, env);
        }

        @Override
        public JComponent getComponent() {
            return component;
        }

        @Override
        public Object getValue() {
            return component.getText();
        }

        @Override
        public void setValue(Object o) {
            component.setText((String) o);
        }
    }
}
