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
package ec.nbdemetra.benchmarking.calendarization;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.ATsControl;
import ec.ui.chart.TsCharts;
import ec.ui.chart.TsXYDatasets;
import ec.ui.interfaces.ITsChart.LinesThickness;
import ec.ui.view.JChartPanel;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.Charts;
import java.awt.BorderLayout;
import java.awt.Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * Chart used to display results of the Calendarization
 *
 * @author Mats Maggi
 */
public final class CalendarizationChartView extends ATsControl {

    private static final int DIFF_INDEX = 0;
    private static final int DAILY_INDEX = 1;
    private static final int SMOOTH_INDEX = 2;
    private static final KnownColor SMOOTH_COLOR = KnownColor.RED;
    private static final KnownColor DAILY_COLOR = KnownColor.GRAY;
    private static final KnownColor DIFF_COLOR = KnownColor.BLUE;
    private final JChartPanel chartPanel;

    /**
     * Constructs a new Chart to display Calendarization results
     *
     * @param title Title of the chart
     */
    public CalendarizationChartView(String title) {
        chartPanel = new JChartPanel(createChart(title));
        Charts.avoidScaling(chartPanel);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);

        onDataFormatChange();
        onColorSchemeChange();
    }

    private static JFreeChart createChart(String title) {
        JFreeChart result = ChartFactory.createXYLineChart("", "", "", Charts.emptyXYDataset(), PlotOrientation.VERTICAL, false, false, false);
        result.setPadding(TsCharts.CHART_PADDING);

        result.setTitle(new TextTitle(title, new Font("SansSerif", Font.PLAIN, 12)));

        XYPlot plot = result.getXYPlot();
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        LinesThickness linesThickness = LinesThickness.Thin;

        XYLineAndShapeRenderer daily = new XYLineAndShapeRenderer(true, false);
        daily.setAutoPopulateSeriesPaint(false);

        daily.setAutoPopulateSeriesStroke(false);
        daily.setBaseStroke(TsCharts.getStrongStroke(linesThickness));
        plot.setRenderer(DAILY_INDEX, daily);

        XYDifferenceRenderer difference = new XYDifferenceRenderer();
        difference.setAutoPopulateSeriesPaint(false);
        difference.setAutoPopulateSeriesStroke(false);
        difference.setBaseStroke(TsCharts.getNormalStroke(linesThickness));
        plot.setRenderer(DIFF_INDEX, difference);

        XYLineAndShapeRenderer smooth = new XYLineAndShapeRenderer(true, false);
        smooth.setAutoPopulateSeriesPaint(false);
        smooth.setAutoPopulateSeriesStroke(false);
        smooth.setBaseStroke(TsCharts.getStrongStroke(linesThickness));
        plot.setRenderer(SMOOTH_INDEX, smooth);

        DateAxis domainAxis = new DateAxis();
        domainAxis.setTickMarkPosition(DateTickMarkPosition.START);
        domainAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setDomainAxis(domainAxis);

        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setRangeAxis(rangeAxis);

        return result;
    }

    /**
     * Sets data to display on the graph. This method is used to add aggregates
     * data to the graph.
     *
     * @param serie Aggregrated serie of data
     * @param lower Aggregated serie of data lowered by the standard deviation
     * @param upper Aggregated serie of data increased by the standard deviation
     */
    public void setData(TsData serie, TsData lower, TsData upper) {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        chartPanel.getChart().setTitle(new TextTitle("Aggregated Data", new Font("SansSerif", Font.PLAIN, 12)));

        plot.setDataset(SMOOTH_INDEX, TsXYDatasets.from("series", serie));
        plot.setDataset(DIFF_INDEX, TsXYDatasets.builder().add("lower", lower).add("upper", upper).build());

        onDataFormatChange();
        onColorSchemeChange();
    }

    /**
     * Sets data to display on the graph. This method is used to add smoothed
     * and given periods data to the graph.
     *
     * @param days All the given periods
     * @param smooth Serie of calculated smoothed daily data
     * @param smoothDevs 2 smoothed data series : 1) smoothed data lowered by
     * the standard deviation and 2) smoothed data increased by the standard
     * deviation
     */
    public void setData(TimeSeriesCollection days, TimeSeries smooth, TimeSeriesCollection smoothDevs) {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        chartPanel.getChart().setTitle(new TextTitle("Smoothed Data", new Font("SansSerif", Font.PLAIN, 12)));

        plot.setDataset(DAILY_INDEX, days);
        plot.setDataset(SMOOTH_INDEX, new TimeSeriesCollection(smooth));
        plot.setDataset(DIFF_INDEX, smoothDevs);

        onDataFormatChange();
        onColorSchemeChange();
    }

    /**
     * Clears the data on the graph
     */
    public void clear() {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        plot.setDataset(DAILY_INDEX, Charts.emptyXYDataset());
        plot.setDataset(SMOOTH_INDEX, Charts.emptyXYDataset());
        plot.setDataset(DIFF_INDEX, Charts.emptyXYDataset());
    }

    @Override
    protected void onDataFormatChange() {
        DateAxis domainAxis = (DateAxis) chartPanel.getChart().getXYPlot().getDomainAxis();
        try {
            domainAxis.setDateFormatOverride(themeSupport.getDataFormat().newDateFormat());
        } catch (IllegalArgumentException ex) {
            // do nothing?
        }
    }

    @Override
    protected void onColorSchemeChange() {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        plot.setBackgroundPaint(themeSupport.getPlotColor());
        plot.setDomainGridlinePaint(themeSupport.getGridColor());
        plot.setRangeGridlinePaint(themeSupport.getGridColor());
        chartPanel.getChart().setBackgroundPaint(themeSupport.getBackColor());

        XYLineAndShapeRenderer main = (XYLineAndShapeRenderer) plot.getRenderer(DAILY_INDEX);
        main.setBasePaint(themeSupport.getLineColor(DAILY_COLOR));

        XYDifferenceRenderer difference = ((XYDifferenceRenderer) plot.getRenderer(DIFF_INDEX));
        difference.setPositivePaint(themeSupport.getAreaColor(DIFF_COLOR));
        difference.setNegativePaint(themeSupport.getAreaColor(DIFF_COLOR));
        difference.setBasePaint(themeSupport.getLineColor(DIFF_COLOR));

        XYLineAndShapeRenderer smooth = (XYLineAndShapeRenderer) plot.getRenderer(SMOOTH_INDEX);
        smooth.setBasePaint(themeSupport.getLineColor(SMOOTH_COLOR));
    }
}
