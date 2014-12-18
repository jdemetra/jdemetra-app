package ec.util.completion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * An implementation of AutoCompletionSource that allows to quickly construct a
 * source by overriding a few methods.
 *
 * @author Philippe Charles
 */
public abstract class AbstractAutoCompletionSource<T> implements AutoCompletionSource, Comparator<T> {

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
        TermMatcher termFilter = createTermMatcher(term);
        List<T> result = new ArrayList<>();
        for (T value : getAllValues()) {
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
    //</editor-fold>

    /**
     * Returns a view on all possible values. This view will be filtered and
     * sorted later on.
     *
     * @return
     * @throws Exception
     */
    abstract protected Iterable<T> getAllValues() throws Exception;

    /**
     * Format a value as a string.<br>Default behavior uses
     * {@link Object#toString()}.
     *
     * @param value the value to be formatted
     * @return
     */
    protected String getValueAsString(T value) {
        return value.toString();
    }

    /**
     * Returns a normalized string used by filtering criteria.<br>Default
     * behavior uses {@link AutoCompletionSources#normalize(java.lang.String)}.
     *
     * @param input the string to be normalized
     * @return a normalized string
     */
    protected String getNormalizedString(String input) {
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
    protected boolean matches(String normalizedTerm, String normalizedInput) {
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
    protected boolean matches(TermMatcher termMatcher, T input) {
        return termMatcher.matches(getValueAsString(input));
    }

    /**
     * Returns the size used to limit the number of values provided by this
     * source.<br>Default behavior uses {@link Integer#MAX_VALUE}.
     *
     * @return
     */
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

    protected TermMatcher createTermMatcher(final String term) {
        return new TermMatcher() {
            final String normalizedTerm = getNormalizedString(term);

            @Override
            public boolean matches(String input) {
                return input != null && AbstractAutoCompletionSource.this.matches(normalizedTerm, getNormalizedString(input));
            }
        };
    }

    public interface TermMatcher {

        boolean matches(String input);
    }
}
