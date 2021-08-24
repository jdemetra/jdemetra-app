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

import demetra.ui.actions.PrintableWithPreview;
import demetra.ui.actions.ResetableZoom;
import demetra.ui.components.parts.HasObsFormat;
import demetra.ui.components.parts.HasTsAction;
import demetra.ui.components.parts.HasTsCollection;
import demetra.ui.components.parts.HasChart;
import demetra.ui.components.parts.HasColorScheme;
import demetra.timeseries.TimeSelector;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDataTable;
import demetra.timeseries.TsDomain;
import demetra.ui.DemetraOptions;
import demetra.ui.TsManager;
import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.components.parts.HasColorSchemeSupport;
import demetra.ui.components.parts.HasObsFormatSupport;
import demetra.ui.design.SwingComponent;
import internal.ui.components.DemoTsBuilder;
import java.awt.Dimension;
import java.beans.Beans;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JComponent;

/**
 *
 * @author Kristof Bayens
 */
@SwingComponent
public final class JTsGrowthChart extends JComponent implements TimeSeriesComponent, PropertyChangeSource.WithWeakListeners,
        HasTsCollection, HasTsAction, HasChart, HasColorScheme, HasObsFormat,
        PrintableWithPreview, ResetableZoom {

    public enum GrowthKind {

        PreviousPeriod,
        PreviousYear
    }

    public static final String GROWTH_KIND_PROPERTY = "growthKind";
    public static final String LAST_YEARS_PROPERTY = "lastYears";
    public static final String USE_TOOL_LAYOUT_PROPERTY = "useToolLayout";

    // DEFAULT PROPERTIES
    private static final GrowthKind DEFAULT_GROWTH_KIND = GrowthKind.PreviousPeriod;

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
    private final PrintableWithPreview printableWithPreview;

    @lombok.experimental.Delegate
    private final ResetableZoom resetableZoom;

    // PROPERTIES
    private GrowthKind growthKind;
    private int lastYears;

    private final TsSelectionBridge tsSelectionBridge;

    public JTsGrowthChart() {
        this.collection = HasTsCollection.of(this::firePropertyChange, TsManager.getDefault());
        this.tsAction = HasTsAction.of(this::firePropertyChange);
        this.chart = HasChart.of(this::firePropertyChange);
        this.colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);
        this.obsFormat = HasObsFormatSupport.of(this::firePropertyChange);
        this.printableWithPreview = PrintableWithPreview.of(this::getActionMap);
        this.resetableZoom = ResetableZoom.of(this::getActionMap);
        this.growthKind = DEFAULT_GROWTH_KIND;
        this.lastYears = DemetraOptions.getDefault().getGrowthLastYears();

        this.tsSelectionBridge = new TsSelectionBridge(this::firePropertyChange);
        tsSelectionBridge.register(this);

        ComponentBackend.getDefault().install(this);

        applyDesignTimeProperties();
    }

    public GrowthKind getGrowthKind() {
        return growthKind;
    }

    public void setGrowthKind(GrowthKind growthKind) {
        GrowthKind old = this.growthKind;
        this.growthKind = growthKind != null ? growthKind : DEFAULT_GROWTH_KIND;
        firePropertyChange(GROWTH_KIND_PROPERTY, old, this.growthKind);
    }

    public int getLastYears() {
        return lastYears;
    }

    public void setLastYears(int lastYears) {
        int old = this.lastYears;
        this.lastYears = lastYears;
        firePropertyChange(LAST_YEARS_PROPERTY, old, this.lastYears);
    }

    //<editor-fold defaultstate="collapsed" desc="Growth data tools">
    public List<Ts> computeGrowthData() {
        return computeGrowthData(getTsCollection(), getGrowthKind(), getLastYears());
    }

    private static List<Ts> computeGrowthData(TsCollection input, GrowthKind kind, int lastyears) {
        return computeGrowthData(input, kind, computeSelector(input, lastyears));
    }

    private static List<Ts> computeGrowthData(TsCollection input, GrowthKind kind, TimeSelector selector) {
        return input
                .stream()
                .map(ts -> Ts.builder().name(ts.getName()).data(computeGrowthData(ts.getData(), kind, selector)).build())
                .collect(Collectors.toList());
    }

    private static TimeSelector computeSelector(TsCollection tss, int lastyears) {
        TsDomain globalDomain = TsDataTable.of(tss, ts -> ts.getData().cleanExtremities()).getDomain();
        if (globalDomain.isEmpty()) {
            return TimeSelector.all();
        }
        int year = globalDomain.getLastPeriod().year() - lastyears;
        return TimeSelector.from(LocalDate.of(year, 1, 1).atStartOfDay());
    }

    private static TsData computeGrowthData(TsData input, GrowthKind kind, TimeSelector selector) {
        if (input == null) {
            return null;
        }
        TsData result = input.cleanExtremities();
        result = result.pctVariation(kind == GrowthKind.PreviousPeriod ? 1 : result.getAnnualFrequency());
        if (result == null) {
            return null;
        }
        result = result.select(selector);
        result.fastFn(x -> x * .01);
        return result;
    }
    //</editor-fold>

    private void applyDesignTimeProperties() {
        if (Beans.isDesignTime()) {
            setTsCollection(DemoTsBuilder.randomTsCollection(3));
            setTsUpdateMode(TsUpdateMode.None);
            setPreferredSize(new Dimension(200, 150));
            setTitle("Chart preview");
        }
    }
}
