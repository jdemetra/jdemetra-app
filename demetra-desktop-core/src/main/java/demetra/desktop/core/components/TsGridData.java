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
package demetra.desktop.core.components;

import demetra.desktop.components.TsGridObs;
import demetra.timeseries.Ts;
import ec.util.chart.ObsIndex;
import java.util.Collections;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 *
 * @author Philippe Charles
 */
interface TsGridData {

    @NonNegative
    int getColumnCount();

    @NonNull
    String getColumnName(int j);

    @NonNegative
    int getRowCount();

    @NonNull
    String getRowName(int i);

    @NonNull
    TsGridObs getObs(int i, int j);

    int getRowIndex(@NonNull ObsIndex index);

    int getColumnIndex(@NonNull ObsIndex index);

    @NonNull
    static TsGridData create(List<Ts> col, int singleSeriesIndex) {
        if (col.isEmpty() || (singleSeriesIndex != -1 && col.get(singleSeriesIndex).getData().isEmpty())) {
            return Empty.INSTANCE;
        }
        if (singleSeriesIndex == -1) {
            return new ByTsColumn(col);
        }
        Ts series = col.get(singleSeriesIndex);
        return series.getData().getAnnualFrequency() == -1
                ? new ByTsColumn(Collections.singletonList(series))
                : new ByAnnualFrequencyColumn(series, singleSeriesIndex);
    }

    enum Empty implements TsGridData {

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
        public TsGridObs getObs(int i, int j) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getRowIndex(ObsIndex index) {
            return -1;
        }

        @Override
        public int getColumnIndex(ObsIndex index) {
            return -1;
        }
    }
}
