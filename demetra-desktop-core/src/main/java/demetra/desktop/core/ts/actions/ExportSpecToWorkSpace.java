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
package demetra.desktop.core.ts.actions;

import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.desktop.workspace.actions.AbstractViewAction;
import demetra.desktop.workspace.ui.WorkspaceTsTopComponent;
import demetra.processing.ProcSpecification;
import demetra.timeseries.TsDocument;
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
        id = "demetra.desktop.core.ts.actions.ExportSpecToWorkSpace")
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
        WorkspaceItem cur = ws.getDocument();
        TsDocument doc = (TsDocument) cur.getElement();
        ProcSpecification spec = doc.getSpecification();
        WorkspaceItemManager wsMgr = WorkspaceFactory.getInstance().getManager(cur.getFamily());
        WorkspaceItem ndoc = WorkspaceItem.newItem(wsMgr.getId(), wsMgr.getNextItemName(null), spec);
        ndoc.setComments(cur.getComments());
        WorkspaceFactory.getInstance().getActiveWorkspace().add(ndoc);
    }
}
