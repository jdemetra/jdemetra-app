/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import ec.tstoolkit.timeseries.regression.Sequence;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Demortier Jeremy
 */
public class SequencesEditor extends AbstractPropertyEditor {

  private Sequence[] seqs_;

    public SequencesEditor() {
        editor = new JButton(new AbstractAction("...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                final ArrayEditorDialog<SequenceDescriptor> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                        null != seqs_ ? getDescriptors() : new SequenceDescriptor[]{}, SequenceDescriptor.class);
                dialog.setTitle("Sequences");
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private SequenceDescriptor[] getDescriptors() {
        SequenceDescriptor[] descs = new SequenceDescriptor[seqs_.length];
        for (int i = 0; i < descs.length; ++i) {
            descs[i] = new SequenceDescriptor(seqs_[i]);
        }
        return descs;
    }

    private void setDescriptors(List<SequenceDescriptor> elements) {
        Sequence[] old=seqs_;
        seqs_ = new Sequence[elements.size()];
        for (int i = 0; i < seqs_.length; ++i) {
            seqs_[i] = elements.get(i).getCore();
        }
        firePropertyChange(old, seqs_);
    }

    @Override
    public Object getValue() {
        return seqs_;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof Sequence[]) {
            Sequence[] val = (Sequence[]) value;
            seqs_ = new Sequence[val.length];
            for (int i = 0; i < val.length; ++i) {
                seqs_[i] = val[i].clone();
            }
        }
        else {
            seqs_ = new Sequence[0];
        }
    }
}
