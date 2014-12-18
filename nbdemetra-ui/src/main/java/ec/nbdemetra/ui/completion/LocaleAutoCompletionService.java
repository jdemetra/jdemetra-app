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
package ec.nbdemetra.ui.completion;

import ec.util.completion.AutoCompletionSource;
import ec.util.completion.ext.QuickAutoCompletionSource;
import ec.util.completion.swing.CustomListCellRenderer;
import ec.util.completion.swing.JAutoCompletion;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 * @since 1.3.2
 */
@ServiceProvider(service = JAutoCompletionService.class, path = JAutoCompletionService.LOCALE_PATH)
public class LocaleAutoCompletionService extends JAutoCompletionService {

    private final AutoCompletionSource source = new LocaleSource();
    private final ListCellRenderer renderer = new LocaleRenderer();

    @Override
    public JAutoCompletion bind(JTextComponent textComponent) {
        JAutoCompletion result = new JAutoCompletion(textComponent);
        result.setMinLength(0);
        result.setSource(source);
        result.getList().setCellRenderer(renderer);
        return result;
    }

    private static class LocaleSource extends QuickAutoCompletionSource<Locale> {

        final List<Locale> locales = Arrays.asList(Locale.getAvailableLocales());

        @Override
        protected Iterable<Locale> getAllValues() throws Exception {
            return locales;
        }

        @Override
        protected boolean matches(TermMatcher termMatcher, Locale input) {
            return termMatcher.matches(input.toString()) || termMatcher.matches(input.getDisplayName());
        }
    }

    private static class LocaleRenderer extends CustomListCellRenderer<Locale> {

        @Override
        protected String getValueAsString(Locale value) {
            return "(" + value.toString() + ") " + value.getDisplayName();
        }
    }
}
