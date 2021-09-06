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
package demetra.desktop.core.tsproviders;

import demetra.desktop.TsManager;
import demetra.desktop.core.actions.OpenNodeAction;
import demetra.desktop.core.actions.TsSaveNodeAction;
import demetra.desktop.datatransfer.DataTransfer;
import demetra.timeseries.TsInformationType;
import demetra.tsprovider.DataSet;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.netbeans.api.actions.Openable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;

import javax.swing.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static demetra.desktop.actions.Actions.COPY_NODE_ACTION_ID;
import static demetra.desktop.tsproviders.TsProviderNodes.SERIES_ACTION_PATH;

/**
 * A node that represents a DataSet of type series.
 *
 * @author Philippe Charles
 */
@ActionReferences({
        @ActionReference(path = SERIES_ACTION_PATH, separatorBefore = 300, position = 310, id = @ActionID(category = "File", id = OpenNodeAction.ID)),
        @ActionReference(path = SERIES_ACTION_PATH, separatorBefore = 300, position = 320, id = @ActionID(category = "Edit", id = OpenWithSetNodeAction.ID)),
        @ActionReference(path = SERIES_ACTION_PATH, separatorBefore = 400, position = 420, id = @ActionID(category = "Edit", id = COPY_NODE_ACTION_ID)),
        @ActionReference(path = SERIES_ACTION_PATH, separatorBefore = 400, position = 425, id = @ActionID(category = "File", id = TsSaveNodeAction.ID))
})
public final class SeriesNode extends DataSetNode {

    public SeriesNode(@NonNull DataSet dataSet) {
        super(dataSet, SERIES_ACTION_PATH);
    }

    private Transferable getData(TsInformationType type) throws IOException {
        DataSet dataSet = getLookup().lookup(DataSet.class);
        return TsManager.getDefault()
                .getTs(dataSet, type)
                .map(DataTransfer.getDefault()::fromTs)
                .orElseThrow(() -> new IOException("Cannot create the TS '" + getDisplayName() + "'; check the logs for further details."));
    }

    @Override
    public Transferable clipboardCopy() throws IOException {
        return getData(TsInformationType.None);
    }

    @Override
    public Transferable drag() throws IOException {
        return getData(TsInformationType.None);
    }

    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getLookup().lookup(Openable.class).open();
            }
        };
    }
}
