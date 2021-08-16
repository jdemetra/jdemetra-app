package demetra.ui.completion;

import demetra.ui.GlobalService;
import ec.util.completion.swing.JAutoCompletion;
import internal.ui.Providers;
import javax.swing.text.JTextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 */
@GlobalService
@ServiceProvider(service = AutoCompletion.class)
public class AutoCompletion {

    public static AutoCompletion getDefault() {
        return Lookup.getDefault().lookup(AutoCompletion.class);
    }
    
    private final Providers<AutoCompletionSpi> providers = new AutoCompletionSpiLoader()::get;

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
