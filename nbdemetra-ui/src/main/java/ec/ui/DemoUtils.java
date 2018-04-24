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
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tstoolkit.random.IRandomNumberGenerator;
import ec.tstoolkit.random.XorshiftRNG;
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
}
