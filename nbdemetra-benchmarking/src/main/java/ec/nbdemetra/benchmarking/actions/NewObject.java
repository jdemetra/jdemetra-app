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
package ec.nbdemetra.benchmarking.actions;

import ec.nbdemetra.benchmarking.CalendarizationDocumentManager;
import ec.nbdemetra.benchmarking.CholetteDocumentManager;
import ec.nbdemetra.benchmarking.DentonDocumentManager;
import ec.nbdemetra.benchmarking.MultiCholetteDocumentManager;
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

@ActionID(category = "Tools",
id = "ec.nbdemetra.benchmarking.actions.NewObject")
@ActionRegistration(displayName = "#CTL_NewObject")
@ActionReferences({
    @ActionReference(path = DentonDocumentManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = CholetteDocumentManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = MultiCholetteDocumentManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = CalendarizationDocumentManager.PATH, position = 1600, separatorBefore = 1300)
})
@Messages("CTL_NewObject=New")
public class NewObject implements ActionListener {

    private final WsNode context;

    public NewObject(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        IWorkspaceItemManager mgr=WorkspaceFactory.getInstance().getManager(context.lookup());
        if (mgr != null){
            Workspace ws=context.getWorkspace();
            mgr.create(ws);
        }
    }
}
