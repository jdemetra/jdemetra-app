package demetra.ui.components.parts;

import demetra.ui.design.SwingProperty;
import ec.util.chart.ColorScheme;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Philippe Charles
 */
public interface HasColorScheme {

    @SwingProperty
    String COLOR_SCHEME_PROPERTY = "colorScheme";

    @Nullable
    ColorScheme getColorScheme();

    void setColorScheme(@Nullable ColorScheme colorScheme);

    default boolean hasColorScheme() {
        return getColorScheme() != null;
    }

    String APPLY_MAIN_COLOR_SCHEME_ACTION = "applyMainColorScheme";
}
