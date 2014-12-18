/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import ec.tstoolkit.algorithm.ProcessingContext;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author pcuser
 */
public class UserVariablesEditor extends AbstractPropertyEditor{
     private String[] vars_;

    public UserVariablesEditor() {
        editor = new JButton(new AbstractAction("...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                final ListSelectionDialog<String> dialog=new ListSelectionDialog<>(SwingUtilities.getWindowAncestor(editor));
                dialog.set(ProcessingContext.getActiveContext().getTsVariableDictionary(), Arrays.asList(vars_));
                dialog.setVisible(true);
                setVariables(dialog.getSelection());
            }
        });
    }

     private void setVariables(List<String> elements) {
        String[] old=vars_;
        vars_ = new String[elements.size()];
        for (int i = 0; i < vars_.length; ++i) {
            vars_[i] = elements.get(i);
        }
        firePropertyChange(old, vars_);
    }

    @Override
    public Object getValue() {
        return new UserVariables(vars_);
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof UserVariables) {
            UserVariables val = (UserVariables) value;
            vars_=val.getNames().clone();
        }
        else {
            vars_ = new String[0];
        }
    }
   
}
