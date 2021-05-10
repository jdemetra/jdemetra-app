package demetra.ui.components.parts;

import demetra.ui.beans.PropertyChangeBroadcaster;
import internal.ui.components.parts.HasChartImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface HasChart {

    static final String LEGEND_VISIBLE_PROPERTY = "legendVisible";

    void setLegendVisible(boolean legendVisible);

    boolean isLegendVisible();

    static final String TITLE_VISIBLE_PROPERTY = "titleVisible";

    void setTitleVisible(boolean titleVisible);

    boolean isTitleVisible();

    static final String AXIS_VISIBLE_PROPERTY = "axisVisible";

    void setAxisVisible(boolean axisVisible);

    boolean isAxisVisible();

    static final String TITLE_PROPERTY = "title";

    void setTitle(@Nullable String title);

    @Nullable
    String getTitle();

    static final String LINES_THICKNESS_PROPERTY = "linesThickness";

    @NonNull
    LinesThickness getLinesThickness();

    void setLinesThickness(@Nullable LinesThickness linesThickness);

    public enum LinesThickness {

        Thin, Thick
    }

    @NonNull
    static HasChart of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasChartImpl(broadcaster);
    }
}
