/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui;

import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
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

/**
 *
 * @author Philippe Charles
 */
public final class DemoUtils {

    private DemoUtils() {
        // static class
    }

    public static TsCollection randomTsCollection(int nSeries) {
        return randomTsCollection(nSeries, 24, new XorshiftRNG(0));
    }

    public static TsCollection randomTsCollection(int nSeries, int nObs, IRandomNumberGenerator rng) {
        return new RandomTsCollectionBuilder().withSeries(nSeries).withObs(nObs).withRNG(rng).build();
    }

    public static final class RandomTsCollectionBuilder implements IBuilder<TsCollection> {

        private final RandomValuesStrategy strategy;
        private int nSeries;
        private int nObs;
        private IRandomNumberGenerator rng;
        private int forecastCount;
        private long startTimeMillis;
        private TsFrequency frequency;

        public RandomTsCollectionBuilder() {
            this.strategy = new CustomStrategy();
            this.nSeries = 3;
            this.nObs = 24;
            this.rng = new XorshiftRNG(0);
            this.forecastCount = 0;
            this.startTimeMillis = System.currentTimeMillis();
            this.frequency = TsFrequency.Monthly;
        }

        //<editor-fold defaultstate="collapsed" desc="Options">
        public RandomTsCollectionBuilder withSeries(int count) {
            this.nSeries = count;
            return this;
        }

        public RandomTsCollectionBuilder withObs(int count) {
            this.nObs = count;
            return this;
        }

        public RandomTsCollectionBuilder withRNG(IRandomNumberGenerator rng) {
            this.rng = rng;
            return this;
        }

        public RandomTsCollectionBuilder withForecast(int count) {
            this.forecastCount = count;
            return this;
        }

        public RandomTsCollectionBuilder withStartTimeMillis(long time) {
            this.startTimeMillis = time;
            return this;
        }

        public RandomTsCollectionBuilder withFrequency(TsFrequency frequency) {
            this.frequency = frequency;
            return this;
        }
        //</editor-fold>

        @Override
        public TsCollection build() {
            TsCollection result = TsFactory.instance.createTsCollection();

            TsPeriod start = new TsPeriod(frequency, new Date(startTimeMillis));
            double[][] values = strategy.getValues(nSeries, nObs, rng, startTimeMillis);

            for (int i = 0; i < values.length; i++) {
                TsData data = new TsData(start, values[i], false);
                MetaData meta = new MetaData();
                if (forecastCount > 0) {
                    meta.put(Ts.END, data.getDomain().get(data.getLength() - forecastCount - 1).lastday().toString());
                }
                String name = "S" + i;
                result.quietAdd(TsFactory.instance.createTs(name, meta, data));
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
