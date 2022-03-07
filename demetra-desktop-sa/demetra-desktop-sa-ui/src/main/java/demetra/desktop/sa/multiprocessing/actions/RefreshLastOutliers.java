/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.ui.ActiveViewAction;
import demetra.sa.EstimationPolicyType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static javax.swing.Action.NAME;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@ActionID(category = "SaProcessing",
id = "demetra.desktop.sa.multiprocessing.actions.RefreshLastOutliers")
@ActionRegistration(displayName = "#CTL_RefreshLastOutliers")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH+RefreshPartial.PATH, position = 1240)
})
@Messages("CTL_RefreshLastOutliers=+ Last outliers")
public final class RefreshLastOutliers extends ActiveViewAction<SaBatchUI> {

    public RefreshLastOutliers() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_RefreshLastOutliers());
    }

    @Override
    protected void process(SaBatchUI ui) {
        // TODO customize the length of the last outliers
        ui.refresh(EstimationPolicyType.LastOutliers, -1, true, true);
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && !ui.getElement().isNew();
    }
}
