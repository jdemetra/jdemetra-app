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

import ec.tss.Ts;
import ec.tstoolkit.data.DescriptiveStatistics;
import ec.tstoolkit.design.FlyweightPattern;
import ec.tstoolkit.timeseries.simplets.TsDataTableInfo;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.chart.DataFeatureModel;
import java.util.function.Supplier;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Philippe Charles
 */
@FlyweightPattern
public abstract class TsGridObs {

    @NonNull
    abstract public TsDataTableInfo getInfo();

    @NonNegative
    abstract public int getSeriesIndex();

    @NonNegative
    abstract public int getIndex() throws IllegalStateException;

    @NonNull
    abstract public TsPeriod getPeriod() throws IllegalStateException;

    abstract public double getValue() throws IllegalStateException;

    @NonNull
    abstract public DescriptiveStatistics getStats() throws IllegalStateException;

    abstract public boolean hasFeature(Ts.@NonNull DataFeature feature) throws IllegalStateException;

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    static final TsGridObs empty(int seriesIndex) {
        return Empty.INSTANCE.with(seriesIndex);
    }

    static final TsGridObs missing(int seriesIndex, int obsIndex, TsPeriod period) {
        return Missing.INSTANCE.with(seriesIndex, obsIndex, period);
    }

    static final TsGridObs valid(int seriesIndex, int obsIndex, TsPeriod period, double value, Supplier<DescriptiveStatistics> stats, DataFeatureModel dataFeatureModel) {
        return Valid.INSTANCE.with(seriesIndex, obsIndex, period, value, stats, dataFeatureModel);
    }

    private static final class Empty extends TsGridObs {

        private static final Empty INSTANCE = new Empty();

        private int seriesIndex;

        private TsGridObs with(int seriesIndex) {
            this.seriesIndex = seriesIndex;
            return this;
        }

        @Override
        public TsDataTableInfo getInfo() {
            return TsDataTableInfo.Empty;
        }

        @Override
        public int getSeriesIndex() {
            return seriesIndex;
        }

        @Override
        public int getIndex() throws IllegalStateException {
            throw new IllegalStateException();
        }

        @Override
        public TsPeriod getPeriod() throws IllegalStateException {
            throw new IllegalStateException();
        }

        @Override
        public double getValue() throws IllegalStateException {
            throw new IllegalStateException();
        }

        @Override
        public DescriptiveStatistics getStats() throws IllegalStateException {
            throw new IllegalStateException();
        }

        @Override
        public boolean hasFeature(Ts.DataFeature feature) throws IllegalStateException {
            throw new IllegalStateException();
        }
    }

    private static final class Missing extends TsGridObs {

        private static final Missing INSTANCE = new Missing();

        private int seriesIndex;
        private int index;
        private TsPeriod period;

        private TsGridObs with(int seriesIndex, int index, TsPeriod period) {
            this.seriesIndex = seriesIndex;
            this.index = index;
            this.period = period;
            return this;
        }

        @Override
        public TsDataTableInfo getInfo() {
            return TsDataTableInfo.Missing;
        }

        @Override
        public int getSeriesIndex() {
            return seriesIndex;
        }

        @Override
        public int getIndex() throws IllegalStateException {
            return index;
        }

        @Override
        public TsPeriod getPeriod() throws IllegalStateException {
            return period;
        }

        @Override
        public double getValue() throws IllegalStateException {
            throw new IllegalStateException();
        }

        @Override
        public DescriptiveStatistics getStats() throws IllegalStateException {
            throw new IllegalStateException();
        }

        @Override
        public boolean hasFeature(Ts.DataFeature feature) throws IllegalStateException {
            throw new IllegalStateException();
        }
    }

    private static final class Valid extends TsGridObs {

        private static final Valid INSTANCE = new Valid();

        private int seriesIndex;
        private int index;
        private TsPeriod period;
        private double value;
        private Supplier<DescriptiveStatistics> stats;
        private DataFeatureModel dataFeatureModel;

        private TsGridObs with(int seriesIndex, int index, TsPeriod period, double value, Supplier<DescriptiveStatistics> stats, DataFeatureModel dataFeatureModel) {
            this.seriesIndex = seriesIndex;
            this.index = index;
            this.period = period;
            this.value = value;
            this.stats = stats;
            this.dataFeatureModel = dataFeatureModel;
            return this;
        }

        @Override
        public TsDataTableInfo getInfo() {
            return TsDataTableInfo.Valid;
        }

        @Override
        public int getSeriesIndex() {
            return seriesIndex;
        }

        @Override
        public int getIndex() throws IllegalStateException {
            return index;
        }

        @Override
        public TsPeriod getPeriod() throws IllegalStateException {
            return period;
        }

        @Override
        public double getValue() throws IllegalStateException {
            return value;
        }

        @Override
        public DescriptiveStatistics getStats() throws IllegalStateException {
            return stats.get();
        }

        @Override
        public boolean hasFeature(Ts.DataFeature feature) throws IllegalStateException {
            return dataFeatureModel.hasFeature(feature, seriesIndex, index);
        }
    }
    //</editor-fold>
}
