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
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.ItemWsNode;
import ec.nbdemetra.x13.RegArimaDocumentManager;
import ec.nbdemetra.x13.RegArimaSpecificationManager;
import ec.nbdemetra.x13.X13DocumentManager;
import ec.nbdemetra.x13.X13SpecificationManager;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit",
id = "ec.nbdemetra.sa.actions.DeleteAction")
@ActionRegistration(
        displayName = "#CTL_DeleteAction", lazy=false)
@ActionReferences({
    //    @ActionReference(path = "Menu/Edit"),
    @ActionReference(path = TramoSpecificationManager.ITEMPATH, position = 1100),
    @ActionReference(path = TramoSeatsSpecificationManager.ITEMPATH, position = 1100),
    @ActionReference(path = TramoDocumentManager.ITEMPATH, position = 1100),
    @ActionReference(path = TramoSeatsDocumentManager.ITEMPATH, position = 1100),
    @ActionReference(path = RegArimaSpecificationManager.ITEMPATH, position = 1100),
    @ActionReference(path = X13SpecificationManager.ITEMPATH, position = 1100),
    @ActionReference(path = RegArimaDocumentManager.ITEMPATH, position = 1100),
    @ActionReference(path = X13DocumentManager.ITEMPATH, position = 1100),
    @ActionReference(path = MultiProcessingManager.ITEMPATH, position = 1100),
    @ActionReference(path = RevisionAnalysisDocumentManager.ITEMPATH, position = 1100)    
})
@Messages("CTL_DeleteAction=Delete")
public final class DeleteAction extends SingleNodeAction<ItemWsNode> {
    
    public static final String DELETE_MESSAGE ="Are you sure you want to delete this item?";

    public DeleteAction() {
        super(ItemWsNode.class);
    }

    @Override
    protected void performAction(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        if (cur != null && !cur.isReadOnly()) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(DELETE_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            context.getWorkspace().remove(cur);
        }
    }

    @Override
    protected boolean enable(ItemWsNode context) {
        WorkspaceItem<?> cur = context.getItem();
        return cur != null && !cur.isReadOnly();
    }

    @Override
    public String getName() {
        return Bundle.CTL_DeleteAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
