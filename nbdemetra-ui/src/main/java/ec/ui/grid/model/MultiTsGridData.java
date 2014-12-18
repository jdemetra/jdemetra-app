/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid.model;

import ec.tss.Ts;
import ec.tstoolkit.timeseries.simplets.TsDataTable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
class MultiTsGridData implements IGridData {

    final List<String> names;
    final TsDataTable dataTable;

    public MultiTsGridData(Iterable<Ts> list) {
        this.names = new ArrayList<>();
        this.dataTable = new TsDataTable();
        for (Ts o : list) {
            names.add(o.getName());
            dataTable.insert(-1, o.getTsData());
        }
    }

    @Override
    public String getRowName(int i) {
        return dataTable.getDomain().get(i).toString();
    }

    @Override
    public String getColumnName(int j) {
        return names.get(j);
    }

    @Override
    public Number getValue(int i, int j) {
        switch (dataTable.getDataInfo(i, j)) {
            case Empty:
                return null;
            case Missing:
                return Double.NaN;
            case Valid:
                return dataTable.getData(i, j);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRowCount() {
        return dataTable.isEmpty() ? 0 : dataTable.getDomain().getLength();
    }

    @Override
    public int getColumnCount() {
        return dataTable.getSeriesCount();
    }
}
