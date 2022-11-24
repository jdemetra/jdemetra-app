/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.desktop.ui.properties.l2fprod.ArrayEditorDialog;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Demortier Jeremy
 */
public class SaTsVariableDescriptorsEditor extends AbstractPropertyEditor {

    private static final SaTsVariableDescriptor[] EMPTY= new SaTsVariableDescriptor[0];

    private SaTsVariableDescriptor[] descriptors;

    public SaTsVariableDescriptorsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ArrayEditorDialog<SaTsVariableDescriptor> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                        null != descriptors ? descriptors : EMPTY, 
                        SaTsVariableDescriptor::new, 
                        SaTsVariableDescriptor::duplicate);
                dialog.setTitle("Variables");
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }


    private void setDescriptors(List<SaTsVariableDescriptor> elements) {
        SaTsVariableDescriptor[] old = descriptors;
        // check that the descriptors are well-formed
       descriptors = elements.toArray(SaTsVariableDescriptor[]::new);
        firePropertyChange(old, descriptors);
    }

    @Override
    public Object getValue() {
        return descriptors;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof SaTsVariableDescriptor[] vars) {
            SaTsVariableDescriptor[] old = descriptors;
            descriptors = vars;
            firePropertyChange(old, descriptors);
        }else
            descriptors=EMPTY;
    }
}
