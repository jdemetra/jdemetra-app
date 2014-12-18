/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.tss.sa.SaItem;
import ec.tstoolkit.algorithm.ProcQuality;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.Accept")
@ActionRegistration(displayName = "#CTL_Accept", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1260),
    @ActionReference(path = MultiProcessingManager.LOCALPATH, position = 1260)
})
@NbBundle.Messages("CTL_Accept=Accept")
public final class Accept extends AbstractViewAction<SaBatchUI> {

    public Accept() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_Accept());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() > 0;
        if (enabled) {
            SaItem[] selection = ui.getSelection();
            boolean accepted = isAccepted(selection);

            if (accepted) {
                putValue(NAME, "Accept");
            } else {
                putValue(NAME, "Reset Quality");
            }
        }
    }

    @Override
    protected void process(SaBatchUI cur) {
        SaItem[] selection = cur.getSelection();
        boolean accepted = isAccepted(selection);
        for (int i = 0; i < selection.length; ++i) {
            SaItem o = selection[i];
            int index = cur.getCurrentProcessing().indexOf(selection[i]);
            if (accepted) {
                o.setQuality(ProcQuality.Accepted);
            } else {
                o.setQuality(ProcQuality.Undefined);
            }
            if (index >= 0) {
                cur.getCurrentProcessing().get(index).setQuality(o.getQuality());
            } 
        }
        
        cur.redrawAll();
    }

    public boolean isAccepted(SaItem[] selection) {
        for (int i = 0; i < selection.length; ++i) {
            if (selection[i].getQuality() != ProcQuality.Accepted) {
                return true;
            }
        }
        return false;
    }
}
