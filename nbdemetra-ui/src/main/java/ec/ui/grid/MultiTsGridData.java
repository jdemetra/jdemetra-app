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
import com.google.common.primitives.Doubles;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsStatus;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsDataTable;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.utilities.IntList;
import ec.ui.chart.DataFeatureModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philippe Charles
 */
final class MultiTsGridData extends TsGridData implements Supplier<DescriptiveStatistics> {

    private final List<String> names;
    private final TsDataTable dataTable;
    private final TsDomain domain;
    private final IntList firstObsIndexes;
    private final DataFeatureModel dataFeatureModel;
    private DescriptiveStatistics stats;

    public MultiTsGridData(TsCollection col, DataFeatureModel dataFeatureModel) {
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
        this.dataFeatureModel = dataFeatureModel;
        this.stats = null;
    }

    @Override
    public DescriptiveStatistics get() {
        if (stats == null) {
            double[][] allValues = new double[dataTable.getSeriesCount()][];
            for (int i = 0; i < allValues.length; i++) {
                allValues[i] = dataTable.series(i).getValues().internalStorage();
            }
            stats = new DescriptiveStatistics(Doubles.concat(allValues));
        }
        return stats;
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
                return TsGridObs.empty(series);
            case Missing:
                return TsGridObs.missing(series, i - firstObsIndexes.get(series), domain.get(i));
            case Valid:
                return TsGridObs.valid(series, i - firstObsIndexes.get(series), domain.get(i), dataTable.getData(i, series), this, dataFeatureModel);
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
