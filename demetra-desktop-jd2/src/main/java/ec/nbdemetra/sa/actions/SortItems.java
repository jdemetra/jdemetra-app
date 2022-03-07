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
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.tramoseats.TramoDocumentManager;
import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import ec.nbdemetra.ui.actions.AbstractSortItems;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.nbdemetra.x13.RegArimaDocumentManager;
import ec.nbdemetra.x13.X13DocumentManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "ec.nbdemetra.sa.actions.SortItems")
@ActionRegistration(
        displayName = "#CTL_SortItems")
@ActionReferences({
    @ActionReference(path = "Shortcuts", name = "S"),
    @ActionReference(path = TramoDocumentManager.PATH, position = 1100),
    @ActionReference(path = TramoSeatsDocumentManager.PATH, position = 1100),
    @ActionReference(path = RegArimaDocumentManager.PATH, position = 1100),
    @ActionReference(path = X13DocumentManager.PATH, position = 1100),
    @ActionReference(path = MultiProcessingManager.PATH, position = 1100)
})
@Messages("CTL_SortItems=Sort")
public final class SortItems implements ActionListener {
    private final WsNode context;

    protected AbstractSortItems(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        IWorkspaceItemManager mgr = WorkspaceFactory.getInstance().getManager(context.lookup());
        if (mgr != null) {
             Workspace ws = context.getWorkspace();
             ws.sortFamily(context.lookup());
         }
    }
}
