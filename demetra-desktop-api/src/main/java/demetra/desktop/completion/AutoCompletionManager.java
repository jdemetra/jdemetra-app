package demetra.desktop.completion;

import demetra.desktop.design.GlobalService;
import demetra.desktop.util.CollectionSupplier;
import demetra.desktop.util.LazyGlobalService;
import ec.util.completion.swing.JAutoCompletion;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.text.JTextComponent;

/**
 *
 */
@GlobalService
public final class AutoCompletionManager {

    @NonNull
    public static AutoCompletionManager get() {
        return LazyGlobalService.get(AutoCompletionManager.class, AutoCompletionManager::new);
    }

    private AutoCompletionManager() {
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
