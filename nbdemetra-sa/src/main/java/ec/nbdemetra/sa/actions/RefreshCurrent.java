/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ui.ActiveViewManager;
import ec.tss.sa.EstimationPolicyType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.RefreshCurrent")
@ActionRegistration(displayName = "#CTL_RefreshCurrent")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH+Refresh.PATH, position = 1201)
})
@Messages("CTL_RefreshCurrent=Current adjustment (AO approach)")
public final class RefreshCurrent implements ActionListener {

    public RefreshCurrent() {
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        SaBatchUI ui = ActiveViewManager.getInstance().getLookup().lookup(SaBatchUI.class);
        if (ui == null) {
            return;
        }
        ui.refresh(EstimationPolicyType.Current, false, true);
    }
}