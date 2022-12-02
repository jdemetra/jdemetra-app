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
id = "demetra.desktop.sa.multiprocessing.actions.Paste")
@ActionRegistration(displayName = "#CTL_Paste", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Edit.PATH, position = 1330)
})
@Messages("CTL_Paste=Paste")
public final class Paste extends ActiveViewAction<SaBatchUI> {

    public Paste() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_Paste());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        enabled = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().getAvailableDataFlavors().length > 0;
    }

    @Override
    protected void process(SaBatchUI cur) {
        SaBatchUI ui = context();
        if (ui != null) {
            ui.paste(true);
        }
    }
}
