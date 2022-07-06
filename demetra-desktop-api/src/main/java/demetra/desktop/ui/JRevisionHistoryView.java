/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui;

import demetra.desktop.design.SwingComponent;
import demetra.desktop.components.parts.HasColorScheme;
import demetra.desktop.components.parts.HasColorSchemeResolver;
import demetra.desktop.components.parts.HasColorSchemeSupport;
import demetra.desktop.util.NbComponents;
import demetra.desktop.components.JHtmlView;
import demetra.desktop.components.tools.JChartPanel;
import demetra.html.HtmlUtil;
import demetra.html.processing.HtmlRevisionsDocument;
import demetra.information.Explorable;
import demetra.timeseries.TimeSelector;
import demetra.timeseries.TsData;
import demetra.timeseries.TsPeriod;
import demetra.timeseries.calendars.CalendarUtility;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import jdplus.timeseries.simplets.analysis.DiagnosticInfo;
import jdplus.timeseries.simplets.analysis.DiagnosticTarget;
import jdplus.timeseries.simplets.analysis.RevisionHistory;

/**
 * @author Kristof Bayens
 */
@SwingComponent
public final class JRevisionHistoryView extends JComponent implements HasColorScheme {

    private String info_ = "SA";
    private RevisionHistory<Explorable> history_;
    private DiagnosticTarget target_ = DiagnosticTarget.Final;
    private DiagnosticInfo diag_ = DiagnosticInfo.RelativeDifference;
    private int revcount_ = 12;
    private int revlag_ = 1;
    private int years_ = 4;
    private final int minyears_ = 5;
    private int threshold_ = 2;
    private final JChartPanel chartpanel_;
    private final JHtmlView documentpanel_;

    @lombok.experimental.Delegate
    private final HasColorScheme colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);

    // TODO: add some code on color scheme change
    private final HasColorSchemeResolver colorSchemeResolver = new HasColorSchemeResolver(colorScheme, this::invalidate);

    public JRevisionHistoryView() {
        setLayout(new BorderLayout());

        chartpanel_ = new JChartPanel(ChartFactory.createLineChart(null, null, null, null, PlotOrientation.VERTICAL, false, false, false));
        documentpanel_ = new JHtmlView();

        JSplitPane splitpane = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, chartpanel_, documentpanel_);
        splitpane.setDividerLocation(0.5);
        splitpane.setResizeWeight(.5);

        this.add(splitpane, BorderLayout.CENTER);
        splitpane.setResizeWeight(0.5);
    }

    private void addSeries(TimeSeriesCollection chartSeries, TsData data) {
        TimeSeries chartTs = new TimeSeries("");
        for (int i = 0; i < data.getDomain().getLength(); ++i) {
            if (Double.isFinite(data.getValue(i))) {
                Day day = new Day(CalendarUtility.toDate(data.getDomain().get(i).start().toLocalDate()));
                chartTs.addOrUpdate(day, data.getValue(i));
            }
        }
        chartSeries.addSeries(chartTs);
    }

    private void addStart(TimeSeriesCollection chartSeries, String name, TsPeriod start) {
        TsData ts = history_.series(start, x->x.getData(name, TsData.class));
        if (ts != null) {
            TimeSeries chartTs = new TimeSeries("");
            int pos = ts.getStart().until(start);
            Day day = new Day(CalendarUtility.toDate(start.start().toLocalDate()));
            chartTs.add(day, ts.getValue(pos));
            chartSeries.addSeries(chartTs);
        }
    }

    private void showResults() {
        if (history_ == null) {
            return;
        }

        final TimeSeriesCollection chartSeries = new TimeSeriesCollection();
        TsData s = history_.referenceSeries(x->x.getData(info_, TsData.class));
        TimeSelector selector=TimeSelector.all();
        int n = s.getDomain().getLength();
        int freq = s.getDomain().getAnnualFrequency();
        int l = years_ * freq + 1;
        int n0 = n - l;
        if (n0 < minyears_ * freq) {
            n0 = minyears_ * freq;
        }
        if (n0 < n) {
            selector=TimeSelector.from(s.getDomain().get(n0).start());
        }
        addSeries(chartSeries, s.select(selector));
        TsData ps = history_.series(s.getDomain().getLastPeriod().plus(-s.getDomain().getAnnualFrequency()), x->x.getData(info_, TsData.class));
        addSeries(chartSeries, ps.select(selector));
        JFreeChart seriesChart = createSeriesChart(chartSeries);

        final TimeSeriesCollection startSeries = new TimeSeriesCollection();
        for (int i = n0; i < n - 1; ++i) {
            addStart(startSeries, info_, s.getDomain().get(i));
        }
        JFreeChart startChart = createStartChart(startSeries);

        XYPlot plot = new XYPlot();
        plot.setDataset(0, seriesChart.getXYPlot().getDataset());
        plot.setRenderer(0, seriesChart.getXYPlot().getRenderer());

        plot.setDataset(1, startChart.getXYPlot().getDataset());
        plot.setRenderer(1, startChart.getXYPlot().getRenderer());

        configureAxis(plot);

        plot.mapDatasetToDomainAxis(0, 0);
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToDomainAxis(1, 0);
        plot.mapDatasetToRangeAxis(1, 0);

        chartpanel_.setChart(new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false));

        TsData rev = revisions();
        showRevisionsChart(rev);
        showRevisionsDocument(rev);
    }

    private void configureAxis(XYPlot plot) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        DateAxis dateAxis = new DateAxis();
        dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateAxis.setDateFormatOverride(sdf);
        plot.setDomainAxis(dateAxis);
        NumberAxis yaxis = new NumberAxis();
        yaxis.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(yaxis);
    }

    private JFreeChart createSeriesChart(TimeSeriesCollection chartSeries) {
        JFreeChart chart = ChartFactory.createXYLineChart("", "x-axis", "y-axis", chartSeries, PlotOrientation.VERTICAL, false, true, false);

        XYPlot plot = chart.getXYPlot();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        DateAxis dateAxis = new DateAxis();
        dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateAxis.setDateFormatOverride(sdf);
        plot.setDomainAxis(dateAxis);
        NumberAxis yaxis = new NumberAxis();
        yaxis.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(yaxis);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(false);
        renderer.setSeriesStroke(1, new BasicStroke(0.75f, 1, 1, 1.0f, new float[]{2f, 3f}, 0.0f));
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.BLACK);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("({0}) {1}: {2}", sdf, NumberFormat.getInstance()));

        return chart;
    }

    private JFreeChart createStartChart(TimeSeriesCollection chartSeries) {
        JFreeChart chart = ChartFactory.createScatterPlot(null, null, null, chartSeries, PlotOrientation.VERTICAL, (chartSeries.getSeriesCount() > 1), false, false);
        XYPlot plot = chart.getXYPlot();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        DateAxis dateAxis = new DateAxis();
        dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateAxis.setDateFormatOverride(sdf);
        plot.setDomainAxis(dateAxis);
        NumberAxis yaxis = new NumberAxis();
        yaxis.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(yaxis);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
        for (int i = 0; i < chartSeries.getSeriesCount(); i++) {
            renderer.setSeriesShape(i, new Ellipse2D.Double(-2, -2, 4, 4));
            renderer.setSeriesShapesFilled(i, false);
            renderer.setSeriesPaint(i, Color.BLUE); // getColorTheme().getCurveColor(i));
        }
        plot.setRenderer(renderer);
        return chart;
    }

    private void showRevisionsChart(TsData s) {
    }

    private void showRevisionsDocument(TsData s) {
        HtmlRevisionsDocument document = new HtmlRevisionsDocument(s, diag_);
        document.setThreshold(threshold_);
        documentpanel_.setHtml(HtmlUtil.toString(document));
    }

    private TsData revisions() {
        TsData s = history_.referenceSeries(x->x.getData(info_, TsData.class));
        int freq = s.getDomain().getAnnualFrequency();
        int l = years_ * freq + 1;
        int n0 = s.getDomain().getLength() - l;
        if (n0 < minyears_ * freq) {
            n0 = minyears_ * freq;
        }
        TsPeriod start = s.getDomain().get(n0);
        TsPeriod end = s.getDomain().getLastPeriod();
        int n = start.until(end);
        if (n <= 0) {
            return null;
        }
        double[] rev=new double[n];
        for (int i = 0; i < n; ++i) {
            double r = history_.seriesRevision(start.plus(i), diag_, x->x.getData(info_, TsData.class));
            if (diag_ != DiagnosticInfo.AbsoluteDifference && diag_ != DiagnosticInfo.PeriodToPeriodDifference) {
                r *= 100;
            }
            rev[i]=r;
        }
        return TsData.ofInternal(start, rev);
    }

    public String getInformation() {
        return info_;
    }

    public void setInformation(String info) {
        info_ = info;
    }

    public void setHistory(RevisionHistory history) {
        history_ = history;
        showResults();
    }

    public DiagnosticTarget getTarget() {
        return target_;
    }

    public void setTarget(DiagnosticTarget target) {
        target_ = target;
    }

    public DiagnosticInfo getDiagInfo() {
        return diag_;
    }

    public void setDiagInfo(DiagnosticInfo info) {
        diag_ = info;
    }

    public int getRevisionsLag() {
        return revlag_;
    }

    public void setRevisionsLag(int revlag) {
        revlag_ = revlag;
    }

    public int getRevisionsCount() {
        return revcount_;
    }

    public void setRevisionsCount(int revcount) {
        revcount_ = revcount;
    }

    public int getThreshold() {
        return threshold_;
    }

    public void setThreshold(int threshold) {
        threshold_ = threshold;
    }

    public int getPeriodLength() {
        return years_;
    }

    public void setPeriodLenth(int years) {
        years_ = years;
    }
}
