/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.variables.actions;

import demetra.desktop.nodes.SingleNodeAction;
import ec.nbdemetra.ui.variables.VariablesDocumentManager;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.tstoolkit.timeseries.regression.ITsVariable;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.utilities.IDynamicObject;
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
id = "ec.nbdemetra.ui.variables.actions.RefreshAction")
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
        WorkspaceItem<TsVariables> cur = (WorkspaceItem<TsVariables>) context.getItem();
        if (cur != null && !cur.isReadOnly()) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(WARNING_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            for (ITsVariable var : cur.getElement().variables()) {
                if (var instanceof IDynamicObject) {
                    IDynamicObject dvar = (IDynamicObject) var;
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