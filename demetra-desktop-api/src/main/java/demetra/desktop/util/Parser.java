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
package demetra.desktop.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Defines a class that creates an object from a {@link CharSequence}.<br> For
 * example, you could use it to parse a String into a Date. Note that it can
 * also be used to convert a String to a new one.<br> The parser must not throw
 * Exceptions; it must swallow it and return {@code null}. This means that
 * {@code null} is not considered has a value (same as Collection API). To
 * create a "null value" from a parser, you should use the NullObject pattern.
 *
 * @author Philippe Charles
 * @param <T> The type of the object to be created
 * @see IFormatter
 * @since 1.0.0
 */
@FunctionalInterface
public interface Parser<T> {

    /**
     * Parse a CharSequence to create an object.
     *
     * @param input the input used to create the object
     * @return a new object if possible, {@code null} otherwise
     * @throws NullPointerException if input is null
     */
    @Nullable
    T parse(@NonNull CharSequence input);

    /**
     * Returns an {@link Optional} containing the object that has bean created
     * by the parsing if this parsing was possible.<p>
     * Use this instead of {@link #parse(java.lang.CharSequence)} to increase
     * readability and prevent NullPointerExceptions.
     *
     * @param input the input used to create the object
     * @return a never-null {@link Optional}
     * @throws NullPointerException if input is null
     * @since 2.2.0
     */
    @NonNull
    default Optional<T> parseValue(@NonNull CharSequence input) {
        return Optional.ofNullable(parse(input));
    }

    /**
     *
     * @param other
     * @return
     * @since 2.2.0
     */
    @NonNull
    @SuppressWarnings("null")
    default Parser<T> orElse(@NonNull Parser<T> other) {
        Objects.requireNonNull(other);
        return o -> {
            T result = parse(o);
            return result != null ? result : other.parse(o);
        };
    }

    @NonNull
    @SuppressWarnings("null")
    default <X> Parser<X> andThen(@NonNull Function<? super T, ? extends X> after) {
        Objects.requireNonNull(after);
        return o -> {
            T tmp = parse(o);
            return tmp != null ? after.apply(tmp) : null;
        };
    }
}
