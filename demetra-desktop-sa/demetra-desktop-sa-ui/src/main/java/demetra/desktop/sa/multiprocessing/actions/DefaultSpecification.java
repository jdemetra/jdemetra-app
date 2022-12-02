/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.ui.ActiveViewAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "demetra.sa.multiprocessing.actions.DefaultSpecification")
@ActionRegistration(displayName = "#CTL_DefaultSpecification", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1000, separatorAfter = 1005),
    @ActionReference(path = "Shortcuts", name = "D")
})
@Messages("CTL_DefaultSpecification=Default specification...")
public final class DefaultSpecification extends ActiveViewAction<SaBatchUI> {

    public DefaultSpecification() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_DefaultSpecification());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null;
    }

    @Override
    protected void process(SaBatchUI cur) {
        cur.editDefaultSpecification();
    }

}
