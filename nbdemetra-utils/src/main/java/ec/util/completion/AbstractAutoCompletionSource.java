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
package ec.util.completion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * An implementation of AutoCompletionSource that allows to quickly construct a
 * source by overriding a few methods.
 *
 * @author Philippe Charles
 * @param <T>
 */
public abstract class AbstractAutoCompletionSource<T> extends ExtAutoCompletionSource implements Comparator<T> {

    //<editor-fold defaultstate="collapsed" desc="AutoCompletionSource">
    @Override
    public final String toString(Object value) {
        return getValueAsString((T) value);
    }

    @Override
    public Behavior getBehavior(String term) {
        return Behavior.SYNC;
    }

    @Override
    public List<?> getValues(String term) throws Exception {
        return getValues(term, getAllValues());
    }

    @Override
    public Request getRequest(String term) {
        return wrap(this, term);
    }
    //</editor-fold>

    /**
     * Returns a view on all possible values. This view will be filtered and
     * sorted later on.
     *
     * @return
     * @throws Exception
     */
    @Nonnull
    abstract protected Iterable<T> getAllValues() throws Exception;

    /**
     * Format a value as a string.<br>Default behavior uses
     * {@link Object#toString()}.
     *
     * @param value the value to be formatted
     * @return
     */
    @Nonnull
    protected String getValueAsString(@Nonnull T value) {
        return value.toString();
    }

    /**
     * Returns a normalized string used by filtering criteria.<br>Default
     * behavior uses {@link AutoCompletionSources#normalize(java.lang.String)}.
     *
     * @param input the string to be normalized
     * @return a normalized string
     */
    @Nonnull
    protected String getNormalizedString(@Nonnull String input) {
        return AutoCompletionSources.normalize(input);
    }

    /**
     * Checks if a normalized input matches a normalized term.<br>Default
     * behavior uses {@link String#contains(java.lang.CharSequence)}.
     *
     * @param normalizedTerm
     * @param normalizedInput
     * @return true if the input matches the term
     */
    protected boolean matches(@Nonnull String normalizedTerm, @Nonnull String normalizedInput) {
        return normalizedInput.contains(normalizedTerm);
    }

    /**
     * Checks if an input matches a term matcher.<br>Default behavior uses
     * {@link #getValueAsString(java.lang.Object)}.
     *
     * @param termMatcher
     * @param input
     * @return true if the input matches the term matcher
     */
    protected boolean matches(@Nonnull TermMatcher termMatcher, @Nonnull T input) {
        return termMatcher.matches(getValueAsString(input));
    }

    /**
     * Returns the size used to limit the number of values provided by this
     * source.<br>Default behavior uses {@link Integer#MAX_VALUE}.
     *
     * @return
     */
    @Nonnegative
    protected int getLimitSize() {
        return Integer.MAX_VALUE;
    }

    /**
     * Compares two values in order to sort them.<br>Default behavior uses
     * {@link #getValueAsString(java.lang.Object)}.
     *
     * @param left
     * @param right
     * @return
     */
    @Override
    public int compare(T left, T right) {
        return getValueAsString(left).compareTo(getValueAsString(right));
    }

    @Nonnull
    protected List<?> getValues(@Nonnull String term, @Nonnull Iterable<T> allValues) {
        TermMatcher termFilter = createTermMatcher(term);
        List<T> result = new ArrayList<>();
        for (T value : allValues) {
            if (matches(termFilter, value)) {
                result.add(value);
                if (result.size() >= getLimitSize()) {
                    break;
                }
            }
        }
        Collections.sort(result, this);
        return result;
    }

    @Nonnull
    protected Request createCachedRequest(@Nonnull final String term, @Nonnull final Iterable<T> allValues) {
        return new Request() {
            @Override
            public String getTerm() {
                return term;
            }

            @Override
            public Behavior getBehavior() {
                return Behavior.SYNC;
            }

            @Override
            public List<?> call() throws Exception {
                return getValues(term, allValues);
            }
        };
    }

    @Nonnull
    protected TermMatcher createTermMatcher(@Nonnull final String term) {
        return new TermMatcher() {
            final String normalizedTerm = getNormalizedString(term);

            @Override
            public boolean matches(String input) {
                return input != null && AbstractAutoCompletionSource.this.matches(normalizedTerm, getNormalizedString(input));
            }
        };
    }

    public interface TermMatcher {

        boolean matches(@Nonnull String input);
    }
}
