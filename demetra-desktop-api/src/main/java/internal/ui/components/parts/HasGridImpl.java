package internal.ui.components.parts;

import demetra.ui.beans.PropertyChangeBroadcaster;
import demetra.ui.components.parts.HasGrid;
import static demetra.ui.components.parts.HasGrid.CROSSHAIR_VISIBLE_PROPERTY;
import static demetra.ui.components.parts.HasGrid.ZOOM_PROPERTY;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasGridImpl implements HasGrid {

    private static final int DEFAULT_ZOOM_RATIO = 100;
    private static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;

    @lombok.NonNull
    private final PropertyChangeBroadcaster broadcaster;
    private int zoomRatio = DEFAULT_ZOOM_RATIO;
    private boolean crosshairVisible = DEFAULT_CROSSHAIR_VISIBLE;

    @Override
    public int getZoomRatio() {
        return zoomRatio;
    }

    @Override
    public void setZoomRatio(int zoomRatio) {
        int old = this.zoomRatio;
        this.zoomRatio = zoomRatio >= 10 && zoomRatio <= 200 ? zoomRatio : DEFAULT_ZOOM_RATIO;
        broadcaster.firePropertyChange(ZOOM_PROPERTY, old, this.zoomRatio);
    }

    @Override
    public boolean isCrosshairVisible() {
        return crosshairVisible;
    }

    @Override
    public void setCrosshairVisible(boolean crosshairVisible) {
        boolean old = this.crosshairVisible;
        this.crosshairVisible = crosshairVisible;
        broadcaster.firePropertyChange(CROSSHAIR_VISIBLE_PROPERTY, old, this.crosshairVisible);
    }
}
