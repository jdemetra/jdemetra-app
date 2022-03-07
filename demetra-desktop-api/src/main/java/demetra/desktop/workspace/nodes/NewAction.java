/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.workspace.nodes;

import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

@ActionID(category = "Edit",
id = "demetra.desktop.workspace.nodes.NewAction")
@ActionRegistration(
        displayName = "#CTL_NewAction", lazy=false)
@NbBundle.Messages("CTL_NewAction=New")
public final class NewAction extends SingleNodeAction<ManagerWsNode> {
    
    public NewAction() {
        super(ManagerWsNode.class);
    }

    @Override
    protected void performAction(ManagerWsNode context) {
        WorkspaceItemManager<?> manager = context.getManager();
         if (manager != null) {
             manager.create(context.getWorkspace());
        }
    }

    @Override
    protected boolean enable(ManagerWsNode context) {
        WorkspaceItemManager<?> manager = context.getManager();
        return manager != null;
    }

    @Override
    public String getName() {
        return Bundle.CTL_NewAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
