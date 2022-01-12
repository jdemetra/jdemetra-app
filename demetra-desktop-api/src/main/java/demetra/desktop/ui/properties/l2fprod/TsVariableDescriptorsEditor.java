/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.timeseries.regression.TsContextVariable;
import demetra.timeseries.regression.TsVariable;
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

    private TsContextVariable[] descriptors_;

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
        TsContextVariable[] old = descriptors_;
        // check that the descriptors are well-formed
        List<TsContextVariable> ldesc = new ArrayList<>();
        for (TsVariableDescriptorUI element : elements) {
            TsContextVariable cur = element.getCore();
            ldesc.add(cur);
        }
        descriptors_ = ldesc.toArray(new TsContextVariable[0]);
        firePropertyChange(old, descriptors_);
    }

    @Override
    public Object getValue() {
        return descriptors_;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof TsContextVariable[]) {
            TsContextVariable[] old = descriptors_;
            TsContextVariable[] ndesc = (TsContextVariable[]) value;
            // check that the descriptors are well-formed
            List<TsContextVariable> ldesc = new ArrayList<>();
            for (TsContextVariable tsVariableDescriptor : ndesc) {
                ldesc.add(tsVariableDescriptor);
            }
            descriptors_ = ldesc.toArray(new TsContextVariable[ldesc.size()]);
            firePropertyChange(old, descriptors_);
        }
    }
}
