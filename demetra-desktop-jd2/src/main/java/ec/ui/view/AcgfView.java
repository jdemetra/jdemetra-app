/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view;

import demetra.ui.design.SwingComponent;
import ec.tstoolkit.arima.AutoCovarianceFunction;
import ec.tstoolkit.arima.LinearModel;
import ec.util.chart.swing.Charts;
import java.awt.BorderLayout;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import javax.swing.JComponent;
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
 * @author Kristof Bayens
 */
@SwingComponent
public final class AcgfView extends JComponent{
    private XYSeriesCollection coll_;
    private ChartPanel panel_;
    private int n_ = 36;

    public AcgfView() {
        setLayout(new BorderLayout());

        panel_ = Charts.newChartPanel(null);
        add(panel_, BorderLayout.CENTER);
    }

    public void add(String name, LinearModel model) {
        add(name, model.doStationary().getAutoCovarianceFunction());
    }

    public void add(String name, double[] acgf){
        XYSeries series = new XYSeries(name);
        for (int i = 0; i <= n_; ++i)
        {
            series.add(i + 1, acgf[i]);
        }
        coll_ = new XYSeriesCollection(series);
        panel_.setChart(
            ChartFactory.createScatterPlot(null, null, null, coll_, PlotOrientation.VERTICAL, (coll_.getSeriesCount() > 1), false, false));

        XYPlot plot = panel_.getChart().getXYPlot();
        configurePlot(plot);
    }

    public void add(String name, AutoCovarianceFunction acgf) {
        XYSeries series = new XYSeries(name);
        double v = acgf.get(0);
        for (int i = 0; i <= n_; ++i)
        {
            series.add(i + 1, acgf.get(i + 1) / v);
        }
        coll_ = new XYSeriesCollection(series);
        panel_.setChart(
            ChartFactory.createScatterPlot(null, null, null, coll_, PlotOrientation.VERTICAL, (coll_.getSeriesCount() > 1), false, false));

        XYPlot plot = panel_.getChart().getXYPlot();
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
        da.setRange(0, 35);
        da.setTickUnit(new NumberTickUnit(5));
        plot.setDomainAxis(da);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        for(int i=0; i<coll_.getSeriesCount(); i++) {
            renderer.setSeriesShape(i, new Rectangle2D.Double(-2,-2,4 ,4));
            renderer.setSeriesShapesFilled(i, false);
            //renderer.setSeriesPaint(i, getColorTheme().getCurveColor(i));
        }
        plot.setRenderer(renderer);
    }
}
