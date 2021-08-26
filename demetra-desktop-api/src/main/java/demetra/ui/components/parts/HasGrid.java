package demetra.ui.components.parts;

/**
 *
 * @author Philippe Charles
 */
public interface HasGrid {

    String ZOOM_PROPERTY = "zoom";

    int getZoomRatio();

    void setZoomRatio(int zoomRatio);

    String CROSSHAIR_VISIBLE_PROPERTY = "crosshairVisible";

    boolean isCrosshairVisible();

    void setCrosshairVisible(boolean crosshairVisible);
}
