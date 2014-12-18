/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.grid.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Philippe Charles
 */
public class GridRowHeaderRenderer extends DefaultTableCellRenderer {

    final Color background = UIManager.getColor("control");
    final Border padding = BorderFactory.createEmptyBorder(0, 5, 0, 5);

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
