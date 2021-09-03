package demetra.desktop.components.parts;

import demetra.desktop.design.SwingProperty;

/**
 *
 * @author Philippe Charles
 */
public interface HasZoomRatio {

    @SwingProperty
    String ZOOM_RATIO_PROPERTY = "zoomRatio";

    int getZoomRatio();

    void setZoomRatio(int zoomRatio);
}
