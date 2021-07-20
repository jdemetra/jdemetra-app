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
package ec.nbdemetra.sa.actions;
import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.revisionanalysis.RevisionAnalysisDocumentManager;
import ec.nbdemetra.tramoseats.TramoDocumentManager;
import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import ec.nbdemetra.tramoseats.TramoSeatsSpecificationManager;
import ec.nbdemetra.tramoseats.TramoSpecificationManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.nbdemetra.x13.RegArimaDocumentManager;
import ec.nbdemetra.x13.RegArimaSpecificationManager;
import ec.nbdemetra.x13.X13DocumentManager;
import ec.nbdemetra.x13.X13SpecificationManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "ec.nbdemetra.sa.actions.NewObject")
@ActionRegistration(displayName = "#CTL_NewObject")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.PATH, position = 1000),
    @ActionReference(path = TramoSpecificationManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = TramoDocumentManager.PATH, position = 1000),
    @ActionReference(path = TramoSeatsSpecificationManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = TramoSeatsDocumentManager.PATH, position = 1000),
    @ActionReference(path = X13SpecificationManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = X13DocumentManager.PATH, position = 1000),
    @ActionReference(path = RegArimaSpecificationManager.PATH, position = 1600, separatorBefore = 1300),
    @ActionReference(path = RegArimaDocumentManager.PATH, position = 1000),
    @ActionReference(path = RevisionAnalysisDocumentManager.PATH, position = 1000)
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
