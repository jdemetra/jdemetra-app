package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Demortier Jeremy
 */
public class InterventionVariablesEditor extends AbstractPropertyEditor {

  private InterventionVariable[] vars_;

  public InterventionVariablesEditor() {
        editor = new JButton(new AbstractAction("...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                final ArrayEditorDialog<InterventionVariableDescriptor> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                        null != vars_ ? getDescriptors() : new InterventionVariableDescriptor[]{}, InterventionVariableDescriptor.class);
                dialog.setTitle("Intervention variables");
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private InterventionVariableDescriptor[] getDescriptors() {
        InterventionVariableDescriptor[] descs = new InterventionVariableDescriptor[vars_.length];
        for (int i = 0; i < descs.length; ++i) {
            descs[i] = new InterventionVariableDescriptor(vars_[i]);
        }
        return descs;
    }

    private void setDescriptors(List<InterventionVariableDescriptor> elements) {
        InterventionVariable[] old=vars_;
        vars_ = new InterventionVariable[elements.size()];
        for (int i = 0; i < vars_.length; ++i) {
            vars_[i] = elements.get(i).getCore();
        }
        firePropertyChange(old, vars_);
    }

    @Override
    public Object getValue() {
        return vars_;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof InterventionVariable[]) {
            InterventionVariable[] val = (InterventionVariable[]) value;
            vars_ = new InterventionVariable[val.length];
            for (int i = 0; i < val.length; ++i) {
                vars_[i] = val[i].clone();
            }
        }
        else {
            vars_ = new InterventionVariable[0];
        }
    }
}
