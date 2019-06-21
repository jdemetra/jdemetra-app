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
import static ec.nbdemetra.ui.tsproviders.SeriesNode.ACTION_PATH;
import ec.tss.TsInformationType;
import ec.tss.tsproviders.DataSet;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.IOException;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.actions.Openable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import demetra.ui.datatransfer.DataTransfer;

/**
 * A node that represents a DataSet of type series.
 *
 * @author Philippe Charles
 */
@ActionReferences({
    @ActionReference(path = ACTION_PATH, position = 1310, separatorBefore = 1300, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.actions.OpenAction"))
    ,
    @ActionReference(path = ACTION_PATH, position = 1320, separatorBefore = 1300, id = @ActionID(category = "Edit", id = "ec.nbdemetra.ui.nodes.actions.OpenWithSetAction"))
    ,
    @ActionReference(path = ACTION_PATH, position = 1420, separatorBefore = 1400, id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"))
    ,
    @ActionReference(path = ACTION_PATH, position = 1425, separatorBefore = 1400, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.tssave.TsSaveAction"))
})
public final class SeriesNode extends DataSetNode {

    public static final String ACTION_PATH = "SeriesNode";

    public SeriesNode(@NonNull DataSet dataSet) {
        super(dataSet, ACTION_PATH);
    }

    private Transferable getData(TsInformationType type) throws IOException {
        return TsManager.getDefault()
                .getTs(getLookup().lookup(DataSet.class), type)
                .map(TsConverter::toTs)
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
