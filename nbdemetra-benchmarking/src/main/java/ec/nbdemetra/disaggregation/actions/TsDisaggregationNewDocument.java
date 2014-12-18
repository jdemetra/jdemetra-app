/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.disaggregation.actions;

import ec.nbdemetra.benchmarking.CholetteDocumentManager;
import ec.nbdemetra.disaggregation.TsDisaggregationModelManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.disaggregation.documents.CholetteDocument;
import ec.tss.disaggregation.documents.TsDisaggregationModelDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Window",
        id = "ec.nbdemetra.disaggregation.actions.TsDisaggregationNewDocument"
)
@ActionRegistration(
        displayName = "#CTL_TsDisaggregationNewDocument"
)
@ActionReference(path = "Menu/Statistical methods/Temporal Disaggregation", position = 1000)
@Messages("CTL_TsDisaggregationNewDocument=Regression Model")
public final class TsDisaggregationNewDocument implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        TsDisaggregationModelManager mgr = WorkspaceFactory.getInstance().getManager(TsDisaggregationModelManager.class);
        if (mgr != null) {
            Workspace ws = WorkspaceFactory.getInstance().getActiveWorkspace();
            WorkspaceItem<TsDisaggregationModelDocument> ndoc = mgr.create(ws);
            mgr.openDocument(ndoc);
        }
    }
}
