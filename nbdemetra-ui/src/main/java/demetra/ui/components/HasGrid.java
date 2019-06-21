package demetra.ui.components;

import demetra.ui.beans.PropertyChangeSource;
import internal.ui.components.HasGridImpl;
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
    static HasGrid of(PropertyChangeSource.@NonNull Broadcaster broadcaster) {
        return new HasGridImpl(broadcaster);
    }
}
