/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.timeseries.regression.Ramp;
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

    private Ramp[] ramps_;

    public RampsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ArrayEditorDialog<RampDescriptor> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                        null != ramps_ ? getDescriptors() : new RampDescriptor[]{}, RampDescriptor.class);
                dialog.setTitle("Ramps");
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private RampDescriptor[] getDescriptors() {
        RampDescriptor[] descs = new RampDescriptor[ramps_.length];
        for (int i = 0; i < descs.length; ++i) {
            descs[i] = new RampDescriptor(ramps_[i]);
        }
        return descs;
    }

    private void setDescriptors(List<RampDescriptor> elements) {
        Ramp[] old=ramps_;
        ramps_ = new Ramp[elements.size()];
        for (int i = 0; i < ramps_.length; ++i) {
            ramps_[i] = elements.get(i).getCore();
        }
        firePropertyChange(old, ramps_);
    }

    @Override
    public Object getValue() {
        return ramps_;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof Ramp[]) {
            ramps_ = ((Ramp[]) value).clone();
        }
        else {
            ramps_ = new Ramp[0];
        }
    }
}
