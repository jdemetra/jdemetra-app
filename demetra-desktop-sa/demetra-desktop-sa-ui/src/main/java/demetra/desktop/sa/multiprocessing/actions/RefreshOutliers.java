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
        id = "demetra.desktop.sa.multiprocessing.actions.RefreshOutliers")
@ActionRegistration(displayName = "#CTL_RefreshOutliers", lazy=true)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + RefreshPartial.PATH, position = 1250)
})
@Messages("CTL_RefreshOutliers=+ All outliers")
public final class RefreshOutliers extends ActiveViewAction<SaBatchUI> {

    public RefreshOutliers() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_RefreshOutliers());
    }

    @Override
    protected void process(SaBatchUI ui) {
        ui.refresh(EstimationPolicyType.Outliers, true, true);
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && !ui.getElement().isNew();
    }
}