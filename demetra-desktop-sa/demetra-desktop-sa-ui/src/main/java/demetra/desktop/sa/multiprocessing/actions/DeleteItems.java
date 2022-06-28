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
        id = "demetra.desktop.ui.sa.multiprocessing.actions.DeleteItems")
@ActionRegistration(displayName = "#CTL_DeleteItems", lazy=false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Edit.PATH, position = 1340)
})
@Messages("CTL_DeleteItems=Delete")
public final class DeleteItems extends ActiveViewAction<SaBatchUI> {

    public static final String DELETE_MESSAGE = "Are you sure you want to delete the selected items?";

    public DeleteItems() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_DeleteItems());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() > 0;
    }

    @Override
    protected void process(SaBatchUI cur) {
        cur.remove(true);
    }
}
