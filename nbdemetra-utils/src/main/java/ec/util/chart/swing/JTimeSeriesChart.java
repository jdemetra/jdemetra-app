/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or â€“ as soon they will be approved 
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
package ec.util.chart.swing;

import ec.util.chart.ObsIndex;
import ec.util.chart.SeriesFunction;
import static ec.util.chart.TimeSeriesChart.Element.AXIS;
import static ec.util.chart.TimeSeriesChart.Element.LEGEND;
import static ec.util.chart.TimeSeriesChart.Element.TITLE;
import static ec.util.chart.TimeSeriesChart.Element.TOOLTIP;
import static ec.util.chart.TimeSeriesChart.RendererType.AREA;
import static ec.util.chart.TimeSeriesChart.RendererType.COLUMN;
import static ec.util.chart.TimeSeriesChart.RendererType.LINE;
import static ec.util.chart.TimeSeriesChart.RendererType.MARKER;
import static ec.util.chart.TimeSeriesChart.RendererType.SPLINE;
import static ec.util.chart.TimeSeriesChart.RendererType.STACKED_AREA;
import static ec.util.chart.TimeSeriesChart.RendererType.STACKED_COLUMN;
import static ec.util.chart.swing.SwingColorSchemeSupport.withAlpha;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.ui.RectangleInsets;

/**
 * Component used to display time series in a chart. Supports drag and drop,
 * copy/paste.
 *
 * @author Demortier Jeremy
 * @author Philippe Charles
 */
public final class JTimeSeriesChart extends ATimeSeriesChart {

    // LOCAL PROPERTIES
    private static final String REVEAL_OBS_PROPERTY = "revealObs";
    // CONSTANTS
    private static final RectangleInsets CHART_PADDING = new RectangleInsets(5, 5, 5, 5);
    private static final int NOT_SELECTED_ALPHA = 50;
    private static final int SELECTED_ALPHA = 255;
    // OTHER
    private final ChartPanel chartPanel;
    private final ChartNotification notification;
    private final ListSelectionModel seriesSelectionModel;
    private final CombinedDomainXYPlot mainPlot;
    // read-only list of plots
    private final java.util.List<XYPlot> roSubPlots;
    private final SeriesMapFactory seriesMapFactory;
    private boolean revealObs;
    // EXPERIMENTAL
    private final SwingFontSupport fontSupport;

    public JTimeSeriesChart() {
        super(Arrays.asList(MARKER, LINE, SPLINE, COLUMN, STACKED_COLUMN, AREA, STACKED_AREA));
        this.chartPanel = new ChartPanel(createTsChart(), false, false, false, false, false);
        this.notification = new ChartNotification(chartPanel.getChart());
        this.seriesSelectionModel = new DefaultListSelectionModel();
        this.mainPlot = (CombinedDomainXYPlot) chartPanel.getChart().getXYPlot();
        this.roSubPlots = mainPlot.getSubplots();
        this.seriesMapFactory = new SeriesMapFactory();
        this.revealObs = false;
        this.fontSupport = new SwingFontSupportImpl();

        notification.suspend();

        // initialization
        onColorSchemeSupportChange();
        onLineThicknessChange();
        onPeriodFormatChange();
        onValueFormatChange();
        onSeriesRendererChange();
        onSeriesFormatterChange();
        onSeriesColoristChange();
        onObsFormatterChange();
        onObsColoristChange();
        onDashPredicateChange();
        onLegendVisibilityPredicateChange();
        onPlotDispatcherChange();
        onDatasetChange();
        onTitleChange();
        onNoDataMessageChange();
        onPlotWeightsChange();
        onElementVisibleChange();
        onCrosshairOrientationChange();
        onHoveredObsChange();
        onSelectedObsChange();
        onObsHighlighterChange();
        onTooltipTriggerChange();
        onCrosshairTriggerChange();
        onRevealObsChange();
        onFontSupportChange();

        Charts.avoidScaling(chartPanel);
        Charts.enableFocusOnClick(chartPanel);

        enableObsTriggering();
        enableRevealObs();
        enableSelection();
        enableProperties();

        chartPanel.setActionMap(getActionMap());
        chartPanel.setInputMap(JComponent.WHEN_FOCUSED, getInputMap());

        notification.resume();

        setLayout(new BorderLayout());
        this.add(chartPanel, BorderLayout.CENTER);

        if (Beans.isDesignTime()) {
            setTitle("Preview");
            setElementVisible(Element.TITLE, true);
            setPreferredSize(new Dimension(400, 300));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Events Handlers">
    private void onColorSchemeSupportChange() {
        chartPanel.getChart().getTitle().setPaint(colorSchemeSupport.getTextColor());
        chartPanel.getChart().setBackgroundPaint(colorSchemeSupport.getBackColor());
        onColorSchemeSupportChange(mainPlot.getDomainAxis());
        for (XYPlot o : roSubPlots) {
            onColorSchemeSupportChange(o);
        }
    }

    private void onColorSchemeSupportChange(XYPlot plot) {
        plot.setNoDataMessagePaint(colorSchemeSupport.getGridColor());
        plot.setBackgroundPaint(colorSchemeSupport.getPlotColor());
        plot.setDomainGridlinePaint(colorSchemeSupport.getGridColor());
        plot.setRangeGridlinePaint(colorSchemeSupport.getGridColor());
        Color crosshairColor = SwingColorSchemeSupport.isDark(colorSchemeSupport.getPlotColor()) ? Color.WHITE : Color.BLACK;
        plot.setDomainCrosshairPaint(crosshairColor);
        plot.setRangeCrosshairPaint(crosshairColor);
        onColorSchemeSupportChange(plot.getRangeAxis());
    }

    private void onColorSchemeSupportChange(Axis axis) {
        axis.setAxisLinePaint(colorSchemeSupport.getAxisColor());
        axis.setTickLabelPaint(colorSchemeSupport.getAxisColor());
        axis.setTickMarkPaint(colorSchemeSupport.getAxisColor());
    }

    private void onLineThicknessChange() {
        notification.forceRefresh();
    }

    private void onPeriodFormatChange() {
        ((DateAxis) mainPlot.getDomainAxis()).setDateFormatOverride(periodFormat);
    }

    private void onValueFormatChange() {
        for (XYPlot o : roSubPlots) {
            onValueFormatChange(o);
        }
    }

    private void onValueFormatChange(XYPlot plot) {
        ((NumberAxis) plot.getRangeAxis()).setNumberFormatOverride(valueFormat);
    }

    private void onSeriesRendererChange() {
        onDatasetChange();
    }

    private void onSeriesFormatterChange() {
        notification.forceRefresh();
    }

    private void onSeriesColoristChange() {
        notification.forceRefresh();
    }

    private void onObsFormatterChange() {
        notification.forceRefresh();
    }

    private void onObsColoristChange() {
        notification.forceRefresh();
    }

    private void onDashPredicateChange() {
        notification.forceRefresh();
    }

    private void onLegendVisibilityPredicateChange() {
        notification.forceRefresh();
    }

    private void onPlotDispatcherChange() {
        onDatasetChange();
    }

    private void onDatasetChange() {
        seriesMapFactory.update(dataset.getSeriesCount(), seriesRenderer, plotDispatcher);
        for (XYPlot o : roSubPlots) {
            onDatasetChange(o);
        }
    }

    private void onDatasetChange(final XYPlot plot) {
        int plotIndex = roSubPlots.indexOf(plot);
        int rendererIndex = 0;
        for (RendererType o : supportedRendererTypes) {
            int[] map = seriesMapFactory.getSeriesMap(o, plotIndex);
            plot.setDataset(rendererIndex++, new FilteredXYDataset(dataset, map));
        }
    }

    private void onTitleChange() {
        chartPanel.getChart().setTitle(title);
    }

    private void onNoDataMessageChange() {
        for (XYPlot o : roSubPlots) {
            onNoDataMessageChange(o);
        }
    }

    private void onNoDataMessageChange(XYPlot plot) {
        plot.setNoDataMessage(noDataMessage);
    }

    private void onPlotWeightsChange() {
        adjustSubPlots();
        List<XYPlot> plots = roSubPlots;
        for (int i = 0; i < plotWeights.length; i++) {
            plots.get(i).setWeight(plotWeights[i]);
        }
    }

    private void onElementVisibleChange() {
        for (Element element : Element.values()) {
            switch (element) {
                case AXIS:
                    boolean visible = elementVisible[element.ordinal()];
                    mainPlot.getDomainAxis().setVisible(visible);
                    for (XYPlot o : roSubPlots) {
                        onElementVisibleChange(o);
                    }
                    break;
                case LEGEND:
                    chartPanel.getChart().getLegend().setVisible(elementVisible[element.ordinal()]);
                    break;
                case TITLE:
                    chartPanel.getChart().getTitle().setVisible(elementVisible[element.ordinal()]);
                    break;
            }
        }
    }

    private void onElementVisibleChange(XYPlot plot) {
        plot.getRangeAxis().setVisible(elementVisible[Element.AXIS.ordinal()]);
    }

    private void onCrosshairOrientationChange() {
        notification.forceRefresh();
    }

    private void onFontSupportChange() {
        chartPanel.getChart().getTitle().setFont(fontSupport.getTitleFont());
        onFontSupportChange(mainPlot.getDomainAxis());
        for (XYPlot o : roSubPlots) {
            onFontSupportChange(o);
        }
    }

    private void onFontSupportChange(XYPlot plot) {
        onFontSupportChange(plot.getRangeAxis());
        plot.setNoDataMessageFont(fontSupport.getNoDataMessageFont());
    }

    private void onFontSupportChange(Axis axis) {
        axis.setTickLabelFont(fontSupport.getAxisFont());
    }

    private void onHoveredObsChange() {
        if (crosshairTrigger != DisplayTrigger.SELECTION) {
            onCrosshairValueChange(hoveredObs);
        }
        notification.forceRefresh();
    }

    private void onSelectedObsChange() {
        if (crosshairTrigger != DisplayTrigger.HOVERING) {
            onCrosshairValueChange(selectedObs);
        }
        notification.forceRefresh();
    }

    private void onCrosshairValueChange(ObsIndex value) {
        if (isElementVisible(Element.CROSSHAIR) && existPredicate.apply(value)) {
            double x = dataset.getXValue(value.getSeries(), value.getObs());
            double y = dataset.getYValue(value.getSeries(), value.getObs());
            int index = plotDispatcher.apply(value.getSeries());
            for (XYPlot subPlot : roSubPlots) {
                subPlot.setDomainCrosshairValue(x);
                subPlot.setDomainCrosshairVisible(crosshairOrientation != CrosshairOrientation.HORIZONTAL);
                if (roSubPlots.indexOf(subPlot) == index && crosshairOrientation != CrosshairOrientation.VERTICAL) {
                    subPlot.setRangeCrosshairValue(y);
                    subPlot.setRangeCrosshairVisible(true);
                } else {
                    subPlot.setRangeCrosshairVisible(false);
                }
            }
        } else {
            for (XYPlot subPlot : roSubPlots) {
                subPlot.setRangeCrosshairVisible(false);
                subPlot.setDomainCrosshairVisible(false);
            }
        }
    }

    private void onObsHighlighterChange() {
        notification.forceRefresh();
    }

    private void onTooltipTriggerChange() {
        notification.forceRefresh();
    }

    private void onCrosshairTriggerChange() {
        notification.forceRefresh();
    }

    private void onRevealObsChange() {
        notification.forceRefresh();
    }

    private void onComponentPopupMenuChange() {
        chartPanel.setPopupMenu(getComponentPopupMenu());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Delegate to chartPanel">
    @Override
    public void setTransferHandler(TransferHandler newHandler) {
        super.setTransferHandler(newHandler);
        chartPanel.setTransferHandler(newHandler);
    }

    @Override
    public TransferHandler getTransferHandler() {
        return chartPanel.getTransferHandler();
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        chartPanel.addMouseListener(l);
    }

    @Override
    public synchronized void removeMouseListener(MouseListener l) {
        chartPanel.removeMouseListener(l);
    }

    @Override
    public synchronized void setDropTarget(DropTarget dt) {
        chartPanel.setDropTarget(dt);
    }

    @Override
    public synchronized DropTarget getDropTarget() {
        return chartPanel.getDropTarget();
    }

    @Deprecated
    public void setPopupMenu(JPopupMenu popupMenu) {
        setComponentPopupMenu(popupMenu);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TimeSeriesChart Implementation">
    @Override
    public void copyImage() {
        Charts.copyChart(chartPanel);
    }

    @Override
    public void saveImage() throws IOException {
        Charts.saveChart(chartPanel);
    }

    @Override
    public void printImage() {
        chartPanel.createChartPrintJob();
    }

    @Override
    public void writeImage(String mediaType, OutputStream stream) throws IOException {
        Charts.writeChart(mediaType, stream, chartPanel.getChart(), chartPanel.getWidth(), chartPanel.getHeight());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Zoom stuff">
    public boolean isMouseWheelEnabled() {
        return chartPanel.isMouseWheelEnabled();
    }

    public void setMouseWheelEnabled(boolean flag) {
        chartPanel.setMouseWheelEnabled(flag);
    }

    public void resetZoom() {
        // XYBarRenderer has a bug that prevents restoreAutoBounds if not previously set
        notification.suspend();
        chartPanel.zoomInRange(1, 10);
        chartPanel.restoreAutoBounds();
        notification.resume();
    }

    @Nonnull
    public double[] getZoom() {
        Range domainRange = roSubPlots.get(0).getDomainAxis().getRange();
        Range rangeRange = roSubPlots.get(0).getRangeAxis().getRange();
        return new double[]{domainRange.getLowerBound(), domainRange.getUpperBound(), rangeRange.getLowerBound(), rangeRange.getUpperBound()};
    }

    public void setZoom(double[] zoom) {
        if (zoom.length == 4) {
            for (XYPlot o : roSubPlots) {
                o.getDomainAxis().setRange(new Range(zoom[0], zoom[1]));
                o.getRangeAxis().setRange(new Range(zoom[2], zoom[3]));
            }
        } else {
            resetZoom();
        }
    }
    //</editor-fold>

    /**
     *
     * @return a non-null selection model
     * @deprecated use {@link #getSeriesSelectionModel()} instead
     */
    @Deprecated
    @Nonnull
    public ListSelectionModel getSelectionModel() {
        return getSeriesSelectionModel();
    }

    /**
     * Returns a selection model of the series in the chart. This model allows
     * you to listen and set selection of series.
     *
     * @return a non-null selection model
     */
    @Nonnull
    public ListSelectionModel getSeriesSelectionModel() {
        return seriesSelectionModel;
    }

    private void setRevealObs(boolean revealObs) {
        boolean old = this.revealObs;
        this.revealObs = revealObs;
        firePropertyChange(REVEAL_OBS_PROPERTY, old, this.revealObs);
    }

    //<editor-fold defaultstate="collapsed" desc="Subplots handlers">
    private void adjustSubPlots() {
        int diff = plotWeights.length - roSubPlots.size();
        if (diff > 0) {
            growSubPlots(diff);
        } else if (diff < 0) {
            shrinkSubPlots(-diff);
        }
    }

    private void growSubPlots(int size) {
        for (int i = 0; i < size; i++) {
            XYPlot plot = new XYPlot();

            for (int rendererIndex = 0; rendererIndex < supportedRendererTypes.size(); rendererIndex++) {
                SeriesIndexResolver resolver = SeriesIndexResolver.create(plot, rendererIndex);
                JTimeSeriesRendererSupport support = new RendererSupport(resolver);
                plot.setRenderer(rendererIndex, support.createRenderer(supportedRendererTypes.get(rendererIndex)));
            }

            NumberAxis rangeAxis = new NumberAxis();
            rangeAxis.setAutoRangeIncludesZero(false);
            rangeAxis.setTickLabelInsets(new RectangleInsets(10, 5, 10, 2));
            rangeAxis.setLowerMargin(0.02);
            rangeAxis.setUpperMargin(0.02);
            plot.setRangeAxis(rangeAxis);

            mainPlot.add(plot);

            onDatasetChange(plot);
            onColorSchemeSupportChange(plot);
            onValueFormatChange(plot);
            onNoDataMessageChange(plot);
            onElementVisibleChange(plot);
            onFontSupportChange(plot);
        }
    }

    private void shrinkSubPlots(int size) {
        for (int i = 0; i < size; i++) {
            mainPlot.remove(roSubPlots.get(roSubPlots.size() - 1));
        }
    }
    //</editor-fold>

    private static JFreeChart createTsChart() {
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot();

        plot.setAxisOffset(RectangleInsets.ZERO_INSETS);

        DateAxis domainAxis = new DateAxis();
        domainAxis.setTickLabelInsets(new RectangleInsets(2, 5, 2, 5));
        domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);
        plot.setDomainAxis(domainAxis);

        JFreeChart result = new JFreeChart("", null, plot, true);
        result.setPadding(CHART_PADDING);
        result.getLegend().setFrame(BlockBorder.NONE);
        result.getLegend().setBackgroundPaint(null);

        return result;
    }

    private final class RendererSupport extends JTimeSeriesRendererSupport {

        private final SeriesIndexResolver r;

        public RendererSupport(SeriesIndexResolver r) {
            this.r = r;
        }

        @Override
        public String getSeriesLabel(int series) {
            int index = r.realIndexOf(series);
            String result = seriesFormatter.apply(index);
            return result != null ? result : String.valueOf(index);
        }

        @Override
        public String getObsLabel(int series, int item) {
            return obsFormatter.apply(r.realIndexOf(series), item);
        }

        @Override
        public boolean isSeriesLabelVisible(int series) {
            return legendVisibilityPredicate.apply(r.realIndexOf(series));
        }

        @Override
        public Color getPlotColor() {
            return colorSchemeSupport.getPlotColor();
        }

        private Color applySelection(int index, Color color) {
            return seriesSelectionModel.isSelectionEmpty() ? color : seriesSelectionModel.isSelectedIndex(index) ? withAlpha(color, SELECTED_ALPHA) : withAlpha(color, NOT_SELECTED_ALPHA);
        }

        @Override
        public Color getSeriesColor(int series) {
            int index = r.realIndexOf(series);
            Color color = seriesColorist.apply(index);
            return applySelection(index, color != null ? color : Color.BLACK);
        }

        @Override
        public Color getObsColor(int series, int item) {
            int index = r.realIndexOf(series);
            Color color = obsColorist.apply(index, item);
            boolean dash = dashPredicate.apply(index, item);
            return applySelection(index, dash && color != null ? color.darker() : color);
        }

        @Override
        public Color getSeriesLabelColor(int series) {
            return colorSchemeSupport.getTextColor();
        }

        @Override
        public Stroke getSeriesStroke(int series) {
            int index = r.realIndexOf(series);
            boolean strong = !seriesSelectionModel.isSelectionEmpty() && seriesSelectionModel.isSelectedIndex(index);
            return lineStrokes.getStroke(strong, false);
        }

        @Override
        public Stroke getObsStroke(int series, int item) {
            int index = r.realIndexOf(series);
            boolean strong = !seriesSelectionModel.isSelectionEmpty() && seriesSelectionModel.isSelectedIndex(index);
            boolean dash = dashPredicate.apply(index, item);
            return lineStrokes.getStroke(strong, dash);
        }

        @Override
        public Font getSeriesLabelFont(int series) {
            return fontSupport.getSeriesFont(r.realIndexOf(series));
        }

        @Override
        public Font getObsLabelFont(int series, int item) {
            return fontSupport.getSeriesFont(r.realIndexOf(series));
        }

        @Override
        public boolean isObsHighlighted(int series, int item) {
            return revealObs ^ obsHighlighter.apply(r.realIndexOf(series), item);
        }

        @Override
        public boolean isObsLabelVisible(int series, int item) {
            return isElementVisible(TOOLTIP) && isRequested(r.realIndexOf(series), item);
        }
    }

    private abstract static class SeriesIndexResolver {

        abstract public int realIndexOf(int series);

        static SeriesIndexResolver create(final XYPlot plot, final int rendererIndex) {
            return new SeriesIndexResolver() {
                @Override
                public int realIndexOf(int series) {
                    return ((FilteredXYDataset) plot.getDataset(rendererIndex)).originalIndexOf(series);
                }
            };
        }
    }

    private static final class SeriesMapFactory {

        private int seriesCount;
        private RendererType[] renderers;
        private int[] plotIndexes;

        public SeriesMapFactory() {
            this.seriesCount = 0;
        }

        void update(int seriesCount, SeriesFunction<RendererType> seriesRenderer, SeriesFunction<Integer> plotDispatcher) {
            this.seriesCount = seriesCount;
            this.renderers = new RendererType[seriesCount];
            this.plotIndexes = new int[seriesCount];
            for (int series = 0; series < seriesCount; series++) {
                RendererType rendererType = seriesRenderer.apply(series);
                this.renderers[series] = rendererType != null ? rendererType : RendererType.LINE;
                Integer plotIndex = plotDispatcher.apply(series);
                this.plotIndexes[series] = plotIndex != null ? plotIndex : 0;
            }
        }

        int[] getSeriesMap(RendererType r, int plotIndex) {
            int size = 0;
            int[] result = new int[seriesCount];
            for (int series = 0; series < seriesCount; series++) {
                if (renderers[series] == r && plotIndexes[series] == plotIndex) {
                    result[size++] = series;
                }
            }
            return Arrays.copyOf(result, size);
        }
    }

    private static final class ChartNotification {

        private final JFreeChart chart;
        private final Deque<Boolean> notifyDeque;

        public ChartNotification(JFreeChart chart) {
            this.chart = chart;
            this.notifyDeque = new LinkedList<>();
        }

        public void suspend() {
            notifyDeque.addLast(chart.isNotify());
            chart.setNotify(false);
        }

        public void resume() {
            chart.setNotify(notifyDeque.removeLast());
        }

        public boolean isSuspended() {
            return !notifyDeque.isEmpty();
        }

        public void forceRefresh() {
            if (!isSuspended()) {
                chart.fireChartChanged();
            }
        }
    }

    private boolean isRequested(int series, int item) {
        switch (tooltipTrigger) {
            case HOVERING:
                return hoveredObs.equals(series, item);
            case SELECTION:
                return selectedObs.equals(series, item);
            case BOTH:
                return hoveredObs.equals(series, item) || selectedObs.equals(series, item);
        }
        throw new RuntimeException();
    }

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private boolean isInteractive() {
        return isEnabled();
    }

    private void enableObsTriggering() {
        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                if (isInteractive()) {
                    setSelectedObs(getObsIndex(event));
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
                if (isInteractive()) {
                    setHoveredObs(getObsIndex(event));
                }
            }

            private ObsIndex getObsIndex(ChartMouseEvent event) {
                if (event.getEntity() instanceof XYItemEntity) {
                    XYItemEntity xxx = (XYItemEntity) event.getEntity();
                    int series = ((FilteredXYDataset) xxx.getDataset()).originalIndexOf(xxx.getSeriesIndex());
                    int obs = xxx.getItem();
                    return ObsIndex.valueOf(series, obs);
                } else {
                    return ObsIndex.NULL;
                }
            }
        });
    }

    private void enableRevealObs() {
        chartPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isInteractive() && e.getKeyChar() == 'r') {
                    setRevealObs(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (isInteractive() && e.getKeyChar() == 'r') {
                    setRevealObs(false);
                }
            }
        });
    }

    private void enableSelection() {
        chartPanel.addMouseListener(new SelectionMouseListener(seriesSelectionModel, true) {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isInteractive()) {
                    super.mousePressed(e);
                }
            }

            @Override
            protected int getSelectionIndex(LegendItemEntity entity) {
                return entity != null ? dataset.indexOf(entity.getSeriesKey()) : -1;
            }
        });
        seriesSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    notification.forceRefresh();
                }
            }
        });
    }

    private void enableProperties() {
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                notification.suspend();
                switch (evt.getPropertyName()) {
                    case COLOR_SCHEME_SUPPORT_PROPERTY:
                        onColorSchemeSupportChange();
                        break;
                    case LINE_THICKNESS_PROPERTY:
                        onLineThicknessChange();
                        break;
                    case PERIOD_FORMAT_PROPERTY:
                        onPeriodFormatChange();
                        break;
                    case VALUE_FORMAT_PROPERTY:
                        onValueFormatChange();
                        break;
                    case SERIES_RENDERER_PROPERTY:
                        onSeriesRendererChange();
                        break;
                    case SERIES_FORMATTER_PROPERTY:
                        onSeriesFormatterChange();
                        break;
                    case SERIES_COLORIST_PROPERTY:
                        onSeriesColoristChange();
                        break;
                    case OBS_FORMATTER_PROPERTY:
                        onObsFormatterChange();
                        break;
                    case OBS_COLORIST_PROPERTY:
                        onObsColoristChange();
                        break;
                    case DASH_PREDICATE_PROPERTY:
                        onDashPredicateChange();
                        break;
                    case LEGEND_VISIBILITY_PREDICATE_PROPERTY:
                        onLegendVisibilityPredicateChange();
                        break;
                    case PLOT_DISPATCHER_PROPERTY:
                        onPlotDispatcherChange();
                        break;
                    case DATASET_PROPERTY:
                        onDatasetChange();
                        break;
                    case TITLE_PROPERTY:
                        onTitleChange();
                        break;
                    case NO_DATA_MESSAGE_PROPERTY:
                        onNoDataMessageChange();
                        break;
                    case PLOT_WEIGHTS_PROPERTY:
                        onPlotWeightsChange();
                        break;
                    case ELEMENT_VISIBLE_PROPERTY:
                        onElementVisibleChange();
                        break;
                    case CROSSHAIR_ORIENTATION_PROPERTY:
                        onCrosshairOrientationChange();
                        break;
                    case HOVERED_OBS_PROPERTY:
                        onHoveredObsChange();
                        break;
                    case SELECTED_OBS_PROPERTY:
                        onSelectedObsChange();
                        break;
                    case OBS_HIGHLIGHTER_PROPERTY:
                        onObsHighlighterChange();
                        break;
                    case TOOLTIP_TRIGGER_PROPERTY:
                        onTooltipTriggerChange();
                        break;
                    case CROSSHAIR_TRIGGER_PROPERTY:
                        onCrosshairTriggerChange();
                        break;
                    case REVEAL_OBS_PROPERTY:
                        onRevealObsChange();
                        break;
                    case "enabled":
                        boolean enabled = isEnabled();
                        chartPanel.setDomainZoomable(enabled);
                        chartPanel.setRangeZoomable(enabled);
                        break;
                    case "componentPopupMenu":
                        onComponentPopupMenuChange();
                        break;
                }
                notification.resume();
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Experimental code">
    private static abstract class FontSupport<F> {

        abstract public F getTitleFont();

        abstract public F getAxisFont();

        abstract public F getNoDataMessageFont();

        abstract public F getSeriesFont(int series);
    }

    private static abstract class SwingFontSupport extends FontSupport<Font> {
    }

    private static final class SwingFontSupportImpl extends SwingFontSupport {

        private final Font titleFont = new Font(Font.SANS_SERIF, Font.ITALIC, 13);
        private final Font axisFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
        private final Font noDataMessageFont = new Font(Font.SANS_SERIF, Font.ITALIC, 13);
        private final Font seriesFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

        @Override
        public Font getTitleFont() {
            return titleFont;
        }

        @Override
        public Font getAxisFont() {
            return axisFont;
        }

        @Override
        public Font getNoDataMessageFont() {
            return noDataMessageFont;
        }

        @Override
        public Font getSeriesFont(int series) {
            return seriesFont;
        }
    }
    //</editor-fold>
}
