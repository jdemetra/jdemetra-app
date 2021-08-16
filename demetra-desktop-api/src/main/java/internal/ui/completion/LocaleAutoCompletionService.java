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
package internal.ui.completion;

import ec.util.completion.AutoCompletionSource;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.completion.swing.CustomListCellRenderer;
import ec.util.completion.swing.JAutoCompletion;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.openide.util.lookup.ServiceProvider;
import demetra.ui.completion.AutoCompletionSpi;

/**
 *
 * @author Philippe Charles
 * @since 1.3.2
 */
@ServiceProvider(service = AutoCompletionSpi.class)
public final class LocaleAutoCompletionService implements AutoCompletionSpi {

    private final AutoCompletionSource source = localeSource();
    private final ListCellRenderer renderer = new LocaleRenderer();

    @Override
    public String getPath() {
        return Locale.class.getName();
    }
    
    @Override
    public JAutoCompletion bind(JTextComponent textComponent) {
        JAutoCompletion result = new JAutoCompletion(textComponent);
        result.setMinLength(0);
        result.setSource(source);
        result.getList().setCellRenderer(renderer);
        return result;
    }

    private static AutoCompletionSource localeSource() {
        return ExtAutoCompletionSource
                .builder(LocaleAutoCompletionService::getLocales)
                .behavior(AutoCompletionSource.Behavior.SYNC)
                .postProcessor(LocaleAutoCompletionService::getLocales)
                .build();
    }

    private static List<Locale> getLocales() {
        return Arrays.asList(Locale.getAvailableLocales());
    }

    private static List<Locale> getLocales(List<Locale> allValues, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return allValues.stream()
                .filter(o -> filter.test(o.toString()) || filter.test(o.getDisplayName()))
                .sorted(Comparator.comparing(Locale::toString))
                .collect(Collectors.toList());
    }

    private static class LocaleRenderer extends CustomListCellRenderer<Locale> {

        @Override
        protected String getValueAsString(Locale value) {
            return "(" + value.toString() + ") " + value.getDisplayName();
        }
    }
}
