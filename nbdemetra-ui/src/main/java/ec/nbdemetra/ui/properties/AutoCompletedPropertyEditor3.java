/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import com.google.common.base.Optional;
import ec.nbdemetra.ui.completion.JAutoCompletionService;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.swing.JAutoCompletion;
import ec.util.various.swing.TextPrompt;
import java.beans.PropertyEditor;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Philippe Charles
 */
public class AutoCompletedPropertyEditor3 extends AbstractExPropertyEditor {

    public static final String SERVICE_PATH_ATTRIBUTE = "servicePath";
    public static final String AUTO_FOCUS_ATTRIBUTE = "autoFocus";
    public static final String DELAY_ATTRIBUTE = "delay";
    public static final String MIN_LENGTH_ATTRIBUTE = "minLength";
    public static final String SEPARATOR_ATTRIBUTE = "separator";
    public static final String SOURCE_ATTRIBUTE = "source";
    public static final String CELL_RENDERER_ATTRIBUTE = "cellRenderer";
    public static final String PROMPT_TEXT_ATTRIBUTE = "promptText";

    @Override
    protected InplaceEditor createInplaceEditor() {
        return new AbstractInplaceEditor() {
            JTextField component;

            @Override
            public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
                component = new JTextField();
                Optional<String> servicePath = getAttribute(env, SERVICE_PATH_ATTRIBUTE, String.class);
                JAutoCompletion completion = servicePath.isPresent() ? JAutoCompletionService.forPathBind(servicePath.get(), component) : new JAutoCompletion(component);
                Optional<Boolean> autoFocus = getAttribute(env, AUTO_FOCUS_ATTRIBUTE, Boolean.class);
                if (autoFocus.isPresent()) {
                    completion.setAutoFocus(autoFocus.get());
                }
                Optional<Integer> delay = getAttribute(env, DELAY_ATTRIBUTE, Integer.class);
                if (delay.isPresent()) {
                    completion.setDelay(delay.get());
                }
                Optional<Integer> minLength = getAttribute(env, MIN_LENGTH_ATTRIBUTE, Integer.class);
                if (minLength.isPresent()) {
                    completion.setMinLength(minLength.get());
                }
                Optional<String> separator = getAttribute(env, SEPARATOR_ATTRIBUTE, String.class);
                if (separator.isPresent()) {
                    completion.setSeparator(separator.get());
                }
                Optional<AutoCompletionSource> source = getAttribute(env, SOURCE_ATTRIBUTE, AutoCompletionSource.class);
                if (source.isPresent()) {
                    completion.setSource(source.get());
                }
                Optional<ListCellRenderer> cellRenderer = getAttribute(env, CELL_RENDERER_ATTRIBUTE, ListCellRenderer.class);
                if (cellRenderer.isPresent()) {
                    completion.getList().setCellRenderer(cellRenderer.get());
                }
                Optional<String> promptText = getAttribute(env, PROMPT_TEXT_ATTRIBUTE, String.class);
                if (promptText.isPresent()) {
                    new TextPrompt(promptText.get(), component).setEnabled(false);
                }
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
        };
    }
}
