package demetra.ui.components.parts;

/**
 *
 * @author Philippe Charles
 */
public interface HasGrid {

    static final String ZOOM_PROPERTY = "zoom";

    int getZoomRatio();

    void setZoomRatio(int zoomRatio);

    static final String CROSSHAIR_VISIBLE_PROPERTY = "crosshairVisible";

    boolean isCrosshairVisible();

    void setCrosshairVisible(boolean crosshairVisible);
}
