/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingController.SaProcessingState;
import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import javax.swing.SwingWorker.StateValue;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.Process")
@ActionRegistration(displayName = "#CTL_Process", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1100),
    @ActionReference(path = "Shortcuts", name = "S")
})
@Messages("CTL_Process=Start")
public final class Process extends AbstractViewAction<SaBatchUI> {

    private boolean start;

    public Process() {
        super(SaBatchUI.class);
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        enabled = false;
        start = true;
        SaBatchUI cur = context();
        if (cur != null) {
            if (!cur.getCurrentProcessing().isEmpty() && !cur.getCurrentProcessing().isProcessed()) {
                SaProcessingState state = cur.getState();
                switch (state) {
                    case PENDING:
                        enabled = true;
                        break;
                    case STARTED:
                        start = false;
                        enabled = true;
                        break;
                    case DONE: // not finished
                        enabled = true;
                        break;
                }
            }
        }
        if (start) {
            putValue(NAME, "Start");
        } else {
            putValue(NAME, "Stop");
        }
    }

    @Override
    public boolean isEnabled() {
        refreshAction();
        return enabled;
    }


    @Override
    protected void process(SaBatchUI cur) {
        if (start) {
            cur.start(false);
        } else {
            cur.stop();
        }
    }
}
