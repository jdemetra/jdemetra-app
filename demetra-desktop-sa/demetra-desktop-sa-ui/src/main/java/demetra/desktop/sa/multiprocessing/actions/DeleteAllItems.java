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
        id = "demetra.desktop.ui.sa.multiprocessing.actions.DeleteAllItems")
@ActionRegistration(displayName = "#CTL_DeleteAllItems", lazy=false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Edit.PATH, position = 1345)
})
@Messages("CTL_DeleteAllItems=Clear")
public final class DeleteAllItems extends ActiveViewAction<SaBatchUI> {

    public DeleteAllItems() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_DeleteAllItems());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() > 0;
    }

    @Override
    protected void process(SaBatchUI cur) {
        cur.clear(true);
    }
}