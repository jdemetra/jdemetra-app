/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
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
public class OutlierDefinitionsEditor extends AbstractPropertyEditor {

    private OutlierDefinition[] definitions_;
    
    public OutlierDefinitionsEditor() {
        editor = new JButton(new AbstractAction("...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Window ancestor = SwingUtilities.getWindowAncestor(editor);
                final ArrayEditorDialog<OutlierDescriptor> dialog = new ArrayEditorDialog<>(ancestor,
                        null != definitions_ ? getDescriptors() : new OutlierDescriptor[]{}, OutlierDescriptor.class);
                dialog.setTitle("Pre-specified outliers");
                dialog.setLocationRelativeTo(ancestor);
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }
    
    private void setDescriptors(List<OutlierDescriptor> elements) {
        OutlierDefinition[] old=definitions_;
        definitions_ = new OutlierDefinition[elements.size()];
        for (int i = 0; i < definitions_.length; ++i) {
            definitions_[i] = elements.get(i).getCore();
        }
        firePropertyChange(old, definitions_);
    }
    
    private OutlierDescriptor[] getDescriptors() {
        OutlierDescriptor[] descs = new OutlierDescriptor[definitions_.length];
        for (int i = 0; i < descs.length; ++i) {
            descs[i] = new OutlierDescriptor(definitions_[i]);
        }
        return descs;
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            definitions_ = new OutlierDefinition[0];
        } else if (value instanceof OutlierDefinition[]) {
            OutlierDefinition[] outs = ((OutlierDefinition[]) value);
            // makes copies
            definitions_=new OutlierDefinition[outs.length];
            for (int i=0; i<definitions_.length; ++i){
                definitions_[i]=outs[i].clone();
            }
        }
    }
    
    @Override
    public Object getValue() {
        return definitions_;
    }
}
