/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.tramoseats.TramoDocumentManager;
import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import demetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.nbdemetra.x13.RegArimaDocumentManager;
import ec.nbdemetra.x13.X13DocumentManager;
import ec.tss.documents.TsDocument;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Tools",
id = "ec.nbdemetra.sa.actions.RefreshDataAction")
@ActionRegistration(
    displayName = "#CTL_RefreshDataAction", lazy=false)
@ActionReferences({
    @ActionReference(path = "Shortcuts", name = "D-R"),
    @ActionReference(path = TramoDocumentManager.ITEMPATH, position = 1900),
    @ActionReference(path = TramoSeatsDocumentManager.ITEMPATH, position = 1900),
    @ActionReference(path = RegArimaDocumentManager.ITEMPATH, position = 1900),
    @ActionReference(path = X13DocumentManager.ITEMPATH, position = 1900)
})
@Messages("CTL_RefreshDataAction=Refresh Data")
public final class RefreshDataAction extends SingleNodeAction<ItemWsNode> {

    public static final String REFRESH_MESSAGE = "Are you sure you want to refresh the data?";

    public RefreshDataAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        if (cur.getElement() instanceof TsDocument) {
            TsDocument doc = (TsDocument) cur.getElement();
            if (doc.isTsFrozen()) {
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation(REFRESH_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
                if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                    return;
                }
                doc.unfreezeTs();
            }
        }
    }

    @Override
    protected boolean enable(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        if (cur.getElement() instanceof TsDocument) {
            TsDocument doc = (TsDocument) cur.getElement();
            return doc.isTsFrozen();
        }
        return false;
    }

    @Override
    public String getName() {
        return Bundle.CTL_RefreshDataAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
