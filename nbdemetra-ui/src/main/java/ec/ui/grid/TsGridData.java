/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.ui.grid;

import ec.tss.TsCollection;
import ec.tss.TsStatus;
import ec.tstoolkit.timeseries.simplets.TsDataTableInfo;
import ec.ui.chart.DataFeatureModel;
import ec.ui.interfaces.ITsGrid.Chronology;
import ec.ui.interfaces.ITsGrid.Orientation;
import ec.util.chart.ObsIndex;
import ec.util.grid.CellIndex;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Philippe Charles
 */
abstract class TsGridData {

    @NonNegative
    abstract public int getColumnCount();

    @NonNull
    abstract public String getColumnName(int j);

    @NonNegative
    abstract public int getRowCount();

    @NonNull
    abstract public String getRowName(int i);

    @NonNull
    abstract public TsGridObs getObs(int i, int j);

    @NonNull
    public TsGridData transpose() {
        return new Transposed(this);
    }

    @NonNull
    public TsGridData flipHorizontaly() {
        return new FlippedHorizontaly(this);
    }

    @NonNull
    public TsGridData flipVerticaly() {
        return new FlippedVerticaly(this);
    }

    @NonNull
    public ObsIndex toObsIndex(@NonNull CellIndex index) {
        if (CellIndex.NULL.equals(index)) {
            return ObsIndex.NULL;
        }
        TsGridObs obs = getObs(index.getRow(), index.getColumn());
        if (TsDataTableInfo.Empty.equals(obs.getInfo())) {
            return ObsIndex.NULL;
        }
        return ObsIndex.valueOf(obs.getSeriesIndex(), obs.getIndex());
    }

    @NonNull
    abstract public CellIndex toCellIndex(@NonNull ObsIndex index);

    @NonNull
    public static TsGridData empty() {
        return Empty.INSTANCE;
    }

    @NonNull
    public static TsGridData create(TsCollection col, int singleSeriesIndex, Orientation orientation, Chronology chronology, DataFeatureModel dataFeatureModel) {
        if (col.isEmpty() || (singleSeriesIndex != -1 && col.get(singleSeriesIndex).hasData() != TsStatus.Valid)) {
            return empty();
        }
        TsGridData result = singleSeriesIndex == -1 ? new MultiTsGridData(col, dataFeatureModel) : new SingleTsGridData(col, singleSeriesIndex, dataFeatureModel);
        result = chronology == Chronology.ASCENDING ? result : result.flipVerticaly();
        result = orientation == Orientation.NORMAL ? result : result.transpose();
        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static final class Empty extends TsGridData {

        static final Empty INSTANCE = new Empty();

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

        @Override
        public TsGridData transpose() {
            return this;
        }

        @Override
        public TsGridData flipHorizontaly() {
            return this;
        }

        @Override
        public TsGridData flipVerticaly() {
            return this;
        }

        @Override
        public CellIndex toCellIndex(ObsIndex index) {
            return CellIndex.NULL;
        }
    }

    private static final class Transposed extends TsGridData {

        private final TsGridData data;

        public Transposed(TsGridData data) {
            this.data = data;
        }

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

        @Override
        public TsGridData transpose() {
            return data;
        }

        @Override
        public CellIndex toCellIndex(ObsIndex index) {
            CellIndex tmp = data.toCellIndex(index);
            return CellIndex.valueOf(tmp.getColumn(), tmp.getRow());
        }
    }

    private static final class FlippedHorizontaly extends TsGridData {

        private final TsGridData data;

        public FlippedHorizontaly(TsGridData data) {
            this.data = data;
        }

        private int flipColumn(int j) {
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

        @Override
        public TsGridData flipHorizontaly() {
            return data;
        }

        @Override
        public CellIndex toCellIndex(ObsIndex index) {
            CellIndex tmp = data.toCellIndex(index);
            return CellIndex.valueOf(tmp.getRow(), flipColumn(tmp.getColumn()));
        }
    }

    private static final class FlippedVerticaly extends TsGridData {

        private final TsGridData data;

        public FlippedVerticaly(TsGridData data) {
            this.data = data;
        }

        private int flipRow(int i) {
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

        @Override
        public TsGridData flipVerticaly() {
            return data;
        }

        @Override
        public CellIndex toCellIndex(ObsIndex index) {
            CellIndex tmp = data.toCellIndex(index);
            return CellIndex.valueOf(flipRow(tmp.getRow()), tmp.getColumn());
        }
    }
    //</editor-fold>
}
