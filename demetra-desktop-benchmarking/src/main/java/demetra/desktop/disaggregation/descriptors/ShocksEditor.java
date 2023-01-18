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
public class ShocksEditor extends AbstractPropertyEditor {

    private static final ShockDescriptor[] EMPTY = new ShockDescriptor[0];

    private ShockDescriptor[] shocks;

    public ShocksEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window ancestor = SwingUtilities.getWindowAncestor(editor);
                final ArrayEditorDialog<ShockDescriptor> dialog = new ArrayEditorDialog<>(ancestor,
                        null != shocks ? shocks : EMPTY,
                        ShockDescriptor::new,
                        ShockDescriptor::duplicate);
                dialog.setTitle("Shocks");
                dialog.setVisible(true);
                dialog.setLocationRelativeTo(ancestor);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private void setDescriptors(List<ShockDescriptor> elements) {
        ShockDescriptor[] old = shocks;
        shocks = elements.toArray(ShockDescriptor[]::new);
        firePropertyChange(old, shocks);
    }

    @Override
    public Object getValue() {
        return shocks;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof ShockDescriptor[] r) {
            ShockDescriptor[] old = shocks;
            shocks = r;
            firePropertyChange(old, shocks);
        } else {
            shocks = EMPTY;
        }
    }
}
