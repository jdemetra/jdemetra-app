/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package ec.util.chart;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Defines a function to apply on an observation. An observation is identified
 * by its series index and its own index in this series.
 *
 * @author Philippe Charles
 * @param <T> the type of object produced by this function
 */
public abstract class ObsFunction<T> {

    /**
     * Returns the result of applying this function to an observation.
     *
     * @param series the index of the series containing an observation
     * @param obs the index of an observation in its series
     * @return the result of this function; null allowed
     */
    @Nullable
    public abstract T apply(int series, int obs);

    @Nullable
    final public T apply(@Nonnull ObsIndex index) throws NullPointerException {
        return apply(index.getSeries(), index.getObs());
    }

    /**
     * Creates a function that formats the observation indexes as a String.
     *
     * @param format A <a href="../util/Formatter.html#syntax">format string</a>
     * @return a non-null function
     * @throws NullPointerException if the format is null
     */
    @Nonnull
    public static ObsFunction<String> format(@Nonnull String format) throws NullPointerException {
        return new FormatFunc(format);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class FormatFunc extends Constant<String> {

        public FormatFunc(@Nonnull String format) throws NullPointerException {
            super(Objects.requireNonNull(format));
        }

        @Override
        public String apply(int series, int obs) {
            return String.format(value, series, obs);
        }
    }

    private static class Constant<X> extends ObsFunction<X> {

        @Nullable
        protected final X value;

        public Constant(X value) {
            this.value = value;
        }

        @Override
        public X apply(int series, int obs) {
            return value;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof Constant && equals((Constant) obj));
        }

        private boolean equals(Constant other) {
            return Objects.equals(this.value, other.value);
        }
    }
    //</editor-fold>
}
