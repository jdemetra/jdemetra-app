/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view;

import ec.tstoolkit.arima.Spectrum;
import ec.util.chart.swing.Charts;
import java.awt.BorderLayout;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Kristof Bayens
 */
public class SpectrumView extends JComponent {
    private XYSeriesCollection coll_;
    private ChartPanel panel_;
    private int n_ = 600;
    private double g_max = 1e6;

    public SpectrumView() {
        setLayout(new BorderLayout());

        panel_ = new ChartPanel(null);
        Charts.avoidScaling(panel_);
        add(panel_, BorderLayout.CENTER);
    }

    public void add(String name, Spectrum spectrum) {
        XYSeries series = new XYSeries(name);
        for (int i = 0; i <= n_; ++i)
        {
            double f = i * Math.PI / n_;
            double s = spectrum.get(f);
            series.add(f, (Double.isNaN(s) || s > g_max ? g_max : s));
        }
        coll_ = new XYSeriesCollection(series);
        panel_.setChart(
            ChartFactory.createXYLineChart(null, null, null, coll_, PlotOrientation.VERTICAL, (coll_.getSeriesCount() > 1), false, false));

        XYPlot plot = panel_.getChart().getXYPlot();
        configurePlot(plot);
    }

    private void configurePlot(XYPlot plot) {
        // Configure range axis
        NumberAxis ra = new NumberAxis();
        ra.setRange(0, 1);
        ra.setTickUnit(new NumberTickUnit(0.1));
        ra.setNumberFormatOverride(new DecimalFormat("0.###"));
        plot.setRangeAxis(ra);

        // Configure domain axis
        NumberAxis da = new NumberAxis();
        da.setRange(0, Math.PI);
        da.setTickUnit(new PiNumberTickUnit(Math.PI / 2));
        plot.setDomainAxis(da);
    }
}
