/*
 * Copyright 2013 National Bank of Belgium
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
package ec.nbdemetra.ui.tsproviders;

import com.google.common.base.Optional;
import ec.tss.TsCollection;
import ec.tss.TsInformationType;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.TsProviders;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import javax.annotation.Nonnull;

/**
 * A node that represents a DataSet of type collection.
 *
 * @author Philippe Charles
 */
public final class CollectionNode extends DataSetNode {

    public static final String ACTION_PATH = "CollectionNode";

    public CollectionNode(@Nonnull DataSet dataSet) {
        super(dataSet, ACTION_PATH);
    }

    private Transferable getData(TsInformationType type) throws IOException {
        Optional<TsCollection> data = TsProviders.getTsCollection(getLookup().lookup(DataSet.class), type);
        if (data.isPresent()) {
            return TssTransferSupport.getDefault().fromTsCollection(data.get());
        }
        throw new IOException("Cannot create the TS collection '" + getDisplayName() + "'; check the logs for further details.");
    }

    @Override
    public Transferable clipboardCopy() throws IOException {
        // Transferable#getTransferData(DataFlavor) might be called by the system clipboard
        // Therefore, we load the data directly in the following call
        return getData(TsInformationType.All);
    }

    @Override
    public Transferable drag() throws IOException {
        return getData(TsInformationType.Definition);
    }
}
