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
public class SaInterventionVariablesEditor extends AbstractPropertyEditor {

  private SaInterventionVariableDescriptor[] vars;

  public SaInterventionVariablesEditor() {
        editor = new JButton(new AbstractAction("...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                final ArrayEditorDialog<SaInterventionVariableDescriptor> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                        null != vars ? vars : EMPTY, 
                        SaInterventionVariableDescriptor::new, 
                        SaInterventionVariableDescriptor::duplicate);
                dialog.setTitle("Intervention variables");
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private void setDescriptors(List<SaInterventionVariableDescriptor> elements) {
        SaInterventionVariableDescriptor[] old=vars;
        vars = elements.toArray(SaInterventionVariableDescriptor[]::new);
        firePropertyChange(old, vars);
    }

    @Override
    public Object getValue() {
        return vars;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof SaInterventionVariableDescriptor[] iv) {
            SaInterventionVariableDescriptor[] old=vars;
            vars = iv;
            firePropertyChange(old, vars);
        }
        else {
            vars =EMPTY;
        }
    }
    
    private static final SaInterventionVariableDescriptor[] EMPTY= new SaInterventionVariableDescriptor[0];
}
