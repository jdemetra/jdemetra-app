/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.tss.sa.SaItem;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.ClearSelection")
@ActionRegistration(displayName = "#CTL_ClearSelection")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1350),
    @ActionReference(path = "Shortcuts", name = "C")
})
@Messages("CTL_ClearSelection=Clear selection")
public final class ClearSelection extends AbstractViewAction<SaBatchUI> {

    public ClearSelection() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_ClearSelection());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() > 0;
    }

    @Override
    protected void process(SaBatchUI cur) {
        cur.setSelection(new SaItem[0]);
    }
}

