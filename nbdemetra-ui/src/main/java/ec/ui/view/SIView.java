/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import ec.satoolkit.DecompositionMode;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.timeseries.simplets.PeriodIterator;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataBlock;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.ATsView;
import ec.ui.chart.BasicXYDataset;
import ec.ui.chart.TsCharts;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.ChartCommand;
import ec.util.chart.swing.Charts;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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
 *
 * @author Kristof Bayens
 */
public class SIView extends ATsView implements ClipboardOwner {

    // CONSTANTS
    private static final int S_INDEX = 0;
    private static final int T_INDEX = 1;
    private static final int SI_INDEX = 2;
    private static final Stroke MARKER_STROKE = new BasicStroke(0.5f); //, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f));
    private static final Paint MARKER_PAINT = Color.DARK_GRAY;
    private static final float MARKER_ALPHA = .8f;
    // OTHER
    private final Map<Bornes, Graphs> graphs_;
    private final JChartPanel chartpanel_;
    private final XYLineAndShapeRenderer sRenderer;
    private final XYLineAndShapeRenderer tRenderer;
    private final XYLineAndShapeRenderer siRenderer1;
    private final PreciseXYLineAndShapeRenderer siRenderer2;
    private final JFreeChart mainchart_;
    private final JFreeChart detailchart_;

    static class Bornes {

        static final Bornes ZERO = new Bornes(0, 0);
        final double min_;
        final double max_;

        Bornes(double min, double max) {
            min_ = min;
            max_ = max;
        }
    }

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

        static double minYear(BasicXYDataset.Series S) {
            return S.getXValue(0);
        }

        static double maxYear(BasicXYDataset.Series S) {
            return S.getXValue(S.getItemCount() - 1);
        }

        int getMinYear() {
            double year = 9999;
            if (S1_.getItemCount() > 0 && minYear(S1_) < year) {
                year = minYear(S1_);
            }
            if (S2_.getItemCount() > 0 && minYear(S2_) < year) {
                year = minYear(S2_);
            }
            if (S3_.getItemCount() > 0 && minYear(S3_) < year) {
                year = minYear(S3_);
            }
            return (int) year;
        }

        int getMaxYear() {
            double year = 0;
            if (S1_ != null && maxYear(S1_) > year) {
                year = maxYear(S1_);
            }
            if (S2_ != null && maxYear(S2_) > year) {
                year = maxYear(S2_);
            }
            if (S3_ != null && maxYear(S3_) > year) {
                year = maxYear(S3_);
            }
            return (int) year;
        }
    }

    public SIView() {
        this.graphs_ = new HashMap<>();

        this.sRenderer = new XYLineAndShapeRenderer(true, false);
        sRenderer.setAutoPopulateSeriesPaint(false);

        this.tRenderer = new XYLineAndShapeRenderer(true, false);
        tRenderer.setAutoPopulateSeriesPaint(false);

        this.siRenderer1 = new XYLineAndShapeRenderer(false, true);
        siRenderer1.setAutoPopulateSeriesPaint(false);
        siRenderer1.setAutoPopulateSeriesShape(false);
        siRenderer1.setBaseShape(new Ellipse2D.Double(-.1, -.1, 2, 2));
        siRenderer1.setBaseShapesFilled(false);

        this.siRenderer2 = new PreciseXYLineAndShapeRenderer(false, true);
        siRenderer2.setAutoPopulateSeriesPaint(false);
        siRenderer2.setAutoPopulateSeriesShape(false);
        siRenderer2.setBaseShape(new Ellipse2D.Double(-.1, -.1, 2, 2));
        siRenderer2.setBaseShapesFilled(false);

        StandardXYToolTipGenerator generator = new StandardXYToolTipGenerator() {
            final DecimalFormat format = new DecimalFormat("0");

            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                for (Bornes b : graphs_.keySet()) {
                    if (dataset.getXValue(series, item) >= b.min_ && dataset.getXValue(series, item) <= b.max_) {
                        return format.format(graphs_.get(b).S3_.getXValue(item));
                    }
                }
                return null;
            }
        };
        siRenderer2.setBaseToolTipGenerator(generator);

        mainchart_ = createMainChart(sRenderer, tRenderer, siRenderer2);
        detailchart_ = createDetailChart(sRenderer, tRenderer, siRenderer1);

        chartpanel_ = new JChartPanel(null);
        chartpanel_.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (chartpanel_.getChart() != null) {
                    rescaleAxis((NumberAxis) chartpanel_.getChart().getXYPlot().getRangeAxis());
                }
            }
        });
        chartpanel_.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    if (mainchart_.equals(chartpanel_.getChart())) {
                        double x = chartpanel_.getChartX(e.getX());
                        Graphs g = null;
                        Bornes fb = Bornes.ZERO;
                        for (Bornes b : graphs_.keySet()) {
                            if (x >= b.min_ && x <= b.max_) {
                                g = graphs_.get(b);
                                fb = b;
                                break;
                            }
                        }
                        if (g == null) {
                            return;
                        }

                        showDetail(g, fb);
                    } else if (detailchart_.equals(chartpanel_.getChart())) {
                        showMain();
                    }
                }
            }
        });

        onComponentPopupMenuChange();
        enableProperties();

        setLayout(new BorderLayout());
        add(chartpanel_, BorderLayout.CENTER);
    }

    private void enableProperties() {
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case "componentPopupMenu":
                        onComponentPopupMenuChange();
                        break;
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        chartpanel_.setComponentPopupMenu(popupMenu != null ? popupMenu : buildMenu().getPopupMenu());
    }
    //</editor-fold>

    private JMenu buildMenu() {
        JMenu result = new JMenu();

        result.add(new JMenuItem(new AbstractAction("Show main") {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMain();
            }
        }));

        JMenu export = new JMenu("Export image to");
        export.add(ChartCommand.printImage().toAction(chartpanel_)).setText("Printer...");
        export.add(ChartCommand.copyImage().toAction(chartpanel_)).setText("Clipboard");
        export.add(ChartCommand.saveImage().toAction(chartpanel_)).setText("File...");
        result.add(export);

        return result;
    }

    private void showMain() {
        chartpanel_.setChart(mainchart_);
        onColorSchemeChange();
    }

    private void showDetail(Graphs g, Bornes fb) {
        XYPlot plot = detailchart_.getXYPlot();

        //configure axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelPaint(Color.GRAY);
        rescaleAxis(yAxis);
        plot.setRangeAxis(yAxis);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelPaint(Color.GRAY);
        xAxis.setTickUnit(new NumberTickUnit(1), true, false);
        xAxis.setNumberFormatOverride(new DecimalFormat("0000"));
        xAxis.setRange(g.getMinYear() - 1, g.getMaxYear() + 1);
        plot.setDomainAxis(xAxis);

        plot.setDataset(S_INDEX, new BasicXYDataset(Collections.singletonList(g.S2_)));
        plot.setDataset(T_INDEX, new BasicXYDataset(Collections.singletonList(g.S1_)));
        plot.setDataset(SI_INDEX, new BasicXYDataset(Collections.singletonList(g.S3_)));

        detailchart_.setTitle(g.label_);
        chartpanel_.setChart(detailchart_);
        onColorSchemeChange();
    }

    public void setData(TsData seas) {
        reset();
        if (seas != null) {
            displayData(seas, null, null);
        }
    }

    public void setData(TsData seas, TsData irr, DecompositionMode mode) {
        reset();
        if (seas == null && irr == null) {
            return;
        }
        if (seas == null) {
            seas = new TsData(irr.getDomain(), mode == DecompositionMode.Multiplicative ? 1 : 0);
        } else if (irr == null) {
            irr = new TsData(seas.getDomain(), mode == DecompositionMode.Multiplicative ? 1 : 0);
        }
        displayData(seas, irr, mode);

    }

    public void setSiData(TsData seas, TsData si) {
        setData(seas, si, DecompositionMode.Undefined);
    }

    public void reset() {
        graphs_.clear();
        chartpanel_.setChart(null);
    }

    private void displayData(TsData seas, TsData irr, DecompositionMode mode) {
        TsFrequency freq = seas.getFrequency();
        if (freq == TsFrequency.Undefined) {
            return;
        }

        TsPeriod end = seas.getEnd().minus(1);
        TsPeriod start = seas.getStart();
        int np = end.getYear() - start.getYear();

        BasicXYDataset sDataset = new BasicXYDataset();
        BasicXYDataset siDataset = new BasicXYDataset();
        BasicXYDataset tDataset = new BasicXYDataset();
        PeriodIterator speriods = new PeriodIterator(seas);
        double xstart = -0.4;
        double xend = 0.4;
        final double xstep = 0.8 / np;
        int il = 0;
        while (speriods.hasMoreElements()) {
            TsDataBlock datablock = speriods.nextElement();
            DataBlock src = datablock.data;
            int startyear = datablock.start.getYear();
            int endyear = startyear + datablock.data.getLength() - 1;

            String key = "p" + Integer.toString(il);

            int n = src.getLength();
            if (n > 0) {
                double m = src.sum() / n;
                double[] tX = {xstart, xend};
                double[] tX2 = {startyear, endyear};
                double[] tY = {m, m};

                double[] sX = new double[n];
                double[] sX2 = new double[n];
                double[] sY = new double[n];
                double[] siY = new double[n];

                double x = xstart + xstep * (startyear - start.getYear());
                for (int i = 0; i < n; ++i, x += xstep, startyear++) {
                    sX[i] = x;
                    sX2[i] = startyear;
                    sY[i] = src.get(i);
                    if (irr != null) {
                        int pos = irr.getDomain().search(datablock.period(i));
                        switch (mode) {
                            case Multiplicative:
                                siY[i] = sY[i] * irr.get(pos);
                                break;
                            case Additive:
                                siY[i] = sY[i] + irr.get(pos);
                                break;
                            default:
                                siY[i] = irr.get(pos);
                                break;
                        }
                    }
                }

                BasicXYDataset.Series t = BasicXYDataset.Series.of(key, tX, tY);
                BasicXYDataset.Series t2 = BasicXYDataset.Series.of(key, tX2, tY);

                BasicXYDataset.Series s = BasicXYDataset.Series.of(key, sX, sY);
                BasicXYDataset.Series s2 = BasicXYDataset.Series.of(key, sX2, sY);

                BasicXYDataset.Series si = irr != null ? BasicXYDataset.Series.of(key, sX, siY) : BasicXYDataset.Series.empty(key);
                BasicXYDataset.Series si2 = irr != null ? BasicXYDataset.Series.of(key, sX2, siY) : BasicXYDataset.Series.empty(key);

                Bornes b = new Bornes(xstart, xend);
                Graphs g = new Graphs(t2, s2, si2, TsPeriod.formatPeriod(freq, il));
                graphs_.put(b, g);

                sDataset.addSeries(s);
                tDataset.addSeries(t);
                siDataset.addSeries(si);
            }

            xstart++;
            xend++;
            il++;
        }

        XYPlot plot = mainchart_.getXYPlot();
        configureAxis(plot, freq);
        plot.setDataset(S_INDEX, sDataset);
        plot.setDataset(T_INDEX, tDataset);
        plot.setDataset(SI_INDEX, siDataset);

        showMain();
    }

    static void configureAxis(XYPlot plot, TsFrequency freq) {
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelPaint(Color.GRAY);
        rescaleAxis(yAxis);
        plot.setRangeAxis(yAxis);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelPaint(Color.GRAY);
        int periods = freq.intValue();
        xAxis.setTickUnit(new TsFrequencyTickUnit(freq));
        xAxis.setRange(-0.5, periods - 0.5);
        plot.setDomainAxis(xAxis);
        plot.setDomainGridlinesVisible(false);
        for (int i = 0; i < periods; i++) {
            ValueMarker marker = new ValueMarker(i + 0.5);
            marker.setStroke(MARKER_STROKE);
            marker.setPaint(MARKER_PAINT);
            marker.setAlpha(MARKER_ALPHA);
            plot.addDomainMarker(marker);
        }
    }

    static void rescaleAxis(NumberAxis axis) {
        axis.setAutoRangeIncludesZero(false);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    // EVENT HANDLERS > 
    @Override
    protected void onTsChange() {
        setData(m_ts != null ? m_ts.getTsData() : null);
    }

    @Override
    protected void onColorSchemeChange() {
        sRenderer.setBasePaint(themeSupport.getLineColor(KnownColor.BLUE));
        tRenderer.setBasePaint(themeSupport.getLineColor(KnownColor.RED));
        siRenderer1.setBasePaint(themeSupport.getLineColor(KnownColor.GRAY));
        siRenderer2.setBasePaint(themeSupport.getLineColor(KnownColor.GRAY));

        XYPlot mainPlot = mainchart_.getXYPlot();
        mainPlot.setBackgroundPaint(themeSupport.getPlotColor());
        mainPlot.setDomainGridlinePaint(themeSupport.getGridColor());
        mainPlot.setRangeGridlinePaint(themeSupport.getGridColor());
        mainchart_.setBackgroundPaint(themeSupport.getBackColor());

        XYPlot detailPlot = detailchart_.getXYPlot();
        detailPlot.setBackgroundPaint(themeSupport.getPlotColor());
        detailPlot.setDomainGridlinePaint(themeSupport.getGridColor());
        detailPlot.setRangeGridlinePaint(themeSupport.getGridColor());
        detailchart_.setBackgroundPaint(themeSupport.getBackColor());
    }

    @Override
    protected void onDataFormatChange() {
        // do nothing?
    }
    // < EVENT HANDLERS

    static JFreeChart createMainChart(XYLineAndShapeRenderer sRenderer, XYLineAndShapeRenderer tRenderer, PreciseXYLineAndShapeRenderer siRenderer2) {
        XYPlot plot = new XYPlot();

        plot.setDataset(S_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(S_INDEX, sRenderer);
        plot.mapDatasetToDomainAxis(S_INDEX, 0);
        plot.mapDatasetToRangeAxis(S_INDEX, 0);

        plot.setDataset(T_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(T_INDEX, tRenderer);
        plot.mapDatasetToDomainAxis(T_INDEX, 0);
        plot.mapDatasetToRangeAxis(T_INDEX, 0);

        plot.setDataset(SI_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(SI_INDEX, siRenderer2);
        plot.mapDatasetToDomainAxis(SI_INDEX, 0);
        plot.mapDatasetToRangeAxis(SI_INDEX, 0);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        JFreeChart result = new JFreeChart("", TsCharts.CHART_TITLE_FONT, plot, false);
        result.setPadding(TsCharts.CHART_PADDING);
        return result;
    }

    static JFreeChart createDetailChart(XYLineAndShapeRenderer sRenderer, XYLineAndShapeRenderer tRenderer, XYLineAndShapeRenderer siRenderer1) {
        XYPlot plot = new XYPlot();

        plot.setDataset(S_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(S_INDEX, sRenderer);
        plot.mapDatasetToDomainAxis(S_INDEX, 0);
        plot.mapDatasetToRangeAxis(S_INDEX, 0);

        plot.setDataset(T_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(T_INDEX, tRenderer);
        plot.mapDatasetToDomainAxis(T_INDEX, 0);
        plot.mapDatasetToRangeAxis(T_INDEX, 0);

        plot.setDataset(SI_INDEX, Charts.emptyXYDataset());
        plot.setRenderer(SI_INDEX, siRenderer1);
        plot.mapDatasetToDomainAxis(SI_INDEX, 0);
        plot.mapDatasetToRangeAxis(SI_INDEX, 0);

        JFreeChart result = new JFreeChart("", TsCharts.CHART_TITLE_FONT, plot, false);
        result.setPadding(TsCharts.CHART_PADDING);
        return result;
    }
}
