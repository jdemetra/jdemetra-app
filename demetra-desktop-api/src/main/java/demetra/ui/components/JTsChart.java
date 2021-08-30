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

import demetra.ui.components.parts.*;
import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.Config;
import demetra.ui.ConfigEditor;
import demetra.ui.Persistable;
import demetra.ui.actions.Configurable;
import demetra.ui.actions.PrintableWithPreview;
import demetra.ui.actions.ResetableZoom;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import internal.ui.components.DemoTsBuilder;
import internal.ui.components.JTsChartConfig;
import java.awt.Dimension;
import java.beans.Beans;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
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
@SwingComponent
public final class JTsChart extends JComponent implements TimeSeriesComponent, PropertyChangeSource.WithWeakListeners,
        HasTsCollection, HasTsAction, HasChart, HasColorScheme, HasObsFormat, HasHoveredObs,
        PrintableWithPreview, ResetableZoom, Configurable, Persistable, ConfigEditor {

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

    @SwingProperty
    public static final String DUAL_CHART_PROPERTY = "dualChart";
    private static final boolean DEFAULT_DUAL_CHART = false;
    private boolean dualChart;

    @SwingProperty
    public static final String DUAL_DISPATCHER_PROPERTY = "dualDispatcher";
    private ListSelectionModel dualDispatcher;

    private final TsSelectionBridge tsSelectionBridge;

    public JTsChart() {
        this.collection = HasTsCollectionSupport.of(this::firePropertyChange);
        this.tsAction = HasTsAction.of(this::firePropertyChange);
        this.chart = HasChartSupport.of(this::firePropertyChange);
        this.colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);
        this.obsFormat = HasObsFormatSupport.of(this::firePropertyChange);
        this.hoveredObs = HasHoveredObs.of(this::firePropertyChange);
        this.printable = PrintableWithPreview.of(this::getActionMap);
        this.resetableZoom = ResetableZoom.of(this::getActionMap);
        this.dualChart = DEFAULT_DUAL_CHART;
        this.dualDispatcher = new DefaultListSelectionModel();

        this.tsSelectionBridge = new TsSelectionBridge(this::firePropertyChange);
        tsSelectionBridge.register(this);

        ComponentBackend.getDefault().install(this);

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

    @NonNull
    public ListSelectionModel getDualDispatcher() {
        return dualDispatcher;
    }

    public void setDualDispatcher(@NonNull ListSelectionModel dualDispatcher) {
        ListSelectionModel old = this.dualDispatcher;
        this.dualDispatcher = Objects.requireNonNull(dualDispatcher);
        firePropertyChange(DUAL_DISPATCHER_PROPERTY, old, this.dualDispatcher);
    }

    @Override
    public Config getConfig() {
        return JTsChartConfig.CONFIGURATOR.getConfig(this);
    }

    @Override
    public void setConfig(Config config) {
        JTsChartConfig.CONFIGURATOR.setConfig(this, config);
    }

    @Override
    public Config editConfig(Config config) {
        return JTsChartConfig.CONFIGURATOR.editConfig(config);
    }

    @Override
    public void configure() {
        Configurable.configure(this, this);
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
