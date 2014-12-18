/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import ec.util.chart.swing.Charts;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

/**
 *
 * @author Kristof Bayens
 */
public class JChartPanel extends ChartPanel {

    public static final String ZOOM_SELECTION_CHANGED = "Zoom Selection Changed";

    public JChartPanel(JFreeChart chart) {
        super(chart);
        Charts.avoidScaling(this);
        this.setDomainZoomable(false);
        this.setRangeZoomable(false);
        this.setPopupMenu(null);
    }

    public double getChartX(double x) {
        Rectangle2D plotArea = this.getScreenDataArea();
        XYPlot plot = (XYPlot) this.getChart().getPlot();
        return plot.getDomainAxis().java2DToValue(x, plotArea, plot.getDomainAxisEdge());
    }

    public double getChartY(double y) {
        Rectangle2D plotArea = this.getScreenDataArea();
        XYPlot plot = (XYPlot) this.getChart().getPlot();
        return plot.getRangeAxis().java2DToValue(y, plotArea, plot.getRangeAxisEdge());
    }

    @Override
    public void zoom(Rectangle2D selection) {
        firePropertyChange(ZOOM_SELECTION_CHANGED, null, selection);
    }
}
