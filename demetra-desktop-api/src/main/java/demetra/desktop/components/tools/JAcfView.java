/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.components.tools;

import demetra.desktop.design.SwingComponent;
import ec.util.chart.swing.Charts;
import internal.ui.interfaces.DoubleSeqView;
import java.awt.BorderLayout;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.function.IntToDoubleFunction;
import javax.swing.JComponent;
import jdplus.arima.AutoCovarianceFunction;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Jean Palates
 */
@SwingComponent
public final class JAcfView extends JComponent{
    private XYSeriesCollection data;
    private final ChartPanel panel;
    private final int n = 36;

    public JAcfView() {
        setLayout(new BorderLayout());

        panel = Charts.newChartPanel(null);
        add(panel, BorderLayout.CENTER);
    }


    public void add(String name, IntToDoubleFunction acf) {
        XYSeries series = new XYSeries(name);
        for (int i = 0; i < n; ++i)
        {
            series.add(i, acf.applyAsDouble(i+1));
        }
        data = new XYSeriesCollection(series);
        panel.setChart(ChartFactory.createScatterPlot(null, null, null, data, PlotOrientation.VERTICAL, (data.getSeriesCount() > 1), false, false));

        XYPlot plot = panel.getChart().getXYPlot();
        configurePlot(plot);
    }

    private void configurePlot(XYPlot plot) {
        // Configure range axis
        NumberAxis ra = new NumberAxis();
        ra.setRange(-1, 1);
        ra.setTickUnit(new NumberTickUnit(0.1));
        ra.setNumberFormatOverride(new DecimalFormat("0.##"));
        plot.setRangeAxis(ra);

        // Configure domain axis
        NumberAxis da = new NumberAxis();
        da.setRange(0, n);
        da.setTickUnit(new NumberTickUnit(5));
        plot.setDomainAxis(da);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        for(int i=0; i<data.getSeriesCount(); i++) {
            renderer.setSeriesShape(i, new Rectangle2D.Double(-2,-2,4 ,4));
            renderer.setSeriesShapesFilled(i, false);
            //renderer.setSeriesPaint(i, getColorTheme().getCurveColor(i));
        }
        plot.setRenderer(renderer);
    }
}
