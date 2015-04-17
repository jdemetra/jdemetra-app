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

import com.google.common.base.Supplier;
import com.google.common.primitives.Doubles;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataTable;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.chart.DataFeatureModel;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
final class MultiTsGridData extends TsGridData implements Supplier<DescriptiveStatistics> {

    private final List<String> names;
    private final TsDataTable dataTable;
    private final TsDomain domain;
    private final TsDataTableCursor cursor;
    private final DataFeatureModel dataFeatureModel;
    private DescriptiveStatistics stats;

    public MultiTsGridData(TsCollection col, DataFeatureModel dataFeatureModel) {
        this.names = new ArrayList<>();
        this.dataTable = new TsDataTable();
        for (Ts o : col) {
            names.add(o.getName());
            dataTable.insert(-1, o.getTsData());
        }
        this.domain = dataTable.getDomain();
        this.cursor = domain != null ? new TsDataTableCursor(dataTable) : null;
        this.dataFeatureModel = dataFeatureModel;
        this.stats = null;
    }

    @Override
    public DescriptiveStatistics get() {
        if (stats == null) {
            double[][] allValues = new double[dataTable.getSeriesCount()][];
            for (int i = 0; i < allValues.length; i++) {
                TsData data = dataTable.series(i);
                allValues[i] = data != null ? data.getValues().internalStorage() : new double[0];
            }
            stats = new DescriptiveStatistics(Doubles.concat(allValues));
        }
        return stats;
    }

    @Override
    public String getRowName(int i) {
        return domain.get(i).toString();
    }

    @Override
    public String getColumnName(int j) {
        return names.get(j);
    }

    @Override
    public TsGridObs getObs(int i, int series) {
        switch (dataTable.getDataInfo(i, series)) {
            case Empty:
                return TsGridObs.empty(series);
            case Missing:
                cursor.moveTo(i, series);
                return TsGridObs.missing(series, cursor.getObsIndex(), cursor.getPeriod());
            case Valid:
                cursor.moveTo(i, series);
                return TsGridObs.valid(series, cursor.getObsIndex(), cursor.getPeriod(), dataTable.getData(i, series), this, dataFeatureModel);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRowCount() {
        return domain != null ? domain.getLength() : 0;
    }

    @Override
    public int getColumnCount() {
        return dataTable.getSeriesCount();
    }

    private static final class TsDataTableCursor {

        private final int tableFirstPosition;
        private final SeriesInfo[] seriesInfo;
        private SeriesInfo currentSeries;
        private int obsIndex;

        public TsDataTableCursor(TsDataTable dataTable) {
            this.tableFirstPosition = dataTable.getDomain().getStart().getPosition();
            this.seriesInfo = new SeriesInfo[dataTable.getSeriesCount()];
            for (int i = 0; i < seriesInfo.length; i++) {
                TsData data = dataTable.series(i);
                if (data != null) {
                    seriesInfo[i] = new SeriesInfo(dataTable.getDomain().getFrequency(), data.getStart());
                }
            }
            currentSeries = null;
            obsIndex = 0;
        }

        public void moveTo(int periodId, int seriesId) {
            currentSeries = seriesInfo[seriesId];
            obsIndex = currentSeries.computeObsIndex(tableFirstPosition, periodId);
        }

        @Nonnegative
        public int getObsIndex() {
            return obsIndex;
        }

        @Nonnull
        public TsPeriod getPeriod() {
            return currentSeries.getPeriod(obsIndex);
        }
    }

    private static final class SeriesInfo {

        private final int ratio;
        private final int firstPosition;
        private final TsPeriod firstPeriod;
        private final TsPeriod period;

        public SeriesInfo(TsFrequency tableFreq, TsPeriod seriesStart) {
            this.ratio = tableFreq.ratio(seriesStart.getFrequency());
            this.firstPosition = seriesStart.getPosition();
            this.firstPeriod = seriesStart;
            this.period = seriesStart.clone();
        }

        public int computeObsIndex(int tableFirstPosition, int periodId) {
            return ((periodId + tableFirstPosition) - (ratio - 1)) / ratio - firstPosition;
        }

        public TsPeriod getPeriod(int obsIndex) {
            // we recycle periods
            period.move(firstPeriod.minus(period) + obsIndex);
            return period;
        }
    }
}
