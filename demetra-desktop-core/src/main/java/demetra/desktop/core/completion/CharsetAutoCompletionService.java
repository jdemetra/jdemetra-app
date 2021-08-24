package demetra.desktop.core.completion;

import demetra.util.List2;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.completion.swing.CustomListCellRenderer;
import ec.util.completion.swing.JAutoCompletion;
import java.nio.charset.Charset;
import java.util.List;
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
public final class CharsetAutoCompletionService implements AutoCompletionSpi {

    private final AutoCompletionSource source = charsetSource();
    private final ListCellRenderer renderer = new CharsetRenderer();

    @Override
    public String getPath() {
        return Charset.class.getName();
    }
    
    @Override
    public JAutoCompletion bind(JTextComponent textComponent) {
        JAutoCompletion result = new JAutoCompletion(textComponent);
        result.setMinLength(0);
        result.setSource(source);
        result.getList().setCellRenderer(renderer);
        return result;
    }

    private static AutoCompletionSource charsetSource() {
        return ExtAutoCompletionSource
                .builder(CharsetAutoCompletionService::getCharsets)
                .behavior(AutoCompletionSource.Behavior.SYNC)
                .postProcessor(CharsetAutoCompletionService::getCharsets)
                .build();
    }

    private static List<Charset> getCharsets() {
        return List2.copyOf(Charset.availableCharsets().values());
    }

    private static List<Charset> getCharsets(List<Charset> allValues, String term) {
        Predicate<String> filter = ExtAutoCompletionSource.basicFilter(term);
        return allValues.stream()
                .filter(o -> filter.test(o.name()))
                .sorted()
                .collect(Collectors.toList());
    }

    private static final class CharsetRenderer extends CustomListCellRenderer<Charset> {

        @Override
        protected String getValueAsString(Charset value) {
            return value.name();
        }
    }
}
