/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid;

import com.google.common.collect.Iterables;
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
class MultiTsGridData extends TsGridData {

    final TsGridObs obs;
    final List<String> names;
    final TsDataTable dataTable;
    final TsDomain domain;
    final IntList firstObsIndexes;

    public MultiTsGridData(TsCollection col) {
        this.obs = new TsGridObs(createStats(col));
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
    }

    static DescriptiveStatistics createStats(TsCollection col) {
        List<double[]> allValues = new ArrayList<>();
        for (Ts o : col) {
            if (o.hasData() == TsStatus.Valid) {
                allValues.add(o.getTsData().getValues().internalStorage());
            }
        }
        return new DescriptiveStatistics(Doubles.concat(Iterables.toArray(allValues, double[].class)));
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
