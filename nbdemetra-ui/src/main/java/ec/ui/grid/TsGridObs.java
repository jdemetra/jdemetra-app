/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid;

import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.design.FlyweightPattern;
import ec.tstoolkit.timeseries.simplets.TsDataTableInfo;
import ec.tstoolkit.timeseries.simplets.TsPeriod;

/**
 *
 * @author Philippe Charles
 */
@FlyweightPattern
public class TsGridObs {

    private final DescriptiveStatistics stats;
    private int seriesIndex;
    private int index;
    private TsPeriod period;
    private double value;

    TsGridObs(DescriptiveStatistics stats) {
        this.stats = stats;
        empty(-1);
    }

    final TsGridObs empty(int seriesIndex) {
        return missing(seriesIndex, -1, null);
    }

    final TsGridObs missing(int seriesIndex, int obsIndex, TsPeriod period) {
        return valid(seriesIndex, obsIndex, period, Double.NaN);
    }

    final TsGridObs valid(int seriesIndex, int obsIndex, TsPeriod period, double value) {
        this.seriesIndex = seriesIndex;
        this.index = obsIndex;
        this.period = period;
        this.value = value;
        return this;
    }

    public DescriptiveStatistics getStats() {
        return stats;
    }

    public TsDataTableInfo getInfo() {
        return period == null ? TsDataTableInfo.Empty : Double.isNaN(value) ? TsDataTableInfo.Missing : TsDataTableInfo.Valid;
    }

    public int getSeriesIndex() {
        return seriesIndex;
    }

    public int getIndex() {
        return index;
    }

    public TsPeriod getPeriod() {
        return period;
    }

    public double getValue() {
        return value;
    }
}
