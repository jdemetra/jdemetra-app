/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.interfaces;

import ec.tss.Ts;

/**
 *
 * @author Philippe Charles
 */
public interface ITsGrowthChart extends ITsChart {

    public enum GrowthKind {

        PreviousPeriod,
        PreviousYear
    }
    public static final String GROWTH_KIND_PROPERTY = "growthKind";
    public static final String LAST_YEARS_PROPERTY = "lastYears";
    public static final String USE_TOOL_LAYOUT_PROPERTY = "useToolLayout";

    GrowthKind getGrowthKind();

    void setGrowthKind(GrowthKind growthKind);

    int getLastYears();

    void setLastYears(int lastYears);

    boolean isUseToolLayout();

    void setUseToolLayout(boolean useToolLayout);
    
    Ts[] getGrowthData();
}
