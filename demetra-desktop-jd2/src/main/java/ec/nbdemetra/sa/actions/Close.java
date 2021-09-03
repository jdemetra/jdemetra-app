/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import demetra.desktop.nodes.SingleNodeAction;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.Close")
@ActionRegistration(
    displayName = "#CTL_Close", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.ITEMPATH, position = 1000, separatorAfter = 1001)
})
@Messages("CTL_Close=Close")
public final class Close extends SingleNodeAction<ItemWsNode> {

    public static final String CLOSE_MESSAGE = "This action will overwrite the previous version, if any. Are you sure you want to process?";

    public Close() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        if (cur == null || cur.isReadOnly() || cur.getStatus() == WorkspaceItem.Status.Undefined) {
            return;
        }
        if (cur.isDirty()) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(CLOSE_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
        }
        context.getItem().close();
        System.gc();
    }

    @Override
    protected boolean enable(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        return cur != null && !cur.isReadOnly() && cur.getStatus() != WorkspaceItem.Status.Undefined;
    }

    @Override
    public String getName() {
        return Bundle.CTL_Close();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
