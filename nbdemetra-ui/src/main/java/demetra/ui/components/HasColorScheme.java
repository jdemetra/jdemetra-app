package demetra.ui.components;

import demetra.ui.beans.PropertyChangeSource;
import ec.util.chart.ColorScheme;
import internal.ui.components.HasColorSchemeImpl;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author Philippe Charles
 */
public interface HasColorScheme {

    static final String COLOR_SCHEME_PROPERTY = "colorScheme";

    void setColorScheme(@Nullable ColorScheme colorScheme);

    @Nullable
    ColorScheme getColorScheme();

    @Nonnull
    static HasColorScheme of(@Nonnull PropertyChangeSource.Broadcaster broadcaster) {
        return new HasColorSchemeImpl(broadcaster);
    }
}
