/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import java.nio.charset.Charset;
import javax.swing.JComponent;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = Charset.class)
public class CharsetPropertyEditor extends AbstractExPropertyEditor {

    @Override
    public InplaceEditor createInplaceEditor() {
        return new AbstractInplaceEditor() {
            final CharsetComponent2 component = new CharsetComponent2();

            @Override
            public JComponent getComponent() {
                return component;
            }

            @Override
            public Object getValue() {
                return component.getCharset();
            }

            @Override
            public void setValue(Object o) {
                component.setCharset((Charset) o);
            }
        };
    }
}
