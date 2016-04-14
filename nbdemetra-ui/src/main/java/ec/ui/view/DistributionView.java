/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.ui.view;

import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.data.IReadDataBlock;
import ec.tstoolkit.dstats.BoundaryType;
import ec.tstoolkit.dstats.IContinuousDistribution;
import ec.ui.ATsControl;
import ec.ui.chart.TsCharts;
import ec.ui.interfaces.IReadDataBlockView;
import ec.ui.interfaces.IColorSchemeAble;
import ec.ui.interfaces.ITsChart.LinesThickness;
import ec.util.chart.ColorScheme;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.ChartCommand;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.ext.MatrixChartCommand;
import java.awt.BorderLayout;
import java.text.DecimalFormat;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.function.Function2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Demortier & BAYENSK
 */
public class DistributionView extends ATsControl implements IReadDataBlockView, IColorSchemeAble {

    // CONSTANTS
    protected static final int HISTOGRAM_INDEX = 1;
    protected static final int DISTRIBUTION_INDEX = 0;
    protected static final KnownColor HISTOGRAM_COLOR = KnownColor.BLUE;
    protected static final KnownColor DISTRIBUTION_COLOR = KnownColor.RED;

    // PROPERTIES DEFINITION
    public static final String L_BOUND_PROPERTY = "lBound";
    public static final String R_BOUND_PROPERTY = "rBound";
    public static final String DISTRIBUTION_PROPERTY = "distribution";
    public static final String ADJUST_DISTRIBUTION_PROPERTY = "adjustDistribution";
    public static final String H_COUNT_PROPERTY = "hCount";
    public static final String DATA_PROPERTY = "data";

    // DEFAULT PROPERTIES
    protected static final double DEFAULT_L_BOUND = 0;
    protected static final double DEFAULT_R_BOUND = 0;
    protected static final IContinuousDistribution DEFAULT_DISTRIBUTION = null;
    protected static final boolean DEFAULT_ADJUST_DISTRIBUTION = true;
    protected static final int DEFAULT_H_COUNT = 0;
    protected static final double[] DEFAULT_DATA = null;

    // PROPERTIES
    protected double lBound;
    protected double rBound;
    protected IContinuousDistribution distribution;
    protected boolean adjustDistribution;
    protected int hCount;
    protected double[] data;

    // OTHER
    protected final ChartPanel chartPanel;

    public DistributionView() {
        this.lBound = DEFAULT_L_BOUND;
        this.rBound = DEFAULT_R_BOUND;
        this.distribution = DEFAULT_DISTRIBUTION;
        this.adjustDistribution = DEFAULT_ADJUST_DISTRIBUTION;
        this.hCount = DEFAULT_H_COUNT;
        this.data = DEFAULT_DATA;

        this.chartPanel = new ChartPanel(createDistributionViewChart());
        Charts.avoidScaling(chartPanel);

        onDataFormatChange();
        onColorSchemeChange();
        onComponentPopupMenuChange();
        enableProperties();

        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case L_BOUND_PROPERTY:
                    onDataChange();
                    break;
                case R_BOUND_PROPERTY:
                    onDataChange();
                    break;
                case DISTRIBUTION_PROPERTY:
                    onDataChange();
                    break;
                case ADJUST_DISTRIBUTION_PROPERTY:
                    onDataChange();
                    break;
                case H_COUNT_PROPERTY:
                    onDataChange();
                    break;
                case DATA_PROPERTY:
                    onDataChange();
                    break;
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    @Override
    protected void onDataFormatChange() {
        // do nothing
    }

    @Override
    protected void onColorSchemeChange() {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        plot.setBackgroundPaint(themeSupport.getPlotColor());
        plot.setDomainGridlinePaint(themeSupport.getGridColor());
        plot.setRangeGridlinePaint(themeSupport.getGridColor());
        chartPanel.getChart().setBackgroundPaint(themeSupport.getBackColor());

        plot.getRenderer(HISTOGRAM_INDEX).setBasePaint(themeSupport.getAreaColor(HISTOGRAM_COLOR));
        plot.getRenderer(HISTOGRAM_INDEX).setBaseOutlinePaint(themeSupport.getLineColor(HISTOGRAM_COLOR));
        plot.getRenderer(DISTRIBUTION_INDEX).setBasePaint(themeSupport.getLineColor(DISTRIBUTION_COLOR));
    }

    protected void onDataChange() {
        XYPlot plot = chartPanel.getChart().getXYPlot();

        if (data != DEFAULT_DATA) {
            DescriptiveStatistics stats = new DescriptiveStatistics(new DataBlock(data));

            double m = 0, M = 0, dv = 1;
            if (adjustDistribution && distribution != DEFAULT_DISTRIBUTION) {
                m = stats.getAverage();
                M = distribution.getExpectation();
                double v = stats.getVar();
                double V = distribution.getVariance();
                dv = Math.sqrt(v / V);
            }

            final double xmin = stats.getMin() < lBound ? stats.getMin() : lBound;
            final double xmax = stats.getMax() > rBound ? stats.getMax() : rBound;
            final int n = hCount != 0 ? hCount : (int) Math.ceil(Math.sqrt(stats.getObservationsCount()));
            final double xstep = (xmax - xmin) / n;

            // distribution >
            if (distribution != DEFAULT_DISTRIBUTION) {
                Function2D density = distribution::getDensity;

                final double zmin = distribution.hasLeftBound() != BoundaryType.None ? distribution.getLeftBound() : ((xmin - xstep - m) / dv + M);
                final double zmax = distribution.hasRightBound() != BoundaryType.None ? distribution.getRightBound() : ((xmax + xstep - m) / dv + M);

                // TODO: create IDistribution#getName() method
                String name = distribution.getClass().getSimpleName();

                ((XYLineAndShapeRenderer) plot.getRenderer(DISTRIBUTION_INDEX)).setLegendItemToolTipGenerator(getTooltipGenerator(distribution));
                plot.setDataset(DISTRIBUTION_INDEX, DatasetUtilities.sampleFunction2D(density, zmin, zmax, n, name));
            } else {
                plot.setDataset(DISTRIBUTION_INDEX, Charts.emptyXYDataset());
            }
            // < distribution

            // histogram >
            XYSeries hSeries = new XYSeries("");
            double nobs = stats.getObservationsCount();
            for (int i = 0; i <= n; ++i) {
                double x0 = xmin + i * xstep;
                double x1 = x0 + xstep;
                double y = stats.countBetween(x0, x1) / (nobs * xstep / dv);
                hSeries.add(((x0 + x1) / 2 - m) / dv + M, y);
            }

            plot.setDataset(HISTOGRAM_INDEX, new XYBarDataset(new XYSeriesCollection(hSeries), xstep / dv + M));
            // < histogram
        } else {
            plot.setDataset(HISTOGRAM_INDEX, Charts.emptyXYDataset());
            plot.setDataset(DISTRIBUTION_INDEX, Charts.emptyXYDataset());
        }

        onColorSchemeChange();
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildMenu(chartPanel).getPopupMenu());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="GETTERS/SETTERS">
    public double getLBound() {
        return lBound;
    }

    public void setLBound(double lBound) {
        double old = this.lBound;
        this.lBound = lBound;
        firePropertyChange(L_BOUND_PROPERTY, old, this.lBound);
    }

    public double getRBound() {
        return rBound;
    }

    public void setRBound(double rBound) {
        double old = this.rBound;
        this.rBound = rBound;
        firePropertyChange(R_BOUND_PROPERTY, old, this.rBound);
    }

    public IContinuousDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(IContinuousDistribution distribution) {
        IContinuousDistribution old = this.distribution;
        this.distribution = distribution != null ? distribution : DEFAULT_DISTRIBUTION;
        firePropertyChange(DISTRIBUTION_PROPERTY, old, this.distribution);
    }

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        double[] old = this.data;
        this.data = data != null ? data : DEFAULT_DATA;
        firePropertyChange(DATA_PROPERTY, old, this.data);
    }

    public boolean isAdjustDistribution() {
        return adjustDistribution;
    }

    public void setAdjustDistribution(boolean adjustDistribution) {
        boolean old = this.adjustDistribution;
        this.adjustDistribution = adjustDistribution;
        firePropertyChange(ADJUST_DISTRIBUTION_PROPERTY, old, this.adjustDistribution);
    }

    public int getHCount() {
        return hCount;
    }

    public void setHCount(int hCount) {
        int old = this.hCount;
        this.hCount = hCount >= 0 ? hCount : DEFAULT_H_COUNT;
        firePropertyChange(H_COUNT_PROPERTY, old, this.hCount);
    }

    @Override
    public ColorScheme getColorScheme() {
        return themeSupport.getLocalColorScheme();
    }

    @Override
    public void setColorScheme(ColorScheme theme) {
        themeSupport.setLocalColorScheme(theme);
    }
    //</editor-fold>

    @Override
    public void setDataBlock(IReadDataBlock data) {
        double[] tmp;
        if (data == null) {
            tmp = null;
        } else {
            tmp = new double[data.getLength()];
            data.copyTo(tmp, 0);
        }
        setData(tmp);
    }

    @Override
    public void reset() {
        setData((double[]) null);
        setDistribution(null);
    }

    private static JFreeChart createDistributionViewChart() {
        XYPlot plot = new XYPlot();

        XYLineAndShapeRenderer dRenderer = new XYSplineRenderer();
        dRenderer.setBaseShapesVisible(false);
        dRenderer.setAutoPopulateSeriesPaint(false);
        dRenderer.setAutoPopulateSeriesStroke(false);
        dRenderer.setBaseStroke(TsCharts.getStrongStroke(LinesThickness.Thin));
        dRenderer.setDrawSeriesLineAsPath(true); // not sure if useful
        plot.setDataset(DISTRIBUTION_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(DISTRIBUTION_INDEX, dRenderer);

        XYBarRenderer hRenderer = new XYBarRenderer();
        hRenderer.setShadowVisible(false);
        hRenderer.setDrawBarOutline(true);
        hRenderer.setAutoPopulateSeriesPaint(false);
        hRenderer.setAutoPopulateSeriesOutlinePaint(false);
        hRenderer.setBaseSeriesVisibleInLegend(false);
        plot.setDataset(HISTOGRAM_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(HISTOGRAM_INDEX, hRenderer);

        NumberAxis domainAxis = new NumberAxis();
        domainAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setDomainAxis(domainAxis);
        plot.setDomainGridlinesVisible(false);

        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        rangeAxis.setTickUnit(new NumberTickUnit(0.05));
        rangeAxis.setNumberFormatOverride(new DecimalFormat("0.###"));
        plot.setRangeAxis(rangeAxis);

        plot.mapDatasetToDomainAxis(0, 0);
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToDomainAxis(1, 0);
        plot.mapDatasetToRangeAxis(1, 0);

        JFreeChart result = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        result.setPadding(TsCharts.CHART_PADDING);
        result.getTitle().setFont(TsCharts.CHART_TITLE_FONT);
        result.getLegend().setFrame(BlockBorder.NONE);
        result.getLegend().setBackgroundPaint(null);
        return result;
    }

    protected static XYSeriesLabelGenerator getTooltipGenerator(final IContinuousDistribution distribution) {
        return (XYDataset dataset, int series) -> distribution.getDescription();
    }

    private static JMenu buildMenu(ChartPanel chartPanel) {
        JMenu result = new JMenu();

        result.add(MatrixChartCommand.copySeries(DISTRIBUTION_INDEX, 0).toAction(chartPanel)).setText("Copy distribution");
        result.add(MatrixChartCommand.copySeries(HISTOGRAM_INDEX, 0).toAction(chartPanel)).setText("Copy histogram");

        JMenu export = new JMenu("Export image to");
        export.add(ChartCommand.printImage().toAction(chartPanel)).setText("Printer...");
        export.add(ChartCommand.copyImage().toAction(chartPanel)).setText("Clipboard");
        export.add(ChartCommand.saveImage().toAction(chartPanel)).setText("File...");
        result.add(export);

        return result;
    }
}
