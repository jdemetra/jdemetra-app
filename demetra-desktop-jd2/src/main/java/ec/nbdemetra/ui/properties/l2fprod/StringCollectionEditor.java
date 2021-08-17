/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Kristof Bayens
 */
public class StringCollectionEditor extends AbstractPropertyEditor {
    private String[] strings_;

    public StringCollectionEditor() {
        editor = new JButton(new AbstractAction("...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayEditorDialog<String> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                    null != strings_ ? strings_ : new String[]{}, String.class);
                dialog.setVisible(true);
                setValue(dialog.getElements().toArray(new String[0]));
            }
        });
    }

    @Override
    public Object getValue() {
        return strings_;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof String[]) {
            String[] old = strings_;
            strings_ = (String[]) value;
            firePropertyChange(old, strings_);
        }
    }
}
