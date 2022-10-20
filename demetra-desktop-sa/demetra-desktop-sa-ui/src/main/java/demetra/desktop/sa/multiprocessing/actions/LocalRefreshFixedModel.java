/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.ui.ActiveViewAction;
import demetra.sa.EstimationPolicyType;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "SaProcessing",
id = "demetra.desktop.sa.multiprocessing.actions.LocalRefreshFixedModel")
@ActionRegistration(displayName = "#CTL_LocalRefreshFixedModel", lazy=true)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.LOCALPATH + LocalRefreshPartial.PATH, position = 1205)
})
@NbBundle.Messages("CTL_LocalRefreshFixedModel=Fixed model")
public final class LocalRefreshFixedModel extends ActiveViewAction<SaBatchUI> {

    public LocalRefreshFixedModel() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_LocalRefreshFixedModel());
    }

    @Override
    protected void process(SaBatchUI ui) {
        ui.refresh(EstimationPolicyType.Fixed, true, false);
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && !ui.getElement().isNew() && ui.getSelectionCount() > 0;
    }
}