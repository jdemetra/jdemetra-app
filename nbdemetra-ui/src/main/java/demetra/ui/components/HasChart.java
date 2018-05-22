package demetra.ui.components;

import demetra.ui.beans.PropertyChangeSource;
import internal.ui.components.HasChartImpl;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @Nonnull
    LinesThickness getLinesThickness();

    void setLinesThickness(@Nullable LinesThickness linesThickness);

    public enum LinesThickness {

        Thin, Thick
    }

    @Nonnull
    static HasChart of(@Nonnull PropertyChangeSource.Broadcaster broadcaster) {
        return new HasChartImpl(broadcaster);
    }
}
