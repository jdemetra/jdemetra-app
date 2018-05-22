package demetra.ui.components;

import demetra.ui.beans.PropertyChangeSource;
import internal.ui.components.HasGridImpl;
import javax.annotation.Nonnull;

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

    @Nonnull
    static HasGrid of(@Nonnull PropertyChangeSource.Broadcaster broadcaster) {
        return new HasGridImpl(broadcaster);
    }
}
