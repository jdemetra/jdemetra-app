/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
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
public class RampsEditor extends AbstractPropertyEditor {

    private static final RampDescriptor[] EMPTY = new RampDescriptor[0];

    private RampDescriptor[] ramps;

    public RampsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window ancestor = SwingUtilities.getWindowAncestor(editor);
                final ArrayEditorDialog<RampDescriptor> dialog = new ArrayEditorDialog<>(ancestor,
                        null != ramps ? ramps : EMPTY,
                        RampDescriptor::new,
                        RampDescriptor::duplicate);
                dialog.setTitle("Ramps");
                dialog.setVisible(true);
                dialog.setLocationRelativeTo(ancestor);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private void setDescriptors(List<RampDescriptor> elements) {
        RampDescriptor[] old = ramps;
        ramps = elements.toArray(RampDescriptor[]::new);
        firePropertyChange(old, ramps);
    }

    @Override
    public Object getValue() {
        return ramps;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof RampDescriptor[] r) {
            RampDescriptor[] old = ramps;
            ramps = r;
            firePropertyChange(old, ramps);
        } else {
            ramps = EMPTY;
        }
    }
}
