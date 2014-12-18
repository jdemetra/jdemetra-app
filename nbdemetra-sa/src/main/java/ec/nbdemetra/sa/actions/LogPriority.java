/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.tss.sa.SaItem;
import java.util.Arrays;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.LogPriority")
@ActionRegistration(displayName = "#CTL_LogPriority", lazy=false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Priority.PATH, position = 1520),
    @ActionReference(path = "Shortcuts", name = "G")
})
@Messages("CTL_LogPriority=Log-based")
public final class LogPriority extends AbstractViewAction<SaBatchUI> {

    public LogPriority() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_LogPriority());
        refreshAction();
    }

    @Override
    protected void process(SaBatchUI ui) {
        SaItem[] sel = ui.getSelection();
        if (sel.length == 0) {
            ui.setLogPriority(ui.getCurrentProcessing());
        }
        else {
            ui.setLogPriority(Arrays.asList(sel));
        }
    }

    @Override
    protected void refreshAction() {
    }
}