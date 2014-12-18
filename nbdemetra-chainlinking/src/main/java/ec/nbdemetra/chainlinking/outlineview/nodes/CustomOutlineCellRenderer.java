/*
 * Copyright 2013 National Bank of Belgium
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
package ec.nbdemetra.chainlinking.outlineview.nodes;

import ec.nbdemetra.chainlinking.outlineview.nodes.ChainLinkingTreeModel.YearlyNode;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import org.netbeans.swing.outline.DefaultOutlineCellRenderer;

/**
 * Renderer for the table cells displaying input data of Chain Linking
 *
 * @author Mats Maggi
 */
public class CustomOutlineCellRenderer extends DefaultOutlineCellRenderer {

    @Override
    @SuppressWarnings("unchecked")
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        if (value != null) {
            l.setText(String.valueOf(value));
        }

        final Object o = table.getValueAt(row, 0);
        if (o instanceof YearlyNode) {
            l.setFont(getFont().deriveFont(Font.BOLD));
        } else {
            l.setFont(getFont().deriveFont(Font.PLAIN));
        }

        l.setOpaque(false);
        return l;
    }
}
