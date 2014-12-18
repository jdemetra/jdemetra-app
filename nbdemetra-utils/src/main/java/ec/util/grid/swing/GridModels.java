/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.grid.swing;

/**
 *
 * @author Philippe Charles
 */
public final class GridModels {

    private GridModels() {
        // static class
    }

    public static GridModel empty() {
        return EMPTY_GRID_MODEL;
    }
    //
    private static final GridModel EMPTY_GRID_MODEL = new AbstractGridModel() {
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 0;
        }
    };
}
