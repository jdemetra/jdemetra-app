/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Philippe Charles
 */
class BarTableCellRenderer extends DefaultTableCellRenderer {

    private boolean horizontalBar;
    private double a, b = 0;

    public BarTableCellRenderer(boolean horizontalBar) {
        this.horizontalBar = true;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        return result;
    }

    public void setBarValues(double min, double max, double val) {
        double diff = max - min;
        if (diff == 0) {
            a = b = 0;
        } else {
            double zero = min > 0 ? min : max < 0 ? max : 0;
            if (val >= zero) {
                a = (zero - min) / diff;
                b = (val - min) / diff;
            } else {
                a = (val - min) / diff;
                b = (zero - min) / diff;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (b - a > 0) {
            Graphics2D g2 = (Graphics2D) g;

            Rectangle oldClip = g2.getClipBounds();
            Color oldForeground = getForeground();
            Color oldBackground = getBackground();

            double d = b - a;
            if (horizontalBar) {
                g2.clipRect((int) (a * getWidth()), 0, (int) (d * getWidth()), getHeight());
            } else {
                g2.clipRect(0, (int) ((1 - b) * getHeight()), getWidth(), (int) (d * getHeight()));
            }
            setForeground(oldBackground);
            setBackground(oldForeground);

            super.paintComponent(g);

            setBackground(oldBackground);
            setForeground(oldForeground);
            g2.setClip(oldClip);
        }
    }
}
