package demetra.ui.components.parts;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface HasChart {

    String LEGEND_VISIBLE_PROPERTY = "legendVisible";

    void setLegendVisible(boolean legendVisible);

    boolean isLegendVisible();

    String TITLE_VISIBLE_PROPERTY = "titleVisible";

    void setTitleVisible(boolean titleVisible);

    boolean isTitleVisible();

    String AXIS_VISIBLE_PROPERTY = "axisVisible";

    void setAxisVisible(boolean axisVisible);

    boolean isAxisVisible();

    String TITLE_PROPERTY = "title";

    void setTitle(@Nullable String title);

    @Nullable
    String getTitle();

    String LINES_THICKNESS_PROPERTY = "linesThickness";

    @NonNull
    LinesThickness getLinesThickness();

    void setLinesThickness(@Nullable LinesThickness linesThickness);

    enum LinesThickness {

        Thin, Thick
    }
}
