/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.util.grid.swing;

import ec.util.various.swing.LineBorder2;
import ec.util.various.swing.StandardSwingColor;
import java.awt.Color;
import java.awt.Component;
import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Philippe Charles
 */
public class GridRowHeaderRenderer extends DefaultTableCellRenderer {

    private final Color background;
    private final Border padding;

    public GridRowHeaderRenderer() {
        this.background = StandardSwingColor.CONTROL.or(Color.LIGHT_GRAY);
        this.padding = createCompoundBorder(new LineBorder2(background.darker(), 0, 0, 1, 1), createEmptyBorder(0, 5, 0, 4));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        result.setBorder(padding);
        if (!isSelected && background != null) {
            result.setBackground(background);
        }
        return result;
    }
}
