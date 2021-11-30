package demetra.desktop.components.parts;

import demetra.desktop.TsEvent;
import demetra.desktop.TsListener;
import demetra.desktop.TsManager;
import demetra.desktop.beans.PropertyChangeBroadcaster;
import static demetra.desktop.components.parts.HasTs.TS_PROPERTY;
import demetra.desktop.datatransfer.DataTransfer;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.util.Optional;

public class HasTsSupport {

    @NonNull
    public static HasTs of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasTsImpl(broadcaster).register(TsManager.getDefault());
    }

    @NonNull
    public static HasTs of(@NonNull HasTsCollection collection) {
        return new HasSingleTs(collection);
    }

    public static TransferHandler newTransferHandler(HasTs delegate) {
        return new HasTsTransferHandler(delegate, DataTransfer.getDefault());
    }

    /**
     * @author Philippe Charles
     */
    @lombok.RequiredArgsConstructor
    private static final class HasTsTransferHandler extends TransferHandler {

        @lombok.NonNull
        private final HasTs delegate;

        @lombok.NonNull
        private final DataTransfer dataTransfer;

        @Override
        public boolean canImport(TransferSupport support) {
            return dataTransfer.canImport(support.getDataFlavors());
        }

        @Override
        public boolean importData(TransferSupport support) {
            Optional<Ts> ts = dataTransfer.toTs(support.getTransferable());
            if (ts.isPresent()) {
                delegate.setTs(ts.get());
                TsManager.getDefault().loadAsync(ts.get(), TsInformationType.All, delegate::replaceTs);
                return true;
            }
            return false;
        }
    }

    /**
     * @author Philippe Charles
     */
    @lombok.RequiredArgsConstructor
    private static final class HasTsImpl implements HasTs, TsListener {

        @lombok.NonNull
        private final PropertyChangeBroadcaster broadcaster;

        Ts ts = null;

        public HasTsImpl register(TsManager manager) {
            manager.addWeakListener(this);
            return this;
        }

        @Override
        public Ts getTs() {
            return ts;
        }

        @Override
        public void setTs(Ts ts) {
            Ts old = this.ts;
            this.ts = ts;
            broadcaster.firePropertyChange(TS_PROPERTY, old, this.ts);
        }

        @Override
        public void tsUpdated(TsEvent event) {
            if (hasTs() && event.getRelated().test(ts.getMoniker())) {
                setTs(event.getSource().makeTs(ts.getMoniker(), ts.getType()));
            }
        }

        private boolean hasTs() {
            return ts != null;
        }
    }

    @lombok.RequiredArgsConstructor
    private static final class HasSingleTs implements HasTs {

        @lombok.NonNull
        private final HasTsCollection coll;

        @Override
        public Ts getTs() {
            TsCollection c = coll.getTsCollection();
            return c.isEmpty() ? null : c.get(0);
        }

        @Override
        public void setTs(Ts ts) {
            if (ts == null) {
                coll.setTsCollection(TsCollection.EMPTY);
            } else {
                coll.setTsCollection(TsCollection.of(ts));
            }
        }

        private boolean hasTs() {
            return !coll.getTsCollection().isEmpty();
        }
    }

}
