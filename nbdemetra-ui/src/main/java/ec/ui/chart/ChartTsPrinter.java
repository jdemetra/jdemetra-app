/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.chart;

import ec.ui.interfaces.ITsPrinter;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public class ChartTsPrinter implements ITsPrinter {

    final ChartPanel chartPanel;

    public ChartTsPrinter(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    @Override
    public boolean printPreview() {
        chartPanel.createChartPrintJob();
        return true;
    }

    @Override
    public boolean print() {
        return printPreview();
    }
}
