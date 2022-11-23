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

  private InterventionVariableDescriptor[] vars;

  public InterventionVariablesEditor() {
        editor = new JButton(new AbstractAction("...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                final ArrayEditorDialog<InterventionVariableDescriptor> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                        null != vars ? vars : EMPTY, 
                        InterventionVariableDescriptor::new, 
                        InterventionVariableDescriptor::duplicate);
                dialog.setTitle("Intervention variables");
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private void setDescriptors(List<InterventionVariableDescriptor> elements) {
        InterventionVariableDescriptor[] old=vars;
        vars = elements.toArray(InterventionVariableDescriptor[]::new);
        firePropertyChange(old, vars);
    }

    @Override
    public Object getValue() {
        return vars;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof InterventionVariableDescriptor[] iv) {
            vars = iv;
        }
        else {
            vars =EMPTY;
        }
    }
    
    private static final InterventionVariableDescriptor[] EMPTY= new InterventionVariableDescriptor[0];
}
