/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid.model;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsPeriod;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
class SingleTsGridData implements IGridData {

    final TsData data;

    public SingleTsGridData(TsData data) {
        this.data = data;
    }

    @Override
    public String getRowName(int i) {
        return Integer.toString(data.getDomain().getStart().getYear() + i);
    }

    @Override
    public String getColumnName(int j) {
        return TsPeriod.formatShortPeriod(data.getDomain().getFrequency(), j);
    }

    @Override
    public Number getValue(int i, int j) {
        int periodId = j + (getColumnCount() * i) - data.getDomain().getStart().getPosition();
        return (periodId < 0 || periodId >= data.getDomain().getLength()) ? null : data.get(periodId);
    }

    @Override
    public int getRowCount() {
        return data.getDomain().getYearsCount();
    }

    @Override
    public int getColumnCount() {
        return data.getDomain().getFrequency().intValue();
    }
}
