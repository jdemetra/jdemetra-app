/*
 * Copyright 2016 National Bank of Belgium
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
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import ec.nbdemetra.tramoseats.TramoSeatsSpecificationManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.nbdemetra.x13.X13DocumentManager;
import ec.nbdemetra.x13.X13SpecificationManager;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.utilities.LinearId;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Action used in the multi processing view to allow the export of the
 * specification of the currently selected item to the workspace for further
 * use.
 *
 * @author Mats Maggi
 */
@ActionID(category = "SaProcessing",
        id = "ec.nbdemetra.sa.actions.CopySpecToWorkSpace")
@ActionRegistration(displayName = "#CTL_CopySpecToWorkSpace", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Specification.PATH, position = 1420),
    @ActionReference(path = MultiProcessingManager.LOCALPATH + Specification.PATH, position = 1420)
})
@Messages("CTL_CopySpecToWorkSpace=Copy to workspace")
public final class CopySpecToWorkSpace extends AbstractViewAction<SaBatchUI> {

    public CopySpecToWorkSpace() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_CopySpecToWorkSpace());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() == 1;
    }

    @Override
    protected void process(SaBatchUI cur) {
        IProcSpecification spec = cur.getSelection()[0].getEstimationSpecification();
        LinearId id;
        Class mgr;
        if (spec instanceof TramoSeatsSpecification) {
            id = TramoSeatsSpecificationManager.ID;
            mgr = TramoSeatsDocumentManager.class;
        } else if (spec instanceof X13Specification) {
            id = X13SpecificationManager.ID;
            mgr = X13DocumentManager.class;
        } else {
            return;
        }

        if (cur.getSelection()[0] != null) {
            IWorkspaceItemManager wsMgr = WorkspaceFactory.getInstance().getManager(mgr);
            WorkspaceItem<IProcSpecification> ndoc = WorkspaceItem.newItem(id, wsMgr.getNextItemName(null), spec);
            WorkspaceFactory.getInstance().getActiveWorkspace().add(ndoc);
        }
    }
}
