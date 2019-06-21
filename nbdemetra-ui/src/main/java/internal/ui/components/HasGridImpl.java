package internal.ui.components;

import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.components.HasGrid;
import static demetra.ui.components.HasGrid.CROSSHAIR_VISIBLE_PROPERTY;
import static demetra.ui.components.HasGrid.ZOOM_PROPERTY;
import ec.nbdemetra.ui.DemetraUI;
import ec.ui.commands.ComponentCommand;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import java.awt.Component;
import java.awt.Dimension;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JSlider;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasGridImpl implements HasGrid {

    private static final int DEFAULT_ZOOM_RATIO = 100;
    private static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;

    @lombok.NonNull
    private final PropertyChangeSource.Broadcaster broadcaster;
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

    @NonNull
    public static JCommand<HasGrid> applyZoomRatio(int zoomRatio) {
        return new ZoomRatioCommand(zoomRatio);
    }

    public static JMenu newZoomRationMenu(HasGrid view, DemetraUI demetraUI) {
        JMenu zoom = new JMenu("Zoom");
        zoom.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_SEARCH));
        final JSlider slider = new JSlider(25, 200, 100);
        {
            slider.setPreferredSize(new Dimension(50, slider.getPreferredSize().height));
            slider.addChangeListener(event -> view.setZoomRatio(slider.getValue()));
            ((JComponent) view).addPropertyChangeListener(HasGrid.ZOOM_PROPERTY, evt -> slider.setValue(view.getZoomRatio()));
        }
        zoom.add(slider);
        for (int o : new int[]{200, 100, 75, 50, 25}) {
            zoom.add(new JCheckBoxMenuItem(applyZoomRatio(o).toAction(view))).setText(o + "%");
        }
        return zoom;
    }

    @NonNull
    public static JCommand<HasGrid> toggleCrosshairVisibility() {
        return ToggleCrosshairVisibilityCommand.INSTANCE;
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
}
