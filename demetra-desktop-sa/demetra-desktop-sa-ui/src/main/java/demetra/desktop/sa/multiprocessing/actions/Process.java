/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingController.SaProcessingState;
import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.ui.ActiveViewAction;
import static javax.swing.Action.NAME;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "demetra.desktop.sa.multiprocessing.actions.Process")
@ActionRegistration(displayName = "#CTL_Process", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1100),
    @ActionReference(path = "Shortcuts", name = "S")
})
@Messages("CTL_Process=Start")
public final class Process extends ActiveViewAction<SaBatchUI> {

    public Process() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_CommentSaItem());
    }

    private boolean start;

    @Override
    protected void refreshAction() {
        SaBatchUI cur = context();
        enabled = false;
        start = true;
        if (cur != null && !cur.getElement().isProcessed()) {
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
                case READY:
                    enabled = true;
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
        SaBatchUI ui = context();
        return enabled && !ui.getElement().getCurrent().isEmpty();
    }

    @Override
    protected void process(SaBatchUI cur) {
        if (start) {
            cur.start(true);
        } else {
            cur.stop();
        }
    }
}
