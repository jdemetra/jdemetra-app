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

import demetra.ui.TsManager;
import demetra.ui.components.parts.HasColorScheme;
import demetra.ui.components.parts.HasTs;
import demetra.ui.components.TimeSeriesComponent;
import demetra.ui.components.parts.HasColorSchemeResolver;
import demetra.ui.components.parts.HasColorSchemeSupport;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.ui.chart.BasicXYDataset;
import demetra.ui.jfreechart.TsCharts;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.SwingColorSchemeSupport;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

/**
 * Component displaying data regarding a model stability
 *
 * @author Mats Maggi
 */
public final class StabilityView extends JComponent implements TimeSeriesComponent, HasTs, HasColorScheme {

    private static final int POINTS_INDEX = 0;
    private static final int MEAN_INDEX = 1;
    private static final int SMOOTH_INDEX = 2;
    private static final Stroke MARKER_STROKE = new BasicStroke(0.5f); //, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f));
    private static final Paint MARKER_PAINT = Color.GRAY;
    private static final float MARKER_ALPHA = 1f;

    private final Map<Bornes, Graphs> graphs_;
    private JFreeChart mainChart;
    private JFreeChart detailChart;
    private JChartPanel panel;
    private final XYLineAndShapeRenderer meanRenderer;
    private final XYLineAndShapeRenderer pointsRenderer;
    private final XYLineAndShapeRenderer smoothRenderer;
    private List<StabilityViewItem> items = new ArrayList<>();
    private JPanel cards;
    private JPanel errorPanel;
    private JLabel errorLabel;
    private final String MAIN_PANEL = "mainPanel";
    private final String ERROR_PANEL = "errorPanel";
    private int indexSelected = -1;

    @lombok.experimental.Delegate
    private final HasTs m_ts = HasTs.of(this::firePropertyChange, TsManager.getDefault());

    @lombok.experimental.Delegate
    private final HasColorScheme colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);

    private final HasColorSchemeResolver colorSchemeResolver = new HasColorSchemeResolver(colorScheme, this::onColorSchemeChange);

    public StabilityView() {
        super();

        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();

        setLayout(new BorderLayout());

        this.graphs_ = new LinkedHashMap<>();

        meanRenderer = new XYLineAndShapeRenderer(true, false);
        meanRenderer.setAutoPopulateSeriesPaint(false);
        meanRenderer.setBasePaint(themeSupport.getLineColor(KnownColor.RED));

        pointsRenderer = new XYLineAndShapeRenderer(false, true);
        pointsRenderer.setAutoPopulateSeriesPaint(false);
        pointsRenderer.setAutoPopulateSeriesShape(false);
        pointsRenderer.setBaseShape(new Ellipse2D.Double(-2, -2, 4, 4));
        pointsRenderer.setBasePaint(themeSupport.getLineColor(KnownColor.BLUE));
        pointsRenderer.setBaseShapesFilled(false);

        smoothRenderer = new XYLineAndShapeRenderer(true, false);
        smoothRenderer.setAutoPopulateSeriesPaint(false);
        smoothRenderer.setBasePaint(themeSupport.getLineColor(KnownColor.GREEN));

        mainChart = createChart();
        detailChart = createChart();

        panel = new JChartPanel(null);

        errorPanel = new JPanel(new BorderLayout());
        errorLabel = new JLabel();
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setFont(errorLabel.getFont().deriveFont(errorLabel.getFont().getSize2D() * 3 / 2));
        errorPanel.add(errorLabel, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                indexSelected = -1;
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    double x = panel.getChartX(e.getX());
                    Graphs g = null;
                    for (Bornes b : graphs_.keySet()) {
                        indexSelected++;
                        if (x >= b.min_ && x <= b.max_) {
                            g = graphs_.get(b);
                            break;
                        }
                    }
                    if (g == null) {
                        return;
                    }

                    showDetail(g);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    showMain();
                    indexSelected = -1;
                }
            }
        });

        StandardXYToolTipGenerator generator = new StandardXYToolTipGenerator() {
            final DecimalFormat format = new DecimalFormat("0.0000");

            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                try {
                    StabilityViewItem i = items.get(indexSelected == -1 ? series : indexSelected);
                    int cpt = 0;
                    for (Map.Entry<TsDomain, Double> e : i.data.entrySet()) {
                        if (cpt == item) {
                            TsDomain dom = e.getKey();
                            return "(" + dom.getStart().toString() + ", " + dom.getEnd().toString() + ") : " + format.format(e.getValue());
                        }
                        cpt++;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                return null;
            }
        };
        pointsRenderer.setBaseToolTipGenerator(generator);
        cards = new JPanel(new CardLayout());

        cards.add(MAIN_PANEL, panel);

        cards.add(ERROR_PANEL, errorPanel);

        add(cards, BorderLayout.CENTER);

        onColorSchemeChange();
    }

    private void showMain() {
        panel.setChart(mainChart);
        onColorSchemeChange();
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, MAIN_PANEL);
    }

    /**
     * Displays an exception message in place of the graph
     */
    public void showException(String msg) {
        errorLabel.setText(msg);
        CardLayout cl = (CardLayout) cards.getLayout();
        cl.show(cards, ERROR_PANEL);
    }

    private void showDetail(Graphs g) {
        XYPlot plot = detailChart.getXYPlot();

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelPaint(Color.GRAY);
        plot.setRangeAxis(yAxis);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelPaint(Color.GRAY);
        xAxis.setTickUnit(new NumberTickUnit(1), true, false);
        xAxis.setRange(-0.5, ((double) g.getMaxElements()) - 0.5);
        plot.setDomainAxis(xAxis);

        plot.setDataset(MEAN_INDEX, new BasicXYDataset(Collections.singletonList(g.S1_)));
        plot.setDataset(POINTS_INDEX, new BasicXYDataset(Collections.singletonList(g.S2_)));
        plot.setDataset(SMOOTH_INDEX, new BasicXYDataset(Collections.singletonList(g.S3_)));

        rescaleAxis((NumberAxis) plot.getRangeAxis());

        detailChart.setTitle(g.label_);
        panel.setChart(detailChart);
        panel.setToolTipText("Right click to show complete data");
        onColorSchemeChange();
    }

    /**
     * Resets the view and all its data
     */
    public void reset() {
        items.clear();
        graphs_.clear();
        panel.setChart(null);
    }

    private void rescaleAxis(NumberAxis axis) {
        axis.setAutoRangeIncludesZero(false);
    }

    private JFreeChart createChart() {
        XYPlot plot = new XYPlot();

        plot.setDataset(SMOOTH_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(SMOOTH_INDEX, smoothRenderer);
        plot.mapDatasetToDomainAxis(SMOOTH_INDEX, 0);
        plot.mapDatasetToRangeAxis(SMOOTH_INDEX, 0);

        plot.setDataset(MEAN_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(MEAN_INDEX, meanRenderer);
        plot.mapDatasetToDomainAxis(MEAN_INDEX, 0);
        plot.mapDatasetToRangeAxis(MEAN_INDEX, 0);

        plot.setDataset(POINTS_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(POINTS_INDEX, pointsRenderer);
        plot.mapDatasetToDomainAxis(POINTS_INDEX, 0);
        plot.mapDatasetToRangeAxis(POINTS_INDEX, 0);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        JFreeChart result = new JFreeChart("", TsCharts.CHART_TITLE_FONT, plot, false);
        result.setPadding(TsCharts.CHART_PADDING);
        return result;
    }

    private void add(StabilityViewItem item, boolean redraw) {
        items.add(item);
        if (redraw) {
            display();
        }
    }

    /**
     * Add an item to display in the graph
     *
     * @param name Item name
     * @param data Data associated with the variable given
     * @param sdata Smoothed data associated with the variable given
     * @param redraw True if the view as to be redrawn after the insertion
     */
    public void add(String name, Map<TsDomain, Double> data, double[] sdata, boolean redraw) {
        add(new StabilityViewItem(name, data, sdata), redraw);
    }

    /**
     * Processes and displays the inserted items
     */
    public void display() {
        BasicXYDataset pointsDataset = new BasicXYDataset();
        BasicXYDataset meanDataset = new BasicXYDataset();
        BasicXYDataset smoothDataset = new BasicXYDataset();
        int np = items.get(0).getDataArray().length - 1;
        double xstart = -0.4;
        double xend = 0.4;
        final double xstep = 0.8 / np;
        boolean smoothData;

        String[] itemNames = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            itemNames[i] = items.get(i).name;
        }

        for (int i = 0; i < items.size(); i++) {
            double x = xstart;
            StabilityViewItem it = items.get(i);
            double[] array = it.getDataArray();
            if (array != null && array.length > 0) {
                smoothData = false;

                int n = array.length;
                double m = it.getAverage();
                double[] meanX = {xstart, xend};
                double[] mean2X = {0, n - 1};
                double[] meanY = {m, m};
                double[] pointsX = new double[n], points2X = new double[n];
                double[] pointsY = new double[n], points2Y = new double[n];
                double[] smoothX = new double[n], smooth2X = new double[n];
                double[] smoothY = new double[n], smooth2Y = new double[n];

                // Inserts the data (points)
                for (int j = 0; j < n; j++) {
                    pointsX[j] = x;
                    pointsY[j] = array[j];
                    points2X[j] = j;
                    points2Y[j] = array[j];
                    x += xstep;
                }

                // Inserts the smoothed data if it's present
                if (it.smoothedData != null && it.smoothedData.length > 0) {
                    smoothData = true;
                    for (int j = 0; j < n; j++) {   // Add of points
                        smoothX[j] = x;
                        smoothY[j] = it.smoothedData[j];
                        smooth2X[j] = j;
                        smooth2Y[j] = it.smoothedData[j];
                        x += xstep;
                    }
                }

                BasicXYDataset.Series mean = BasicXYDataset.Series.of(itemNames[i], meanX, meanY);
                BasicXYDataset.Series mean2 = BasicXYDataset.Series.of(itemNames[i], mean2X, meanY);
                BasicXYDataset.Series points = BasicXYDataset.Series.of(itemNames[i], pointsX, pointsY);
                BasicXYDataset.Series points2 = BasicXYDataset.Series.of(itemNames[i], points2X, points2Y);
                BasicXYDataset.Series smooth = smoothData ? BasicXYDataset.Series.of(itemNames[i], smoothX, smoothY) : BasicXYDataset.Series.empty(itemNames[i]);
                BasicXYDataset.Series smooth2 = smoothData ? BasicXYDataset.Series.of(itemNames[i], smooth2X, smooth2Y) : BasicXYDataset.Series.empty(itemNames[i]);

                Bornes b = new Bornes(xstart, xend);
                Graphs g = new Graphs(mean2, points2, smooth2, itemNames[i]);
                graphs_.put(b, g);      // Map used to display detail chart on double click

                smoothDataset.addSeries(smooth);
                meanDataset.addSeries(mean);
                pointsDataset.addSeries(points);
            }

            xstart++;
            xend++;
        }

        XYPlot plot = mainChart.getXYPlot();
        configureAxis(plot);
        plot.setDataset(SMOOTH_INDEX, smoothDataset);
        plot.setDataset(MEAN_INDEX, meanDataset);
        plot.setDataset(POINTS_INDEX, pointsDataset);

        showMain();
    }

    private void configureAxis(XYPlot plot) {
        int nb = graphs_.size();
        List<String> names = new ArrayList<>();
        for (Map.Entry<Bornes, Graphs> entry : graphs_.entrySet()) {
            names.add(entry.getValue().label_);
        }

        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelPaint(Color.GRAY);
        xAxis.setTickUnit(new StabilityTickUnit(names));
        xAxis.setRange(-0.5, nb - 0.5);
        plot.setDomainAxis(xAxis);
        plot.setDomainGridlinesVisible(false);
        NumberAxis yaxis = new NumberAxis();
        rescaleAxis(yaxis);
        plot.setRangeAxis(yaxis);

        for (int i = 0; i < nb; i++) {
            ValueMarker marker = new ValueMarker(i + 0.5);
            marker.setStroke(MARKER_STROKE);
            marker.setPaint(MARKER_PAINT);
            marker.setAlpha(MARKER_ALPHA);
            plot.addDomainMarker(marker);
        }
    }

    private void onColorSchemeChange() {
        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();

        pointsRenderer.setBasePaint(themeSupport.getLineColor(KnownColor.BLUE));
        meanRenderer.setBasePaint(themeSupport.getLineColor(KnownColor.RED));
        smoothRenderer.setBasePaint(themeSupport.getLineColor(KnownColor.GREEN));

        XYPlot mainPlot = mainChart.getXYPlot();
        mainPlot.setBackgroundPaint(themeSupport.getPlotColor());
        mainPlot.setDomainGridlinePaint(themeSupport.getGridColor());
        mainPlot.setRangeGridlinePaint(themeSupport.getGridColor());
        mainChart.setBackgroundPaint(themeSupport.getBackColor());

        XYPlot detailPlot = detailChart.getXYPlot();
        detailPlot.setBackgroundPaint(themeSupport.getPlotColor());
        detailPlot.setDomainGridlinePaint(themeSupport.getGridColor());
        detailPlot.setRangeGridlinePaint(themeSupport.getGridColor());
        detailChart.setBackgroundPaint(themeSupport.getBackColor());

    }

    /**
     * Represents a range of data
     */
    static class Bornes {

        static final Bornes ZERO = new Bornes(0, 0);
        final double min_;
        final double max_;

        Bornes(double min, double max) {
            min_ = min;
            max_ = max;
        }
    }

    /**
     * Represents the 3 series (mean, data, smoothed data) of a given variable
     */
    static class Graphs {

        final BasicXYDataset.Series S1_;
        final BasicXYDataset.Series S2_;
        final BasicXYDataset.Series S3_;
        final String label_;

        Graphs(BasicXYDataset.Series s1, BasicXYDataset.Series s2, BasicXYDataset.Series s3, String label) {
            S1_ = s1;
            S2_ = s2;
            S3_ = s3;
            label_ = label;
        }

        int getMaxElements() {
            int elements = S1_.getItemCount();
            if (elements < S2_.getItemCount()) {
                elements = S2_.getItemCount();
            }
            if (elements < S3_.getItemCount()) {
                elements = S3_.getItemCount();
            }

            return elements;
        }
    }

    static class StabilityViewItem {

        private String name;
        private Map<TsDomain, Double> data;
        private double[] smoothedData;

        public StabilityViewItem(String name, Map<TsDomain, Double> data, double[] sdata) {
            this.name = name;
            this.data = data;
            this.smoothedData = sdata;
        }

        public double[] getDataArray() {
            double[] array = new double[data.size()];
            int i = 0;
            for (Map.Entry<TsDomain, Double> e : data.entrySet()) {
                array[i] = e.getValue();
                i++;
            }

            return array;
        }

        public double getAverage() {
            if (data == null) {
                return Double.NaN;
            } else {
                int n = 0;
                double s = 0;
                for (Map.Entry<TsDomain, Double> e : data.entrySet()) {
                    if (!Double.isNaN(e.getValue())) {
                        s += e.getValue();
                        ++n;
                    }

                }
                if (n == 0) {
                    return Double.NaN;
                } else {
                    return s / n;
                }
            }
        }
    }
}
