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
package internal.ui.components;

import demetra.ui.components.TsGridObs;
import ec.util.chart.ObsIndex;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
interface TsGridData {

    @Nonnegative
    int getColumnCount();

    @Nonnull
    String getColumnName(int j);

    @Nonnegative
    int getRowCount();

    @Nonnull
    String getRowName(int i);

    @Nonnull
    TsGridObs getObs(int i, int j);

    int getRowIndex(@Nonnull ObsIndex index);

    int getColumnIndex(@Nonnull ObsIndex index);

    @Nonnull
    static TsGridData create(List<demetra.tsprovider.Ts> col, int singleSeriesIndex) {
        if (col.isEmpty() || (singleSeriesIndex != -1 && col.get(singleSeriesIndex).getData().isEmpty())) {
            return Empty.INSTANCE;
        }
        return singleSeriesIndex == -1 ? new MultiTsGridData(col) : new SingleTsGridData(col, singleSeriesIndex);
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
