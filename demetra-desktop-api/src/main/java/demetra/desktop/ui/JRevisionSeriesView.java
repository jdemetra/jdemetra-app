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
package demetra.desktop.ui;

import demetra.desktop.components.parts.*;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.components.TimeSeriesComponent;
import demetra.desktop.jfreechart.TsCharts;
import demetra.desktop.util.NbComponents;
import demetra.desktop.components.JHtmlView;
import demetra.desktop.components.tools.JChartPanel;
import demetra.html.HtmlUtil;
import demetra.html.processing.HtmlRevisionsDocument;
import demetra.information.Explorable;
import demetra.timeseries.TimeSelector;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDomain;
import demetra.timeseries.TsPeriod;
import demetra.timeseries.calendars.CalendarUtility;
import ec.util.chart.ColorScheme;
import ec.util.chart.swing.SwingColorSchemeSupport;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jdplus.timeseries.simplets.analysis.DiagnosticInfo;
import jdplus.timeseries.simplets.analysis.RevisionHistory;
import jdplus.timeseries.simplets.analysis.TsDataFunction;

/**
 * View of the Revision History of a series.
 *
 * @author Mats Maggi
 */
@SwingComponent
public final class JRevisionSeriesView extends JComponent implements TimeSeriesComponent, HasColorScheme {

    private static final int S_INDEX = 1;
    private static final int REV_INDEX = 0;
    private final JChartPanel chartpanel;  // Panel displaying chart
    private final JHtmlView documentpanel;
    private final XYLineAndShapeRenderer sRenderer;
    private final XYLineAndShapeRenderer revRenderer;
    private final JFreeChart mainChart;
    private String info = "sa";
    private RevisionHistory<Explorable> revisionHistory;
    private DiagnosticInfo diagnostic = DiagnosticInfo.RelativeDifference, activeDiag = diagnostic;
    private final int nyears = 4;
    private final int minyears = 5;
    private final int threshold = 2;
    private int lastIndexSelected = -1;
    private final ChartPopup popup;
    private TsPeriod firstPeriod;
    private TsData sRef;
    private Range range;

    @lombok.experimental.Delegate
    private final HasColorScheme colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);

    private final HasColorSchemeResolver colorSchemeResolver = new HasColorSchemeResolver(colorScheme, this::onColorSchemeChange);

    /**
     * Constructs a new view
     */
    public JRevisionSeriesView() {
        setLayout(new BorderLayout());

        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();

        sRenderer = new XYLineAndShapeRenderer();
        sRenderer.setBaseShapesVisible(false);
        //sRenderer.setSeriesStroke(1, new BasicStroke(0.75f, 1, 1, 1.0f, new float[]{2f, 3f}, 0.0f));
        sRenderer.setBasePaint(themeSupport.getLineColor(ColorScheme.KnownColor.RED));

        revRenderer = new XYLineAndShapeRenderer(false, true);

        mainChart = createMainChart();

        chartpanel = new JChartPanel(ChartFactory.createLineChart(null, null, null, null, PlotOrientation.VERTICAL, false, false, false));

        documentpanel = new JHtmlView();

        JSplitPane splitpane = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, chartpanel, NbComponents.newJScrollPane(documentpanel));
        splitpane.setDividerLocation(0.5);
        splitpane.setResizeWeight(.5);

        popup = new ChartPopup(null, false);

        chartpanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent e) {
                if (lastIndexSelected != -1) {
                    revRenderer.setSeriesShapesFilled(lastIndexSelected, false);
                }
                if (e.getEntity() != null) {
                    if (e.getEntity() instanceof XYItemEntity item) {
                        if (item.getDataset().equals(mainChart.getXYPlot().getDataset(REV_INDEX))) {
                            int i = item.getSeriesIndex();

                            revRenderer.setSeriesShape(i, new Ellipse2D.Double(-3, -3, 6, 6));
                            revRenderer.setSeriesShapesFilled(i, true);
                            revRenderer.setSeriesPaint(i, themeSupport.getLineColor(ColorScheme.KnownColor.BLUE));

                            lastIndexSelected = i;

                            showRevisionPopup(e);
                        }
                    }
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {
            }
        });

        chartpanel.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals(JChartPanel.ZOOM_SELECTION_CHANGED)) {
                showSelectionPopup((Rectangle2D) evt.getNewValue());
            }
        });

        this.add(splitpane, BorderLayout.CENTER);
        splitpane.setResizeWeight(0.5);

        onColorSchemeChange();
    }

    private void showSelectionPopup(Rectangle2D rectangle) {
        XYPlot plot = chartpanel.getChart().getXYPlot();
        Rectangle2D dataArea = chartpanel.getScreenDataArea();
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        double minX = domainAxis.java2DToValue(rectangle.getMinX(), dataArea, plot.getDomainAxisEdge());
        double maxX = domainAxis.java2DToValue(rectangle.getMaxX(), dataArea, plot.getDomainAxisEdge());

        Date startDate = new Date((long) minX);
        Date endDate = new Date((long) maxX);
        LocalDate lstart = CalendarUtility.toLocalDate(startDate),
                lend = CalendarUtility.toLocalDate(endDate);
        TsPeriod start = firstPeriod.withDate(lstart.atStartOfDay());
        TsPeriod end = firstPeriod.withDate(lend.atStartOfDay());

        if (start.until(end) == 0) {
            return;
        }

        TimeSelector sel = TimeSelector.between(lstart.atStartOfDay(), lend.atStartOfDay());
        List<TsData> listSeries = revisionHistory.select(lstart, lend, x -> x.getData(info, TsData.class));
        List<TsData> revSeries = new ArrayList<>();

        for (TsData t : listSeries) {
            revSeries.add(transform(t, sel, activeDiag.asTsDataFunction()));
        }

        Point pt = new Point((int) rectangle.getX(), (int) rectangle.getY());
        pt.translate(3, 3);
        SwingUtilities.convertPointToScreen(pt, chartpanel);
        popup.setLocation(pt);
        popup.setChartTitle(info.toUpperCase() + " First estimations");
        popup.setTsData(transform(sRef, sel, activeDiag.asTsDataFunction()), revSeries);
        popup.setVisible(true);
        chartpanel.repaint();
    }

    private static TsData transform(TsData origin, TimeSelector selector, TsDataFunction fn) {
        TsDomain odomain = origin.getDomain();
        TsDomain domain = odomain.select(selector);
        double[] s = new double[domain.length()];
        int del = odomain.getStartPeriod().until(domain.getStartPeriod());
        for (int i = 0, j = del; i < s.length; ++i, ++j) {
            if (fn == null) {
                s[i] = origin.getValue(j);
            } else {
                s[i] = fn.apply(origin, j);
            }
        }
        return TsData.ofInternal(domain.getStartPeriod(), s);
    }

    private void showRevisionsDocument(TsData s) {
        HtmlRevisionsDocument document = new HtmlRevisionsDocument(s, activeDiag);
        document.setThreshold(threshold);
        documentpanel.setHtml(HtmlUtil.toString(document));
    }

    private TsData revisions() {
        if (sRef == null) {
            sRef = revisionHistory.referenceSeries(x -> x.getData(info, TsData.class));
        }
        int freq = sRef.getDomain().getAnnualFrequency();
        int l = nyears * freq + 1;
        int n0 = sRef.getDomain().getLength() - l;
        if (n0 < minyears * freq) {
            n0 = minyears * freq;
        }
        TsPeriod start = sRef.getDomain().get(n0);
        TsPeriod end = sRef.getDomain().getLastPeriod();
        int n = start.until(end);
        if (n <= 0) {
            return null;
        }
        double[] rev = new double[n];
        for (int i = 0; i < n; ++i) {
            double r = revisionHistory.seriesRevision(start.plus(i), activeDiag, x -> x.getData(info, TsData.class));
//            if (activeDiag != DiagnosticInfo.AbsoluteDifference && activeDiag != DiagnosticInfo.PeriodToPeriodDifference) {
//                r *= 100;
//            }
            rev[i] = r;
        }
        return TsData.ofInternal(start, rev);
    }

    private void showRevisionPopup(ChartMouseEvent e) {
        XYItemEntity entity = (XYItemEntity) e.getEntity();
        TsPeriod start = firstPeriod.plus(entity.getSeriesIndex());
        popup.setTsData(revisionHistory.tsRevision(start, start, activeDiag.asTsDataFunction(), x -> x.getData(info, TsData.class)), null);
        Point p = e.getTrigger().getLocationOnScreen();
        p.translate(3, 3);
        popup.setLocation(p);
        popup.setChartTitle(info.toUpperCase() + "[" + start.toString() + "] Revisions");
        popup.setVisible(true);
    }

    /**
     * Get information of the type of the view. Generally "sa" (seasonally
     * adjusted) or "t" (Trend)
     *
     * @return Type of the view
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets the type of the view
     *
     * @param info_ Type of the view
     */
    public void setInfo(String info_) {
        this.info = info_;
    }

    private void addSeries(TimeSeriesCollection chartSeries, TsData data, TimeSelector sel, TsDataFunction fn) {
        TimeSeries chartTs = new TimeSeries("");
        TsDomain select = data.getDomain().select(sel);
        for (int i = 0; i < select.getLength(); ++i) {
            TsPeriod cur = select.get(i);
            int pos = data.getDomain().indexOf(cur);
            if (pos >= 0) {
                double x = fn.apply(data, pos);
                if (Double.isFinite(x)) {
                    Day day = new Day(CalendarUtility.toDate(cur.start().toLocalDate()));
                    chartTs.addOrUpdate(day, x);
                }
            }
        }
        chartSeries.addSeries(chartTs);
    }

    private void addStart(TimeSeriesCollection chartSeries, String name, TsPeriod start, TsDataFunction fn) {
        TsData ts = revisionHistory.series(start, x -> x.getData(name, TsData.class));
        if (ts != null) {
            TimeSeries chartTs = new TimeSeries("");
            int pos = ts.getStart().until(start);
            Day day = new Day(CalendarUtility.toDate(start.start().toLocalDate()));
            chartTs.add(day, fn.apply(ts, pos));
            chartSeries.addSeries(chartTs);
        }
    }

    private void showResults() {
        if (revisionHistory == null) {
            return;
        }

        lastIndexSelected = -1;

        final TimeSeriesCollection chartSeries = new TimeSeriesCollection();
        sRef = revisionHistory.referenceSeries(x -> x.getData(info, TsData.class));
        TimeSelector selector = TimeSelector.all();
        TsDomain refdom = sRef.getDomain();
        int n = refdom.getLength();
        int freq = refdom.getAnnualFrequency();
        int l = nyears * freq + 1;
        int n0 = n - l;
        if (n0 < minyears * freq) {
            n0 = minyears * freq;
        }
        if (n0 < n) {
            firstPeriod = refdom.get(n0);
            selector = TimeSelector.from(firstPeriod.start());
        } else {
            firstPeriod = sRef.getStart();
        }
        addSeries(chartSeries, sRef, selector, activeDiag.asTsDataFunction());

        final TimeSeriesCollection startSeries = new TimeSeriesCollection();
        TsDomain dom = sRef.getDomain();
        for (int i = n0; i < n - 1; ++i) {
            addStart(startSeries, info, dom.get(i), activeDiag.asTsDataFunction());
        }

        if (startSeries.getSeriesCount() == 0 || chartSeries.getSeriesCount() == 0) {
            chartpanel.setChart(mainChart);
            return;
        }

        setRange(chartSeries, startSeries);

        XYPlot plot = mainChart.getXYPlot();
        plot.setDataset(S_INDEX, chartSeries);

        plot.setDataset(REV_INDEX, startSeries);

        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();
        for (int i = 0; i < startSeries.getSeriesCount(); i++) {
            revRenderer.setSeriesShape(i, new Ellipse2D.Double(-3, -3, 6, 6));
            revRenderer.setSeriesShapesFilled(i, false);
            revRenderer.setSeriesPaint(i, themeSupport.getLineColor(ColorScheme.KnownColor.BLUE));
        }
        plot.setRenderer(REV_INDEX, revRenderer);

        setRange(chartSeries, startSeries);
        configureAxis(plot);

        plot.mapDatasetToDomainAxis(S_INDEX, REV_INDEX);
        plot.mapDatasetToRangeAxis(S_INDEX, REV_INDEX);
        plot.mapDatasetToDomainAxis(REV_INDEX, REV_INDEX);
        plot.mapDatasetToRangeAxis(REV_INDEX, REV_INDEX);

        chartpanel.setChart(mainChart);

        showRevisionsDocument(revisions());
    }

    private JFreeChart createMainChart() {
        XYPlot plot = new XYPlot();
        configureAxis(plot);

        plot.setRenderer(S_INDEX, sRenderer);

        plot.setRenderer(REV_INDEX, revRenderer);

        plot.setNoDataMessage("Not enough data to compute revision history !");
        JFreeChart result = new JFreeChart("", TsCharts.CHART_TITLE_FONT, plot, false);
        result.setPadding(TsCharts.CHART_PADDING);
        return result;
    }

    private void configureAxis(XYPlot plot) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        DateAxis dateAxis = new DateAxis();
        dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateAxis.setDateFormatOverride(sdf);
        plot.setDomainAxis(dateAxis);
        NumberAxis yaxis = new NumberAxis();
        if (range != null) {
            yaxis.setRange(range);
        }
        plot.setRangeAxis(yaxis);
    }

    /**
     * Sets the Revision History processor used by the view to display results
     * in different graphs
     *
     * @param history Revision History to pass to the view
     */
    public void setHistory(RevisionHistory history) {
        if (this.revisionHistory == null || !this.revisionHistory.equals(history)) {
            this.revisionHistory = history;
            if (history != null) {
                TsData data = revisionHistory.getReferenceInfo().getData(info, TsData.class);
                if (data.getValues().allMatch(x -> !Double.isFinite(x) || x > 0)) {
                    activeDiag = diagnostic;
                } else {
                    activeDiag = diagnostic.adaptForNegativeValues();
                }

            }
            chartpanel.setChart(null);

            showResults();
        }
    }

    private void onColorSchemeChange() {
        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();

        sRenderer.setBasePaint(themeSupport.getLineColor(ColorScheme.KnownColor.RED));
        revRenderer.setBasePaint(themeSupport.getLineColor(ColorScheme.KnownColor.BLUE));

        XYPlot mainPlot = mainChart.getXYPlot();
        for (int i = 0; i < mainPlot.getSeriesCount(); i++) {
            revRenderer.setSeriesShape(i, new Ellipse2D.Double(-3, -3, 6, 6));
            revRenderer.setSeriesShapesFilled(i, false);
            revRenderer.setSeriesPaint(i, themeSupport.getLineColor(ColorScheme.KnownColor.BLUE));
        }

        mainPlot.setBackgroundPaint(themeSupport.getPlotColor());
        mainPlot.setDomainGridlinePaint(themeSupport.getGridColor());
        mainPlot.setRangeGridlinePaint(themeSupport.getGridColor());
        mainChart.setBackgroundPaint(themeSupport.getBackColor());
    }

    private void setRange(TimeSeriesCollection chartSeries, TimeSeriesCollection startSeries) {
        double min, max;
        Range chart = chartSeries.getRangeBounds(true);
        Range start = startSeries.getRangeBounds(true);
        min = chart.getLowerBound();
        max = chart.getUpperBound();

        if (min > start.getLowerBound()) {
            min = start.getLowerBound();
        }

        if (max < start.getUpperBound()) {
            max = start.getUpperBound();
        }

        min -= (Math.abs(min) * .03);
        max += (Math.abs(max) * .03);

        range = new Range(min, max);
    }

    /**
     * @return the diag_
     */
    public DiagnosticInfo getDiagnosticInfo() {
        return diagnostic;
    }

    /**
     * @param diag_ the diag_ to set
     */
    public void setDiagnosticInfo(DiagnosticInfo diag) {
        this.diagnostic = diag;
    }
}
