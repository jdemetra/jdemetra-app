/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid.model;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
final class GridDatas {

    private GridDatas() {
        // static class
    }

    public static IGridData empty() {
        return EmptyGridData.INSTANCE;
    }

    public static IGridData transpose(final IGridData data) {
        return new IGridData() {

            @Override
            public int getColumnCount() {
                return data.getRowCount();
            }

            @Override
            public String getColumnName(int j) {
                return data.getRowName(j);
            }

            @Override
            public int getRowCount() {
                return data.getColumnCount();
            }

            @Override
            public String getRowName(int i) {
                return data.getColumnName(i);
            }

            @Override
            public Number getValue(int i, int j) {
                return data.getValue(j, i);
            }
        };
    }

    public static IGridData flipHorizontaly(final IGridData data) {
        return new IGridData() {

            @Override
            public int getColumnCount() {
                return data.getColumnCount();
            }

            @Override
            public String getColumnName(int j) {
                return data.getColumnName(data.getColumnCount() - j - 1);
            }

            @Override
            public int getRowCount() {
                return data.getRowCount();
            }

            @Override
            public String getRowName(int i) {
                return data.getRowName(i);
            }

            @Override
            public Number getValue(int i, int j) {
                return data.getValue(i, data.getColumnCount() - j - 1);
            }
        };
    }

    public static IGridData flipVerticaly(final IGridData data) {
        return new IGridData() {

            @Override
            public int getColumnCount() {
                return data.getColumnCount();
            }

            @Override
            public String getColumnName(int j) {
                return data.getColumnName(j);
            }

            @Override
            public int getRowCount() {
                return data.getRowCount();
            }

            @Override
            public String getRowName(int i) {
                return data.getRowName(data.getRowCount() - i - 1);
            }

            @Override
            public Number getValue(int i, int j) {
                return data.getValue(data.getRowCount() - i - 1, j);
            }
        };
    }

    private enum EmptyGridData implements IGridData {

        INSTANCE;

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public String getColumnName(int j) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public String getRowName(int i) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Number getValue(int i, int j) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
