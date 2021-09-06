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
import demetra.desktop.datatransfer.DataSourceTransfer;
import demetra.desktop.datatransfer.DataTransfers;
import demetra.tsprovider.DataSource;
import demetra.tsprovider.DataSourceLoader;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;

@ActionID(category = "Edit", id = PasteProvidersNodeAction.ID)
@ActionRegistration(displayName = "#CTL_PasteProvidersAction", lazy = false)
@Messages("CTL_PasteProvidersAction=Paste")
public final class PasteProvidersNodeAction extends NodeAction {

    public static final String ID = "demetra.desktop.core.tsproviders.PasteProvidersAction";

    private static void pasteDataSource(DataSource source) {
        TsManager.getDefault()
                .getProvider(DataSourceLoader.class, source)
                .ifPresent(loader -> loader.open(source));
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        DataSourceTransfer.getDefault()
                .getDataSource(DataTransfers.systemClipboardAsTransferable())
                .ifPresent(PasteProvidersNodeAction::pasteDataSource);
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return DataSourceTransfer.getDefault().canHandle(DataTransfers.systemClipboardAsTransferable());
    }

    @Override
    public String getName() {
        return Bundle.CTL_PasteProvidersAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
