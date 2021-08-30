package demetra.ui.components.parts;

import demetra.desktop.design.SwingProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface HasChart {

    void setLegendVisible(boolean legendVisible);

    boolean isLegendVisible();

    @SwingProperty
    String TITLE_VISIBLE_PROPERTY = "titleVisible";

    void setTitleVisible(boolean titleVisible);

    boolean isTitleVisible();

    @SwingProperty
    String AXIS_VISIBLE_PROPERTY = "axisVisible";

    void setAxisVisible(boolean axisVisible);

    boolean isAxisVisible();

    @SwingProperty
    String TITLE_PROPERTY = "title";

    void setTitle(@Nullable String title);

    @Nullable
    String getTitle();

    @SwingProperty
    String LINES_THICKNESS_PROPERTY = "linesThickness";

    @NonNull
    LinesThickness getLinesThickness();

    void setLinesThickness(@Nullable LinesThickness linesThickness);

    enum LinesThickness {

        Thin, Thick
    }

    @SwingProperty
    String LEGEND_VISIBLE_PROPERTY = "legendVisible";

    String TOGGLE_TITLE_VISIBILITY_ACTION = "toggleTitleVisibility";
    String TOGGLE_LEGEND_VISIBILITY_ACTION = "legendVisible";
    String APPLY_THIN_LINE_ACTION = "thinLine";
    String APPLY_THICK_LINE_ACTION = "thickLine";
}
