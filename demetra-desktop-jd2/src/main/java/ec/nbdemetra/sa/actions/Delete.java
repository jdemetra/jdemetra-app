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
        id = "ec.nbdemetra.sa.actions.Delete")
@ActionRegistration(displayName = "#CTL_Delete")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Edit.PATH, position = 1340)
})
@Messages("CTL_Delete=Delete")
public final class Delete extends AbstractViewAction<SaBatchUI> {

    public static final String DELETE_MESSAGE = "Are you sure you want to delete the selected items?";

    public Delete() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_Delete());
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
