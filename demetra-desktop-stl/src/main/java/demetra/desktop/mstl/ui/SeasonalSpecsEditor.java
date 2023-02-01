/*
 * Copyright 2023 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.mstl.ui;

import demetra.desktop.stl.ui.SeasonalSpecUI;
import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.desktop.ui.properties.l2fprod.ArrayEditorDialog;
import demetra.stl.SeasonalSpec;
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

    private SeasonalSpec[] seasSpecs;

    public SeasonalSpecsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final ArrayEditorDialog<SeasonalSpecUI> dialog = new ArrayEditorDialog<>(SwingUtilities.getWindowAncestor(editor),
                        null != seasSpecs ? getDescriptors() : new SeasonalSpecUI[]{}, 
                        SeasonalSpecUI::new, SeasonalSpecUI::duplicate);
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
        SeasonalSpec[] old=seasSpecs;
        seasSpecs = new SeasonalSpec[elements.size()];
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
        if (null != value && value instanceof SeasonalSpec[]) {
            seasSpecs = ((SeasonalSpec[]) value).clone();
        }
        else {
            seasSpecs = new SeasonalSpec[0];
        }
    }
    
    @Override
    public String toString(){
        return seasSpecs == null ? "" : seasSpecs.length +" seas";
    }
}
