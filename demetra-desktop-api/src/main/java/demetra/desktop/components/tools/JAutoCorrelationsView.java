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
package demetra.desktop.components.tools;

import demetra.data.DoubleSeq;
import demetra.desktop.components.parts.HasColorScheme;
import demetra.desktop.components.TimeSeriesComponent;
import demetra.desktop.components.parts.HasColorSchemeResolver;
import demetra.desktop.components.parts.HasColorSchemeSupport;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.jfreechart.TsCharts;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.ChartCommand;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.SwingColorSchemeSupport;
import demetra.desktop.jfreechart.MatrixChartCommand;
import demetra.desktop.util.Collections2;
import demetra.util.Arrays2;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Stroke;
import java.util.Collection;
import java.util.function.IntToDoubleFunction;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import demetra.stats.AutoCovariances;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;

/**
 *
 * @author Demortier Jeremy
 */
@SwingComponent
public final class JAutoCorrelationsView extends JComponent implements TimeSeriesComponent, HasColorScheme {

    public enum ACKind {

        Normal, Partial
    }

    // CONSTANTS
    private static final Stroke MARKER_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f);
    private static final float MARKER_ALPHA = 0.8f;
    private static final KnownColor NORMAL_COLOR = KnownColor.BLUE;
    private static final KnownColor PARTIAL_COLOR = KnownColor.BROWN;
    private static final KnownColor MARKER_COLOR = KnownColor.GREEN;

    // PROPERTIES DEFINITIONS
    @SwingProperty
    public static final String LENGTH_PROPERTY = "length";

    @SwingProperty
    public static final String KIND_PROPERTY = "kind";

    @SwingProperty
    public static final String MEAN_CORRECTION_PROPERTY = "meanCorrection";

    @SwingProperty
    public static final String SAMPLE_PROPERTY = "sample";

    // DEFAULT PROPERTIES
    private static final int DEFAULT_LENGTH = 60;
    private static final ACKind DEFAULT_KIND = ACKind.Normal;
    private static final boolean DEFAULT_MEAN_CORRECTION = true;

    // PROPERTIES
    private int length;
    private ACKind kind;
    private boolean meanCorrection;
    private DoubleSeq sample;

    // OTHER
    private final ChartPanel chartPanel;

    @lombok.experimental.Delegate
    private final HasColorScheme colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);

    private final HasColorSchemeResolver colorSchemeResolver = new HasColorSchemeResolver(colorScheme, this::onColorSchemeChange);

    public JAutoCorrelationsView() {
        this.length = DEFAULT_LENGTH;
        this.kind = DEFAULT_KIND;
        this.meanCorrection = DEFAULT_MEAN_CORRECTION;
        this.sample = DoubleSeq.empty();

        this.chartPanel = Charts.newChartPanel(createAutoCorrelationsViewChart());

        onColorSchemeChange();
        onComponentPopupMenuChange();
        enableProperties();

        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case LENGTH_PROPERTY:
                    onDataChange();
                    break;
                case KIND_PROPERTY:
                    onDataChange();
                    break;
                case MEAN_CORRECTION_PROPERTY:
                    onDataChange();
                    break;
                case SAMPLE_PROPERTY:
                    onDataChange();
                    break;
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    private void onColorSchemeChange() {
        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();

        XYPlot plot = chartPanel.getChart().getXYPlot();
        plot.setBackgroundPaint(themeSupport.getPlotColor());
        plot.setDomainGridlinePaint(themeSupport.getGridColor());
        plot.setRangeGridlinePaint(themeSupport.getGridColor());
        chartPanel.getChart().setBackgroundPaint(themeSupport.getBackColor());

        XYItemRenderer renderer = plot.getRenderer();
        KnownColor color = ACKind.Normal == kind ? NORMAL_COLOR : PARTIAL_COLOR;
        renderer.setBasePaint(themeSupport.getAreaColor(color));
        renderer.setBaseOutlinePaint(themeSupport.getLineColor(color));

        Collection<Marker> markers = (Collection<Marker>) plot.getDomainMarkers(Layer.FOREGROUND);
        if (! Collections2.isNullOrEmpty(markers)) {
            Color markerColor = themeSupport.getLineColor(MARKER_COLOR);
            for (Marker o : markers) {
                o.setPaint(markerColor);
            }
        }
    }

    private void onDataChange() {
        chartPanel.getChart().setTitle(ACKind.Normal == kind ? "Autocorrelations" : "Partial autocorrelations");
        NumberAxis domainAxis = (NumberAxis) chartPanel.getChart().getXYPlot().getDomainAxis();
        domainAxis.setRange(0, length);
        domainAxis.setTickUnit(new NumberTickUnit(length / 6));

        if (sample == null || sample.isEmpty()) {
            chartPanel.getChart().getXYPlot().setDataset(Charts.emptyXYDataset());
        } else {
            double mean = 0;
            if (meanCorrection) {
                mean = sample.average();
            }

            double[] ac = new double[length];
            IntToDoubleFunction fn = AutoCovariances.autoCorrelationFunction(sample, mean);
            if (kind == ACKind.Normal) {
                for (int i = 0; i < length; ++i) {
                    ac[i] = fn.applyAsDouble(i + 1);
                }
            } else {
                ac = AutoCovariances.partialAutoCorrelations(fn, length);
            }

            XYSeries series = new XYSeries("");
            for (int i = 0; i < ac.length; ++i) {
                series.add(i + 1, ac[i]);
            }

            XYPlot plot = chartPanel.getChart().getXYPlot();
            plot.clearRangeMarkers();
            plot.setDataset(new XYBarDataset(new XYSeriesCollection(series), 1));

            double z = 2.0d / Math.sqrt(sample.length());
            for (double o : new double[]{z, -z}) {
                ValueMarker marker = new ValueMarker(o);
                marker.setStroke(MARKER_STROKE);
                marker.setAlpha(MARKER_ALPHA);
                plot.addRangeMarker(marker);
            }

            onColorSchemeChange();
        }
    }

    private void onComponentPopupMenuChange() {
        JPopupMenu popupMenu = getComponentPopupMenu();
        chartPanel.setComponentPopupMenu(popupMenu != null ? popupMenu : buildMenu(chartPanel).getPopupMenu());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="GETTERS/SETTERS">
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        int old = this.length;
        this.length = length >= 0 ? length : DEFAULT_LENGTH;
        firePropertyChange(LENGTH_PROPERTY, old, this.length);
    }

    public ACKind getKind() {
        return kind;
    }

    public void setKind(ACKind kind) {
        ACKind old = this.kind;
        this.kind = kind != null ? kind : DEFAULT_KIND;
        firePropertyChange(KIND_PROPERTY, old, this.kind);
    }

    public boolean isMeanCorrection() {
        return meanCorrection;
    }

    public void setMeanCorrection(boolean meanCorrection) {
        boolean old = this.meanCorrection;
        this.meanCorrection = meanCorrection;
        firePropertyChange(MEAN_CORRECTION_PROPERTY, old, this.meanCorrection);
    }

    public void setSample(@NonNull DoubleSeq data) {
        DoubleSeq old = this.sample;
        this.sample = data;
        firePropertyChange(SAMPLE_PROPERTY, old, this.sample);
    }

    public DoubleSeq getSample() {
        return sample;
    }

    //</editor-fold>

    private static JFreeChart createAutoCorrelationsViewChart() {
        JFreeChart result = ChartFactory.createXYBarChart("", "", false, "", Charts.emptyXYDataset(), PlotOrientation.VERTICAL, false, false, false);
        result.getTitle().setFont(TsCharts.CHART_TITLE_FONT);
        result.setPadding(TsCharts.CHART_PADDING);

        XYPlot plot = result.getXYPlot();

        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(true);
        renderer.setAutoPopulateSeriesPaint(false);
        renderer.setAutoPopulateSeriesOutlinePaint(false);

        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setRangeAxis(rangeAxis);

        NumberAxis domainAxis = new NumberAxis();
        domainAxis.setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.setDomainAxis(domainAxis);

        return result;
    }

    private static JMenu buildMenu(ChartPanel chartPanel) {
        JMenu result = new JMenu();

        result.add(MatrixChartCommand.copySeries(0, 0).toAction(chartPanel)).setText("Copy series");

        JMenu export = new JMenu("Export image to");
        export.add(ChartCommand.printImage().toAction(chartPanel)).setText("Printer...");
        export.add(ChartCommand.copyImage().toAction(chartPanel)).setText("Clipboard");
        export.add(ChartCommand.saveImage().toAction(chartPanel)).setText("File...");
        result.add(export);

        return result;
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (!Arrays2.arrayEquals(oldValue, newValue)) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
}
