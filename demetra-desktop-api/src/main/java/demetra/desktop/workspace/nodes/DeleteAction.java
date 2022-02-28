/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package demetra.desktop.workspace.nodes;

import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.workspace.WorkspaceItem;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit",
id = "demetra.desktop.workspace.nodes.DeleteAction")
@ActionRegistration(
        displayName = "#CTL_DeleteAction", lazy=false)
@Messages("CTL_DeleteAction=Delete")
public final class DeleteAction extends SingleNodeAction<ItemWsNode> {
    
    public static final String DELETE_MESSAGE ="Are you sure you want to delete this item?";

    public DeleteAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        if (cur != null && !cur.isReadOnly()) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(DELETE_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            cur.closeView();
            context.getWorkspace().remove(cur);
        }
    }

    @Override
    protected boolean enable(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        return cur != null && !cur.isReadOnly();
    }

    @Override
    public String getName() {
        return Bundle.CTL_DeleteAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
