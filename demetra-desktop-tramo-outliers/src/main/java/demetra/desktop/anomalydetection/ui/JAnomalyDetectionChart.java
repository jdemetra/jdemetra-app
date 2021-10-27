/*
 * Copyright 2015 National Bank of Belgium
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
package demetra.desktop.anomalydetection.ui;

import ec.util.chart.ColorScheme;
import ec.util.chart.ObsFunction;
import ec.util.chart.ObsIndex;
import ec.util.chart.ObsPredicate;
import ec.util.chart.SeriesFunction;
import ec.util.chart.SeriesPredicate;
import ec.util.chart.swing.JTimeSeriesChart;
import ec.util.various.swing.JCommand;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.JComponent;
import javax.swing.JMenu;
import demetra.desktop.datatransfer.DataTransfer;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import static ec.util.chart.swing.JTimeSeriesChartCommand.copyImage;
import static ec.util.chart.swing.JTimeSeriesChartCommand.printImage;
import static ec.util.chart.swing.JTimeSeriesChartCommand.saveImage;

/**
 *
 * @author Philippe Charles
 */
@SwingComponent
final class JAnomalyDetectionChart extends JComponent {

    @SwingProperty
    public static final String MODEL_PROPERTY = "model";

    @SwingProperty
    public static final String HOVERED_OBS_PROPERTY = "hoveredObs";

    private final JTimeSeriesChart chart;
    private final ChartHandler chartHandler;
    private Model model;
    private int hoveredObs;

    public JAnomalyDetectionChart() {
        this.chart = new JTimeSeriesChart();
        this.chartHandler = new ChartHandler();
        this.model = null;
        this.hoveredObs = -1;

        chart.setLegendVisibilityPredicate(SeriesPredicate.alwaysFalse());
        setSeriesColorist(chart, SeriesFunction.always(ColorScheme.KnownColor.GRAY));
        chart.setObsFormatter(new ObsFunction<String>() {
            @Override
            public String apply(int series, int obs) {
                TsData data = model.getTs().getTsData();
                TsPeriod period = data.getDomain().get(obs);
                double value = data.get(obs);
                NumberFormat valueFormat = chart.getValueFormat();
                OutlierEstimation outlier = model.outliers[obs];
                return outlier != null
                        ? new StringBuilder()
                        .append("Period : ").append(period).append('\n')
                        .append("Value : ").append(valueFormat.format(value)).append('\n')
                        .append("Outlier Value : ").append(valueFormat.format(outlier.getValue())).append('\n')
                        .append("Std Err : ").append(valueFormat.format(outlier.getStdev())).append('\n')
                        .append("TStat : ").append(valueFormat.format(outlier.getTStat())).append('\n')
                        .append("Outlier type : ").append(outlier.getCode()).toString()
                        : new StringBuilder()
                        .append("Period : ").append(period).append('\n')
                        .append("Value : ").append(valueFormat.format(value)).toString();
            }
        });
        chart.setObsHighlighter(new ObsPredicate() {
            @Override
            public boolean apply(int series, int obs) {
                return model.outliers[obs] != null || chart.getHoveredObs().equals(series, obs);
            }
        });
        chart.setObsColorist(new ObsFunction<Color>() {
            @Override
            public Color apply(int series, int obs) {
                OutlierEstimation outlier = model.outliers[obs];
                return outlier != null
                        ? ColorChooser.getColor(outlier.getCode())
                        : chart.getSeriesColorist().apply(series);
            }
        });
        chart.setComponentPopupMenu(newMenu().getPopupMenu());

        enableProperties();

        setLayout(new BorderLayout());
        add(chart, BorderLayout.CENTER);
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case MODEL_PROPERTY:
                    onModelChange();
                    break;
                case HOVERED_OBS_PROPERTY:
                    onHoveredObsChange();
                    break;
            }
        });
        chart.addPropertyChangeListener(chartHandler);
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    private void onModelChange() {
        chart.setDataset(model != null ? TsXYDatasets.from(model.getTs()) : null);
    }

    private void onHoveredObsChange() {
        chartHandler.applyHoveredObs(hoveredObs);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Nullable
    public Model getModel() {
        return model;
    }

    public void setModel(@Nullable Model model) {
        Model old = this.model;
        this.model = model;
        firePropertyChange(MODEL_PROPERTY, old, this.model);
    }

    public int getHoveredObs() {
        return hoveredObs;
    }

    public void setHoveredObs(int hoveredObs) {
        int old = this.hoveredObs;
        this.hoveredObs = hoveredObs;
        firePropertyChange(HOVERED_OBS_PROPERTY, old, this.hoveredObs);
    }
    //</editor-fold>

    public static final class Model {

        private final Ts ts;
        private final OutlierEstimation[] outliers;

        Model(@NonNull Ts ts, @NonNull PreprocessingModel model) {
            this.ts = ts;
            TsData data = ts.getTsData();
            this.outliers = new OutlierEstimation[data.getLength()];
            TsPeriod start = data.getStart();
            for (OutlierEstimation o : model.outliersEstimation(true, false)) {
                outliers[o.getPosition().minus(start)] = o;
            }
        }

        @NonNull
        public Ts getTs() {
            return ts;
        }

        @NonNull
        public OutlierEstimation[] getOutliers() {
            return outliers;
        }
    }

    private final class ChartHandler implements PropertyChangeListener {

        private boolean updating = false;

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!updating) {
                updating = true;
                switch (evt.getPropertyName()) {
                    case JTimeSeriesChart.HOVERED_OBS_PROPERTY:
                        setHoveredObs(chart.getHoveredObs().getObs());
                        break;
                }
                updating = false;
            }
        }

        public void applyHoveredObs(int obs) {
            if (!updating) {
                chart.setHoveredObs(hoveredObs != -1 && model != null ? ObsIndex.valueOf(0, hoveredObs) : ObsIndex.NULL);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Menus">
    private JMenu newMenu() {
        JMenu result = new JMenu();

        result.add(new CopyData().toAction(this)).setText("Copy data");
        result.add(new CopyOutliers().toAction(this)).setText("Copy outliers");
        result.addSeparator();
        result.add(newExportImageMenu(chart));

        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Commands">
    private static abstract class ModelCommand extends JCommand<JAnomalyDetectionChart> {

        @Override
        public boolean isEnabled(JAnomalyDetectionChart c) {
            return c.getModel() != null;
        }

        @Override
        public JCommand.ActionAdapter toAction(JAnomalyDetectionChart c) {
            return super.toAction(c).withWeakPropertyChangeListener(c, MODEL_PROPERTY);
        }
    }

    private static final class CopyData extends ModelCommand {

        @Override
        public void execute(JAnomalyDetectionChart c) throws Exception {
            Transferable t = DataTransfer.getDefault().fromTs(TsConverter.toTs(c.model.getTs()));
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
        }
    }

    private static final class CopyOutliers extends ModelCommand {

        @Override
        public void execute(JAnomalyDetectionChart c) throws Exception {
            Transferable t = DataTransfer.getDefault().fromTable(TsConverter.toTable(toTable(c.model.getOutliers())));
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
        }

        private Table<Object> toTable(OutlierEstimation[] input) {
            OutlierEstimation[] tmp = Arrays.stream(input).filter(Objects::nonNull).toArray(OutlierEstimation[]::new);
            Table<Object> result = new Table<>(tmp.length + 1, 5);
            result.set(0, 0, "");
            result.set(0, 1, "Period");
            result.set(0, 2, "Value");
            result.set(0, 3, "StdErr");
            result.set(0, 4, "TStat");
            for (int i = 0; i < tmp.length; i++) {
                OutlierEstimation o = tmp[i];
                result.set(i + 1, 0, o.getCode());
                result.set(i + 1, 1, o.getPosition());
                result.set(i + 1, 2, o.getValue());
                result.set(i + 1, 3, o.getStdev());
                result.set(i + 1, 4, o.getTStat());
            }
            return result;
        }
    }
    //</editor-fold>
    
    private static void setSeriesColorist(final @NonNull JTimeSeriesChart chart, final @NonNull SeriesFunction<ColorScheme.KnownColor> colorist) {
        chart.setSeriesColorist(new SeriesFunction<Color>() {
            @Override
            public Color apply(int series) {
                ColorScheme.KnownColor color = colorist.apply(series);
                return color != null ? chart.getColorSchemeSupport().getLineColor(color) : null;
            }
        });
    }

    @NonNull
    private static JMenu newExportImageMenu(@NonNull JTimeSeriesChart chart) {
        JMenu result = new JMenu("Export image to");
        result.add(printImage().toAction(chart)).setText("Printer...");
        result.add(copyImage().toAction(chart)).setText("Clipboard");
        result.add(saveImage().toAction(chart)).setText("File...");
        return result;
    }
}
