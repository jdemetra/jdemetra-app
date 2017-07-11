/*
 * Copyright 2014 National Bank of Belgium
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

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.util.list.swing.JLists;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import static javax.swing.SwingConstants.CENTER;

/**
 *
 * @author Mats Maggi
 */
public class OutlierTypeSelector extends ComboBoxPropertyEditor {

    public OutlierTypeSelector() {
    }

    @Override
    public Component getCustomEditor() {
        OutlierType[] types = new OutlierType[]{OutlierType.AO, OutlierType.LS, OutlierType.TC, OutlierType.SO};

        setAvailableValues(types);
        JComboBox box = (JComboBox) super.getCustomEditor();
        box.setRenderer(JLists.cellRendererOf(OutlierTypeSelector::renderOutlierType));

        return box;
    }

    private static void renderOutlierType(JLabel label, Object value) {
        label.setHorizontalAlignment(CENTER);
        if (null != value) {
            OutlierType oType = (OutlierType) value;
            label.setText(oType.toString());
            label.setBackground(ColorChooser.getColor(oType.name()));
            label.setForeground(ColorChooser.getForeColor(oType.name()));
            label.setOpaque(true);
        } else {
            label.setBackground(Color.white);
            label.setText("");
        }
    }
}
