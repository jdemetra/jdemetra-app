/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.uihelper.DiscreteDisplayDomain;
import ec.tstoolkit.uihelper.IDiscreteInformationProvider;
import ec.ui.chart.TsCharts;
import ec.util.chart.swing.ChartCommand;
import ec.util.chart.swing.ext.MatrixChartCommand;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import javax.swing.JMenu;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Kristof Bayens
 */
public class ScatterView extends AChartView {

    // CONSTANTS
    public static final double DEFAULT_MIN_X = 0;
    public static final double DEFAULT_MAX_X = 35;
    public static final double DEFAULT_MIN_Y = -1;
    public static final double DEFAULT_MAX_Y = 1;
    public static final NumberTickUnit DEFAULT_TICKUNIT_X = new NumberTickUnit(2);
    public static final NumberTickUnit DEFAULT_TICKUNIT_Y = new NumberTickUnit(0.1);
    public static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat("0.##");
    // OTHER
    protected final IDiscreteInformationProvider provider;

    public ScatterView(IDiscreteInformationProvider provider) {
        super(36, PlotOrientation.VERTICAL, 1, 36, DEFAULT_MIN_Y, DEFAULT_MAX_Y, DEFAULT_TICKUNIT_X, DEFAULT_TICKUNIT_Y, DEFAULT_FORMAT);
        setZoomable(false);
        this.provider = provider;
        this.chartPanel.setChart(createScatterViewChart(seriesCollection));
        onDomainChange();
        chartPanel.setPopupMenu(buildMenu(chartPanel).getPopupMenu());
    }

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    @Override
    protected void onColorSchemeChange() {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        plot.setBackgroundPaint(themeSupport.getPlotColor());
        plot.setDomainGridlinePaint(themeSupport.getGridColor());
        plot.setRangeGridlinePaint(themeSupport.getGridColor());
        chartPanel.getChart().setBackgroundPaint(themeSupport.getBackColor());

        XYItemRenderer renderer = plot.getRenderer();
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, themeSupport.getLineColor(i));
        }
    }

    @Override
    public void onDomainChange() {
        DiscreteDisplayDomain domain = isBaseValues()
                ? provider.getDiscreteDisplayDomain(getPoints())
                : provider.getDiscreteDisplayDomain((int) getMinX(), (int) getMaxX());

        seriesCollection.removeAllSeries();

        String[] components = provider.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (provider.isDefined(i)) {
                double[] data = provider.getDataArray(i, domain);
                XYSeries series = new XYSeries(components[i]);
                for (int j = 0; j < data.length; j++) {
                    series.add(domain.x(j), data[j]);
                }
                seriesCollection.addSeries(series);
            }
        }

        int fx = getFactorX();
        chartPanel.getChart().getXYPlot().getRenderer().setBaseShape(new Rectangle2D.Double(-2 * fx, -2 * fx, 4 * fx, 4 * fx));
        chartPanel.getChart().getLegend().setVisible(seriesCollection.getSeriesCount() > 1);

        configureAxis();
        onColorSchemeChange();
        onFocusChange();
    }

    @Override
    protected void onFocusChange() {
        int focusIndex = getFocusIndex();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chartPanel.getChart().getXYPlot().getRenderer();
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            renderer.setSeriesShapesFilled(i, focusIndex == i);
        }
    }
    //</editor-fold>

    private static JFreeChart createScatterViewChart(XYDataset dataset) {
        JFreeChart result = ChartFactory.createScatterPlot("", "", "", dataset, PlotOrientation.VERTICAL, true, false, false);
        result.setPadding(TsCharts.CHART_PADDING);
        result.getTitle().setFont(TsCharts.CHART_TITLE_FONT);
        result.getLegend().setFrame(BlockBorder.NONE);
        result.getLegend().setBackgroundPaint(null);

        XYPlot plot = result.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setAutoPopulateSeriesShape(false);
        renderer.setAutoPopulateSeriesFillPaint(false);
        renderer.setBaseShapesFilled(false);
        plot.setRenderer(renderer);

        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setRangeAxis(rangeAxis);

        NumberAxis domainAxis = new NumberAxis();
        domainAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setDomainAxis(domainAxis);

        return result;
    }

    private static JMenu buildMenu(ChartPanel chartPanel) {
        JMenu result = new JMenu();

        result.add(new CustomCommand().toAction(chartPanel)).setText("Copy all visible");

        JMenu export = new JMenu("Export image to");
        export.add(ChartCommand.printImage().toAction(chartPanel)).setText("Printer...");
        export.add(ChartCommand.copyImage().toAction(chartPanel)).setText("Clipboard");
        export.add(ChartCommand.saveImage().toAction(chartPanel)).setText("File...");
        result.add(export);

        return result;
    }

    private static class CustomCommand extends MatrixChartCommand {

        @Override
        protected Matrix toMatrix(ChartPanel chartPanel) {
            XYDataset dataset = chartPanel.getChart().getXYPlot().getDataset(0);
            Matrix result = new Matrix(dataset.getItemCount(0), dataset.getSeriesCount() + 1);
            for (int i = 0; i < result.getRowsCount(); i++) {
                result.set(i, 0, dataset.getXValue(0, i));
                for (int j = 0; j < dataset.getSeriesCount(); j++) {
                    result.set(i, j + 1, dataset.getYValue(j, i));
                }
            }
            return result;
        }

        @Override
        public boolean isEnabled(ChartPanel chartPanel) {
            XYPlot plot = chartPanel.getChart().getXYPlot();
            return plot.getDatasetCount() > 0 && plot.getDataset(0).getSeriesCount() > 0;
        }
    }
}
