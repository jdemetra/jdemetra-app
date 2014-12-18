/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid;

import ec.util.grid.swing.GridModel;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Philippe Charles
 */
final class GridModelAdapter extends AbstractTableModel implements GridModel {

    private final TsGridData data;

    public GridModelAdapter(TsGridData data) {
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
        return data.getObs(rowIndex, columnIndex);
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
        return TsGridObs.class;
    }
}
