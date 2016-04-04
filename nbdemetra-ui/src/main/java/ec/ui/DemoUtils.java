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

import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.TsStatus;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.arima.ArimaModelBuilder;
import ec.tstoolkit.data.ReadDataBlock;
import ec.tstoolkit.design.IBuilder;
import ec.tstoolkit.random.IRandomNumberGenerator;
import ec.tstoolkit.random.XorshiftRNG;
import ec.tstoolkit.sarima.SarimaModel;
import ec.tstoolkit.sarima.SarimaSpecification;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
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
        return new RandomTsCollectionBuilder().withSeries(nSeries).withObs(nObs).withRNG(rng).build();
    }

    public enum TsNamingScheme {

        DEFAULT, UNIQUE, DESCRIPTIVE;
    }

    public static final class RandomTsCollectionBuilder implements IBuilder<TsCollection> {

        private static final AtomicLong UID = new AtomicLong();

        private final RandomValuesStrategy strategy;
        private int nSeries;
        private int nObs;
        private IRandomNumberGenerator rng;
        private int forecastCount;
        private long startTimeMillis;
        private TsFrequency frequency;
        private TsStatus status;
        private TsNamingScheme naming;
        private int missingValues;

        public RandomTsCollectionBuilder() {
            this.strategy = new CustomStrategy();
            this.nSeries = 3;
            this.nObs = 24;
            this.rng = new XorshiftRNG(0);
            this.forecastCount = 0;
            this.startTimeMillis = System.currentTimeMillis();
            this.frequency = TsFrequency.Monthly;
            this.status = TsStatus.Valid;
            this.naming = TsNamingScheme.DEFAULT;
            this.missingValues = 0;
        }

        //<editor-fold defaultstate="collapsed" desc="Options">
        @Nonnull
        public RandomTsCollectionBuilder withSeries(@Nonnegative int count) {
            this.nSeries = count;
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withObs(@Nonnegative int count) {
            this.nObs = count;
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withRNG(@Nonnull IRandomNumberGenerator rng) {
            this.rng = rng;
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withForecast(@Nonnegative int count) {
            this.forecastCount = count;
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
            this.status = status;
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withNaming(@Nonnull TsNamingScheme naming) {
            this.naming = naming;
            return this;
        }

        @Nonnull
        public RandomTsCollectionBuilder withMissingValues(@Nonnegative int missingValues) {
            this.missingValues = missingValues;
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
                    return nSeries + ":" + nObs + ":" + rng.getClass().getSimpleName() + ":"
                            + forecastCount + ":" + getStart() + ":" + frequency + ":" + status + ":" + index;
                case UNIQUE:
                    return "S" + UID.getAndIncrement();
            }
            throw new RuntimeException();
        }

        @Override
        public TsCollection build() {
            TsCollection result = TsFactory.instance.createTsCollection();

            switch (status) {
                case Undefined:
                    for (int i = 0; i < nSeries; i++) {
                        String name = getName(i);
                        result.quietAdd(TsFactory.instance.createTs(name, (MetaData) null, (TsData) null));
                    }
                    break;
                case Invalid:
                    for (int i = 0; i < nSeries; i++) {
                        String name = getName(i);
                        MetaData meta = new MetaData();
                        result.quietAdd(TsFactory.instance.createTs(name, meta, (TsData) null));
                    }
                    break;
                case Valid:
                    TsPeriod start = getStart();
                    double[][] values = strategy.getValues(nSeries, nObs, rng, startTimeMillis);
                    for (int i = 0; i < nSeries; i++) {
                        String name = getName(i);
                        TsData data = new TsData(start, values[i], false);
                        MetaData meta = new MetaData();
                        if (forecastCount > 0) {
                            meta.put(Ts.END, data.getDomain().get(data.getLength() - forecastCount - 1).lastday().toString());
                        }
                        if (missingValues > 0 && !data.isEmpty()) {
                            for (int x = 0; x < missingValues; x++) {
                                data.setMissing(rng.nextInt(data.getLength()));
                            }
                        }
                        result.quietAdd(TsFactory.instance.createTs(name, meta, data));
                    }
                    break;
            }

            return result;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="RandomValuesStrategies">
    private interface RandomValuesStrategy {

        double[][] getValues(int series, int obs, IRandomNumberGenerator rng, long startTimeMillis);
    }

    private static class CustomStrategy implements RandomValuesStrategy {

        @Override
        public double[][] getValues(int series, int obs, IRandomNumberGenerator rng, long startTimeMillis) {
            double[][] result = new double[series][obs];
            for (int i = 0; i < series; i++) {
                for (int j = 0; j < obs; j++) {
                    result[i][j] = Math.abs((100 * (Math.cos(startTimeMillis * i))) + (100 * (Math.sin(startTimeMillis) - Math.cos(rng.nextDouble()) + Math.tan(rng.nextDouble()))));
                }
            }
            return result;
        }
    }

    private static class ArimaStrategy implements RandomValuesStrategy {

        final SarimaModel model;

        public ArimaStrategy() {
            SarimaSpecification spec = new SarimaSpecification(12);
            spec.setP(0);
            spec.setD(1);
            spec.setQ(1);
            spec.setBP(0);
            spec.setBD(1);
            spec.setBQ(1);
            model = new SarimaModel(spec);
            model.setParameters(new ReadDataBlock(new double[]{-.8, -.6}));
        }

        @Override
        public double[][] getValues(int series, int obs, IRandomNumberGenerator rng, long startTimeMillis) {
            // 1. create a random engine based on arima
            ArimaModelBuilder builder = new ArimaModelBuilder();
            builder.setRandomNumberGenerator(rng);
            // 2. generate data
            double[][] result = new double[series][];
            for (int i = 0; i < series; ++i) {
                result[i] = builder.generate(model, obs);
            }
            return result;
        }
    }
    //</editor-fold>
}
