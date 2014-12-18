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
package ec.nbdemetra.ui.actions;

import ec.nbdemetra.ui.calendars.CalendarDocumentManager;
import ec.nbdemetra.ui.variables.VariablesDocumentManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.nodes.WsNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "ec.nbdemetra.ui.actions.SortItems")
@ActionRegistration(
        displayName = "#CTL_SortItems")
@ActionReferences({
    @ActionReference(path = "Shortcuts", name = "S"),
    @ActionReference(path = CalendarDocumentManager.PATH, position = 1100),
    @ActionReference(path = VariablesDocumentManager.PATH, position = 1100)
})
@Messages("CTL_SortItems=Sort")
public final class SortItems extends AbstractSortItems {
    public SortItems(WsNode context) {
        super(context);
    }
}
