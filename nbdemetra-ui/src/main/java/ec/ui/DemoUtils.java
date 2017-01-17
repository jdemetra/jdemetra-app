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
package ec.ui;

import internal.RandomTsBuilder;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.TsStatus;
import ec.tstoolkit.design.IBuilder;
import ec.tstoolkit.random.IRandomNumberGenerator;
import ec.tstoolkit.random.XorshiftRNG;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
public final class DemoUtils {

    private DemoUtils() {
        // static class
    }

    @Nonnull
    public static TsCollection randomTsCollection(int nSeries) {
        return randomTsCollection(nSeries, 24, new XorshiftRNG(0));
    }

    @Nonnull
    public static TsCollection randomTsCollection(@Nonnegative int nSeries, @Nonnegative int nObs, @Nonnull IRandomNumberGenerator rng) {
        RandomTsBuilder builder = new RandomTsBuilder();
        return IntStream.range(0, nSeries)
                .mapToObj(o -> builder.withObsCount(nObs).withRng(rng).withName("S" + o).build().toTs())
                .collect(TsFactory.toTsCollection());
    }

    public enum TsNamingScheme {

        DEFAULT, UNIQUE, DESCRIPTIVE;
    }

    @Deprecated
    public static final class RandomTsCollectionBuilder implements IBuilder<TsCollection> {

        private static final AtomicLong UID = new AtomicLong();

        private final RandomTsBuilder delegate;
        private int nSeries;
        private long startTimeMillis;
        private TsFrequency frequency;
        private TsNamingScheme naming;

        public RandomTsCollectionBuilder() {
            this.delegate = new RandomTsBuilder();
            this.nSeries = 3;
            this.startTimeMillis = System.currentTimeMillis();
            this.frequency = TsFrequency.Monthly;
            this.naming = TsNamingScheme.DEFAULT;
        }

        //<editor-fold defaultstate="collapsed" desc="Options">
        @Nonnull
        public RandomTsCollectionBuilder withSeries(@Nonnegative int count) {
            this.nSeries = count;
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withObs(@Nonnegative int count) {
            delegate.withObsCount(count);
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withRNG(@Nonnull IRandomNumberGenerator rng) {
            delegate.withRng(rng);
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withForecast(@Nonnegative int count) {
            delegate.withForecastCount(count);
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withStartTimeMillis(long time) {
            this.startTimeMillis = time;
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withFrequency(@Nonnull TsFrequency frequency) {
            this.frequency = frequency;
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withStartPeriod(@Nonnull TsPeriod period) {
            this.frequency = period.getFrequency();
            this.startTimeMillis = period.firstday().getTimeInMillis();
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withStatus(@Nonnull TsStatus status) {
            delegate.withStatus(status);
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withNaming(@Nonnull TsNamingScheme naming) {
            this.naming = naming;
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withMissingValues(@Nonnegative int missingValues) {
            delegate.withMissingCount(missingValues);
            return this;
        }
        //</editor-fold>

        private TsPeriod getStart() {
            return new TsPeriod(frequency, new Date(startTimeMillis));
        }

        private String getName(int index) {
            switch (naming) {
                case DEFAULT:
                    return "S" + index;
                case DESCRIPTIVE:
                    return nSeries + ":" + delegate.getObsCount() + ":" + delegate.getRng().getClass().getSimpleName() + ":"
                            + delegate.getForecastCount() + ":" + getStart() + ":" + delegate.getStatus() + ":" + index;
                case UNIQUE:
                    return "S" + UID.getAndIncrement();
            }
            throw new RuntimeException();
        }

        private Ts buildTs(int seriesIndex) {
            return delegate.withName(getName(seriesIndex)).build().toTs();
        }

        @Override
        public TsCollection build() {
            delegate.withStart(getStart());
            return IntStream.range(0, nSeries)
                    .mapToObj(this::buildTs)
                    .collect(TsFactory.toTsCollection());
        }
    }
}
