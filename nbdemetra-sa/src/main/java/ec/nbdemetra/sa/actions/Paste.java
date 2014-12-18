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
id = "ec.nbdemetra.sa.actions.Paste")
@ActionRegistration(displayName = "#CTL_Paste")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Edit.PATH, position = 1310)
})
@Messages("CTL_Paste=Paste")
public final class Paste extends AbstractViewAction<SaBatchUI> {

    public Paste() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_Paste());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        enabled = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null) != null;
    }

    @Override
    protected void process(SaBatchUI cur) {
        SaBatchUI ui = context();
        if (ui != null) {
            ui.paste(true);
        }
    }
}
