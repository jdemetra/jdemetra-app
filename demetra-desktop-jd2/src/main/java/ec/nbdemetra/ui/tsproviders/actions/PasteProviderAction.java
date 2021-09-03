/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tsproviders.actions;

import demetra.tsprovider.DataSourceLoader;
import demetra.ui.datatransfer.DataTransfers;
import demetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.tsproviders.ProviderNode;
import ec.tss.datatransfer.DataSourceTransferSupport;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit", id = "ec.nbdemetra.ui.nodes.actions.PasteProviderAction")
@ActionRegistration(displayName = "#CTL_PasteProviderAction", lazy = false)
@Messages("CTL_PasteProviderAction=Paste")
public final class PasteProviderAction extends SingleNodeAction<ProviderNode> {

    public PasteProviderAction() {
        super(ProviderNode.class);
    }

    @Override
    protected boolean enable(ProviderNode activatedNode) {
        DataSourceLoader loader = activatedNode.getLookup().lookup(DataSourceLoader.class);
        return loader != null && DataSourceTransferSupport.getDefault().canHandle(DataTransfers.systemClipboardAsTransferable(), loader.getSource());
    }

    @Override
    public String getName() {
        return Bundle.CTL_PasteProviderAction();
    }

    @Override
    protected void performAction(ProviderNode activatedNode) {
        DataSourceLoader loader = activatedNode.getLookup().lookup(DataSourceLoader.class);
        DataSourceTransferSupport.getDefault()
                .getDataSource(DataTransfers.systemClipboardAsTransferable(), loader.getSource())
                .ifPresent(activatedNode::paste);
    }
}
