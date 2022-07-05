/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.variables.actions;

import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.ui.variables.VariablesDocumentManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.timeseries.DynamicTsDataSupplier;
import demetra.timeseries.TsDataSupplier;
import demetra.timeseries.regression.TsDataSuppliers;
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
id = "demetra.desktop.ui.variables.actions.RefreshAction")
@ActionRegistration(displayName = "#CTL_RefreshAction", lazy = false)
@ActionReferences({
    @ActionReference(path = VariablesDocumentManager.ITEMPATH, position = 1700, separatorBefore = 1699)
})
@Messages("CTL_RefreshAction=Refresh")
public final class RefreshAction extends SingleNodeAction<ItemWsNode> {

    public static final String WARNING_MESSAGE = "Refreshing variables may modify some estimations. Are you sure you want to continue?";

    public RefreshAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode context) {
        WorkspaceItem<TsDataSuppliers> cur = (WorkspaceItem<TsDataSuppliers>) context.getItem();
        if (cur != null && !cur.isReadOnly()) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(WARNING_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            for (TsDataSupplier var : cur.getElement().variables()) {
                if (var instanceof DynamicTsDataSupplier dvar) {
                    dvar.refresh();
                }
            }
        }
    }
    
    @Override
    protected boolean enable(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        return cur != null && !cur.isReadOnly();
    }

    @Override
    public String getName() {
        return Bundle.CTL_RefreshAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}