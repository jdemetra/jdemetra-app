/*
 * Copyright 2017 National Bank of Belgium
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
package demetra.desktop.tramo.ui.actions;

import demetra.desktop.tramo.ui.TramoSpecManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.tramo.TramoSpec;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "demetra.desktop.tramo.ui.actions.CreateTramoDoc")
@ActionRegistration(displayName = "#CTL_CreateTramoDoc")
@ActionReferences({
    @ActionReference(path = TramoSpecManager.ITEMPATH, position = 1620, separatorBefore = 1300)
})
@Messages("CTL_CreateTramoDoc=Create Document")
public final class CreateTramoDoc implements ActionListener {

    private final WsNode context;

    public CreateTramoDoc(WsNode context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<TramoSpec> xdoc = context.getWorkspace().searchDocument(context.lookup(), TramoSpec.class);
        if (xdoc == null||xdoc.getElement() == null) {
            return;
        }
        TramoSpecManager mgr = (TramoSpecManager) WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        if (mgr != null) {
            mgr.createDocument(context.getWorkspace(), xdoc);
        }
    }
}

