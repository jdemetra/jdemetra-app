package demetra.ui.components;

import demetra.ui.beans.PropertyChangeSource;
import ec.util.chart.ColorScheme;
import internal.ui.components.HasColorSchemeImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Philippe Charles
 */
public interface HasColorScheme {

    static final String COLOR_SCHEME_PROPERTY = "colorScheme";

    void setColorScheme(@Nullable ColorScheme colorScheme);

    @Nullable
    ColorScheme getColorScheme();

    @NonNull
    static HasColorScheme of(PropertyChangeSource.@NonNull Broadcaster broadcaster) {
        return new HasColorSchemeImpl(broadcaster);
    }
}
