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

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Defines a function to apply on a series. A series is identified by its own
 * index in a collection.
 *
 * @author Philippe Charles
 * @param <T> the type of object produced by this function
 */
public abstract class SeriesFunction<T> {

    /**
     * Returns the result of applying this function to a series.
     *
     * @param series the index of a series in its collection
     * @return the result of this function; null allowed
     */
    @Nullable
    public abstract T apply(int series);

    @Nonnull
    public static <X> SeriesFunction<X> always(@Nullable X value) {
        return new Constant<>(value);
    }

    @Nonnull
    public static <X> SeriesFunction<X> array(@Nonnull X... values) throws NullPointerException {
        return new FromArray(values);
    }

    /**
     * Creates a function that formats the series index as a String.
     *
     * @param format A <a href="../util/Formatter.html#syntax">format string</a>
     * @return a non-null function
     * @throws NullPointerException if the format is null
     */
    @Nonnull
    public static SeriesFunction<String> format(@Nonnull String format) throws NullPointerException {
        return new FormatFunc(format);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class FormatFunc extends Constant<String> {

        public FormatFunc(@Nonnull String format) throws NullPointerException {
            super(Objects.requireNonNull(format));
        }

        @Override
        public String apply(int series) {
            return String.format(value, series);
        }
    }

    private static class Constant<X> extends SeriesFunction<X> {

        @Nullable
        protected final X value;

        public Constant(X value) {
            this.value = value;
        }

        @Override
        public X apply(int series) {
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

        @Override
        public String toString() {
            return value != null ? value.toString() : "null";
        }
    }

    private static class FromArray<X> extends SeriesFunction<X> {

        protected final X[] values;

        public FromArray(X[] value) {
            this.values = value;
        }

        @Override
        public X apply(int series) {
            return 0 <= series && series < values.length ? values[series] : null;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(values);
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof FromArray && equals((FromArray) obj));
        }

        private boolean equals(FromArray other) {
            return Arrays.equals(values, other.values);
        }

        @Override
        public String toString() {
            return Arrays.toString(values);
        }
    }
    //</editor-fold>
}
