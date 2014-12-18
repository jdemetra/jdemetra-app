/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ui.ActiveViewManager;
import ec.tss.sa.EstimationPolicyType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.RefreshOutliers")
@ActionRegistration(displayName = "#CTL_RefreshOutliers")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH+RefreshPartial.PATH, position = 1250)
})
@Messages("CTL_RefreshOutliers=Outliers")
public final class RefreshOutliers implements ActionListener {

    public RefreshOutliers() {
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        SaBatchUI ui = ActiveViewManager.getInstance().getLookup().lookup(SaBatchUI.class);
        if (ui == null) {
            return;
        }
        ui.refresh(EstimationPolicyType.Outliers, false, true);
    }
}
