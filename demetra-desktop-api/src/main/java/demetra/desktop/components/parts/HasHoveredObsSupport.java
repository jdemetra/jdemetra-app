package demetra.desktop.components.parts;

import demetra.desktop.beans.PropertyChangeBroadcaster;
import ec.util.chart.ObsIndex;
import org.checkerframework.checker.nullness.qual.NonNull;

@lombok.experimental.UtilityClass
public class HasHoveredObsSupport {

    @NonNull
    public static HasHoveredObs of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasHoveredObsImpl(broadcaster);
    }

    /**
     * @author Philippe Charles
     */
    @lombok.RequiredArgsConstructor
    private static final class HasHoveredObsImpl implements HasHoveredObs {

        @lombok.NonNull
        private final PropertyChangeBroadcaster broadcaster;

        private static final ObsIndex DEFAULT_HOVERED_OBS = ObsIndex.NULL;
        private ObsIndex hoveredObs = DEFAULT_HOVERED_OBS;

        @Override
        public ObsIndex getHoveredObs() {
            return hoveredObs;
        }

        @Override
        public void setHoveredObs(ObsIndex hoveredObs) {
            ObsIndex old = this.hoveredObs;
            this.hoveredObs = hoveredObs != null ? hoveredObs : DEFAULT_HOVERED_OBS;
            broadcaster.firePropertyChange(HOVERED_OBS_PROPERTY, old, this.hoveredObs);
        }
    }
}
