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
import com.google.common.base.Suppliers;
import ec.tss.TsCollection;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.data.Values;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.chart.DataFeatureModel;

/**
 *
 * @author Philippe Charles
 */
final class SingleTsGridData extends TsGridData {

    private final TsGridObs obs;
    private final int seriesIndex;
    private final Values data;
    private final TsDomain domain;
    private final int startYear;
    private final int startPosition;

    public SingleTsGridData(TsCollection col, int seriesIndex, DataFeatureModel dataFeatureModel) {
        this.seriesIndex = seriesIndex;
        this.data = col.get(seriesIndex).getTsData().getValues();
        this.domain = col.get(seriesIndex).getTsData().getDomain();
        this.startYear = domain.getStart().getYear();
        this.startPosition = domain.getStart().getPosition();
        this.obs = new TsGridObs(Suppliers.memoize(createStats(data)), dataFeatureModel);
    }

    private static Supplier<DescriptiveStatistics> createStats(final Values data) {
        return new Supplier<DescriptiveStatistics>() {
            @Override
            public DescriptiveStatistics get() {
                return new DescriptiveStatistics(data);
            }
        };
    }

    int getPeriodId(int i, int j) {
        int periodId = j + (getColumnCount() * i) - startPosition;
        return (periodId < 0 || periodId >= domain.getLength()) ? -1 : periodId;
    }

    @Override
    public String getRowName(int i) {
        return Integer.toString(startYear + i);
    }

    @Override
    public String getColumnName(int j) {
        return TsPeriod.formatShortPeriod(domain.getFrequency(), j);
    }

    @Override
    public TsGridObs getObs(int i, int j) {
        int periodId = getPeriodId(i, j);
        return periodId == -1 ? obs.empty(seriesIndex) : obs.valid(seriesIndex, periodId, domain.get(periodId), data.get(periodId));
    }

    @Override
    public int getRowCount() {
        return domain.getYearsCount();
    }

    @Override
    public int getColumnCount() {
        return domain.getFrequency().intValue();
    }
}
