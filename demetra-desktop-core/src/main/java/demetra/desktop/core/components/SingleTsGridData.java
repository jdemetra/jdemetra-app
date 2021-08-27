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
package demetra.desktop.core.components;

import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDataTable;
import demetra.timeseries.TsDomain;
import demetra.ui.components.TsGridObs;
import ec.util.chart.ObsIndex;

import java.util.List;

/**
 * @author Philippe Charles
 */
final class SingleTsGridData implements TsGridData {

    private final int seriesIndex;
    private final TsData data;
    private final TsDomain domain;
    private final int startYear;
    private final int startPosition;
    private final TsGridObs obs;

    public SingleTsGridData(List<Ts> col, int seriesIndex) {
        this.seriesIndex = seriesIndex;
        Ts ts = col.get(seriesIndex);
        this.data = ts.getData();
        this.domain = ts.getData().getDomain();
        this.startYear = domain.getStartPeriod().year();
        this.startPosition = domain.getStartPeriod().annualPosition();
        this.obs = new TsGridObs();
    }

    private int getPeriodId(int i, int j) {
        int periodId = j + (getColumnCount() * i) - startPosition;
        return (periodId < 0 || periodId >= domain.getLength()) ? -1 : periodId;
    }

    @Override
    public String getRowName(int i) {
        return Integer.toString(startYear + i);
    }

    @Override
    public String getColumnName(int j) {
        return domain.get(j).display();
    }

    @Override
    public TsGridObs getObs(int i, int j) {
        int obsIndex = getPeriodId(i, j);
        obs.setIndex(obsIndex);
        obs.setPeriod(obsIndex != -1 ? domain.get(obsIndex) : null);
        obs.setSeriesIndex(seriesIndex);
        obs.setStatus(obsIndex != -1 ? TsDataTable.ValueStatus.PRESENT : TsDataTable.ValueStatus.EMPTY);
        obs.setValue(obsIndex != -1 ? data.getValue(obsIndex) : Double.NaN);
        return obs;
    }

    @Override
    public int getRowCount() {
        return domain.getEndPeriod().year() - domain.getStartPeriod().year() + 1;
    }

    @Override
    public int getColumnCount() {
        return domain.getAnnualFrequency();
    }

    @Override
    public int getRowIndex(ObsIndex index) {
        return index.getSeries() != seriesIndex
                ? -1
                : index.getObs() + startPosition / getColumnCount();
    }

    @Override
    public int getColumnIndex(ObsIndex index) {
        return index.getSeries() != seriesIndex
                ? -1
                : index.getObs() + startPosition % getColumnCount();
    }
}
