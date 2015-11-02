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

import java.text.Normalizer;
import java.util.Arrays;
import javax.annotation.Nonnull;

public final class AutoCompletionSources {

    private AutoCompletionSources() {
        // static class
    }

    private static final AutoCompletionSource NONE = of(false);

    @Nonnull
    public static AutoCompletionSource empty() {
        return NONE;
    }

    @Nonnull
    public static <T> AutoCompletionSource of(boolean strict, @Nonnull T... list) {
        return new DefaultAutoCompletion(strict, Arrays.asList(list));
    }

    @Nonnull
    public static <T> AutoCompletionSource of(boolean strict, @Nonnull Iterable<T> list) {
        return new DefaultAutoCompletion(strict, list);
    }

    /**
     * @see
     * http://www.drillio.com/en/software-development/java/removing-accents-diacritics-in-any-language/
     * @param input
     * @return
     */
    @Nonnull
    public static String removeDiacritics(@Nonnull String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * Normalize a string by removing its diacritics and converting the result
     * to lowercase.
     *
     * @param input
     * @return
     */
    @Nonnull
    public static String normalize(@Nonnull String input) {
        return removeDiacritics(input).toLowerCase();
    }

    //<editor-fold defaultstate="collapsed" desc="Internal implementation">
    private static final class DefaultAutoCompletion<T> extends AbstractAutoCompletionSource<T> {
        
        private final boolean strict;
        private final Iterable<T> availableValues;
        
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
    //</editor-fold>
}
