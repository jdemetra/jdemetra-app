package demetra.ui.components.parts;

import demetra.ui.beans.PropertyChangeBroadcaster;
import ec.util.chart.ColorScheme;
import internal.ui.components.parts.HasColorSchemeImpl;
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
    static HasColorScheme of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasColorSchemeImpl(broadcaster);
    }
}
