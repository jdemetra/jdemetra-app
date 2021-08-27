package demetra.ui.components.parts;

import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import demetra.ui.TsManager;
import demetra.ui.beans.PropertyChangeBroadcaster;
import demetra.ui.datatransfer.DataTransfer;
import internal.ui.components.parts.HasTsImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.util.Optional;

public class HasTsSupport {

    @NonNull
    public static HasTs of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasTsImpl(broadcaster).register(TsManager.getDefault());
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
}
