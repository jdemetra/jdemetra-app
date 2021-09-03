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

import demetra.desktop.components.TsGridObs;
import demetra.timeseries.Ts;
import demetra.timeseries.TsDataTable;
import demetra.timeseries.TsDomain;
import demetra.timeseries.TsPeriod;
import ec.util.chart.ObsIndex;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Philippe Charles
 */
final class MultiTsGridData implements TsGridData {

    private final List<String> names;
    private final TsDataTable dataTable;
    private final TsDomain domain;
    private final TsDataTable.Cursor cursor;
    private final TsGridObs obs;

    public MultiTsGridData(List<Ts> col) {
        this.names = col.stream().map(demetra.timeseries.Ts::getName).collect(Collectors.toList());
        this.dataTable = TsDataTable.of(col, demetra.timeseries.Ts::getData);
        this.domain = dataTable.getDomain();
        this.cursor = dataTable.cursor(TsDataTable.DistributionType.FIRST);
        this.obs = new TsGridObs();
    }

    @Override
    public String getRowName(int i) {
        return domain.get(i).display();
    }

    @Override
    public String getColumnName(int j) {
        return names.get(j);
    }

    @Override
    public TsGridObs getObs(int period, int series) {
        cursor.moveTo(period, series);
        obs.setIndex(cursor.getIndex());
        obs.setPeriod(domain.get(period));
        obs.setSeriesIndex(series);
        obs.setStatus(cursor.getStatus());
        obs.setValue(cursor.getValue());
        return obs;
    }

    @Override
    public int getRowCount() {
        return cursor.getPeriodCount();
    }

    @Override
    public int getColumnCount() {
        return cursor.getSeriesCount();
    }

    @Override
    public int getRowIndex(ObsIndex index) {
        if (ObsIndex.NULL.equals(index)) {
            return -1;
        }
        TsPeriod x = dataTable.getData().get(index.getSeries()).getDomain().get(index.getObs()).withUnit(domain.getTsUnit());
        return domain.indexOf(x);
    }

    @Override
    public int getColumnIndex(ObsIndex index) {
        return index.getSeries();
    }
}
