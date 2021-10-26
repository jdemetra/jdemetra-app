/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import demetra.bridge.TsConverter;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.uihelper.DiscreteDisplayDomain;
import ec.tstoolkit.uihelper.IDiscreteInformationProvider;
import demetra.desktop.jfreechart.TsCharts;
import ec.util.chart.swing.ChartCommand;
import ec.util.chart.swing.SwingColorSchemeSupport;
import demetra.desktop.jfreechart.MatrixChartCommand;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import javax.swing.JMenu;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Kristof Bayens
 */
public class FilterView extends AChartView {

    // CONSTANTS
    public static final double DEFAULT_MIN_X = -35;
    public static final double DEFAULT_MAX_X = 35;
    public static final double DEFAULT_MIN_Y = -0.2;
    public static final double DEFAULT_MAX_Y = 1;
    public static final NumberTickUnit DEFAULT_TICKUNIT_X = new NumberTickUnit(5);
    public static final NumberTickUnit DEFAULT_TICKUNIT_Y = new NumberTickUnit(0.1);
    public static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat("0.##");
    protected static final int DOT_INDEX = 0;
    protected static final int BAR_INDEX = 1;
    // OTHER
    protected final IDiscreteInformationProvider provider;

    public FilterView(IDiscreteInformationProvider provider) {
        super(100, PlotOrientation.VERTICAL, DEFAULT_MIN_X, DEFAULT_MAX_X, DEFAULT_MIN_Y, DEFAULT_MAX_Y, DEFAULT_TICKUNIT_X, DEFAULT_TICKUNIT_Y, DEFAULT_FORMAT);
        setZoomable(false);
        this.provider = provider;
        this.chartPanel.setChart(createFilterView(seriesCollection));
        onDomainChange();
        chartPanel.setPopupMenu(buildMenu(chartPanel).getPopupMenu());
    }

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    @Override
    protected void onColorSchemeChange() {
        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();

        XYPlot plot = chartPanel.getChart().getXYPlot();
        plot.setBackgroundPaint(themeSupport.getPlotColor());
        plot.setDomainGridlinePaint(themeSupport.getGridColor());
        plot.setRangeGridlinePaint(themeSupport.getGridColor());
        chartPanel.getChart().setBackgroundPaint(themeSupport.getBackColor());

        XYLineAndShapeRenderer dotRenderer = (XYLineAndShapeRenderer) plot.getRenderer(DOT_INDEX);
        XYBarRenderer barRenderer = (XYBarRenderer) plot.getRenderer(BAR_INDEX);
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            Color color = themeSupport.getLineColor(i);
            dotRenderer.setSeriesPaint(i, color);
            barRenderer.setSeriesPaint(i, color);
        }
    }

    @Override
    protected void onDomainChange() {
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
        chartPanel.getChart().getXYPlot().getRenderer(DOT_INDEX).setBaseShape(new Ellipse2D.Double(-2 * fx, -2 * fx, 4 * fx, 4 * fx));
        chartPanel.getChart().getLegend().setVisible(seriesCollection.getSeriesCount() > 1);

        configureAxis();
        chartPanel.getChart().getXYPlot().getDomainAxis().setAutoTickUnitSelection(true);
        onColorSchemeChange();
        onFocusChange();
    }

    @Override
    protected void onFocusChange() {
        int focusIndex = getFocusIndex();
        XYPlot plot = chartPanel.getChart().getXYPlot();
        XYLineAndShapeRenderer dotRenderer = (XYLineAndShapeRenderer) plot.getRenderer(DOT_INDEX);
        //XYBarRenderer barRenderer = (XYBarRenderer) plot.getRenderer(BAR_INDEX);
        for (int i = 0; i < seriesCollection.getSeriesCount(); i++) {
            boolean hasFocus = i == focusIndex;
            dotRenderer.setSeriesShapesFilled(i, hasFocus);
            //barRenderer.setSeriesStroke(i, hasFocus ? Utils.THICK_STROKE : Utils.STROKE);
        }
    }
    //</editor-fold>

    private static JFreeChart createFilterView(XYDataset dataset) {
        XYPlot plot = new XYPlot();

        XYLineAndShapeRenderer dotRenderer = new XYLineAndShapeRenderer(false, true);
        dotRenderer.setAutoPopulateSeriesShape(false);
        dotRenderer.setAutoPopulateSeriesFillPaint(false);
        dotRenderer.setBaseShapesFilled(false);
        plot.setRenderer(DOT_INDEX, dotRenderer);
        plot.setDataset(DOT_INDEX, dataset);

        XYBarRenderer barRenderer = new XYBarRenderer();
        barRenderer.setShadowVisible(false);
        barRenderer.setBaseSeriesVisibleInLegend(false);
        plot.setRenderer(BAR_INDEX, barRenderer);
        plot.setDataset(BAR_INDEX, new XYBarDataset(dataset, 0.1));

        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setRangeAxis(rangeAxis);

        NumberAxis domainAxis = new NumberAxis();
        domainAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setDomainAxis(domainAxis);

        plot.mapDatasetToDomainAxis(DOT_INDEX, 0);
        plot.mapDatasetToRangeAxis(DOT_INDEX, 0);
        plot.mapDatasetToDomainAxis(BAR_INDEX, 0);
        plot.mapDatasetToRangeAxis(BAR_INDEX, 0);

        JFreeChart result = new JFreeChart("", TsCharts.CHART_TITLE_FONT, plot, true);
        result.setPadding(TsCharts.CHART_PADDING);
        result.getLegend().setFrame(BlockBorder.NONE);
        result.getLegend().setBackgroundPaint(null);
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
        protected demetra.math.matrices.Matrix toMatrix(ChartPanel chartPanel) {
            XYDataset dataset = chartPanel.getChart().getXYPlot().getDataset(0);
            Matrix result = new Matrix(dataset.getItemCount(0), dataset.getSeriesCount() + 1);
            for (int i = 0; i < result.getRowsCount(); i++) {
                result.set(i, 0, dataset.getXValue(0, i));
                for (int j = 0; j < dataset.getSeriesCount(); j++) {
                    result.set(i, j + 1, dataset.getYValue(j, i));
                }
            }
            return TsConverter.toMatrix(result);
        }

        @Override
        public boolean isEnabled(ChartPanel chartPanel) {
            XYPlot plot = chartPanel.getChart().getXYPlot();
            return plot.getDatasetCount() > 0 && plot.getDataset(0).getSeriesCount() > 0;
        }
    }
}
