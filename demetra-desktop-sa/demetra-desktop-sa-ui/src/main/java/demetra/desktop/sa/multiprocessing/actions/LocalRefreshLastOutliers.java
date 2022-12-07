/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.ui.ActiveViewAction;
import demetra.sa.EstimationPolicyType;
import static javax.swing.Action.NAME;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "demetra.desktop.sa.multiprocessing.actions.LocalRefreshLastOutliers")
@ActionRegistration(displayName = "#CTL_LocalRefreshLastOutliers", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.LOCALPATH + LocalRefreshPartial.PATH, position = 1240)
})
@Messages("CTL_LocalRefreshLastOutliers=+ Last outliers")
public final class LocalRefreshLastOutliers extends ActiveViewAction<SaBatchUI> {

    public LocalRefreshLastOutliers() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_LocalRefreshLastOutliers());
    }

    @Override
    protected void process(SaBatchUI ui) {
        // TODO customize the length of the last outliers
        ui.refresh(EstimationPolicyType.LastOutliers, -1, true, false);
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getElement().isRefreshable() && ui.getSelectionCount() > 0;
    }
}
