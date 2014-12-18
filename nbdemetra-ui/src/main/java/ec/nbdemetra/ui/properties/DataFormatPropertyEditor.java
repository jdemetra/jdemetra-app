/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import ec.tss.tsproviders.utils.DataFormat;
import javax.swing.JComponent;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.nodes.PropertyEditorRegistration;

/**
 *
 * @author Philippe Charles
 */
@PropertyEditorRegistration(targetType = DataFormat.class)
public class DataFormatPropertyEditor extends AbstractExPropertyEditor {

    @Override
    public InplaceEditor createInplaceEditor() {
        return new AbstractInplaceEditor() {
            final DataFormatComponent2 component = new DataFormatComponent2();

            @Override
            public JComponent getComponent() {
                return component;
            }

            @Override
            public Object getValue() {
                return component.getDataFormat();
            }

            @Override
            public void setValue(Object o) {
                component.setDataFormat((DataFormat) o);
            }
        };
    }
}
