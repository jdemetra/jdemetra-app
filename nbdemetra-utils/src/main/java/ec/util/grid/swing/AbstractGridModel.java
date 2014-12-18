package ec.util.grid.swing;

import javax.swing.table.AbstractTableModel;

/**
 * This abstract class provides default implementations for most of the methods
 * in the
 * <code>GridModel</code> interface. It takes care of the management of
 * listeners and provides some conveniences for generating
 * <code>TableModelEvents</code> and dispatching them to the listeners.
 *
 * @author Philippe Charles
 */
public abstract class AbstractGridModel extends AbstractTableModel implements GridModel {

    @Override
    public String getRowName(int rowIndex) {
        return Integer.toString(rowIndex);
    }
}
