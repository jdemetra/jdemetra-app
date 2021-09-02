package demetra.ui.components.parts;

import demetra.ui.beans.PropertyChangeBroadcaster;
import demetra.ui.components.ComponentCommand;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.awt.*;

import static demetra.ui.components.parts.HasZoomRatio.ZOOM_RATIO_PROPERTY;

@lombok.experimental.UtilityClass
public class HasZoomRatioSupport {

    @NonNull
    public static HasZoomRatio of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasZoomRatioImpl(broadcaster);
    }

    public static <C extends JComponent & HasZoomRatio> JMenu newZoomRatioMenu(C component) {
        JMenu zoom = new JMenu("Zoom");
        final JSlider slider = new JSlider(25, 200, 100);
        {
            slider.setPreferredSize(new Dimension(50, slider.getPreferredSize().height));
            slider.addChangeListener(event -> component.setZoomRatio(slider.getValue()));
            component.addPropertyChangeListener(ZOOM_RATIO_PROPERTY, evt -> slider.setValue(component.getZoomRatio()));
        }
        zoom.add(slider);
        for (int o : new int[]{200, 100, 75, 50, 25}) {
            zoom.add(new JCheckBoxMenuItem((new ZoomRatioCommand(o).toAction(component)))).setText(o + "%");
        }
        return zoom;
    }

    private static final class ZoomRatioCommand extends ComponentCommand<HasZoomRatio> {

        private final int zoomRatio;

        public ZoomRatioCommand(int zoomRatio) {
            super(ZOOM_RATIO_PROPERTY);
            this.zoomRatio = zoomRatio;
        }

        @Override
        public boolean isSelected(HasZoomRatio component) {
            return zoomRatio == component.getZoomRatio();
        }

        @Override
        public void execute(HasZoomRatio component) throws Exception {
            component.setZoomRatio(zoomRatio);
        }
    }

    @lombok.RequiredArgsConstructor
    private static final class HasZoomRatioImpl implements HasZoomRatio {

        @lombok.NonNull
        private final PropertyChangeBroadcaster broadcaster;

        private static final int DEFAULT_ZOOM_RATIO = 100;
        private int zoomRatio = DEFAULT_ZOOM_RATIO;

        @Override
        public int getZoomRatio() {
            return zoomRatio;
        }

        @Override
        public void setZoomRatio(int zoomRatio) {
            int old = this.zoomRatio;
            this.zoomRatio = zoomRatio >= 10 && zoomRatio <= 200 ? zoomRatio : DEFAULT_ZOOM_RATIO;
            broadcaster.firePropertyChange(ZOOM_RATIO_PROPERTY, old, this.zoomRatio);
        }
    }
}
