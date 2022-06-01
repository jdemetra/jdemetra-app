package demetra.desktop.components.parts;

import demetra.desktop.TsEvent;
import demetra.desktop.TsListener;
import demetra.desktop.TsManager;
import demetra.desktop.beans.PropertyChangeBroadcaster;
import static demetra.desktop.components.parts.HasTs.TS_PROPERTY;
import demetra.desktop.datatransfer.DataTransferManager;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.util.Optional;

public class HasTsSupport {

    @NonNull
    public static HasTs of(@NonNull PropertyChangeBroadcaster broadcaster, TsInformationType info) {
        return new HasTsImpl(broadcaster, info).register(TsManager.get());
    }

    @NonNull
    public static HasTs of(@NonNull HasTsCollection collection) {
        return new HasSingleTs(collection);
    }

    public static TransferHandler newTransferHandler(HasTs delegate) {
        return new HasTsTransferHandler(delegate, DataTransferManager.get());
    }

    /**
     * @author Philippe Charles
     */
    @lombok.RequiredArgsConstructor
    private static final class HasTsTransferHandler extends TransferHandler {

        @lombok.NonNull
        private final HasTs delegate;

        @lombok.NonNull
        private final DataTransferManager dataTransfer;

        @Override
        public boolean canImport(TransferSupport support) {
            return dataTransfer.canImport(support.getDataFlavors());
        }

        @Override
        public boolean importData(TransferSupport support) {
            Optional<Ts> ts = dataTransfer.toTs(support.getTransferable());
            if (ts.isPresent()) {
                delegate.setTs(ts.get());
//                TsManager.get().loadAsync(ts.get(), TsInformationType.All, delegate::updateTs);
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
        
        @lombok.NonNull
        private final TsInformationType info;
      

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
            if (info != TsInformationType.None){
                if (ts != null && ! ts.getType().encompass(info)){
                    TsManager.get().loadAsync(ts, info, this::setTs);
                    // The broadcast is fired when the requested information is available
                    return;
                }
            }
            broadcaster.firePropertyChange(TS_PROPERTY, old, this.ts);
        }

        @Override
        public void tsUpdated(TsEvent event) {
            if (hasTs() && event.getRelated().test(ts.getMoniker())) {
                setTs(event.getSource().makeTs(ts.getMoniker(), ts.getType()).withName(ts.getName()));
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
