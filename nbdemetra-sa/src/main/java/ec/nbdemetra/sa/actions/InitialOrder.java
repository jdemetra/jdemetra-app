/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.InitialOrder")
@ActionRegistration(displayName = "#CTL_InitialOrder", lazy=true)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1600),
    @ActionReference(path = "Shortcuts", name = "I")
})
@Messages("CTL_InitialOrder=InitialOrder")
public final class InitialOrder extends AbstractViewAction<SaBatchUI> {

    public InitialOrder() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_InitialOrder());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui=context();
        enabled = ui != null && !ui.getCurrentProcessing().isEmpty();
    }

    @Override
    protected void process(SaBatchUI cur) {
        SaBatchUI ui=context();
        if (ui != null)
            ui.setInitialOrder();
    }

 }
