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
public final class AutoCompletion {

    @NonNull
    public static AutoCompletion getDefault() {
        return LazyGlobalService.get(AutoCompletion.class, AutoCompletion::new);
    }

    private AutoCompletion() {
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
