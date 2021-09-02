package demetra.ui.components.parts;

import demetra.desktop.design.SwingProperty;

/**
 *
 * @author Philippe Charles
 */
public interface HasCrosshair {

    @SwingProperty
    String CROSSHAIR_VISIBLE_PROPERTY = "crosshairVisible";

    boolean isCrosshairVisible();

    void setCrosshairVisible(boolean crosshairVisible);
}
