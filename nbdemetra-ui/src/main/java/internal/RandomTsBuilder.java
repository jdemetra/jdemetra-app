/*
 * Copyright 2016 National Bank of Belgium
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
package internal;

import demetra.bridge.TsConverter;
import demetra.tsprovider.TsMeta;
import ec.tss.TsInformation;
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
import java.util.Objects;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
public final class RandomTsBuilder implements IBuilder<TsInformation> {

    private String name;
    private TsStatus status;
    private BiFunction<Integer, IRandomNumberGenerator, double[]> generator;
    private int forecastCount;
    private int missingCount;
    private int obsCount;
    private IRandomNumberGenerator rng;
    private TsPeriod start;

    public RandomTsBuilder() {
        this.name = "";
        this.status = TsStatus.Valid;
        this.forecastCount = 0;
        this.missingCount = 0;
        this.obsCount = 24;
        this.rng = new XorshiftRNG(0);
        this.start = new TsPeriod(TsFrequency.Monthly, 2010, 0);
        this.generator = (x, y) -> generateValues(x, y, start.firstday().getTimeInMillis());
    }

    public int getObsCount() {
        return obsCount;
    }

    @Nonnull
    public IRandomNumberGenerator getRng() {
        return rng;
    }

    public int getForecastCount() {
        return forecastCount;
    }

    @Nonnull
    public TsStatus getStatus() {
        return status;
    }

    @Nonnull
    public RandomTsBuilder withName(@Nonnull String name) {
        this.name = Objects.requireNonNull(name);
        return this;
    }

    @Nonnull
    public RandomTsBuilder withStatus(@Nonnull TsStatus status) {
        this.status = Objects.requireNonNull(status);
        return this;
    }

    @Nonnull
    public RandomTsBuilder withStart(@Nonnull TsPeriod start) {
        this.start = Objects.requireNonNull(start);
        return this;
    }

    @Nonnull
    public RandomTsBuilder withGenerator(@Nonnull BiFunction<Integer, IRandomNumberGenerator, double[]> generator) {
        this.generator = Objects.requireNonNull(generator);
        return this;
    }

    @Nonnull
    public RandomTsBuilder withForecastCount(int forecastCount) {
        this.forecastCount = forecastCount;
        return this;
    }

    @Nonnull
    public RandomTsBuilder withMissingCount(int missingCount) {
        this.missingCount = missingCount;
        return this;
    }

    @Nonnull
    public RandomTsBuilder withObsCount(int obsCount) {
        this.obsCount = obsCount;
        return this;
    }

    @Nonnull
    public RandomTsBuilder withRng(@Nonnull IRandomNumberGenerator rng) {
        this.rng = Objects.requireNonNull(rng);
        return this;
    }

    @Override
    public TsInformation build() {
        switch (status) {
            case Undefined:
                return buildUndefined();
            case Invalid:
                return buildInvalid();
            case Valid:
                return buildValid();
            default:
                throw new RuntimeException();
        }
    }

    private TsInformation buildUndefined() {
        TsInformation result = new TsInformation();
        result.name = name;
        return result;
    }

    private TsInformation buildInvalid() {
        TsInformation result = new TsInformation();
        result.name = name;
        result.metaData = new MetaData();
        return result;
    }

    private TsInformation buildValid() {
        TsInformation result = new TsInformation();
        result.name = name;
        result.metaData = new MetaData();
        double[] values = generator.apply(obsCount, rng);
        TsData data = new TsData(start, values, false);
        if (forecastCount > 0) {
            TsMeta.END.store(result.metaData, TsConverter.toDateTime(data.getDomain().get(data.getLength() - forecastCount - 1).lastday()));
        }
        if (missingCount > 0 && !data.isEmpty()) {
            for (int x = 0; x < missingCount; x++) {
                data.setMissing(rng.nextInt(data.getLength()));
            }
        }
        result.data = data;
        return result;
    }

    private static double[] generateValues(int obsCount, IRandomNumberGenerator rng, long startTimeMillis) {
        int seriesIndex = rng.nextInt();
        double[] result = new double[obsCount];
        for (int j = 0; j < obsCount; j++) {
            result[j] = Math.abs((100 * (Math.cos(startTimeMillis * seriesIndex))) + (100 * (Math.sin(startTimeMillis) - Math.cos(rng.nextDouble()) + Math.tan(rng.nextDouble()))));
        }
        return result;
    }

    private static class ArimaStrategy {

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
}
