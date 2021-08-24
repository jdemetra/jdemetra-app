package demetra.desktop.core.completion;

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
import demetra.ui.completion.AutoCompletionSpi;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Philippe Charles
 * @since 1.3.2
 */
@DirectImpl
@ServiceProvider
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
