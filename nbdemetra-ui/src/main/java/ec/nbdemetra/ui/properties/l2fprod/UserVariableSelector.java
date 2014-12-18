/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import ec.tstoolkit.algorithm.ProcessingContext;
import java.awt.Component;
import java.util.List;

/**
 *
 * @author pcuser
 */
public class UserVariableSelector extends ComboBoxPropertyEditor {

    public UserVariableSelector() {
    }

    @Override
    public Component getCustomEditor() {
        List<String> dic = ProcessingContext.getActiveContext().getTsVariableDictionary();
        Value[] values = new Value[dic.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = new Value(new UserVariable(dic.get(i)), dic.get(i));
        }
        setAvailableValues(values);
        return super.getCustomEditor();
    }
}
