/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.variables.actions;

import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.ui.variables.VariablesDocumentManager;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.timeseries.regression.ModellingContext;
import demetra.timeseries.regression.TsDataSuppliers;
import demetra.util.NameManager;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit",
id = "demetra.desktop.ui.variables.actions.RemoveVariablesAction")
@ActionRegistration(
        displayName = "#CTL_RemoveVariablesAction", lazy=false)
@ActionReferences({
    //    @ActionReference(path = "Menu/Edit"),
    @ActionReference(path = VariablesDocumentManager.ITEMPATH, position = 1100)
})
@Messages("CTL_RemoveVariablesAction=Remove")
public final class RemoveVariablesAction extends SingleNodeAction<ItemWsNode> {
    
    public static final String DELETE_MESSAGE ="Are you sure you want to delete this item?";

    public RemoveVariablesAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode activeNode) {
        WorkspaceItem<TsDataSuppliers> cur = (WorkspaceItem<TsDataSuppliers>) activeNode.getItem();
        if (cur != null && !cur.isReadOnly()) {
            TsDataSuppliers o=cur.getElement();
            removeVariables(o, activeNode);
        }
    }

    @Override
    protected boolean enable(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        return cur != null && !cur.isReadOnly();
    }

    @Override
    public String getName() {
        return Bundle.CTL_RemoveVariablesAction();
    }

    @Messages({
        "RemoveVariables.dialog.title=Remove calendar",
        "RemoveVariables.dialog.message=Are you sure?"
    })
    static void removeVariables(TsDataSuppliers p, ItemWsNode node) {
        DialogDescriptor.Confirmation dd = new DialogDescriptor.Confirmation(
                Bundle.RemoveVariables_dialog_message(),
                Bundle.RemoveVariables_dialog_title(),
                NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.YES_OPTION) {
            NameManager<TsDataSuppliers> manager = ModellingContext.getActiveContext().getTsVariableManagers();
            manager.remove(p);
            node.getWorkspace().remove(node.getItem());
        }
    }
}
