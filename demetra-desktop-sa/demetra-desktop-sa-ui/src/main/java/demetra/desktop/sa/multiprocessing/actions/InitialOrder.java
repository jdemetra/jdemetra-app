/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.ui.ActiveViewAction;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.InitialOrder")
@ActionRegistration(displayName = "#CTL_InitialOrder", lazy=true)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1610),
    @ActionReference(path = "Shortcuts", name = "I")
})
@Messages("CTL_InitialOrder=InitialOrder")
public final class InitialOrder extends ActiveViewAction<SaBatchUI> {

    public InitialOrder() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_InitialOrder());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui=context();
        enabled = ui != null && !ui.getElement().getCurrent().isEmpty();
    }

    @Override
    protected void process(SaBatchUI cur) {
        SaBatchUI ui=context();
        if (ui != null)
            ui.setInitialOrder();
    }

 }
