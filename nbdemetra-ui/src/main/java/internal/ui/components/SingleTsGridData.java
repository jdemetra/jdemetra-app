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
package internal.ui.components;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsDataTable;
import demetra.timeseries.TsSeq;
import ec.tss.Ts;
import ec.tstoolkit.data.IReadDataBlock;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import demetra.ui.components.TsGridObs;
import ec.util.chart.ObsIndex;

/**
 *
 * @author Philippe Charles
 */
final class SingleTsGridData implements TsGridData {

    private final int seriesIndex;
    private final IReadDataBlock data;
    private final TsDomain domain;
    private final int startYear;
    private final int startPosition;
    private final TsGridObs obs;

    public SingleTsGridData(TsSeq col, int seriesIndex) {
        this.seriesIndex = seriesIndex;
        Ts ts = TsConverter.fromTs(col.get(seriesIndex));
        this.data = ts.getTsData();
        this.domain = ts.getTsData().getDomain();
        this.startYear = domain.getStart().getYear();
        this.startPosition = domain.getStart().getPosition();
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
        return TsPeriod.formatShortPeriod(domain.getFrequency(), j);
    }

    @Override
    public TsGridObs getObs(int i, int j) {
        int obsIndex = getPeriodId(i, j);
        obs.setIndex(obsIndex);
        obs.setPeriod(obsIndex != -1 ? TsConverter.toTsPeriod(domain.get(obsIndex)) : null);
        obs.setSeriesIndex(seriesIndex);
        obs.setStatus(obsIndex != -1 ? TsDataTable.ValueStatus.PRESENT : TsDataTable.ValueStatus.EMPTY);
        obs.setValue(obsIndex != -1 ? data.get(obsIndex) : Double.NaN);
        return obs;
    }

    @Override
    public int getRowCount() {
        return domain.getYearsCount();
    }

    @Override
    public int getColumnCount() {
        return domain.getFrequency().intValue();
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
