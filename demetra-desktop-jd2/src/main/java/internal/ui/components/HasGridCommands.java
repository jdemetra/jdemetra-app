package internal.ui.components;

import demetra.ui.components.ComponentCommand;
import demetra.ui.components.parts.HasGrid;
import static demetra.ui.components.parts.HasGrid.CROSSHAIR_VISIBLE_PROPERTY;
import static demetra.ui.components.parts.HasGrid.ZOOM_PROPERTY;
import ec.util.various.swing.JCommand;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JSlider;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 */
@lombok.experimental.UtilityClass
public class HasGridCommands {

    @NonNull
    public static JCommand<HasGrid> applyZoomRatio(int zoomRatio) {
        return new ZoomRatioCommand(zoomRatio);
    }

    public static JMenu newZoomRationMenu(HasGrid view) {
        JMenu zoom = new JMenu("Zoom");
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
