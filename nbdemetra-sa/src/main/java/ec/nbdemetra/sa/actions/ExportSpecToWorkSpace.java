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

import ec.nbdemetra.tramoseats.TramoSeatsSpecificationManager;
import ec.nbdemetra.tramoseats.TramoSpecificationManager;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.nbdemetra.ws.ui.WorkspaceTsTopComponent;
import ec.nbdemetra.x13.RegArimaSpecificationManager;
import ec.nbdemetra.x13.X13SpecificationManager;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import ec.tss.documents.TsDocument;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.utilities.Id;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Action used in the SA views (for single time series) to allow the export of
 * the specification used to the workspace for further use.
 *
 * @author Mats Maggi
 */
@ActionID(
        category = "Tools",
        id = "ec.nbdemetra.sa.actions.ExportSpecToWorkSpace")
@ActionRegistration(
        displayName = "#CTL_ExportSpecToWorkSpace", lazy = false)
@ActionReferences({
    @ActionReference(path = WorkspaceFactory.TSCONTEXTPATH, position = 1950)
})
@Messages("CTL_ExportSpecToWorkSpace=Copy spec. to workspace")
public final class ExportSpecToWorkSpace extends AbstractViewAction<WorkspaceTsTopComponent> {

    public ExportSpecToWorkSpace() {
        super(WorkspaceTsTopComponent.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_ExportSpecToWorkSpace());
    }

    @Override
    protected void refreshAction() {
        if (context() != null) {
            WorkspaceItem<?> cur = context().getDocument();
            enabled = cur.getElement() instanceof TsDocument;
        } else {
            enabled = false;
        }
    }

    @Override
    protected void process(WorkspaceTsTopComponent ws) {
        WorkspaceItem<?> cur = ws.getDocument();
        TsDocument doc = (TsDocument) cur.getElement();
        IProcSpecification spec = doc.getSpecification();
        Id id = getId(spec);
        if (id != null) {
            IWorkspaceItemManager wsMgr = WorkspaceFactory.getInstance().getManager(cur.getFamily());
            WorkspaceItem<IProcSpecification> ndoc = WorkspaceItem.newItem(id, wsMgr.getNextItemName(null), spec);
            ndoc.setComments(cur.getComments());
            WorkspaceFactory.getInstance().getActiveWorkspace().add(ndoc);
        }
    }

    private Id getId(IProcSpecification spec) {
        if (spec instanceof TramoSeatsSpecification) {
            return TramoSeatsSpecificationManager.ID;
        } else if (spec instanceof TramoSpecification) {
            return TramoSpecificationManager.ID;
        } else if (spec instanceof X13Specification) {
            return X13SpecificationManager.ID;
        } else if (spec instanceof RegArimaSpecification) {
            return RegArimaSpecificationManager.ID;
        } else {
            return null;
        }
    }

}
