/*
 * Copyright 2017 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author Mats Maggi
 */
public class FixedCoefficientsEditor extends AbstractPropertyEditor {

    private Coefficients coefficients;

    public FixedCoefficientsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final CoefficientsEditorDialog dialog = new CoefficientsEditorDialog(SwingUtilities.getWindowAncestor(editor),
                        coefficients != null ? coefficients : new Coefficients());
                dialog.setTitle("Fixed Coefficients");
                dialog.setVisible(true);
                if (dialog.isDirty()) {
                    coefficients = dialog.getCoefficients();
                }
            }
        });
    }

    @Override
    public Object getValue() {
        return coefficients;
    }

    @Override
    public void setValue(Object value) {
        Coefficients c = (Coefficients) value;
        if (null != c) {
            coefficients = new Coefficients(c.getFixedCoefficients());
            coefficients.setAllNames(c.getAllNames());
        } else {
            coefficients = new Coefficients();
        }
    }
}
