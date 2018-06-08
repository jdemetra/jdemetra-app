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

import demetra.bridge.TsConverter;
import demetra.ui.TsManager;
import static ec.nbdemetra.ui.tsproviders.CollectionNode.ACTION_PATH;
import ec.tss.TsInformationType;
import ec.tss.tsproviders.DataSet;
import static internal.TsEventHelper.SHOULD_BE_NONE;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import demetra.ui.DataTransfer;

/**
 * A node that represents a DataSet of type collection.
 *
 * @author Philippe Charles
 */
@ActionReferences({
    @ActionReference(path = ACTION_PATH, position = 1420, separatorBefore = 1400, id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction")),
    @ActionReference(path = ACTION_PATH, position = 1425, separatorBefore = 1400, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.tssave.TsSaveAction"))
})
public final class CollectionNode extends DataSetNode {

    public static final String ACTION_PATH = "CollectionNode";

    public CollectionNode(@Nonnull DataSet dataSet) {
        super(dataSet, ACTION_PATH);
    }

    private Transferable getData(TsInformationType type) throws IOException {
        return TsManager.getDefault()
                .getTsCollection(getLookup().lookup(DataSet.class), type)
                .map(TsConverter::toTsCollection)
                .map(DataTransfer.getDefault()::fromTsCollection)
                .orElseThrow(() -> new IOException("Cannot create the TS collection '" + getDisplayName() + "'; check the logs for further details."));
    }

    @Override
    public Transferable clipboardCopy() throws IOException {
        return getData(SHOULD_BE_NONE);
    }

    @Override
    public Transferable drag() throws IOException {
        return getData(SHOULD_BE_NONE);
    }
}
