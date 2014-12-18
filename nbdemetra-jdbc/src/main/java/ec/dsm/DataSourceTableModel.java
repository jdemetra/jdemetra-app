package ec.dsm;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import ec.tss.tsproviders.jdbc.dsm.datasource.DataSourceManager;
import ec.tss.tsproviders.jdbc.dsm.datasource.interfaces.IManagedDataSource;
import javax.swing.table.TableModel;

/**
 * Implementation of TableModel presenting the data found in the manager.
 * @author Demortier Jeremy
 * @see DataSourceManager
 * @see TableModel
 */
@Deprecated
public class DataSourceTableModel extends AbstractTableModel {
  private final List<IManagedDataSource> elements_;

  public DataSourceTableModel() {
    elements_ = DataSourceManager.INSTANCE.toList();
  }

  @Override
  public int getRowCount() {
    return elements_.size();
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    switch(columnIndex) {
      case 0:
        return elements_.get(rowIndex).getName();
      case 1:
        return elements_.get(rowIndex).getSourceType();
      default:
        return null;
    }
  }

  @Override
  public String getColumnName(int column) {
    switch (column) {
      case 0:
        return "Name";
      case 1:
        return "Type";
      default:
        return null;
    }
  }
}
