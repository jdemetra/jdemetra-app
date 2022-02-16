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
package demetra.desktop.components;

import demetra.desktop.DemetraOptions;
import demetra.desktop.actions.PrintableWithPreview;
import demetra.desktop.actions.ResetableZoom;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.components.parts.*;
import demetra.desktop.design.SwingAction;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.timeseries.*;
import internal.ui.components.DemoTsBuilder;

import javax.swing.*;
import java.awt.*;
import java.beans.Beans;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Kristof Bayens
 */
@SwingComponent
public final class JTsGrowthChart extends JComponent implements TimeSeriesComponent, PropertyChangeSource.WithWeakListeners,
        HasTsCollection, HasTsAction, HasChart, HasColorScheme, HasObsFormat,
        PrintableWithPreview, ResetableZoom {

    @SwingAction
    public static final String PREVIOUS_PERIOD_ACTION = "previousPeriod";

    @SwingAction
    public static final String PREVIOUS_YEAR_ACTION = "previousYear";

    public enum GrowthKind {

        PreviousPeriod,
        PreviousYear
    }

    @SwingProperty
    public static final String GROWTH_KIND_PROPERTY = "growthKind";

    @SwingProperty
    public static final String LAST_YEARS_PROPERTY = "lastYears";

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

    public JTsGrowthChart(){
            this(TsInformationType.None);
    }
    
    public JTsGrowthChart(TsInformationType info) {
        this.collection = HasTsCollectionSupport.of(this::firePropertyChange, info);
        this.tsAction = HasTsActionSupport.of(this::firePropertyChange);
        this.chart = HasChartSupport.of(this::firePropertyChange);
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
        return result.fastFn(x -> x * .01);
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
