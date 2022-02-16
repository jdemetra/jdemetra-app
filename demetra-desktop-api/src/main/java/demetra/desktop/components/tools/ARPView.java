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
import demetra.desktop.TsManager;
import demetra.desktop.components.TimeSeriesComponent;
import demetra.desktop.components.parts.*;
import demetra.desktop.jfreechart.TsCharts;
import ec.util.chart.ColorScheme.KnownColor;
import ec.util.chart.swing.ChartCommand;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.SwingColorSchemeSupport;
import demetra.desktop.jfreechart.MatrixChartCommand;
import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsInformationType;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jdplus.data.analysis.Periodogram;

/**
 * @author Jeremy Demortier
 */
public abstract class ARPView extends JComponent implements TimeSeriesComponent, HasTs, HasColorScheme {

    // CONSTANTS
    protected static final double TWO_PI = 2 * Math.PI;
    protected static final Stroke FREQ_MARKER_STROKE = new BasicStroke(5.0f);
    protected static final float FREQ_MARKER_ALPHA = .4f;
    // OTHER
    protected final ChartPanel chartPanel;
    protected ARPData data;

    @lombok.experimental.Delegate
    private final HasTs tsSupport = HasTsSupport.of(this::firePropertyChange, TsInformationType.Data);

    @lombok.experimental.Delegate
    private final HasColorScheme colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);

    private final HasColorSchemeResolver colorSchemeResolver = new HasColorSchemeResolver(colorScheme, this::onColorSchemeChange);

    protected ARPView() {
        this.chartPanel = Charts.newChartPanel(createARPChart());
        this.data = null;
        initComponents();
    }

    private void initComponents() {
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);

        setTransferHandler(HasTsSupport.newTransferHandler(this));

        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case TS_PROPERTY:
                    onTsChange();
                    break;
            }
        });

        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);

    }

    public void reset() {
        XYPlot plot = getPlot();
        plot.clearDomainMarkers();
        plot.clearRangeMarkers();

        if (data == null) {
            plot.setDataset(Charts.emptyXYDataset());
            chartPanel.getChart().getTitle().setText("");
        }
    }

    public void setData(@NonNull String name, @NonNegative double freq, @NonNull DoubleSeq values) {
        data = new ARPData(name, freq, values);
        onARPDataChange();
        onColorSchemeChange();
    }

    private void clear() {
        data = null;
        onARPDataChange();
        onColorSchemeChange();
    }

    public TsData requestData() {
        Ts ts = getTs();
        if (ts == null) {
            return null;
        }
        return ts.getData();
    }

    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLERS">
    private void onTsChange() {
        TsData tsdata = requestData();
        if (tsdata == null || tsdata.isEmpty()) {
            clear();
        } else {
            String name = getTs().getName();
            setData(name, tsdata.getAnnualFrequency(), tsdata.getValues());
        }
    }

    protected void onColorSchemeChange() {
        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();

        XYPlot plot = getPlot();
        plot.setBackgroundPaint(themeSupport.getPlotColor());
        plot.setDomainGridlinePaint(themeSupport.getGridColor());
        plot.setRangeGridlinePaint(themeSupport.getGridColor());
        chartPanel.getChart().setBackgroundPaint(themeSupport.getBackColor());

        plot.getRenderer().setBasePaint(themeSupport.getLineColor(KnownColor.BROWN));

        List<Marker> markers = new ArrayList<>();
        Collection rm = plot.getRangeMarkers(Layer.FOREGROUND);
        if (rm != null) {
            markers.addAll(rm);
        }
        Collection dm = plot.getDomainMarkers(Layer.FOREGROUND);
        if (dm != null) {
            markers.addAll(dm);
        }
        markers.stream()
                .filter((o) -> (o instanceof ExtValueMarker))
                .forEach((o) -> ((ExtValueMarker) o).applyColorScheme(themeSupport));
    }

    protected void onARPDataChange() {
        XYPlot plot = getPlot();
        reset();
        if (data == null || data.values == null) {
            return;
        }

        XYSeries series = computeSeries();

        plot.setDataset(new XYSeriesCollection(series));
        chartPanel.getChart().getTitle().setText(data.name);

        if (data.freq > 0) {
            double freq2 = data.freq / 2;

            for (int i = 1; i <= freq2; ++i) {
                double f = i * TWO_PI / data.freq;
                addFreqMarker(f, KnownColor.BLUE);
            }

            int ifreq = (int) data.freq;
            if (Math.abs(ifreq - data.freq) < demetra.math.Complex.EPS) {
                double[] tdfreq = Periodogram.getTradingDaysFrequencies(ifreq);
                if (tdfreq != null) {
                    for (double v : tdfreq) {
                        addFreqMarker(v, KnownColor.RED);
                    }
                }
            }
            configureChart(series);
        }
    }
    //</editor-fold>

    abstract protected XYSeries computeSeries();

    protected XYPlot getPlot() {
        return chartPanel.getChart().getXYPlot();
    }

    protected void addFreqMarker(double f, KnownColor basicColor) {
        ExtValueMarker vm = new ExtValueMarker(f, basicColor);
        vm.setStroke(FREQ_MARKER_STROKE);
        vm.setAlpha(FREQ_MARKER_ALPHA);
        getPlot().addDomainMarker(vm);
    }

    protected void configureChart(XYSeries series) {
        // Configure range axis
        double max = series.getMaxY();
        double min = series.getMinY();
        double inset;
        if (max == min) {
            inset = 1;
        } else {
            inset = 3 * (max - min) / 100;
        }
        getPlot().getRangeAxis().setRange(min - inset, max + inset);

        // Configure domain axis
        NumberAxis na = (NumberAxis) getPlot().getDomainAxis();
        na.setRange(0, Math.PI);
        na.setTickUnit(new PiNumberTickUnit(Math.PI / 2));
    }

    protected JMenu buildMenu() {
        JMenu result = new JMenu();

        result.add(MatrixChartCommand.copySeries(0, 0).toAction(chartPanel)).setText("Copy series");

        JMenu export = new JMenu("Export image to");
        export.add(ChartCommand.printImage().toAction(chartPanel)).setText("Printer...");
        export.add(ChartCommand.copyImage().toAction(chartPanel)).setText("Clipboard");
        export.add(ChartCommand.saveImage().toAction(chartPanel)).setText("File...");
        result.add(export);

        return result;
    }

    static JFreeChart createARPChart() {
        JFreeChart result = ChartFactory.createXYLineChart("", "", "", Charts.emptyXYDataset(), PlotOrientation.VERTICAL, false, false, false);
        result.setPadding(TsCharts.CHART_PADDING);
        result.getTitle().setFont(TsCharts.CHART_TITLE_FONT);

        XYPlot plot = result.getXYPlot();

        plot.setNoDataMessage("Drop data here");
        plot.getRangeAxis().setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);
        plot.getDomainAxis().setTickLabelPaint(TsCharts.CHART_TICK_LABEL_COLOR);

        ((XYLineAndShapeRenderer) plot.getRenderer()).setAutoPopulateSeriesPaint(false);

        return result;
    }

    @lombok.Value
    protected static class ARPData {

        String name;
        double freq;
        DoubleSeq values;
    }

    protected static class ExtValueMarker extends ValueMarker {

        final KnownColor color;

        public ExtValueMarker(double value, KnownColor color) {
            super(value);
            this.color = color;
        }

        void applyColorScheme(SwingColorSchemeSupport support) {
            setPaint(support.getAreaColor(color));
        }
    }
}
