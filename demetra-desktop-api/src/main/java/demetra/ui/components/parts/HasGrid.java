package demetra.ui.components.parts;

import demetra.desktop.design.SwingProperty;

/**
 *
 * @author Philippe Charles
 */
public interface HasGrid {

    @SwingProperty
    String ZOOM_RATIO_PROPERTY = "zoomRatio";

    int getZoomRatio();

    void setZoomRatio(int zoomRatio);

    @SwingProperty
    String CROSSHAIR_VISIBLE_PROPERTY = "crosshairVisible";

    boolean isCrosshairVisible();

    void setCrosshairVisible(boolean crosshairVisible);
}
