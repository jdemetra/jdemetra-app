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
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingController;
import demetra.desktop.sa.multiprocessing.ui.MultiProcessingDocument;
import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.sa.multiprocessing.ui.SaNode;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.ui.ActiveViewAction;
import demetra.sa.EstimationPolicyType;
import demetra.sa.SaManager;
import demetra.sa.SaSpecification;
import demetra.timeseries.TsDomain;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "demetra.sa.multiprocessing.actions.ResultSpecification")
@ActionRegistration(displayName = "#CTL_ResultSpecification", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Specification.PATH, position = 1430),
    @ActionReference(path = MultiProcessingManager.LOCALPATH + Specification.PATH, position = 1430)
})
@Messages("CTL_ResultSpecification=Select result specification")
public final class ResultSpecification extends ActiveViewAction<SaBatchUI> {

    public ResultSpecification() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_ResultSpecification());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() > 0;
    }

    @Override
    protected void process(SaBatchUI cur) {
        cur.stop();
        SaNode[] selection = cur.getSelection();
        MultiProcessingController controller = cur.getController();
        WorkspaceItem<MultiProcessingDocument> document = controller.getDocument();
        for (SaNode o : selection) {
            if (o.isProcessed()) {
                SaSpecification pspec = o.getOutput().getEstimation().getPointSpec();
                pspec=SaManager.factoryFor(pspec).refreshSpec(pspec, o.domainSpec(), EstimationPolicyType.FreeParameters, null);
                SaNode n = o.withEstimationSpecification((SaSpecification) pspec);
                document.getElement().replace(o.getId(), n);
            }
        }
        cur.redrawAll();
        document.setDirty();
        controller.setSaProcessingState(MultiProcessingController.SaProcessingState.READY);
    }
}
