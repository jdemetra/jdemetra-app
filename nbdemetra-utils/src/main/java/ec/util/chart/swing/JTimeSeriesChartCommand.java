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
package ec.util.chart.swing;

import ec.util.chart.TimeSeriesChart.RendererType;
import ec.util.chart.SeriesFunction;
import ec.util.chart.ColorScheme;
import ec.util.chart.ColorSchemeSupport;
import ec.util.chart.ObsPredicate;
import ec.util.chart.SeriesPredicate;
import ec.util.chart.TimeSeriesChart.CrosshairOrientation;
import ec.util.chart.TimeSeriesChart.DisplayTrigger;
import ec.util.chart.TimeSeriesChart.Element;
import ec.util.chart.TimeSeriesChartCommand;
import ec.util.various.swing.JCommand;
import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JOptionPane;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.IntervalXYDataset;

/**
 *
 * @author Philippe Charles
 */
public abstract class JTimeSeriesChartCommand extends JCommand<JTimeSeriesChart> {

    @Override
    public ActionAdapter toAction(JTimeSeriesChart chart) {
        return super.toAction(chart)
                .withWeakPropertyChangeListener(chart)
                .withWeakListSelectionListener(chart.getSeriesSelectionModel());
    }

    //<editor-fold defaultstate="collapsed" desc="Adapted commands">
    public static JTimeSeriesChartCommand reset() {
        return new Adapter(TimeSeriesChartCommand.reset()) {
            @Override
            public void execute(JTimeSeriesChart chart) {
                chart.getSeriesSelectionModel().clearSelection();
                super.execute(chart);
            }
        };
    }

    public static JTimeSeriesChartCommand clearDataset() {
        return new Adapter(TimeSeriesChartCommand.clearDataset()) {
            @Override
            public boolean isEnabled(JTimeSeriesChart chart) {
                return !Charts.isNullOrEmpty(chart.getDataset());
            }
        };
    }

    public static JTimeSeriesChartCommand toggleElementVisibility(Element element) {
        return new Adapter(TimeSeriesChartCommand.toggleElementVisibility(element));
    }

    public static JTimeSeriesChartCommand applyLineThickness(float thickness) {
        return new Adapter(TimeSeriesChartCommand.applyLineThickness(thickness));
    }

    public static JTimeSeriesChartCommand applyDash(ObsPredicate predicate) {
        return new Adapter(TimeSeriesChartCommand.applyDash(predicate));
    }

    public static JTimeSeriesChartCommand applyLegendVisibility(SeriesPredicate predicate) {
        return new Adapter(TimeSeriesChartCommand.applyLegendVisibility(predicate));
    }

    public static JTimeSeriesChartCommand applyRenderer(SeriesFunction<RendererType> renderer) {
        return new Adapter(TimeSeriesChartCommand.applyRenderer(renderer));
    }

    public static JTimeSeriesChartCommand applyRenderer(RendererType... typeIndex) {
        return new Adapter(TimeSeriesChartCommand.applyRenderer(typeIndex));
    }

    public static JTimeSeriesChartCommand applyPlotDispatcher(SeriesFunction<Integer> plotDispatcher) {
        return new Adapter(TimeSeriesChartCommand.applyPlotDispatcher(plotDispatcher));
    }

    public static JTimeSeriesChartCommand applyPlotDispatcher(Integer... plotIndex) {
        return new Adapter(TimeSeriesChartCommand.applyPlotDispatcher(plotIndex));
    }

    public static JTimeSeriesChartCommand applySeriesFormatter(SeriesFunction<String> formatter) {
        return new Adapter(TimeSeriesChartCommand.applySeriesFormatter(formatter));
    }

    public static JTimeSeriesChartCommand applySeriesFormatter(String... values) {
        return new Adapter(TimeSeriesChartCommand.applySeriesFormatter(values));
    }

    public static JTimeSeriesChartCommand applyPeriod(DateFormat periodFormat) {
        return new Adapter(TimeSeriesChartCommand.applyPeriod(periodFormat));
    }

    public static JTimeSeriesChartCommand applyPeriod(String format) {
        return new Adapter(TimeSeriesChartCommand.applyPeriod(format));
    }

    public static JTimeSeriesChartCommand applyWeights(int... weights) {
        return new Adapter(TimeSeriesChartCommand.applyWeights(weights));
    }

    public static JTimeSeriesChartCommand applyTitle(String title) {
        return new Adapter(TimeSeriesChartCommand.applyTitle(title));
    }

    @Nonnull
    public static JTimeSeriesChartCommand applyCrosshairOrientation(CrosshairOrientation crosshairOrientation) {
        return new Adapter(TimeSeriesChartCommand.applyCrosshairOrientation(crosshairOrientation));
    }

    @Nonnull
    public static JTimeSeriesChartCommand applyObsHighlighter(@Nullable ObsPredicate obsHighlighter) {
        return new Adapter(TimeSeriesChartCommand.applyObsHighlighter(obsHighlighter));
    }

    @Nonnull
    public static JTimeSeriesChartCommand applyTooltipTrigger(@Nonnull DisplayTrigger tooltipTrigger) {
        return new Adapter(TimeSeriesChartCommand.applyTooltipTrigger(tooltipTrigger));
    }

    @Nonnull
    public static JTimeSeriesChartCommand applyCrosshairTrigger(@Nonnull DisplayTrigger crosshairTrigger) {
        return new Adapter(TimeSeriesChartCommand.applyCrosshairTrigger(crosshairTrigger));
    }

    public static JTimeSeriesChartCommand copyImage() {
        return new Adapter(TimeSeriesChartCommand.copyImage());
    }

    public static JTimeSeriesChartCommand saveImage() {
        return new Adapter(TimeSeriesChartCommand.saveImage());
    }

    public static JTimeSeriesChartCommand printImage() {
        return new Adapter(TimeSeriesChartCommand.printImage());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Specific commands">
    public static JTimeSeriesChartCommand resetZoom() {
        return RESET_ZOOM;
    }

    public static JTimeSeriesChartCommand selectAll() {
        return SELECT_ALL;
    }

    public static JTimeSeriesChartCommand editTitle(String label) {
        return new ShowInputDialog(label) {
            @Override
            protected String getValueAsString(JTimeSeriesChart chart) {
                return chart.getTitle();
            }

            @Override
            protected void setValueAsString(JTimeSeriesChart chart, String value) {
                chart.setTitle(value);
            }
        };
    }

    public static JTimeSeriesChartCommand editNoDataMessage(String label) {
        return new ShowInputDialog(label) {
            @Override
            protected String getValueAsString(JTimeSeriesChart chart) {
                return chart.getNoDataMessage();
            }

            @Override
            protected void setValueAsString(JTimeSeriesChart chart, String value) {
                chart.setNoDataMessage(value);
            }
        };
    }

    public static JTimeSeriesChartCommand editPeriodFormat(String label) {
        return new ShowInputDialog(label) {
            @Override
            protected String getValueAsString(JTimeSeriesChart chart) {
                DateFormat format = chart.getPeriodFormat();
                return format instanceof SimpleDateFormat ? ((SimpleDateFormat) format).toPattern() : "?";
            }

            @Override
            protected void setValueAsString(JTimeSeriesChart chart, String value) {
                try {
                    chart.setPeriodFormat(new SimpleDateFormat(value));
                } catch (IllegalArgumentException ex) {
                    // do nothing
                }
            }
        };
    }

    public static JTimeSeriesChartCommand editValueFormat(String label) {
        return new ShowInputDialog(label) {
            @Override
            protected String getValueAsString(JTimeSeriesChart chart) {
                NumberFormat format = chart.getValueFormat();
                return format instanceof DecimalFormat ? ((DecimalFormat) format).toPattern() : "?";
            }

            @Override
            protected void setValueAsString(JTimeSeriesChart chart, String value) {
                try {
                    chart.setValueFormat(new DecimalFormat(value));
                } catch (IllegalArgumentException ex) {
                    // do nothing
                }
            }
        };
    }

    public static JTimeSeriesChartCommand applyColorSchemeSupport(final ColorSchemeSupport<? extends Color> colorSchemeSupport) {
        return new JTimeSeriesChartCommand() {
            @Override
            public void execute(JTimeSeriesChart chart) {
                chart.setColorSchemeSupport(colorSchemeSupport);
            }

            @Override
            public boolean isSelected(JTimeSeriesChart chart) {
                return chart.getColorSchemeSupport().equals(colorSchemeSupport);
            }
        };
    }

    public static JTimeSeriesChartCommand applyColorScheme(ColorScheme colorScheme) {
        return applyColorSchemeSupport(SwingColorSchemeSupport.from(colorScheme));
    }

    public static JTimeSeriesChartCommand applyDataset(final IntervalXYDataset dataset) {
        return new JTimeSeriesChartCommand() {
            @Override
            public void execute(JTimeSeriesChart component) throws Exception {
                component.setDataset(dataset);
            }

            @Override
            public boolean isEnabled(JTimeSeriesChart component) {
                return component.getDataset() != dataset;
            }
        };
    }

    public static JTimeSeriesChartCommand applyRandomData() {
        return RANDOM_DATA;
    }

    public static JTimeSeriesChartCommand applySelection(final int... selection) {
        return new JTimeSeriesChartCommand() {
            @Override
            public void execute(JTimeSeriesChart chart) {
                chart.getSeriesSelectionModel().clearSelection();
                for (int index : selection) {
                    chart.getSeriesSelectionModel().addSelectionInterval(index, index);
                }
            }
        };
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static class Adapter extends JTimeSeriesChartCommand {

        private final TimeSeriesChartCommand delegate;

        public Adapter(TimeSeriesChartCommand delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute(JTimeSeriesChart chart) {
            delegate.execute(chart);
        }

        @Override
        public boolean isEnabled(JTimeSeriesChart chart) {
            return delegate.isEnabled(chart);
        }

        @Override
        public boolean isSelected(JTimeSeriesChart chart) {
            return delegate.isSelected(chart);
        }
    }

    private static abstract class ShowInputDialog extends JTimeSeriesChartCommand {

        private final String label;

        public ShowInputDialog(String label) {
            this.label = label;
        }

        @Override
        public void execute(JTimeSeriesChart chart) {
            String result = JOptionPane.showInputDialog(label, getValueAsString(chart));
            if (result != null) {
                setValueAsString(chart, result);
            }
        }

        abstract protected String getValueAsString(JTimeSeriesChart chart);

        abstract protected void setValueAsString(JTimeSeriesChart chart, String value);
    }
    //
    private static final JTimeSeriesChartCommand RESET_ZOOM = new JTimeSeriesChartCommand() {
        @Override
        public void execute(JTimeSeriesChart chart) {
            chart.resetZoom();
        }
    };
    private static final JTimeSeriesChartCommand SELECT_ALL = new JTimeSeriesChartCommand() {
        @Override
        public void execute(JTimeSeriesChart chart) {
            chart.getSeriesSelectionModel().setSelectionInterval(0, chart.getDataset().getSeriesCount());
        }

        @Override
        public boolean isEnabled(JTimeSeriesChart chart) {
            return !Charts.isNullOrEmpty(chart.getDataset()) && chart.getSeriesSelectionModel().getMaxSelectionIndex() - chart.getSeriesSelectionModel().getMinSelectionIndex() != chart.getDataset().getSeriesCount();
        }
    };
    private static final JTimeSeriesChartCommand RANDOM_DATA = new JTimeSeriesChartCommand() {
        final Random random = new Random();
        final Calendar cal = Calendar.getInstance();

        @Override
        public void execute(JTimeSeriesChart chart) {
            cal.set(Calendar.YEAR, 2012);
            cal.set(Calendar.MONTH, 02);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long start = cal.getTimeInMillis();
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            dataset.setXPosition(TimePeriodAnchor.MIDDLE);
            double[][] values = getValues(3, 24, random, start);
            for (int i = 0; i < values.length; i++) {
                TimeSeries ts = new TimeSeries(i);
                cal.setTimeInMillis(start);
                for (int j = 0; j < values[i].length; j++) {
                    cal.add(Calendar.MONTH, 1);
                    ts.add(new TimeSeriesDataItem(new Month(cal.getTime()), values[i][j]));
                }
                dataset.addSeries(ts);
            }

            chart.setDataset(dataset);
        }

        double[][] getValues(int series, int obs, Random rng, long startTimeMillis) {
            double[][] result = new double[series][obs];
            for (int i = 0; i < series; i++) {
                for (int j = 0; j < obs; j++) {
                    result[i][j] = Math.abs((100 * (Math.cos(startTimeMillis * i))) + (100 * (Math.sin(startTimeMillis) - Math.cos(rng.nextDouble()) + Math.tan(rng.nextDouble())))) - 50;
                }
            }
            return result;
        }
    };
    //</editor-fold>
}
