/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid;

import ec.tss.TsCollection;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.data.Values;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsPeriod;

/**
 *
 * @author Philippe Charles
 */
class SingleTsGridData extends TsGridData {

    final TsGridObs obs;
    final int seriesIndex;
    final Values data;
    final TsDomain domain;
    final int startYear;
    final int startPosition;

    public SingleTsGridData(TsCollection col, int seriesIndex) {
        this.seriesIndex = seriesIndex;
        this.data = col.get(seriesIndex).getTsData().getValues();
        this.domain = col.get(seriesIndex).getTsData().getDomain();
        this.startYear = domain.getStart().getYear();
        this.startPosition = domain.getStart().getPosition();
        this.obs = new TsGridObs(new DescriptiveStatistics(data));
    }

    int getPeriodId(int i, int j) {
        int periodId = j + (getColumnCount() * i) - startPosition;
        return (periodId < 0 || periodId >= domain.getLength()) ? -1 : periodId;
    }

    @Override
    public String getRowName(int i) {
        return Integer.toString(startYear + i);
    }

    @Override
    public String getColumnName(int j) {
        return TsPeriod.formatShortPeriod(domain.getFrequency(), j);
    }

    @Override
    public TsGridObs getObs(int i, int j) {
        int periodId = getPeriodId(i, j);
        return periodId == -1 ? obs.empty(seriesIndex) : obs.valid(seriesIndex, periodId, domain.get(periodId), data.get(periodId));
    }

    @Override
    public int getRowCount() {
        return domain.getYearsCount();
    }

    @Override
    public int getColumnCount() {
        return domain.getFrequency().intValue();
    }
}
