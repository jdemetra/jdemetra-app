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
id = "ec.nbdemetra.sa.actions.LevelPriority")
@ActionRegistration(displayName = "#CTL_LevelPriority", lazy=false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Priority.PATH, position = 1510),
    @ActionReference(path = "Shortcuts", name = "L")
})
@Messages("CTL_LevelPriority=Level-based")
public final class LevelPriority extends AbstractViewAction<SaBatchUI> {

    public LevelPriority() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_LevelPriority());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
    }

    @Override
    protected void process(SaBatchUI ui) {
          SaItem[] sel = ui.getSelection();
        if (sel.length == 0) {
            ui.setLevelPriority(ui.getCurrentProcessing());
        }
        else {
            ui.setLevelPriority(Arrays.asList(sel));
        }
    }
}
