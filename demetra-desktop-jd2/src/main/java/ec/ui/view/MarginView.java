/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import ec.nbdemetra.ui.OldTsUtil;
import demetra.ui.components.parts.HasColorScheme;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.tstoolkit.utilities.Jdk6;
import demetra.ui.jfreechart.TsCharts;
import ec.ui.chart.TsXYDatasets;
import demetra.ui.components.parts.HasChart.LinesThickness;
import demetra.ui.components.parts.HasObsFormat;
import demetra.ui.components.TimeSeriesComponent;
import demetra.ui.components.parts.HasColorSchemeResolver;
import demetra.ui.components.parts.HasColorSchemeSupport;
import demetra.ui.components.parts.HasObsFormatResolver;
import demetra.ui.datatransfer.DataTransfer;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.utilities.Arrays2;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.ChartCommand;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.SwingColorSchemeSupport;
import demetra.ui.components.parts.HasObsFormatSupport;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import nbbrd.design.SkipProcessing;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Layer;

/**
 *
 * @author Kristof Bayens
 */
@SwingComponent
public final class MarginView extends JComponent implements TimeSeriesComponent, HasColorScheme, HasObsFormat {

    // PROPERTIES
    @SkipProcessing(target = SwingProperty.class, reason = "to be refactored")
    @SwingProperty
    private static final String DATA_PROPERTY = "data";

    @SkipProcessing(target = SwingProperty.class, reason = "to be refactored")
    @SwingProperty
    private static final String PRECISION_MARKERS_VISIBLE_PROPERTY = "precisionMarkersVisible";

    // CONSTANTS
    private static final int MAIN_INDEX = 1;
    private static final int DIFFERENCE_INDEX = 0;
    private static final Stroke DATE_MARKER_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f);
    private static final float DATE_MARKER_ALPHA = 0.8f;
    private static final KnownColor MAIN_COLOR = KnownColor.RED;
    private static final KnownColor DIFFERENCE_COLOR = KnownColor.BLUE;
    private static final KnownColor DATE_MARKER_COLOR = KnownColor.ORANGE;

    // OTHER
    private final ChartPanel chartPanel;
    private MarginData data;
    private boolean precisionMarkersVisible;

    private final RevealObs revealObs;
    private static XYItemEntity highlight;

    @lombok.experimental.Delegate
    private final HasColorScheme colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);

    @lombok.experimental.Delegate
    private final HasObsFormat obsFormat = HasObsFormatSupport.of(this::firePropertyChange);

    private final HasObsFormatResolver obsFormatResolver;
    private final HasColorSchemeResolver colorSchemeResolver;

    public MarginView() {
        this.chartPanel = Charts.newChartPanel(createMarginViewChart());
        this.data = new MarginData(null, null, null, false, null);
        this.precisionMarkersVisible = false;
        this.revealObs = new RevealObs();
        this.obsFormatResolver = new HasObsFormatResolver(obsFormat, this::onDataFormatChange);
        this.colorSchemeResolver = new HasColorSchemeResolver(colorScheme, this::onColorSchemeChange);

        registerActions();

        Charts.enableFocusOnClick(chartPanel);

        chartPanel.addChartMouseListener(new HighlightChartMouseListener2());
        chartPanel.addKeyListener(revealObs);

        onDataFormatChange();
        onColorSchemeChange();
        onComponentPopupMenuChange();
        enableProperties();

        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }

    private void registerActions() {
        HasObsFormatSupport.registerActions(this, getActionMap());
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case COLOR_SCHEME_PROPERTY:
                    onColorSchemeChange();
                    break;
                case OBS_FORMAT_PROPERTY:
                    onDataFormatChange();
                    break;
                case DATA_PROPERTY:
                    onDataChange();
                    break;
                case PRECISION_MARKERS_VISIBLE_PROPERTY:
                    onPrecisionMarkersVisible();
                    break;
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    private void onDataFormatChange() {
        DateAxis domainAxis = (DateAxis) chartPanel.getChart().getXYPlot().getDomainAxis();
        try {
            DataFormat dataFormat = TsConverter.fromObsFormat(obsFormatResolver.resolve());
            domainAxis.setDateFormatOverride(dataFormat.newDateFormat());
        } catch (IllegalArgumentException ex) {
            // do nothing?
        }
    }

    private void onColorSchemeChange() {
        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();

        XYPlot plot = chartPanel.getChart().getXYPlot();
        plot.setBackgroundPaint(themeSupport.getPlotColor());
        plot.setDomainGridlinePaint(themeSupport.getGridColor());
        plot.setRangeGridlinePaint(themeSupport.getGridColor());
        chartPanel.getChart().setBackgroundPaint(themeSupport.getBackColor());

        XYLineAndShapeRenderer main = (XYLineAndShapeRenderer) plot.getRenderer(MAIN_INDEX);
        main.setBasePaint(themeSupport.getLineColor(MAIN_COLOR));

        XYDifferenceRenderer difference = ((XYDifferenceRenderer) plot.getRenderer(DIFFERENCE_INDEX));
        Color diffArea = SwingColorSchemeSupport.withAlpha(themeSupport.getAreaColor(DIFFERENCE_COLOR), 150);
        difference.setPositivePaint(diffArea);
        difference.setNegativePaint(diffArea);
        difference.setBasePaint(themeSupport.getLineColor(DIFFERENCE_COLOR));

        Collection<Marker> markers = (Collection<Marker>) plot.getDomainMarkers(Layer.FOREGROUND);
        if (!Jdk6.Collections.isNullOrEmpty(markers)) {
            Color markerColor = themeSupport.getLineColor(DATE_MARKER_COLOR);
            for (Marker o : markers) {
                o.setPaint(markerColor);
            }
        }

        Collection<Marker> intervalMarkers = (Collection<Marker>) plot.getDomainMarkers(Layer.BACKGROUND);
        if (!Jdk6.Collections.isNullOrEmpty(intervalMarkers)) {
            Color markerColor = themeSupport.getLineColor(KnownColor.ORANGE);
            for (Marker o : intervalMarkers) {
                o.setPaint(markerColor);
            }
        }
    }

    private void onDataChange() {
        chartPanel.getChart().setNotify(false);

        XYPlot plot = chartPanel.getChart().getXYPlot();

        plot.setDataset(MAIN_INDEX, TsXYDatasets.from("series", data.series));
        plot.setDataset(DIFFERENCE_INDEX, TsXYDatasets.builder().add("lower", data.lower).add("upper", data.upper).build());

        onPrecisionMarkersVisible();
        onDataFormatChange();

        chartPanel.getChart().setNotify(true);
    }

    private void onPrecisionMarkersVisible() {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        plot.clearDomainMarkers();
        addDateMarkers();
        if (precisionMarkersVisible) {
            addPrecisionMarkers();
        }
        onColorSchemeChange();
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        chartPanel.setPopupMenu(popupMenu != null ? popupMenu : buildMenu().getPopupMenu());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public void setData(TsData series, TsData lower, TsData upper, Date... markers) {
        setData(series, lower, upper, false, markers);
    }

    public void setData(TsData series, TsData lower, TsData upper, boolean multiplicative, Date... markers) {
        this.data = new MarginData(series, lower, upper, multiplicative, markers);
        firePropertyChange(DATA_PROPERTY, null, data);
    }

    private boolean isPrecisionMarkersVisible() {
        return precisionMarkersVisible;
    }

    private void setPrecisionMarkersVisible(boolean precisionMarkersVisible) {
        boolean old = this.precisionMarkersVisible;
        this.precisionMarkersVisible = precisionMarkersVisible;
        firePropertyChange(PRECISION_MARKERS_VISIBLE_PROPERTY, old, this.precisionMarkersVisible);
    }
    //</editor-fold>

    private void addDateMarkers() {
        XYPlot plot = chartPanel.getChart().getXYPlot();
        if (data.markers != null) {
            for (Date o : data.markers) {
                ValueMarker marker = new ValueMarker(new Day(o).getFirstMillisecond());
                marker.setStroke(DATE_MARKER_STROKE);
                marker.setAlpha(DATE_MARKER_ALPHA);
                plot.addDomainMarker(marker, Layer.FOREGROUND);
            }
        }
    }

    private void addPrecisionMarkers() {
        TsData tmp = data.series.fittoDomain(data.upper.getDomain());
        TsData values = data.multiplicative ? tmp.div(data.upper) : tmp.minus(data.upper);
        DescriptiveStatistics stats = new DescriptiveStatistics(values);
        double min = stats.getMin();
        double max = stats.getMax();
        if (max - min > 0) {
            XYPlot plot = chartPanel.getChart().getXYPlot();
            TsDomain domain = values.getDomain().extend(0, 1);
            for (int i = 0; i < domain.getLength() - 1; i++) {
                float val = (float) ((values.get(i) - min) / (max - min));
                IntervalMarker marker = new IntervalMarker(
                        domain.get(i).firstday().getTime().getTime(),
                        domain.get(i + 1).firstday().getTime().getTime());
                marker.setOutlineStroke(null);
                marker.setAlpha(1f - val);
                plot.addDomainMarker(marker, Layer.BACKGROUND);
            }
        }
    }

    private JFreeChart createMarginViewChart() {
        JFreeChart result = ChartFactory.createXYLineChart("", "", "", Charts.emptyXYDataset(), PlotOrientation.VERTICAL, false, false, false);
        result.setPadding(TsCharts.CHART_PADDING);

        XYPlot plot = result.getXYPlot();
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        LinesThickness linesThickness = LinesThickness.Thin;

        XYLineAndShapeRenderer main = new LineRenderer();
        plot.setRenderer(MAIN_INDEX, main);

        XYDifferenceRenderer difference = new XYDifferenceRenderer();
        difference.setAutoPopulateSeriesPaint(false);
        difference.setAutoPopulateSeriesStroke(false);
        difference.setBaseStroke(TsCharts.getNormalStroke(linesThickness));
        plot.setRenderer(DIFFERENCE_INDEX, difference);

        DateAxis domainAxis = new DateAxis();
        domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        domainAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setDomainAxis(domainAxis);

        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setRangeAxis(rangeAxis);

        return result;
    }

    private JMenu buildMenu() {
        JMenu result = new JMenu();

        result.add(new JCheckBoxMenuItem(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPrecisionMarkersVisible(!isPrecisionMarkersVisible());
            }
        })).setText("Show precision gradient");

        result.add(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TsCollection col = Stream.of(
                        OldTsUtil.toTs("series", data.series),
                        OldTsUtil.toTs("lower", data.lower),
                        OldTsUtil.toTs("upper", data.upper)
                ).collect(TsCollection.toTsCollection());
                Transferable t = DataTransfer.getDefault().fromTsCollection(col);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
            }
        }).setText("Copy all series");

        JMenu export = new JMenu("Export image to");
        export.add(ChartCommand.printImage().toAction(chartPanel)).setText("Printer...");
        export.add(ChartCommand.copyImage().toAction(chartPanel)).setText("Clipboard");
        export.add(ChartCommand.saveImage().toAction(chartPanel)).setText("File...");
        result.add(export);

        return result;
    }

    private static final class MarginData {

        final TsData series;
        final TsData lower;
        final TsData upper;
        final Date[] markers;
        final boolean multiplicative;

        public MarginData(TsData series, TsData lower, TsData upper, boolean multiplicative, Date[] markers) {
            this.series = series;
            this.lower = lower;
            this.upper = upper;
            this.markers = markers;
            this.multiplicative = multiplicative;
        }
    }

    private final class HighlightChartMouseListener2 implements ChartMouseListener {

        @Override
        public void chartMouseClicked(ChartMouseEvent event) {
        }

        @Override
        public void chartMouseMoved(ChartMouseEvent event) {
            if (event.getEntity() instanceof XYItemEntity) {
                XYItemEntity xxx = (XYItemEntity) event.getEntity();
                setHighlightedObs(xxx);
            } else {
                setHighlightedObs(null);
            }
        }
    }

    private void setHighlightedObs(XYItemEntity item) {
        if (item == null || highlight != item) {
            highlight = item;
            chartPanel.getChart().fireChartChanged();
        }
    }

    public final class RevealObs implements KeyListener {

        private boolean enabled = false;

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == 'r') {
                setEnabled(true);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyChar() == 'r') {
                setEnabled(false);
            }
        }

        private void setEnabled(boolean enabled) {
            if (this.enabled != enabled) {
                this.enabled = enabled;
                firePropertyChange("revealObs", !enabled, enabled);
                chartPanel.getChart().fireChartChanged();
            }
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    private static final Shape ITEM_SHAPE = new Ellipse2D.Double(-3, -3, 6, 6);

    private class LineRenderer extends XYLineAndShapeRenderer {

        public LineRenderer() {
            setBaseItemLabelsVisible(true);
            setAutoPopulateSeriesShape(false);
            setAutoPopulateSeriesFillPaint(false);
            setAutoPopulateSeriesOutlineStroke(false);
            setBaseShape(ITEM_SHAPE);
            setUseFillPaint(true);
        }

        @Override
        public boolean getItemShapeVisible(int series, int item) {
            return revealObs.isEnabled() || isObsHighlighted(series, item);
        }

        private boolean isObsHighlighted(int series, int item) {
            XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
            if (highlight != null && highlight.getDataset().equals(plot.getDataset(MAIN_INDEX))) {
                return highlight.getSeriesIndex() == series && highlight.getItem() == item;
            } else {
                return false;
            }
        }

        @Override
        public boolean isItemLabelVisible(int series, int item) {
            return isObsHighlighted(series, item);
        }

        @Override
        public Paint getSeriesPaint(int series) {
            return colorSchemeResolver.resolve().getLineColor(MAIN_COLOR);
        }

        @Override
        public Paint getItemPaint(int series, int item) {
            return colorSchemeResolver.resolve().getLineColor(MAIN_COLOR);
        }

        @Override
        public Paint getItemFillPaint(int series, int item) {
            return chartPanel.getChart().getPlot().getBackgroundPaint();
        }

        @Override
        public Stroke getSeriesStroke(int series) {
            return TsCharts.getStrongStroke(LinesThickness.Thin);
        }

        @Override
        public Stroke getItemOutlineStroke(int series, int item) {
            return TsCharts.getStrongStroke(LinesThickness.Thin);
        }

        @Override
        protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y, boolean negative) {
            String label = generateLabel();
            Font font = chartPanel.getFont();
            Paint paint = chartPanel.getChart().getPlot().getBackgroundPaint();
            Paint fillPaint = colorSchemeResolver.resolve().getLineColor(MAIN_COLOR);
            Stroke outlineStroke = AbstractRenderer.DEFAULT_STROKE;
            Charts.drawItemLabelAsTooltip(g2, x, y, 3d, label, font, paint, fillPaint, paint, outlineStroke);
        }

        private String generateLabel() {
            TsPeriod p = new TsPeriod(data.series.getFrequency(), new Date(highlight.getDataset().getX(0, highlight.getItem()).longValue()));
            String label = "Period : " + p + "\nValue : ";
            label += obsFormatResolver.resolve().numberFormatter().formatAsString(data.series.get(p));
            return label;
        }
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (!Arrays2.arrayEquals(oldValue, newValue)) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}
