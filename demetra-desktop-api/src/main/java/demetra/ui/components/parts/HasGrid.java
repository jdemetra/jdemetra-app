package demetra.ui.components.parts;

import demetra.ui.beans.PropertyChangeBroadcaster;
import internal.ui.components.parts.HasGridImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

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

    @NonNull
    static HasGrid of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasGridImpl(broadcaster);
    }
}
