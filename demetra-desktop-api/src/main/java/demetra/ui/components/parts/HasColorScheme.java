package demetra.ui.components.parts;

import ec.util.chart.ColorScheme;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Philippe Charles
 */
public interface HasColorScheme {

    String COLOR_SCHEME_PROPERTY = "colorScheme";

    @Nullable
    ColorScheme getColorScheme();

    void setColorScheme(@Nullable ColorScheme colorScheme);

    default boolean hasColorScheme() {
        return getColorScheme() != null;
    }
}
