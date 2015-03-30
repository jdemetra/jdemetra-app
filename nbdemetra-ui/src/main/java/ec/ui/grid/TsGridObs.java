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
import ec.tss.Ts;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.design.FlyweightPattern;
import ec.tstoolkit.timeseries.simplets.TsDataTableInfo;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.chart.DataFeatureModel;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
@FlyweightPattern
public final class TsGridObs {

    private final Supplier<DescriptiveStatistics> stats;
    private final DataFeatureModel dataFeatureModel;
    private int seriesIndex;
    private int index;
    private TsPeriod period;
    private double value;

    TsGridObs(Supplier<DescriptiveStatistics> stats, DataFeatureModel dataFeatureModel) {
        this.stats = stats;
        this.dataFeatureModel = dataFeatureModel;
        empty(-1);
    }

    final TsGridObs empty(int seriesIndex) {
        return missing(seriesIndex, -1, null);
    }

    final TsGridObs missing(int seriesIndex, int obsIndex, TsPeriod period) {
        return valid(seriesIndex, obsIndex, period, Double.NaN);
    }

    final TsGridObs valid(int seriesIndex, int obsIndex, TsPeriod period, double value) {
        this.seriesIndex = seriesIndex;
        this.index = obsIndex;
        this.period = period;
        this.value = value;
        return this;
    }

    @Nonnull
    public DescriptiveStatistics getStats() {
        return stats.get();
    }

    @Nonnull
    public TsDataTableInfo getInfo() {
        return period == null ? TsDataTableInfo.Empty : Double.isNaN(value) ? TsDataTableInfo.Missing : TsDataTableInfo.Valid;
    }

    @Nonnegative
    public int getSeriesIndex() {
        return seriesIndex;
    }

    @Nonnegative
    public int getIndex() throws IllegalStateException {
        if (index == -1) {
            throw new IllegalStateException("Empty or missing");
        }
        return index;
    }

    @Nonnull
    public TsPeriod getPeriod() throws IllegalStateException {
        if (period == null) {
            throw new IllegalStateException("Empty");
        }
        return period;
    }

    public double getValue() throws IllegalStateException {
        if (index == -1) {
            throw new IllegalStateException("Empty or missing");
        }
        return value;
    }

    public boolean hasFeature(@Nonnull Ts.DataFeature feature) throws IllegalStateException {
        if (index == -1) {
            throw new IllegalStateException("Empty or missing");
        }
        return dataFeatureModel.hasFeature(feature, seriesIndex, index);
    }
}
