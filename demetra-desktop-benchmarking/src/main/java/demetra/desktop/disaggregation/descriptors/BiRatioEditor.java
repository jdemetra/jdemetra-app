/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.disaggregation.descriptors;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.desktop.ui.properties.l2fprod.ArrayEditorDialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Demortier Jeremy
 */
public class BiRatioEditor extends AbstractPropertyEditor {

    private static final BiRatioDescriptor[] EMPTY = new BiRatioDescriptor[0];

    private BiRatioDescriptor[] biratio;

    public BiRatioEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window ancestor = SwingUtilities.getWindowAncestor(editor);
                final ArrayEditorDialog<BiRatioDescriptor> dialog = new ArrayEditorDialog<>(ancestor,
                        null != biratio ? biratio : EMPTY,
                        BiRatioDescriptor::new,
                        BiRatioDescriptor::duplicate);
                dialog.setTitle("Bi-Ratios");
                dialog.setVisible(true);
                dialog.setLocationRelativeTo(ancestor);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private void setDescriptors(List<BiRatioDescriptor> elements) {
        BiRatioDescriptor[] old = biratio;
        biratio = elements.toArray(BiRatioDescriptor[]::new);
        firePropertyChange(old, biratio);
    }

    @Override
    public Object getValue() {
        return biratio;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof BiRatioDescriptor[] r) {
            BiRatioDescriptor[] old = biratio;
            biratio = r;
            firePropertyChange(old, biratio);
        } else {
            biratio = EMPTY;
        }
    }
}
