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
package demetra.ui.components;

import demetra.demo.DemoTsBuilder;
import demetra.ui.TsManager;
import demetra.ui.beans.PropertyChangeSource;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.IConfigurable;
import internal.ui.components.InternalTsChartUI;
import internal.ui.components.InternalTsChartConfig;
import internal.ui.components.InternalUI;
import java.awt.Dimension;
import java.beans.Beans;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;

/**
 * Component used to display time series in a chart. Supports drag and drop,
 * copy/paste.
 *
 * @author Demortier Jeremy
 * @author Philippe Charles
 */
public final class JTsChart extends JComponent implements TimeSeriesComponent, PropertyChangeSource,
        HasTsCollection, HasTsAction, HasChart, HasColorScheme, HasObsFormat, HasHoveredObs,
        PrintableWithPreview, ResetableZoom, IConfigurable {

    @lombok.experimental.Delegate
    private final HasTsCollection collection;

    @lombok.experimental.Delegate
    private final HasTsAction tsAction;

    @lombok.experimental.Delegate
    private final HasChart chart;

    @lombok.experimental.Delegate
    private final HasColorScheme colorScheme;

    @lombok.experimental.Delegate
    private final HasObsFormat obsFormat;

    @lombok.experimental.Delegate
    private final HasHoveredObs hoveredObs;

    @lombok.experimental.Delegate
    private final PrintableWithPreview printable;

    @lombok.experimental.Delegate
    private final ResetableZoom resetableZoom;

    public static final String DUAL_CHART_PROPERTY = "dualChart";
    private static final boolean DEFAULT_DUAL_CHART = false;
    private boolean dualChart;

    public static final String DUAL_DISPATCHER_PROPERTY = "dualDispatcher";
    private ListSelectionModel dualDispatcher;

    private final TsSelectionBridge tsSelectionBridge;

    private final InternalUI<JTsChart> internalUI;

    public JTsChart() {
        this.collection = HasTsCollection.of(this::firePropertyChange, TsManager.getDefault());
        this.tsAction = HasTsAction.of(this::firePropertyChange);
        this.chart = HasChart.of(this::firePropertyChange);
        this.colorScheme = HasColorScheme.of(this::firePropertyChange);
        this.obsFormat = HasObsFormat.of(this::firePropertyChange);
        this.hoveredObs = HasHoveredObs.of(this::firePropertyChange);
        this.printable = PrintableWithPreview.of(this::getActionMap);
        this.resetableZoom = ResetableZoom.of(this::getActionMap);
        this.dualChart = DEFAULT_DUAL_CHART;
        this.dualDispatcher = new DefaultListSelectionModel();

        this.tsSelectionBridge = new TsSelectionBridge(this::firePropertyChange);
        tsSelectionBridge.register(this);

        this.internalUI = new InternalTsChartUI();
        internalUI.install(this);

        applyDesignTimeProperties();
    }

    public boolean isDualChart() {
        return dualChart;
    }

    public void setDualChart(boolean dualChart) {
        boolean old = this.dualChart;
        this.dualChart = dualChart;
        firePropertyChange(DUAL_CHART_PROPERTY, old, this.dualChart);
    }

    @Nonnull
    public ListSelectionModel getDualDispatcher() {
        return dualDispatcher;
    }

    public void setDualDispatcher(@Nonnull ListSelectionModel dualDispatcher) {
        ListSelectionModel old = this.dualDispatcher;
        this.dualDispatcher = Objects.requireNonNull(dualDispatcher);
        firePropertyChange(DUAL_DISPATCHER_PROPERTY, old, this.dualDispatcher);
    }

    @Override
    public Config getConfig() {
        return InternalTsChartConfig.CONFIGURATOR.getConfig(this);
    }

    @Override
    public void setConfig(Config config) {
        InternalTsChartConfig.CONFIGURATOR.setConfig(this, config);
    }

    @Override
    public Config editConfig(Config config) {
        return InternalTsChartConfig.CONFIGURATOR.editConfig(config);
    }

    private void applyDesignTimeProperties() {
        if (Beans.isDesignTime()) {
            setTsCollection(DemoTsBuilder.randomTsCollection(3));
            setTsUpdateMode(TsUpdateMode.None);
            setPreferredSize(new Dimension(200, 150));
            setTitle("Chart preview");
        }
    }
}
