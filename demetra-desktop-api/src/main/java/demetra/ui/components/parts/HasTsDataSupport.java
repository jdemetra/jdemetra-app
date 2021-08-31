package demetra.ui.components.parts;

import demetra.timeseries.TsData;
import demetra.ui.beans.PropertyChangeBroadcaster;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HasTsDataSupport {

    @NonNull
    public static HasTsData of(@NonNull PropertyChangeBroadcaster support) {
        return new HasTsDataImpl(support);
    }

    /**
     * @author Philippe Charles
     */
    @lombok.RequiredArgsConstructor
    private static final class HasTsDataImpl implements HasTsData {

        @lombok.NonNull
        private final PropertyChangeBroadcaster broadcaster;

        private TsData tsData = null;

        @Override
        public TsData getTsData() {
            return tsData;
        }

        @Override
        public void setTsData(TsData tsData) {
            TsData old = this.tsData;
            this.tsData = tsData;
            broadcaster.firePropertyChange(TS_DATA_PROPERTY, old, this.tsData);
        }
    }
}
