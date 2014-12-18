package ec.util.grid.swing;

import javax.swing.table.TableModel;

/**
 * The
 * <code>GridModel</code> interface specifies the methods the
 * <code>JGrid</code> will use to interrogate a tabular data model. <p>
 *
 * @see JGrid
 * @author Philippe Charles
 */
public interface GridModel extends TableModel {

    /**
     * Returns the name of the row at
     * <code>rowIndex</code>. This is used to initialize the grid's row header
     * name. Note: this name does not need to be unique; two rows in a grid can
     * have the same name.
     *
     * @param rowIndex the index of the row
     * @return the name of the row
     */
    String getRowName(int rowIndex);
}