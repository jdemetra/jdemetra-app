/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.timeseries.regression.ModellingContext;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jean Palate
 */
public class UserVariablesEditor extends AbstractPropertyEditor {

    private String[] variables;
    
    public UserVariablesEditor() {
        editor = new JButton(new AbstractAction("...") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Window owner = SwingUtilities.getWindowAncestor(editor);
                final ListSelectionDialog<String> dialog = new ListSelectionDialog<>(owner);
                dialog.setTitle("Variables");
                dialog.set(ModellingContext.getActiveContext().getTsVariableDictionary(), Arrays.asList(variables));
                dialog.setLocationRelativeTo(owner);
                dialog.setVisible(true);
                setVariables(dialog.getSelection());
            }
        });
    }
    
    private void setVariables(List<String> elements) {
        String[] old = variables;
        variables = new String[elements.size()];
        for (int i = 0; i < variables.length; ++i) {
            variables[i] = elements.get(i);
        }
        firePropertyChange(old, variables);
    }
    
    @Override
    public Object getValue() {
        return new UserVariables(variables);
    }
    
    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof UserVariables) {
            UserVariables val = (UserVariables) value;
            variables = val.getNames().clone();
        } else {
            variables = new String[0];
        }
    }
    
}
