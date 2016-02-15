/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import ec.nbdemetra.ui.NbComponents;
import ec.satoolkit.DecompositionMode;
import ec.tss.html.implementation.HtmlSlidingSpanDocument;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.modelling.ModellingDictionary;
import ec.tstoolkit.timeseries.analysis.DiagnosticInfo;
import ec.tstoolkit.timeseries.analysis.SlidingSpans;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.chart.TsXYDatasets;
import ec.ui.chart.TsCharts;
import ec.ui.interfaces.IColorSchemeAble;
import ec.nbdemetra.ui.ThemeSupport;
import ec.tss.datatransfer.TssTransferSupport;
import ec.ui.Disposables;
import ec.ui.view.tsprocessing.ITsViewToolkit;
import ec.ui.view.tsprocessing.TsViewToolkit;
import ec.util.chart.ColorScheme;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.ChartCommand;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.ext.MatrixChartCommand;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JSplitPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author Kristof Bayens
 */
public class SlidingSpanView extends JComponent implements IColorSchemeAble {

    // CONSTANTS
    protected static final int N = 18;
    // PROPERTIES DEFINITION
    public static final String SLIDING_SPANS_PROPERTY = "slidingSpans";
    public static final String INFO_NAME_PROPERTY = "infoName";
    public static final String THRESHOLD_PROPERTY = "threshold";
    public static final String INFO_PROPERTY = "info";
    // DEFAULT PROPERTIES
    protected static final double DEFAULT_THRESHOLD = 3;
    protected static final DiagnosticInfo DEFAULT_INFO = DiagnosticInfo.RelativeDifference;
    // PROPERTIES
    protected SlidingSpans<?> slidingSpans;
    protected String infoName;
    protected double threshold;
    protected DiagnosticInfo info;
    protected final ThemeSupport themeSupport;
    // OTHER
    private final JChartPanel seriesPanel;
    private final JChartPanel distributionPanel;
    private final Box documentPanel;
    private ITsViewToolkit toolkit_ = TsViewToolkit.getInstance();

    public SlidingSpanView() {

        this.slidingSpans = null;
        this.infoName = null;
        this.threshold = DEFAULT_THRESHOLD;
        this.info = DEFAULT_INFO;
        this.themeSupport = new ThemeSupport() {
            @Override
            protected void colorSchemeChanged() {
                onColorSchemeChange();
            }
        };

        this.seriesPanel = new JChartPanel(createSeriesChart());
        Charts.avoidScaling(seriesPanel);
        this.distributionPanel = new JChartPanel(createDistributionChart());
        Charts.avoidScaling(distributionPanel);
        this.documentPanel = Box.createHorizontalBox();

        JSplitPane splitpane1 = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, distributionPanel, documentPanel);
        splitpane1.setDividerLocation(0.5);
        splitpane1.setResizeWeight(0.5);

        JSplitPane splitpane2 = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, true, seriesPanel, splitpane1);
        splitpane2.setDividerLocation(0.4);
        splitpane2.setResizeWeight(0.5);

        setLayout(new BorderLayout());
        add(splitpane2, BorderLayout.CENTER);

        themeSupport.register();

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case SLIDING_SPANS_PROPERTY:
                        onSlidingSpansChange();
                        break;
                    case INFO_NAME_PROPERTY:
                        onInfoNameChange();
                        break;
                    case THRESHOLD_PROPERTY:
                        onThresholdChange();
                        break;
                    case INFO_PROPERTY:
                        onInfoChange();
                        break;
                }
            }
        });

        seriesPanel.setPopupMenu(createSeriesMenu(seriesPanel).getPopupMenu());
        distributionPanel.setPopupMenu(createDistributionMenu(distributionPanel).getPopupMenu());
    }

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    protected void onSlidingSpansChange() {
        if (slidingSpans == null) {
            return;
        }

        clear();
        TsData data = slidingSpans.Statistics(infoName, info);
        if (data == null || data.getValues().getMissingValuesCount() == data.getValues().getLength()) {
            return;
        }

        DescriptiveStatistics stats = new DescriptiveStatistics(new DataBlock(data.getValues().internalStorage()));
        if (stats.isConstant()) {
            return;
        }

        DecompositionMode mode = slidingSpans.getReferenceInfo().getData(ModellingDictionary.MODE, DecompositionMode.class);

        showSeries(data, mode);
        showDistribution(stats, mode);
        showStatistics();

        onColorSchemeChange();
    }

    protected void onInfoNameChange() {
        onSlidingSpansChange();
    }

    protected void onThresholdChange() {
        onSlidingSpansChange();
    }

    protected void onInfoChange() {
        onSlidingSpansChange();
    }

    protected void onColorSchemeChange() {
        for (JChartPanel o : Arrays.asList(seriesPanel, distributionPanel)) {
            XYPlot plot = o.getChart().getXYPlot();
            plot.setBackgroundPaint(themeSupport.getPlotColor());
            plot.setDomainGridlinePaint(themeSupport.getGridColor());
            plot.setRangeGridlinePaint(themeSupport.getGridColor());
            plot.getRenderer().setBasePaint(themeSupport.getAreaColor(KnownColor.BLUE));
            plot.getRenderer().setBaseOutlinePaint(themeSupport.getLineColor(KnownColor.BLUE));
            o.getChart().setBackgroundPaint(themeSupport.getBackColor());
        }
    }
    //</editor-fold>

    public void setTsToolkit(ITsViewToolkit toolkit) {
        toolkit_ = toolkit;
    }

    public ITsViewToolkit getTsToolkit() {
        return toolkit_;
    }

    private Range calcRange(double[] values) {
        double min = Double.NEGATIVE_INFINITY, max = -Double.POSITIVE_INFINITY;

        DescriptiveStatistics stats = new DescriptiveStatistics(new DataBlock(values));
        double smin = stats.getMin(), smax = stats.getMax();
        if (Double.isInfinite(min) || smin < min) {
            min = smin;
        }
        if (Double.isInfinite(max) || smax > max) {
            max = smax;
        }

        if (Double.isInfinite(max) || Double.isInfinite(min)) {
            return new Range(0, 1);
        }
        double length = max - min;
        if (length == 0) {
            return new Range(0, 1);
        } else {
            //double eps = length * .05;
            //return new Range(min - eps, max + eps);
            return new Range(min, max);
        }
    }

    private double calcTick(Range rng) {
        double tick = 0;
        double avg = (rng.getUpperBound() - rng.getLowerBound()) / 6;
        for (int i = 0; i < 10 && tick == 0; i++) {
            double power = Math.pow(10, i);
            if (avg > (0.1 * power) && avg <= (0.2 * power)) {
                tick = (0.2 * power);
            } else if (avg > (0.2 * power) && avg <= (0.5 * power)) {
                tick = (0.5 * power);
            } else if (avg > (0.5 * power) && avg <= (1 * power)) {
                tick = (1 * power);
            }
        }
        return tick;
    }

    private void showDistribution(DescriptiveStatistics stats, DecompositionMode mode) {
        DefaultXYDataset dataset = new DefaultXYDataset();

        double nobs = stats.getObservationsCount();
        double step = threshold / 6;
        double[] xvalues = new double[N];
        double[] values = new double[N];
        for (int i = 0; i < N; ++i) {
            xvalues[i] = step * (i + .5);
            values[i] = (stats.countBetween(i * step, (i + 1) * step) / nobs);
        }
        dataset.addSeries("", new double[][]{xvalues, values});

        XYPlot plot = distributionPanel.getChart().getXYPlot();
        plot.setDataset(dataset);

        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        switch (mode) {
            case Multiplicative:
                xAxis.setTickUnit(new NumberTickUnit(0.02), true, false);
                xAxis.setRange(0, 0.1);
                yAxis.setTickUnit(new PercentageTickUnit(0.05), true, false);
                break;
            case Additive:
                Range rng = calcRange(xvalues);
                xAxis.setTickUnit(new NumberTickUnit(calcTick(rng)), true, false);
                yAxis.setTickUnit(new PercentageTickUnit(0.05), true, false);
                break;
        }
    }

    private void showStatistics() {
        HtmlSlidingSpanDocument document = new HtmlSlidingSpanDocument(slidingSpans, infoName, info);
        document.setThreshold(threshold);
        Disposables.disposeAndRemoveAll(documentPanel).add(toolkit_.getHtmlViewer(document));
    }

    private void showSeries(TsData data, DecompositionMode mode) {
        // used by copy action
        seriesPanel.putClientProperty("TS_DATA", data);

        XYPlot plot = seriesPanel.getChart().getXYPlot();
        plot.setDataset(TsXYDatasets.from(infoName, data));
    }

    private void clear() {
        //dataChart_.getTsCollection().clear();
    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS/SETTERS">
    public String getInfoName() {
        return infoName;
    }

    public void setInfoName(String infoName) {
        String old = this.infoName;
        this.infoName = infoName;
        firePropertyChange(INFO_NAME_PROPERTY, old, this.infoName);
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        double old = this.threshold;
        this.threshold = threshold;
        firePropertyChange(THRESHOLD_PROPERTY, old, this.threshold);
    }

    public DiagnosticInfo getInfo() {
        return info;
    }

    public void setInfo(DiagnosticInfo info) {
        DiagnosticInfo old = this.info;
        this.info = info;
        firePropertyChange(INFO_NAME_PROPERTY, old, this.info);
    }

    public SlidingSpans getSlidingSpans() {
        return slidingSpans;
    }

    public void setSlidingSpans(SlidingSpans<?> slidingspans) {
        SlidingSpans<?> old = this.slidingSpans;
        this.slidingSpans = slidingspans;
        firePropertyChange(SLIDING_SPANS_PROPERTY, old, this.slidingSpans);
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

    static JFreeChart createSeriesChart() {
        JFreeChart result = ChartFactory.createXYBarChart("", "", false, "", Charts.emptyXYDataset(), PlotOrientation.VERTICAL, false, false, false);
        result.setPadding(TsCharts.CHART_PADDING);
        result.getTitle().setFont(TsCharts.CHART_TITLE_FONT);

        XYPlot plot = result.getXYPlot();

        DateAxis domainAxis = new DateAxis();
        //dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 1), true, false);
        domainAxis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM"));
        domainAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setDomainAxis(domainAxis);

        plot.getRangeAxis().setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);

        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(true);
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesOutlinePaint(false);

        return result;
    }

    static JMenu createSeriesMenu(ChartPanel chartPanel) {
        JMenu result = new JMenu();

        ChartCommand copy = new ChartCommand() {
            @Override
            public void execute(ChartPanel chartPanel) {
                TsData data = (TsData) chartPanel.getClientProperty("TS_DATA");
                Transferable t = TssTransferSupport.getDefault().fromTsData(data);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
            }

            @Override
            public boolean isEnabled(ChartPanel chartPanel) {
                return chartPanel.getClientProperty("TS_DATA") instanceof TsData;
            }
        };

        result.add(copy.toAction(chartPanel)).setText("Copy series");

        JMenu export = new JMenu("Export image to");
        export.add(ChartCommand.printImage().toAction(chartPanel)).setText("Printer...");
        export.add(ChartCommand.copyImage().toAction(chartPanel)).setText("Clipboard");
        export.add(ChartCommand.saveImage().toAction(chartPanel)).setText("File...");
        result.add(export);

        return result;
    }

    static JFreeChart createDistributionChart() {
        JFreeChart result = ChartFactory.createXYAreaChart("Distribution", "", "", Charts.emptyXYDataset(), PlotOrientation.VERTICAL, false, false, false);
        result.setPadding(TsCharts.CHART_PADDING);
        result.getTitle().setFont(TsCharts.CHART_TITLE_FONT);

        XYPlot plot = result.getXYPlot();
        plot.getDomainAxis().setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.getRangeAxis().setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);

        XYAreaRenderer renderer = (XYAreaRenderer) plot.getRenderer();
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setOutline(true);

        return result;
    }

    static JMenu createDistributionMenu(ChartPanel chartPanel) {
        JMenu result = new JMenu();

        result.add(MatrixChartCommand.copySeries(0, 0).toAction(chartPanel)).setText("Copy distribution");

        JMenu export = new JMenu("Export image to");
        export.add(ChartCommand.printImage().toAction(chartPanel)).setText("Printer...");
        export.add(ChartCommand.copyImage().toAction(chartPanel)).setText("Clipboard");
        export.add(ChartCommand.saveImage().toAction(chartPanel)).setText("File...");
        result.add(export);

        return result;
    }
}
