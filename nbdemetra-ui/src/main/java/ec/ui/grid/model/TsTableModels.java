/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid.model;

import ec.tss.TsCollection;
import ec.tss.TsStatus;
import ec.ui.interfaces.ITsGrid.Chronology;
import ec.ui.interfaces.ITsGrid.Mode;
import ec.ui.interfaces.ITsGrid.Orientation;
import ec.util.grid.swing.GridModel;
import ec.util.grid.swing.GridModels;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public final class TsTableModels {

    private TsTableModels() {
        // static class
    }

    public static GridModel chooseGridModel(TsCollection col, Mode mode, Orientation orientation, Chronology chronology) {
        if (col.isEmpty() || (mode == Mode.SINGLETS && col.get(0).hasData() != TsStatus.Valid)) {
            return GridModels.empty();
        }
        IGridData data = mode == Mode.MULTIPLETS ? new MultiTsGridData(col) : new SingleTsGridData(col.get(0).getTsData());
        data = chronology == Chronology.ASCENDING ? data : GridDatas.flipVerticaly(data);
        data = orientation == Orientation.NORMAL ? data : GridDatas.transpose(data);
        return new GridDataAdapter(data);
    }

    static class GridDataAdapter extends AbstractTableModel implements GridModel {

        IGridData data;

        public GridDataAdapter(IGridData data) {
            this.data = data;
        }

        @Override
        public int getRowCount() {
            return data.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return data.getColumnCount();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.getValue(rowIndex, columnIndex);
        }

        @Override
        public String getRowName(int rowIndex) {
            return data.getRowName(rowIndex);
        }

        @Override
        public String getColumnName(int column) {
            return data.getColumnName(column);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Number.class;
        }
    }
}
