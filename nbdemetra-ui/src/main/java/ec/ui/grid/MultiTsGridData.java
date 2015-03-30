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
package ec.ui.grid;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.primitives.Doubles;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsStatus;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsDataTable;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.utilities.IntList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philippe Charles
 */
final class MultiTsGridData extends TsGridData {

    private final TsGridObs obs;
    private final List<String> names;
    private final TsDataTable dataTable;
    private final TsDomain domain;
    private final IntList firstObsIndexes;

    public MultiTsGridData(TsCollection col) {
        this.names = new ArrayList<>();
        this.dataTable = new TsDataTable();
        for (Ts o : col) {
            if (o.hasData() == TsStatus.Valid) {
                names.add(o.getName());
                dataTable.insert(-1, o.getTsData());
            }
        }
        this.domain = dataTable.getDomain();
        this.firstObsIndexes = new IntList();
        if (domain != null) {
            for (int i = 0; i < dataTable.getSeriesCount(); i++) {
                firstObsIndexes.add(domain.search(dataTable.series(i).getStart()));
            }
        }
        this.obs = new TsGridObs(Suppliers.memoize(createStats(dataTable)));
    }

    private static Supplier<DescriptiveStatistics> createStats(final TsDataTable dataTable) {
        return new Supplier<DescriptiveStatistics>() {
            @Override
            public DescriptiveStatistics get() {
                double[][] allValues = new double[dataTable.getSeriesCount()][];
                for (int i = 0; i < allValues.length; i++) {
                    allValues[i] = dataTable.series(i).getValues().internalStorage();
                }
                return new DescriptiveStatistics(Doubles.concat(allValues));
            }
        };
    }

    @Override
    public String getRowName(int i) {
        return domain.get(i).toString();
    }

    @Override
    public String getColumnName(int j) {
        return names.get(j);
    }

    @Override
    public TsGridObs getObs(int i, int series) {
        switch (dataTable.getDataInfo(i, series)) {
            case Empty:
                return obs.empty(series);
            case Missing:
                return obs.missing(series, i - firstObsIndexes.get(series), domain.get(i));
            case Valid:
                return obs.valid(series, i - firstObsIndexes.get(series), domain.get(i), dataTable.getData(i, series));
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRowCount() {
        return domain != null ? domain.getLength() : 0;
    }

    @Override
    public int getColumnCount() {
        return dataTable.getSeriesCount();
    }
}
