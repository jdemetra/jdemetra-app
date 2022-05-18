/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.stl.ui;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.desktop.ui.properties.l2fprod.ArrayEditorDialog;
import demetra.stl.SeasonalSpecification;
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
public class SeasonalSpecsEditor extends AbstractPropertyEditor {

    private SeasonalSpecification[] seasSpecs;

    public SeasonalSpecsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ArrayEditorDialog<SeasonalSpecUI> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                        null != seasSpecs ? getDescriptors() : new SeasonalSpecUI[]{}, SeasonalSpecUI.class);
                dialog.setTitle("Seasonal specs");
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    setDescriptors(dialog.getElements());
                }
            }
        });
    }

    private SeasonalSpecUI[] getDescriptors() {
        SeasonalSpecUI[] descs = new SeasonalSpecUI[seasSpecs.length];
        for (int i = 0; i < descs.length; ++i) {
            descs[i] = new SeasonalSpecUI(seasSpecs[i], false,null);
        }
        return descs;
    }

    private void setDescriptors(List<SeasonalSpecUI> elements) {
        SeasonalSpecification[] old=seasSpecs;
        seasSpecs = new SeasonalSpecification[elements.size()];
        for (int i = 0; i < seasSpecs.length; ++i) {
            seasSpecs[i] = elements.get(i).getCore();
        }
        firePropertyChange(old, seasSpecs);
    }

    @Override
    public Object getValue() {
        return seasSpecs;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof SeasonalSpecification[]) {
            seasSpecs = ((SeasonalSpecification[]) value).clone();
        }
        else {
            seasSpecs = new SeasonalSpecification[0];
        }
    }
    
    @Override
    public String toString(){
        return seasSpecs == null ? "" : seasSpecs.length +" seas";
    }
}
