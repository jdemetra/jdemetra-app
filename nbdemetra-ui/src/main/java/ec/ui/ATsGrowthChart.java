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
package ec.ui;

import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.TsMoniker;
import ec.tss.TsStatus;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.Month;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataTable;
import ec.ui.commands.TsGrowthChartCommand;
import ec.ui.interfaces.ITsGrowthChart;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Kristof Bayens
 */
public abstract class ATsGrowthChart extends ATsChart implements ITsGrowthChart {

    // ACTION KEYS
    public static final String PREVIOUS_PERIOD_ACTION = "previousPeriod";
    public static final String PREVIOUS_YEAR_ACTION = "previousYear";

    // DEFAULT PROPERTIES
    protected static final GrowthKind DEFAULT_GROWTH_KIND = GrowthKind.PreviousPeriod;
    public static final int DEFAULT_LAST_YEARS = 4;
    protected static final boolean DEFAULT_USE_TOOL_LAYOUT = false;

    // PROPERTIES
    protected GrowthKind growthKind;
    protected int lastYears;
    protected boolean useToolLayout;

    // OTHER
    protected final TsCollection growthCollection = TsFactory.instance.createTsCollection();

    public ATsGrowthChart() {
        this.growthKind = DEFAULT_GROWTH_KIND;
        this.lastYears = demetraUI.getGrowthLastYears();
        this.useToolLayout = DEFAULT_USE_TOOL_LAYOUT;

        enableProperties();
        registerActions();
    }

    private void registerActions() {
        ActionMap am = getActionMap();
        am.put(PREVIOUS_PERIOD_ACTION, TsGrowthChartCommand.applyGrowthKind(GrowthKind.PreviousPeriod).toAction(this));
        am.put(PREVIOUS_YEAR_ACTION, TsGrowthChartCommand.applyGrowthKind(GrowthKind.PreviousYear).toAction(this));
    }

    private void enableProperties() {
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case GROWTH_KIND_PROPERTY:
                        onGrowthKindChange();
                        break;
                    case LAST_YEARS_PROPERTY:
                        onLastYearsChange();
                        break;
                    case USE_TOOL_LAYOUT_PROPERTY:
                        onUseToolLayoutChange();
                        break;
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    abstract protected void onGrowthKindChange();

    abstract protected void onLastYearsChange();

    abstract protected void onUseToolLayoutChange();
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public GrowthKind getGrowthKind() {
        return growthKind;
    }

    @Override
    public void setGrowthKind(GrowthKind growthKind) {
        GrowthKind old = this.growthKind;
        this.growthKind = growthKind != null ? growthKind : DEFAULT_GROWTH_KIND;
        firePropertyChange(GROWTH_KIND_PROPERTY, old, this.growthKind);
    }

    @Override
    public int getLastYears() {
        return lastYears;
    }

    @Override
    public void setLastYears(int lastYears) {
        int old = this.lastYears;
        this.lastYears = lastYears;
        firePropertyChange(LAST_YEARS_PROPERTY, old, this.lastYears);
    }

    @Override
    public boolean isUseToolLayout() {
        return useToolLayout;
    }

    @Override
    public void setUseToolLayout(boolean useToolLayout) {
        boolean old = this.useToolLayout;
        this.useToolLayout = useToolLayout;
        firePropertyChange(USE_TOOL_LAYOUT_PROPERTY, old, this.useToolLayout);
    }

    @Override
    public Ts[] getGrowthData() {
        return growthCollection.toArray();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Growth data tools">
    protected static TsPeriodSelector computeSelector(TsCollection col, int lastyears) {
        TsPeriodSelector result = new TsPeriodSelector();
        boolean isvalid = false;
        Ts[] tss = col.toArray();
        for (Ts o : tss) {
            if (o.hasData() == TsStatus.Valid) {
                isvalid = true;
                break;
            }
        }
        if (isvalid) {
            TsDataTable tmp = new TsDataTable();
            for (Ts o : tss) {
                if (o.hasData() == TsStatus.Valid) {
                    tmp.insert(-1, o.getTsData().cleanExtremities());
                }
            }
            int year = tmp.getDomain().getLast().getYear() - lastyears;
            result.from(new Day(year, Month.valueOf(0), 0));
        }
        return result;
    }

    protected static TsData computeGrowthData(TsData input, GrowthKind kind, TsPeriodSelector selector) {
        if (input == null) {
            return null;
        }
        TsData result = input.cleanExtremities();
        result = result.pctVariation(kind == GrowthKind.PreviousPeriod ? 1 : result.getFrequency().intValue());
        result = result.select(selector);
        result.getValues().mul(.01);
        return result;
    }

    protected static Ts[] computeGrowthData(Ts[] input, GrowthKind kind, TsPeriodSelector selector) {
        Ts[] result = new Ts[input.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = TsFactory.instance.createTs(input[i].getName(), new TsMoniker(), null, computeGrowthData(input[i].getTsData(), kind, selector));
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Menus">
    protected JMenu buildKindMenu() {
        ActionMap am = getActionMap();
        JMenu result = new JMenu("Kind");

        JMenuItem item;

        item = new JCheckBoxMenuItem(am.get(PREVIOUS_PERIOD_ACTION));
        item.setText("Previous Period");
        result.add(item);

        item = new JCheckBoxMenuItem(am.get(PREVIOUS_YEAR_ACTION));
        item.setText("Previous Year");
        result.add(item);

        return result;
    }

    @Override
    protected JMenu buildChartMenu() {
        JMenu result = super.buildChartMenu();

        int index = 0;
        JMenuItem item;

        index += 5;
        item = new JMenuItem(TsGrowthChartCommand.copyGrowthData().toAction(this));
        item.setText("Copy growth data");
        ExtAction.hideWhenDisabled(item);
        result.add(item, index++);

        index += 6;
        result.insert(buildKindMenu(), index++);

        item = new JMenuItem(TsGrowthChartCommand.editLastYears().toAction(this));
        item.setText("Edit last years...");
        result.insert(item, index++);

        index += 5;
        result.remove(index); // linesThickness

        return result;
    }
    //</editor-fold>
}
