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
id = "ec.nbdemetra.sa.actions.Cut")
@ActionRegistration(displayName = "#CTL_Cut")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH+Edit.PATH, position = 1330)
})
@Messages("CTL_Cut=Cut")
public final class Cut extends AbstractViewAction<SaBatchUI> {

    public Cut() {
         super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_Cut());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() > 0;
    }

    @Override
    protected void process(SaBatchUI cur) {
        cur.cut();
    }
 }
