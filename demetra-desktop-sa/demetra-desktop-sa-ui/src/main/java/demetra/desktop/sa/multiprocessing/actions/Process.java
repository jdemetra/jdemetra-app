/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingController.SaProcessingState;
import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.sa.multiprocessing.ui.SaNode;
import demetra.desktop.ui.ActiveViewAction;
import demetra.sa.SaItem;
import java.awt.Dimension;
import static javax.swing.Action.NAME;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
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
        enabled = false;
        start = true;
        SaBatchUI cur = context();
        if (cur != null) {
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
