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
id = "ec.nbdemetra.sa.actions.LocalRefreshParameters")
@ActionRegistration(displayName = "#CTL_LocalRefreshParameters")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.LOCALPATH+LocalRefreshPartial.PATH, position = 1230)
})
@Messages("CTL_LocalRefreshParameters=Parameters")
public final class LocalRefreshParameters implements ActionListener {

    public LocalRefreshParameters() {
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        SaBatchUI ui = ActiveViewManager.getInstance().getLookup().lookup(SaBatchUI.class);
        if (ui == null) {
            return;
        }
        ui.refreshSelection(EstimationPolicyType.FreeParameters, true, false);
    }
}
