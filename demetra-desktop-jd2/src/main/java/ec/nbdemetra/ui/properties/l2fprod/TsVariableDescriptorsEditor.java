/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Demortier Jeremy
 */
public class TsVariableDescriptorsEditor extends AbstractPropertyEditor {

    private TsVariableDescriptor[] descriptors_;

    public TsVariableDescriptorsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ArrayEditorDialog<TsVariableDescriptorUI> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                        null != descriptors_ ? getDescriptors() : new TsVariableDescriptorUI[]{}, TsVariableDescriptorUI.class);
                dialog.setTitle("Variables");
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private TsVariableDescriptorUI[] getDescriptors() {
        TsVariableDescriptorUI[] descs = new TsVariableDescriptorUI[descriptors_.length];
        for (int i = 0; i < descs.length; ++i) {
            descs[i] = new TsVariableDescriptorUI(descriptors_[i]);
        }
        return descs;
    }

    private void setDescriptors(List<TsVariableDescriptorUI> elements) {
        TsVariableDescriptor[] old = descriptors_;
        // check that the descriptors are well-formed
        List<TsVariableDescriptor> ldesc = new ArrayList<>();
        for (TsVariableDescriptorUI element : elements) {
            TsVariableDescriptor cur = element.getCore();
            if (cur.getName() != null) {
                ldesc.add(cur);
            }
        }
        descriptors_ = ldesc.toArray(new TsVariableDescriptor[0]);
        firePropertyChange(old, descriptors_);
    }

    @Override
    public Object getValue() {
        return descriptors_;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof TsVariableDescriptor[]) {
            TsVariableDescriptor[] old = descriptors_;
            TsVariableDescriptor[] ndesc = (TsVariableDescriptor[]) value;
            // check that the descriptors are well-formed
            List<TsVariableDescriptor> ldesc = new ArrayList<>();
            for (TsVariableDescriptor tsVariableDescriptor : ndesc) {
                if (tsVariableDescriptor.getName() != null) {
                    ldesc.add(tsVariableDescriptor.clone());
                }
            }
            descriptors_ = ldesc.toArray(new TsVariableDescriptor[0]);
            firePropertyChange(old, descriptors_);
        }
    }
}
