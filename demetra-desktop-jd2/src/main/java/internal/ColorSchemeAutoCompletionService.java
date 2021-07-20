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
package internal;

import demetra.ui.completion.JAutoCompletionService;
import ec.nbdemetra.ui.DemetraUI;
import ec.util.chart.ColorScheme;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.completion.swing.CustomListCellRenderer;
import ec.util.completion.swing.JAutoCompletion;
import internal.ui.components.HasColorSchemeCommands;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 * @since 1.3.2
 */
@ServiceProvider(service = JAutoCompletionService.class, path = JAutoCompletionService.COLOR_SCHEME_PATH)
public final class ColorSchemeAutoCompletionService implements JAutoCompletionService {

    private final AutoCompletionSource source = colorSchemeSource();
    private final ListCellRenderer renderer = new ColorSchemeRenderer();

    @Override
    public JAutoCompletion bind(JTextComponent textComponent) {
        JAutoCompletion result = new JAutoCompletion(textComponent);
        result.setMinLength(0);
        result.setSeparator(" ");
        result.setSource(source);
        result.getList().setCellRenderer(renderer);
        return result;
    }

    private static AutoCompletionSource colorSchemeSource() {
        return ExtAutoCompletionSource
                .builder(ColorSchemeAutoCompletionService::getColorSchemes)
                .behavior(AutoCompletionSource.Behavior.SYNC)
                .valueToString(ColorScheme::getName)
                .build();
    }

    private static List<ColorScheme> getColorSchemes(String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return DemetraUI.getDefault().getColorSchemes().stream()
                .filter(o -> filter.test(o.getName()) || filter.test(o.getDisplayName()))
                .sorted(Comparator.comparing(ColorScheme::getDisplayName))
                .collect(Collectors.toList());
    }

    private static class ColorSchemeRenderer extends CustomListCellRenderer<ColorScheme> {

        @Override
        protected String getValueAsString(ColorScheme value) {
            return value.getDisplayName();
        }

        @Override
        protected Icon toIcon(String term, JList list, ColorScheme value, int index, boolean isSelected, boolean cellHasFocus) {
            return HasColorSchemeCommands.iconOf(value);
        }
    }
}
