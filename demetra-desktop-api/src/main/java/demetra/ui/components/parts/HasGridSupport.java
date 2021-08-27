package demetra.ui.components.parts;

import demetra.ui.DemetraOptions;
import demetra.ui.beans.PropertyChangeBroadcaster;
import demetra.ui.components.ComponentCommand;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.awt.*;

import static demetra.ui.components.parts.HasGrid.CROSSHAIR_VISIBLE_PROPERTY;
import static demetra.ui.components.parts.HasGrid.ZOOM_PROPERTY;

/**
 *
 */
@lombok.experimental.UtilityClass
public class HasGridSupport {

    @NonNull
    public static HasGrid of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasGridImpl(broadcaster);
    }

    public static <C extends JComponent & HasGrid> JMenu newZoomRatioMenu(C component) {
        JMenu zoom = new JMenu("Zoom");
        final JSlider slider = new JSlider(25, 200, 100);
        {
            slider.setPreferredSize(new Dimension(50, slider.getPreferredSize().height));
            slider.addChangeListener(event -> component.setZoomRatio(slider.getValue()));
            component.addPropertyChangeListener(HasGrid.ZOOM_PROPERTY, evt -> slider.setValue(component.getZoomRatio()));
        }
        zoom.add(slider);
        for (int o : new int[]{200, 100, 75, 50, 25}) {
            zoom.add(new JCheckBoxMenuItem((new ZoomRatioCommand(o).toAction(component)))).setText(o + "%");
        }
        return zoom;
    }

    public static <C extends JComponent & HasGrid> JMenuItem newToggleCrosshairVisibilityMenu(C component) {
        JCommand<HasGrid>.ActionAdapter action = ToggleCrosshairVisibilityCommand.INSTANCE.toAction(component);
        JCheckBoxMenuItem result = new JCheckBoxMenuItem(action);
        result.setText("Show crosshair");
        result.setIcon(DemetraOptions.getDefault().getPopupMenuIcon(FontAwesome.FA_CROSSHAIRS));
        return result;
    }

    private static final class ZoomRatioCommand extends ComponentCommand<HasGrid> {

        private final int zoomRatio;

        public ZoomRatioCommand(int zoomRatio) {
            super(ZOOM_PROPERTY);
            this.zoomRatio = zoomRatio;
        }

        @Override
        public boolean isSelected(HasGrid component) {
            return zoomRatio == component.getZoomRatio();
        }

        @Override
        public void execute(HasGrid component) throws Exception {
            component.setZoomRatio(zoomRatio);
        }
    }

    private static final class ToggleCrosshairVisibilityCommand extends JCommand<HasGrid> {

        public static final ToggleCrosshairVisibilityCommand INSTANCE = new ToggleCrosshairVisibilityCommand();

        @Override
        public void execute(HasGrid component) throws Exception {
            component.setCrosshairVisible(!component.isCrosshairVisible());
        }

        @Override
        public boolean isSelected(HasGrid component) {
            return component.isCrosshairVisible();
        }

        @Override
        public JCommand.ActionAdapter toAction(HasGrid c) {
            JCommand.ActionAdapter result = super.toAction(c);
            return c instanceof Component ? result.withWeakPropertyChangeListener((Component) c, CROSSHAIR_VISIBLE_PROPERTY) : result;
        }
    }

    @lombok.RequiredArgsConstructor
    private static final class HasGridImpl implements HasGrid {

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
}
