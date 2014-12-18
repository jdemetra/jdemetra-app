/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid;

import ec.tss.TsCollection;
import ec.tss.TsStatus;
import ec.ui.interfaces.ITsGrid.Chronology;
import ec.ui.interfaces.ITsGrid.Orientation;

/**
 *
 * @author Philippe Charles
 */
abstract class TsGridData {

    public static TsGridData empty() {
        return EmptyTsGridData.INSTANCE;
    }

    public static TsGridData create(TsCollection col, int singleSeriesIndex, Orientation orientation, Chronology chronology) {
        if (col.isEmpty() || (singleSeriesIndex != -1 && col.get(singleSeriesIndex).hasData() != TsStatus.Valid)) {
            return empty();
        }
        TsGridData result = singleSeriesIndex == -1 ? new MultiTsGridData(col) : new SingleTsGridData(col, singleSeriesIndex);
        result = chronology == Chronology.ASCENDING ? result : result.flipVerticaly();
        result = orientation == Orientation.NORMAL ? result : result.transpose();
        return result;
    }

    abstract public int getColumnCount();

    abstract public String getColumnName(int j);

    abstract public int getRowCount();

    abstract public String getRowName(int i);

    abstract public TsGridObs getObs(int i, int j);

    public TsGridData transpose() {
        final TsGridData data = this;
        return new TsGridData() {
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
            public TsGridObs getObs(int i, int j) {
                return data.getObs(j, i);
            }
        };
    }

    public TsGridData flipHorizontaly() {
        final TsGridData data = this;
        return new TsGridData() {
            int flipColumn(int j) {
                return data.getColumnCount() - j - 1;
            }

            @Override
            public int getColumnCount() {
                return data.getColumnCount();
            }

            @Override
            public String getColumnName(int j) {
                return data.getColumnName(flipColumn(j));
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
            public TsGridObs getObs(int i, int j) {
                return data.getObs(i, flipColumn(j));
            }
        };
    }

    public TsGridData flipVerticaly() {
        final TsGridData data = this;
        return new TsGridData() {
            int flipRow(int i) {
                return data.getRowCount() - i - 1;
            }

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
                return data.getRowName(flipRow(i));
            }

            @Override
            public TsGridObs getObs(int i, int j) {
                return data.getObs(flipRow(i), j);
            }
        };
    }

    private static class EmptyTsGridData extends TsGridData {

        static final EmptyTsGridData INSTANCE = new EmptyTsGridData();

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
        public TsGridObs getObs(int i, int j) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
