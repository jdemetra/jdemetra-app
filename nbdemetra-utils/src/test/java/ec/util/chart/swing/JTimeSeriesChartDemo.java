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
package ec.util.chart.swing;

import static ec.util.chart.swing.JTimeSeriesChartCommand.*;
import ec.util.chart.ColorScheme;
import ec.util.chart.ObsFunction;
import ec.util.chart.ObsPredicate;
import ec.util.chart.TimeSeriesChart.Element;
import ec.util.chart.TimeSeriesChart.RendererType;
import ec.util.chart.SeriesFunction;
import ec.util.chart.TimeSeriesChart.CrosshairType;
import ec.util.chart.impl.AndroidColorScheme;
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jfree.data.xy.IntervalXYDataset;

/**
 *
 * @author Philippe Charles
 */
public final class JTimeSeriesChartDemo extends JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(JTimeSeriesChartDemo.class)
                .title("Time Series Chart Demo")
                .icons(new Callable<List<? extends Image>>() {
                    @Override
                    public List<? extends Image> call() throws Exception {
                        return FontAwesome.FA_LINE_CHART.getImages(Color.BLACK, 16f, 32f, 64f);
                    }
                })
                .logLevel(Level.FINE)
                .launch();
    }

    private final JTimeSeriesChart chart;

    public JTimeSeriesChartDemo() {
        this.chart = new JTimeSeriesChart();

        chart.setTitle("Some random data");
        chart.setNoDataMessage("No data available ?");
        chart.setSeriesFormatter(new SeriesFunction<String>() {
            @Override
            public String apply(int series) {
                return "Series n°" + (series + 1);
            }
        });
        chart.setObsFormatter(new ObsFunction<String>() {
            @Override
            public String apply(int series, int obs) {
                IntervalXYDataset dataset = chart.getDataset();
                boolean dash = chart.getDashPredicate().apply(series, obs);
                String value = chart.getValueFormat().format(dataset.getY(series, obs));
                return "[" + chart.getSeriesFormatter().apply(series)
                        + "]\n" + chart.getPeriodFormat().format(new Date(dataset.getX(series, obs).longValue()))
                        + " : " + value + (dash ? ("\nForecast") : "");
            }
        });
        chart.setDashPredicate(last3ObsPredicate());
        chart.setPlotWeights(new int[]{2, 1});

        chart.setPopupMenu(newMenu().getPopupMenu());

        setLayout(new BorderLayout());
        add(chart, BorderLayout.CENTER);

        applyRandomData().executeSafely(chart);
        chart.setPlotDispatcher(SeriesFunction.array(0, 0, 1));
        chart.setSeriesRenderer(SeriesFunction.array(RendererType.LINE, RendererType.LINE, RendererType.COLUMN));
        chart.setColorSchemeSupport(SwingColorSchemeSupport.from(new AndroidColorScheme.AndroidDarkColorScheme()));
        chart.setLineThickness(2);
    }

    private JMenu newMenu() {
        JMenu result = new JMenu();

        {
            final JMenu moveTo = new JMenu("Move to");
            moveTo.add(new MoveToPlot(0).toAction(chart)).setText("First plot");
            moveTo.add(new MoveToPlot(1).toAction(chart)).setText("Second plot");
            result.add(moveTo);

            final JMenu renderAs = new JMenu("Render as");
            for (RendererType o : chart.getSupportedRendererTypes()) {
                renderAs.add(new SetRenderer(o).toAction(chart)).setText(o.name());
            }
            result.add(renderAs);

            final JSeparator separator = new JSeparator();
            result.add(separator);

            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    boolean hasSelection = !chart.getSeriesSelectionModel().isSelectionEmpty();
                    moveTo.setVisible(hasSelection && chart.getPlotWeights().length > 1);
                    renderAs.setVisible(hasSelection);
                    separator.setVisible(hasSelection);
                }
            };
            r.run();
            chart.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    String p = evt.getPropertyName();
                    if (p.equals(JTimeSeriesChart.PLOT_WEIGHTS_PROPERTY)) {
                        r.run();
                    }
                }
            });
            chart.getSeriesSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    r.run();
                }
            });
        }

        JMenu item;

        item = new JMenu("Data");
        item.add(applyRandomData().toAction(chart)).setText("Random data");
        item.add(applyDataset(SomeTimeSeries.getCol1()).toAction(chart)).setText("AutoRangeIncludesZero?");
        result.add(item);

        result.add(selectAll().toAction(chart)).setText("Select all");
        result.add(clearDataset().toAction(chart)).setText("Clear");

        result.addSeparator();
        result.add(new JCheckBoxMenuItem(toggleElementVisibility(Element.TITLE).toAction(chart))).setText("Show title");
        result.add(new JCheckBoxMenuItem(toggleElementVisibility(Element.LEGEND).toAction(chart))).setText("Show legend");

        item = new JMenu("Color scheme");
        for (ColorScheme o : ServiceLoader.load(ColorScheme.class)) {
            JMenuItem subItem = item.add(new JCheckBoxMenuItem(applyColorScheme(o).toAction(chart)));
            subItem.setText(o.getDisplayName());
            subItem.setIcon(new ColorSchemeIcon(o));
        }
        result.add(item);

        item = new JMenu("Line thickness");
        item.add(new JCheckBoxMenuItem(applyLineThickness(1f).toAction(chart))).setText("Thin");
        item.add(new JCheckBoxMenuItem(applyLineThickness(2f).toAction(chart))).setText("Thick");
        result.add(item);

        item = new JMenu("Crosshair type");
        item.add(new JCheckBoxMenuItem(applyCrosshairType(CrosshairType.NONE).toAction(chart))).setText("None");
        item.add(new JCheckBoxMenuItem(applyCrosshairType(CrosshairType.DOMAIN).toAction(chart))).setText("Domain");
        item.add(new JCheckBoxMenuItem(applyCrosshairType(CrosshairType.RANGE).toAction(chart))).setText("Range");
        item.add(new JCheckBoxMenuItem(applyCrosshairType(CrosshairType.BOTH).toAction(chart))).setText("Both");
        result.add(item);

        result.addSeparator();
        result.add(resetZoom().toAction(chart)).setText("Reset zoom");
        result.add(newExportMenu());
        result.add(newConfigureMenu());

        return result;
    }

    private JMenu newExportMenu() {
        JMenu result = new JMenu("Export image to");
        result.add(printImage().toAction(chart)).setText("Printer...");
        result.add(copyImage().toAction(chart)).setText("Clipboard");
        result.add(saveImage().toAction(chart)).setText("File...");
        return result;
    }

    private JMenu newConfigureMenu() {
        JMenu result = new JMenu("Configure");

        result.add(new JCheckBoxMenuItem(toggleElementVisibility(Element.AXIS).toAction(chart))).setText("Show axis");

        result.addSeparator();
        result.add(new JCheckBoxMenuItem(applyWeights(1).toAction(chart))).setText("Single chart");
        result.add(new JCheckBoxMenuItem(applyWeights(2, 1).toAction(chart))).setText("Dual chart");

        result.addSeparator();
        result.add(editTitle("New title:").toAction(chart)).setText("Title...");
        result.add(editNoDataMessage("New no-data message:").toAction(chart)).setText("No-data message...");
        result.add(editPeriodFormat("New period format:").toAction(chart)).setText("Period format...");
        result.add(editValueFormat("New value format:").toAction(chart)).setText("Value format...");

        return result;
    }

    private ObsPredicate last3ObsPredicate() {
        return new ObsPredicate() {
            @Override
            public boolean apply(int series, int obs) {
                return obs >= chart.getDataset().getItemCount(series) - 3;
            }
        };
    }

    private static final class MoveToPlot extends SeriesFunctionCommand<Integer> {

        public MoveToPlot(int plotIndex) {
            super(plotIndex, 0);
        }

        @Override
        protected SeriesFunction<Integer> get(JTimeSeriesChart chart) {
            return chart.getPlotDispatcher();
        }

        @Override
        protected void set(JTimeSeriesChart chart, SeriesFunction<Integer> function) {
            chart.setPlotDispatcher(function);
        }
    }

    private static final class SetRenderer extends SeriesFunctionCommand<RendererType> {

        public SetRenderer(RendererType value) {
            super(value, RendererType.LINE);
        }

        @Override
        protected SeriesFunction<RendererType> get(JTimeSeriesChart chart) {
            return chart.getSeriesRenderer();
        }

        @Override
        protected void set(JTimeSeriesChart chart, SeriesFunction<RendererType> function) {
            chart.setSeriesRenderer(function);
        }
    }

    private abstract static class SeriesFunctionCommand<X> extends JTimeSeriesChartCommand {

        final X value;
        final X defaultValue;

        public SeriesFunctionCommand(X value, X defaultValue) {
            this.value = value;
            this.defaultValue = defaultValue;
        }

        @Override
        public void execute(JTimeSeriesChart chart) {
            int seriesCount = chart.getDataset().getSeriesCount();
            List<X> result = new ArrayList<>(seriesCount);
            SeriesFunction<X> function = get(chart);
            ListSelectionModel selectionModel = chart.getSeriesSelectionModel();
            for (int series = 0; series < seriesCount; series++) {
                result.add(selectionModel.isSelectedIndex(series) ? value : function.apply(series));
            }
            set(chart, SeriesFunction.array((X[]) result.toArray()));
        }

        @Override
        public boolean isEnabled(JTimeSeriesChart chart) {
            ListSelectionModel selectionModel = chart.getSeriesSelectionModel();
            if (selectionModel.isSelectionEmpty()) {
                return false;
            }
            return !all(selectionModel, get(chart));
        }

        boolean all(ListSelectionModel selectionModel, SeriesFunction<X> function) {
            int min = selectionModel.getMinSelectionIndex();
            int max = selectionModel.getMaxSelectionIndex();
            for (int series = min; series <= max; series++) {
                if (selectionModel.isSelectedIndex(series)) {
                    X result = function.apply(series);
                    if ((result != null ? result : defaultValue) != value) {
                        return false;
                    }
                }
            }
            return true;
        }

        abstract protected SeriesFunction<X> get(JTimeSeriesChart chart);

        abstract protected void set(JTimeSeriesChart chart, SeriesFunction<X> function);
    }
}
