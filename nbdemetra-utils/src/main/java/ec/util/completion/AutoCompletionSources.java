package ec.util.completion;

import java.text.Normalizer;
import java.util.Arrays;

public final class AutoCompletionSources {

    private AutoCompletionSources() {
        // static class
    }
    private static final AutoCompletionSource NONE = of(false);

    public static AutoCompletionSource empty() {
        return NONE;
    }

    public static <T> AutoCompletionSource of(boolean strict, T... list) {
        return new AutoCompletionSources.DefaultAutoCompletion(strict, Arrays.asList(list));
    }

    public static <T> AutoCompletionSource of(boolean strict, Iterable<T> list) {
        return new AutoCompletionSources.DefaultAutoCompletion(strict, list);
    }

    /**
     * @see
     * http://www.drillio.com/en/software-development/java/removing-accents-diacritics-in-any-language/
     * @param input
     * @return
     */
    public static String removeDiacritics(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Normalize a string by removing its diacritics and converting the result
     * to lowercase.
     *
     * @param input
     * @return
     */
    public static String normalize(String input) {
        return removeDiacritics(input).toLowerCase();
    }

    private static final class DefaultAutoCompletion<T> extends AbstractAutoCompletionSource<T> {

        final boolean strict;
        final Iterable<T> availableValues;

        DefaultAutoCompletion(boolean strict, Iterable<T> availableValues) {
            this.strict = strict;
            this.availableValues = availableValues;
        }

        @Override
        protected Iterable<T> getAllValues() throws Exception {
            return availableValues;
        }

        @Override
        protected String getNormalizedString(String input) {
            return strict ? input : normalize(input);
        }
    }
}