package demetra.ui.components.parts;

import demetra.ui.IconManager;
import demetra.ui.beans.PropertyChangeBroadcaster;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.awt.*;

import static demetra.ui.components.parts.HasCrosshair.CROSSHAIR_VISIBLE_PROPERTY;

/**
 *
 */
@lombok.experimental.UtilityClass
public class HasCrosshairSupport {

    @NonNull
    public static HasCrosshair of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasCrosshairImpl(broadcaster);
    }

    public static <C extends JComponent & HasCrosshair> JMenuItem newToggleCrosshairVisibilityMenu(C component) {
        JCommand<HasCrosshair>.ActionAdapter action = ToggleCrosshairVisibilityCommand.INSTANCE.toAction(component);
        JCheckBoxMenuItem result = new JCheckBoxMenuItem(action);
        result.setText("Show crosshair");
        result.setIcon(IconManager.getDefault().getPopupMenuIcon(FontAwesome.FA_CROSSHAIRS));
        return result;
    }

    private static final class ToggleCrosshairVisibilityCommand extends JCommand<HasCrosshair> {

        public static final ToggleCrosshairVisibilityCommand INSTANCE = new ToggleCrosshairVisibilityCommand();

        @Override
        public void execute(HasCrosshair component) throws Exception {
            component.setCrosshairVisible(!component.isCrosshairVisible());
        }

        @Override
        public boolean isSelected(HasCrosshair component) {
            return component.isCrosshairVisible();
        }

        @Override
        public JCommand.ActionAdapter toAction(HasCrosshair c) {
            JCommand.ActionAdapter result = super.toAction(c);
            return c instanceof Component ? result.withWeakPropertyChangeListener((Component) c, CROSSHAIR_VISIBLE_PROPERTY) : result;
        }
    }

    @lombok.RequiredArgsConstructor
    private static final class HasCrosshairImpl implements HasCrosshair {

        @lombok.NonNull
        private final PropertyChangeBroadcaster broadcaster;

        private static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;
        private boolean crosshairVisible = DEFAULT_CROSSHAIR_VISIBLE;

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
