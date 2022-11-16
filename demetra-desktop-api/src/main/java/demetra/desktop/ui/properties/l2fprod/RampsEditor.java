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

    private Ramp[] ramps;

    public RampsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ArrayEditorDialog<RampDescriptor> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                        null != ramps ? getDescriptors() : new RampDescriptor[]{}, RampDescriptor.class);
                dialog.setTitle("Ramps");
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private RampDescriptor[] getDescriptors() {
        RampDescriptor[] descs = new RampDescriptor[ramps.length];
        for (int i = 0; i < descs.length; ++i) {
            descs[i] = new RampDescriptor(ramps[i]);
        }
        return descs;
    }

    private void setDescriptors(List<RampDescriptor> elements) {
        Ramp[] old=ramps;
        ramps = new Ramp[elements.size()];
        for (int i = 0; i < ramps.length; ++i) {
            ramps[i] = elements.get(i).getCore();
        }
        firePropertyChange(old, ramps);
    }

    @Override
    public Object getValue() {
        return ramps;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof Ramp[]) {
            ramps = ((Ramp[]) value).clone();
        }
        else {
            ramps = new Ramp[0];
        }
    }
}
