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
import ec.util.chart.ColorSchemeSupport;
import ec.util.chart.ObsFunction;
import ec.util.chart.ObsIndex;
import ec.util.chart.ObsPredicate;
import ec.util.chart.TimeSeriesChart.Element;
import ec.util.chart.TimeSeriesChart.RendererType;
import ec.util.chart.SeriesFunction;
import ec.util.chart.TimeSeriesChart.CrosshairOrientation;
import ec.util.chart.TimeSeriesChart.DisplayTrigger;
import ec.util.chart.impl.AndroidColorScheme;
import static ec.util.chart.swing.ATimeSeriesChart.ELEMENT_VISIBLE_PROPERTY;
import static ec.util.chart.swing.SwingColorSchemeSupport.blend;
import static ec.util.chart.swing.SwingColorSchemeSupport.isDark;
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.AbstractAction;
import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BorderFactory.createLineBorder;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
    private final CustomTooltip customTooltip;
    private final SeriesState seriesState;

    public JTimeSeriesChartDemo() {
        this.chart = new JTimeSeriesChart();
        this.customTooltip = new CustomTooltip();
        this.seriesState = new SeriesState();

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
                boolean dash = chart.getDashPredicate().apply(series, obs);
                String value = chart.getValueFormatter().apply(series, obs);
                return "[" + chart.getSeriesFormatter().apply(series)
                        + "]\n" + chart.getPeriodFormatter().apply(series, obs)
                        + " : " + value + (dash ? ("\nForecast") : "");
            }
        });
        chart.setObsColorist(new ObsFunction<Color>() {
            @Override
            public Color apply(int series, int obs) {
                Color color = chart.getSeriesColorist().apply(series);
                return obs == 2 && color != null ? blend(color, isDark(color) ? Color.WHITE : Color.BLACK, .5) : color;
            }
        });
        chart.setDashPredicate(lastObsPredicate(3));
        chart.setPlotWeights(new int[]{2, 1});
        chart.setPlotDispatcher(seriesState.plotDispatcher());
        chart.setSeriesRenderer(seriesState.seriesRenderer());
        chart.setSeriesColorist(seriesState.seriesColorist(chart));

        chart.setComponentPopupMenu(newMenu().getPopupMenu());

        seriesState.withPlotIndex(0, 0, 0, 1);
        seriesState.withRendererType(0, RendererType.LINE, RendererType.LINE, RendererType.COLUMN);
        applyRandomData().executeSafely(chart);
        chart.setColorSchemeSupport(SwingColorSchemeSupport.from(new AndroidColorScheme.AndroidDarkColorScheme()));
        chart.setLineThickness(2);
        chart.setMouseWheelEnabled(true);

        customTooltip.enable(chart);

        setLayout(new BorderLayout());
        add(chart, BorderLayout.CENTER);
    }

    //<editor-fold defaultstate="collapsed" desc="Menus factories">
    private JMenu newMenu() {
        JMenu result = new JMenu();

        {
            final JMenu moveTo = new JMenu("Move to");
            moveTo.add(new MoveToPlot(0, seriesState).toAction(chart)).setText("First plot");
            moveTo.add(new MoveToPlot(1, seriesState).toAction(chart)).setText("Second plot");
            result.add(moveTo);

            final JMenu renderAs = new JMenu("Render as");
            for (RendererType o : chart.getSupportedRendererTypes()) {
                renderAs.add(new SetRenderer(o, seriesState).toAction(chart)).setText(o.name());
            }
            result.add(renderAs);

            final JMenu colorWith = new JMenu("Color with");
            for (ColorScheme.KnownColor o : ColorScheme.KnownColor.values()) {
                colorWith.add(new SetColor(o, seriesState).toAction(chart)).setText(o.name());
            }
            result.add(colorWith);

            final JSeparator separator = new JSeparator();
            result.add(separator);

            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    boolean hasSelection = !chart.getSeriesSelectionModel().isSelectionEmpty();
                    moveTo.setVisible(hasSelection && chart.getPlotWeights().length > 1);
                    renderAs.setVisible(hasSelection);
                    separator.setVisible(hasSelection);
                    colorWith.setVisible(hasSelection);
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

        result.add(newTooltipMenu());
        result.add(newCrosshairMenu());

        item = new JMenu("Highlighter");
        item.add(new JCheckBoxMenuItem(applyObsHighlighter(chart.getObsHighlighter()).toAction(chart))).setText("Hovered");
        item.add(new JCheckBoxMenuItem(applyObsHighlighter(highlightSelected(chart)).toAction(chart))).setText("Selected");
        item.add(new JCheckBoxMenuItem(applyObsHighlighter(highlightBoth(chart)).toAction(chart))).setText("Both");
        item.add(new JCheckBoxMenuItem(applyObsHighlighter(highlightSerie(chart)).toAction(chart))).setText("Serie");
        item.add(new JCheckBoxMenuItem(applyObsHighlighter(highlightObs(chart)).toAction(chart))).setText("Period");
        item.add(new JCheckBoxMenuItem(applyObsHighlighter(ObsPredicate.alwaysTrue()).toAction(chart))).setText("All");
        item.add(new JCheckBoxMenuItem(applyObsHighlighter(ObsPredicate.alwaysFalse()).toAction(chart))).setText("None");
        result.add(item);

        item = new JMenu("Show element");
        item.add(new JCheckBoxMenuItem(toggleElementVisibility(Element.TITLE).toAction(chart))).setText("Title");
        item.add(new JCheckBoxMenuItem(toggleElementVisibility(Element.LEGEND).toAction(chart))).setText("Legend");
        item.add(new JCheckBoxMenuItem(toggleElementVisibility(Element.AXIS).toAction(chart))).setText("Axis");
        result.add(item);

        result.addSeparator();
        result.add(resetZoom().toAction(chart)).setText("Reset zoom");
        result.add(newExportMenu());
        result.add(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chart.setEnabled(!chart.isEnabled());
            }
        }).setText("Enable/Disable");
        result.add(newConfigureMenu());

        return result;
    }

    private JMenu newTooltipMenu() {
        JMenu result = new JMenu("Tooltip");

        result.add(new JCheckBoxMenuItem(toggleElementVisibility(Element.TOOLTIP).toAction(chart))).setText("Default Tooltip");
        result.add(new JCheckBoxMenuItem(new ToggleCustomTooltip(chart).toAction(customTooltip))).setText("Custom tooltip");

        final JMenu trigger = new JMenu("Trigger");
        for (DisplayTrigger o : DisplayTrigger.values()) {
            trigger.add(new JCheckBoxMenuItem(applyTooltipTrigger(o).toAction(chart))).setText(o.name());
        }
        result.add(trigger);

        chart.addPropertyChangeListener(ELEMENT_VISIBLE_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                boolean enabled = chart.isElementVisible(Element.TOOLTIP);
                trigger.setEnabled(enabled);
            }
        });

        return result;
    }

    private JMenu newCrosshairMenu() {
        JMenu result = new JMenu("Crosshair");

        result.add(new JCheckBoxMenuItem(toggleElementVisibility(Element.CROSSHAIR).toAction(chart))).setText("Enabled");

        final JMenu orientation = new JMenu("Orientation");
        for (CrosshairOrientation o : CrosshairOrientation.values()) {
            orientation.add(new JCheckBoxMenuItem(applyCrosshairOrientation(o).toAction(chart))).setText(o.name());
        }
        orientation.setEnabled(false);
        result.add(orientation);

        final JMenu trigger = new JMenu("Trigger");
        for (DisplayTrigger o : DisplayTrigger.values()) {
            trigger.add(new JCheckBoxMenuItem(applyTooltipTrigger(o).toAction(chart))).setText(o.name());
        }
        trigger.setEnabled(false);
        result.add(trigger);

        chart.addPropertyChangeListener(ELEMENT_VISIBLE_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                boolean enabled = chart.isElementVisible(Element.CROSSHAIR);
                orientation.setEnabled(enabled);
                trigger.setEnabled(enabled);
            }
        });

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

        result.add(new JCheckBoxMenuItem(applyWeights(1).toAction(chart))).setText("Single chart");
        result.add(new JCheckBoxMenuItem(applyWeights(2, 1).toAction(chart))).setText("Dual chart");

        result.addSeparator();
        result.add(editTitle("New title:").toAction(chart)).setText("Title...");
        result.add(editNoDataMessage("New no-data message:").toAction(chart)).setText("No-data message...");
        result.add(editPeriodFormat("New period format:").toAction(chart)).setText("Period format...");
        result.add(editValueFormat("New value format:").toAction(chart)).setText("Value format...");

        return result;
    }
    //</editor-fold>

    private ObsPredicate lastObsPredicate(final int count) {
        return new ObsPredicate() {
            @Override
            public boolean apply(int series, int obs) {
                return obs >= chart.getDataset().getItemCount(series) - count;
            }
        };
    }

    private static ObsPredicate highlightSelected(final JTimeSeriesChart chart) {
        return new ObsPredicate() {
            @Override
            public boolean apply(int series, int obs) {
                return chart.getSelectedObs().equals(series, obs);
            }
        };
    }

    private static ObsPredicate highlightBoth(final JTimeSeriesChart chart) {
        return new ObsPredicate() {
            @Override
            public boolean apply(int series, int obs) {
                return chart.getHoveredObs().equals(series, obs)
                        || chart.getSelectedObs().equals(series, obs);
            }
        };
    }

    private static ObsPredicate highlightSerie(final JTimeSeriesChart chart) {
        return new ObsPredicate() {
            @Override
            public boolean apply(int series, int obs) {
                return chart.getHoveredObs().getSeries() == series;
            }
        };
    }

    private static ObsPredicate highlightObs(final JTimeSeriesChart chart) {
        return new ObsPredicate() {
            @Override
            public boolean apply(int series, int obs) {
                return chart.getHoveredObs().getObs() == obs;
            }
        };
    }

    private static final class MoveToPlot extends SeriesStateCommand<Integer> {

        public MoveToPlot(int plotIndex, SeriesState seriesState) {
            super(plotIndex, seriesState);
        }

        @Override
        protected void putValue(int series) {
            seriesState.withPlotIndex(series, value);
        }

        @Override
        protected void update(JTimeSeriesChart chart) {
            chart.setPlotDispatcher(seriesState.plotDispatcher());
        }
    }

    private static final class SetRenderer extends SeriesStateCommand<RendererType> {

        public SetRenderer(RendererType value, SeriesState seriesState) {
            super(value, seriesState);
        }

        @Override
        protected void putValue(int series) {
            seriesState.withRendererType(series, value);
        }

        @Override
        protected void update(JTimeSeriesChart chart) {
            chart.setSeriesRenderer(seriesState.seriesRenderer());
        }
    }

    private static final class SetColor extends SeriesStateCommand<ColorScheme.KnownColor> {

        public SetColor(ColorScheme.KnownColor color, SeriesState seriesState) {
            super(color, seriesState);
        }

        @Override
        protected void putValue(int series) {
            seriesState.withKnownColor(series, value);
        }
    }

    private static abstract class SeriesStateCommand<X> extends JTimeSeriesChartCommand {

        protected final X value;
        protected final SeriesState seriesState;

        protected SeriesStateCommand(X value, SeriesState seriesState) {
            this.value = value;
            this.seriesState = seriesState;
        }

        abstract protected void putValue(int series);

        protected void update(JTimeSeriesChart chart) {
            chart.invalidate();
        }

        @Override
        public void execute(JTimeSeriesChart chart) throws Exception {
            ListSelectionModel selectionModel = chart.getSeriesSelectionModel();
            for (int series = 0; series < chart.getDataset().getSeriesCount(); series++) {
                if (selectionModel.isSelectedIndex(series)) {
                    putValue(series);
                }
            }
            update(chart);
        }

        @Override
        public boolean isEnabled(JTimeSeriesChart chart) {
            return !chart.getSeriesSelectionModel().isSelectionEmpty();
        }
    }

    private static final class SeriesState {

        private final Map<Integer, Map<Class<?>, Object>> data = new HashMap<>();

        public SeriesState withPlotIndex(int series, int... list) {
            for (int i = 0; i < list.length; i++) {
                putValue(series + i, Integer.class, list[i]);
            }
            return this;
        }

        public SeriesState withRendererType(int series, RendererType... list) {
            for (int i = 0; i < list.length; i++) {
                putValue(series + i, RendererType.class, list[i]);
            }
            return this;
        }

        public SeriesState withKnownColor(int series, ColorScheme.KnownColor... list) {
            for (int i = 0; i < list.length; i++) {
                putValue(series + i, ColorScheme.KnownColor.class, list[i]);
            }
            return this;
        }

        private <X> void putValue(int series, @Nonnull Class<X> clazz, @Nullable X value) {
            Map<Class<?>, Object> tmp = data.get(series);
            if (tmp == null) {
                tmp = new HashMap<>();
                data.put(series, tmp);
            }
            tmp.put(clazz, value);
        }

        @Nullable
        private <X> X getValue(int series, @Nonnull Class<X> clazz) {
            Map<Class<?>, Object> tmp = data.get(series);
            return tmp != null ? clazz.cast(tmp.get(clazz)) : null;
        }

        public SeriesFunction<Integer> plotDispatcher() {
            return new SeriesFunction<Integer>() {
                @Override
                public Integer apply(int series) {
                    return getValue(series, Integer.class);
                }
            };
        }

        public SeriesFunction<RendererType> seriesRenderer() {
            return new SeriesFunction<RendererType>() {
                @Override
                public RendererType apply(int series) {
                    return getValue(series, RendererType.class);
                }
            };
        }

        public SeriesFunction<Color> seriesColorist(final JTimeSeriesChart chart) {
            return new SeriesFunction<Color>() {
                @Override
                public Color apply(int series) {
                    ColorScheme.KnownColor tmp = getValue(series, ColorScheme.KnownColor.class);
                    return tmp != null ? chart.getColorSchemeSupport().getLineColor(tmp) : chart.getColorSchemeSupport().getLineColor(series);
                }
            };
        }
    }

    private static final class CustomTooltip extends JLabel {

        private Popup popup;

        public CustomTooltip() {
            this.popup = null;
            setEnabled(false);
        }

        public void enable(final JTimeSeriesChart chart) {
            updateColors(chart);
            chart.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    switch (evt.getPropertyName()) {
                        case JTimeSeriesChart.TOOLTIP_TRIGGER_PROPERTY:
                            if (popup != null) {
                                popup.hide();
                            }
                            break;
                        case JTimeSeriesChart.HOVERED_OBS_PROPERTY:
                            if (isEnabled() && chart.getTooltipTrigger() != DisplayTrigger.SELECTION) {
                                updateCustomTooltip(chart, chart.getObsExistPredicate().apply(chart.getHoveredObs()));
                            }
                            break;
                        case JTimeSeriesChart.SELECTED_OBS_PROPERTY:
                            if (isEnabled() && chart.getTooltipTrigger() != DisplayTrigger.HOVERING) {
                                updateCustomTooltip(chart, chart.getObsExistPredicate().apply(chart.getSelectedObs()));
                            }
                            break;
                        case JTimeSeriesChart.COLOR_SCHEME_SUPPORT_PROPERTY:
                            updateColors(chart);
                            break;
                    }
                }
            });
        }

        private void updateColors(JTimeSeriesChart chart) {
            ColorSchemeSupport<? extends Color> csc = chart.getColorSchemeSupport();
            setOpaque(true);
            setBackground(csc.getBackColor());
            setForeground(csc.getTextColor());
            setBorder(createCompoundBorder(createLineBorder(csc.getGridColor(), 1), createEmptyBorder(5, 5, 5, 5)));
        }

        private void updateCustomTooltip(JTimeSeriesChart chart, boolean visible) {
            if (popup != null) {
                popup.hide();
            }
            if (visible) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                popup = PopupFactory.getSharedInstance().getPopup(chart, getCustomTooltip(chart), p.x + 5, p.y + 5);
                popup.show();
            }
        }

        private Component getCustomTooltip(JTimeSeriesChart chart) {
            ObsIndex o = chart.getHoveredObs();
            String serie = chart.getSeriesFormatter().apply(o.getSeries());
            String value = chart.getValueFormatter().apply(o);
            String period = chart.getPeriodFormatter().apply(o);
            boolean forecast = chart.getDashPredicate().apply(o);
            Color color = chart.getColorSchemeSupport().getLineColor(o.getSeries());
            setText("<html><b>" + serie + "</b><br>" + period + ": " + value);
            setIcon(forecast ? FontAwesome.FA_REFRESH.getSpinningIcon(this, color, 24f) : getFA(o.getSeries()).getIcon(color, 24f));
            return this;
        }

        private FontAwesome getFA(int series) {
            FontAwesome[] tmp = FontAwesome.values();
            return tmp[series % tmp.length];
        }
    }

    private static final class ToggleCustomTooltip extends JCommand<CustomTooltip> {

        private final JTimeSeriesChart chart;

        public ToggleCustomTooltip(JTimeSeriesChart chart) {
            this.chart = chart;
        }

        @Override
        public void execute(CustomTooltip component) throws Exception {
            component.setEnabled(!component.isEnabled());
            chart.setElementVisible(Element.TOOLTIP, !component.isEnabled());
        }

        @Override
        public boolean isSelected(CustomTooltip component) {
            return component.isEnabled();
        }

        @Override
        public ActionAdapter toAction(CustomTooltip component) {
            return super.toAction(component).withWeakPropertyChangeListener(component);
        }
    }
}
