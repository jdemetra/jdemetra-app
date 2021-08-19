package demetra.ui.completion;

import demetra.ui.GlobalService;
import ec.util.completion.swing.JAutoCompletion;
import javax.swing.text.JTextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import demetra.ui.util.CollectionSupplier;

/**
 *
 */
@GlobalService
public final class AutoCompletion {

    private static final AutoCompletion INSTANCE = new AutoCompletion();

    @NonNull
    public static AutoCompletion getDefault() {
        return INSTANCE;
    }

    private final CollectionSupplier<AutoCompletionSpi> providers = AutoCompletionSpiLoader::get;

    @NonNull
    public JAutoCompletion bind(@NonNull Class<?> path, @NonNull JTextComponent textComponent) {
        return bind(path.getName(), textComponent);
    }

    @NonNull
    public JAutoCompletion bind(@NonNull String path, @NonNull JTextComponent textComponent) {
        return providers
                .stream()
                .filter(spi -> spi.getPath().equals(path))
                .findFirst()
                .map(spi -> spi.bind(textComponent))
                .orElseGet(() -> new JAutoCompletion(textComponent));
    }
}
