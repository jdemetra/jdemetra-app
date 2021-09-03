package demetra.desktop.components.parts;

import demetra.desktop.beans.PropertyChangeBroadcaster;
import org.checkerframework.checker.nullness.qual.NonNull;

@lombok.experimental.UtilityClass
public class HasTsActionSupport {

    @NonNull
    public static HasTsAction of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasTsActionImpl(broadcaster);
    }

    /**
     * @author Philippe Charles
     */
    @lombok.RequiredArgsConstructor
    private static final class HasTsActionImpl implements HasTsAction {

        @lombok.NonNull
        private final PropertyChangeBroadcaster broadcaster;
        private String tsAction = null;

        @Override
        public void setTsAction(String tsAction) {
            String old = this.tsAction;
            this.tsAction = tsAction;
            broadcaster.firePropertyChange(TS_ACTION_PROPERTY, old, this.tsAction);
        }

        @Override
        public String getTsAction() {
            return tsAction;
        }
    }
}
