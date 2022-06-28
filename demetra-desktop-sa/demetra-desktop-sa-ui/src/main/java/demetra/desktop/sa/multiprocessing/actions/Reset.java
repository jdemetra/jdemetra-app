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
        id = "demetra.desktop.ui.sa.multiprocessing.actions.Reset")
@ActionRegistration(displayName = "#CTL_Reset", lazy=false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Edit.PATH, position = 1346)
})
@Messages("CTL_Reset=Reset")
public final class Reset extends ActiveViewAction<SaBatchUI> {

    public Reset() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_Reset());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ! ui.getElement().isNew();
    }

    @Override
    protected void process(SaBatchUI cur) {
        cur.reset(true);
    }
}
