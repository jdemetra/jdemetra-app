package demetra.desktop.components.parts;

import demetra.desktop.design.SwingAction;
import demetra.desktop.design.SwingProperty;
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
        return false;
        //return getColorScheme() != null;
    }

    @SwingAction
    String APPLY_MAIN_COLOR_SCHEME_ACTION = "applyMainColorScheme";
}
