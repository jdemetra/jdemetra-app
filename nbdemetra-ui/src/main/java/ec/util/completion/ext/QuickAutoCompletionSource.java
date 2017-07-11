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
package ec.util.completion.ext;

import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;
import com.google.common.collect.Streams;
import ec.util.completion.AbstractAutoCompletionSource;
import ec.util.completion.AutoCompletionSources;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * An implementation of AutoCompletionSource that allows to quickly construct a
 * source by overriding a few methods.
 *
 * @author Philippe Charles
 * @param <T>
 * @Deprecated use {@link ec.util.completion.ExtAutoCompletionSource} instead
 */
@Deprecated
public abstract class QuickAutoCompletionSource<T> extends AbstractAutoCompletionSource<T> {

    @Nonnull
    public static <X> QuickAutoCompletionSource<X> from(@Nonnull Iterable<X> list) {
        return new QuickAutoCompletionSource<X>() {
            @Override
            protected Iterable<X> getAllValues() throws Exception {
                return list;
            }
        };
    }

    @Override
    protected List<?> getValues(String term, Iterable<T> allValues) {
        return (List<?>) Streams.stream(allValues)
                .filter(getFilter(term)::apply)
                .limit(getLimitSize())
                .sorted(getSorter())
                .collect(Collectors.toList());
    }

    /**
     * Returns the master predicate used to filter values.
     *
     * @param term
     * @return a new filter
     * @see #getNormalizedString(java.lang.String)
     * @see #matches(java.lang.String, java.lang.String)
     * @see
     * #matches(ec.util.completion.AbstractAutoCompletionSource.TermMatcher,
     * java.lang.Object)
     */
    @Nonnull
    protected Predicate<T> getFilter(@Nonnull String term) {
        final TermMatcher termMatcher = createTermMatcher(term);
        return o -> matches(termMatcher, o);
    }

    /**
     * Returns the master ordering used to sort values.<br>Default behavior uses
     * {@link #compare(java.lang.Object, java.lang.Object)}.
     *
     * @return a new ordering
     */
    @Nonnull
    protected Ordering getSorter() {
        return Ordering.from(this);
    }

    /**
     * Format a value as a string.<br>Default behavior uses
     * {@link Object#toString()}.
     *
     * @param value the value to be formatted
     * @return
     * @deprecated use {@link #getValueAsString(java.lang.Object) } instead
     */
    @Deprecated
    protected String valueToString(T value) {
        return value.toString();
    }

    @Override
    protected String getValueAsString(T value) {
        return valueToString(value);
    }

    @Deprecated
    static protected Predicate<String> getLoosePredicate(String term) {
        final String tmp = AutoCompletionSources.normalize(term);
        return o -> o != null && AutoCompletionSources.normalize(o).contains(tmp);
    }
}
