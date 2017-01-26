/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tsproviders.actions;

import com.google.common.base.Optional;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.tsproviders.ProviderNode;
import ec.tss.datatransfer.DataSourceTransferSupport;
import ec.tss.datatransfer.DataTransfers;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceLoader;
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
        IDataSourceLoader loader = activatedNode.getLookup().lookup(IDataSourceLoader.class);
        return loader != null && DataSourceTransferSupport.getDefault().canHandle(DataTransfers.systemClipboardAsTransferable(), loader.getSource());
    }

    @Override
    public String getName() {
        return Bundle.CTL_PasteProviderAction();
    }

    @Override
    protected void performAction(ProviderNode activatedNode) {
        IDataSourceLoader loader = activatedNode.getLookup().lookup(IDataSourceLoader.class);
        Optional<DataSource> dataSource = DataSourceTransferSupport.getDefault().getDataSource(DataTransfers.systemClipboardAsTransferable(), loader.getSource());
        if (dataSource.isPresent()) {
            activatedNode.paste(dataSource.get());
        }
    }
}
