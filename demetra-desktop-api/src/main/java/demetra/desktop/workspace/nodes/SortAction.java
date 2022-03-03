/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
 versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 * See the Licence for the specific language governing
 permissions and limitations under the Licence.
 */
package demetra.desktop.workspace.nodes;

import demetra.desktop.nodes.SingleNodeAction;
import demetra.desktop.workspace.Workspace;
import demetra.desktop.workspace.WorkspaceItemManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

@ActionID(
        category = "Edit",
        id = "demetra.desktop.workspace.nodes.SortAction")
@ActionRegistration(
        displayName = "#CTL_SortAction", lazy=false)
@NbBundle.Messages("CTL_SortAction=New")
public final class SortAction extends SingleNodeAction<ManagerWsNode> {
    
    public SortAction() {
        super(ManagerWsNode.class);
    }

    @Override
    public void performAction(ManagerWsNode context) {
        WorkspaceItemManager<?> manager = context.getManager();
        if (manager != null) {
             Workspace ws = context.getWorkspace();
             ws.sortFamily(context.lookup());
         }
    }
    @Override
    protected boolean enable(ManagerWsNode context) {
        WorkspaceItemManager<?> manager = context.getManager();
        return manager != null;
    }

    @Override
    public String getName() {
        return Bundle.CTL_SortAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
