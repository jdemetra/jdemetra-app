/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.chart.swing;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * @see Charts#createSparkLineChart(org.jfree.data.xy.XYDataset)
 * @author Philippe Charles
 */
public class SparklineCellRenderer implements ListCellRenderer, TableCellRenderer {

    protected final ChartPanel sparkline;
    protected final ListCellRenderer listCellRenderer;
    protected final TableCellRenderer tableCellRenderer;

    public SparklineCellRenderer() {
        this.sparkline = Charts.newChartPanel(Charts.createSparkLineChart(Charts.emptyXYDataset()));
        this.listCellRenderer = new DefaultListCellRenderer();
        this.tableCellRenderer = new DefaultTableCellRenderer();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) listCellRenderer.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
        return getCellRendererComponent(label, value);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) tableCellRenderer.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
        return getCellRendererComponent(label, value);
    }

    protected Component getCellRendererComponent(JLabel label, Object value) {
        sparkline.getChart().getXYPlot().setDataset(getDataset(value));
        sparkline.getChart().getXYPlot().getRenderer().setBasePaint(label.getForeground());
        sparkline.setBackground(label.getBackground());
        sparkline.setBorder(label.getBorder());
        return sparkline;
    }

    protected XYDataset getDataset(Object value) {
        if (value instanceof double[][]) {
            DefaultXYDataset result = new DefaultXYDataset();
            result.addSeries("", (double[][]) value);
            return result;
        }
        return null;
    }
}
