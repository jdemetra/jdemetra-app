package ec.ui.interfaces;

import ec.nbdemetra.ui.awt.IPropertyChangeSource;

public interface ITsChart extends ITsCollectionView, IColorSchemeAble, IPropertyChangeSource {

    public static final String LEGEND_VISIBLE_PROPERTY = "legendVisible";
    public static final String TITLE_VISIBLE_PROPERTY = "titleVisible";
    public static final String AXIS_VISIBLE_PROPERTY = "axisVisible";
    public static final String TITLE_PROPERTY = "title";
    public static final String LINES_THICKNESS_PROPERTY = "linesThickness";

    void setLegendVisible(boolean legendVisible);

    boolean isLegendVisible();

    void setTitleVisible(boolean titleVisible);

    boolean isTitleVisible();

    void setAxisVisible(boolean axisVisible);

    boolean isAxisVisible();

    void setTitle(String title);

    String getTitle();

    void showAll();

    LinesThickness getLinesThickness();

    void setLinesThickness(LinesThickness linesThickness);

    public enum LinesThickness {

        Thin, Thick
    }
}
