package demetra.ui.components.parts;

import demetra.ui.design.SwingProperty;

/**
 *
 * @author Philippe Charles
 */
public interface HasGrid {

    @SwingProperty
    String ZOOM_PROPERTY = "zoom";

    int getZoomRatio();

    void setZoomRatio(int zoomRatio);

    @SwingProperty
    String CROSSHAIR_VISIBLE_PROPERTY = "crosshairVisible";

    boolean isCrosshairVisible();

    void setCrosshairVisible(boolean crosshairVisible);
}
