/*
 * Copyright 2018 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package internal.ui.components;

import demetra.ui.TsManager;
import demetra.ui.components.parts.HasTs;
import javax.swing.TransferHandler;
import demetra.ui.datatransfer.DataTransfer;
import java.util.Optional;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasTsTransferHandler extends TransferHandler {

    @lombok.NonNull
    private final HasTs delegate;

    @lombok.NonNull
    private final DataTransfer tssSupport;

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return tssSupport.canImport(support.getDataFlavors());
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        Optional<demetra.timeseries.Ts> ts = tssSupport.toTs(support.getTransferable());
        if (ts.isPresent()) {
            TsManager.getDefault().loadAsync(ts.get(), demetra.timeseries.TsInformationType.All);
            delegate.setTs(ts.get());
            return true;
        }
        return false;
    }
}
