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
package ec.util.grid.swing.ext;

import ec.util.grid.swing.GridRowHeaderRenderer;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Philippe Charles
 */
public final class SheetRowRenderer implements TableCellRenderer {

    private final GridRowHeaderRenderer delegate = new GridRowHeaderRenderer();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel result = (JLabel) delegate.getTableCellRendererComponent(table, value, false, hasFocus, row, column);
        result.setText(String.valueOf(row + 1));
        result.setHorizontalAlignment(JLabel.CENTER);
        return result;
    }
}
