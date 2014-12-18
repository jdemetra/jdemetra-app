/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tsproviders.actions;

import com.google.common.base.Optional;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.tsproviders.ProviderNode;
import ec.tss.datatransfer.DataSourceTransferSupport;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceLoader;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit", id = "ec.nbdemetra.ui.nodes.actions.PasteProviderAction")
@ActionRegistration(displayName = "#CTL_PasteProviderAction", lazy = false)
@ActionReferences({
    @ActionReference(path = ProviderNode.ACTION_PATH, position = 1420, separatorBefore = 1400)
})
@Messages("CTL_PasteProviderAction=Paste")
public final class PasteProviderAction extends SingleNodeAction<ProviderNode> {

    public PasteProviderAction() {
        super(ProviderNode.class);
    }

    @Override
    protected boolean enable(ProviderNode activatedNode) {
        IDataSourceLoader loader = activatedNode.getLookup().lookup(IDataSourceLoader.class);
        return loader != null && DataSourceTransferSupport.getDefault().canHandle(getTransferable(activatedNode), loader.getSource());
    }

    @Override
    public String getName() {
        return Bundle.CTL_PasteProviderAction();
    }

    @Override
    protected void performAction(ProviderNode activatedNode) {
        IDataSourceLoader loader = activatedNode.getLookup().lookup(IDataSourceLoader.class);
        Optional<DataSource> dataSource = DataSourceTransferSupport.getDefault().getDataSource(getTransferable(activatedNode), loader.getSource());
        if (dataSource.isPresent()) {
            activatedNode.paste(dataSource.get());
        }
    }

    static Transferable getTransferable(ProviderNode activatedNode) {
        return Toolkit.getDefaultToolkit().getSystemClipboard().getContents(activatedNode);
    }
}
