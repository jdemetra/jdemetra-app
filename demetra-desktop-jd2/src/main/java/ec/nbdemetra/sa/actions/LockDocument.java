/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.nbdemetra.ws.ui.WorkspaceTsTopComponent;
import ec.tss.documents.TsDocument;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.LockDocument")
@ActionRegistration(displayName = "#CTL_LockDocument", lazy = false)
@ActionReferences({
    @ActionReference(path = WorkspaceFactory.TSCONTEXTPATH, position = 1900),
    @ActionReference(path = "Shortcuts", name = "L")
})
@Messages("CTL_LockDocument=Lock")
public final class LockDocument extends AbstractViewAction<WorkspaceTsTopComponent> {
    
    private boolean locked;
    
    public LockDocument(){
        super(WorkspaceTsTopComponent.class);
    }
    
   @Override
    protected void refreshAction() {
        enabled = false;
        locked = false;
        WorkspaceTsTopComponent<TsDocument<?, ?>> cur = context();
        if (cur != null) {
            enabled = true;
            locked = cur.getDocument().getElement().isLocked();
        }
        if (locked) {
            putValue(NAME, "Unlock");
        }
        else {
            putValue(NAME, "Lock");
        }
    }
    
    @Override
    public boolean isEnabled() {
        refreshAction();
        return enabled;
    }
    
    @Override
    protected void process(WorkspaceTsTopComponent cur) {
        WorkspaceTsTopComponent<TsDocument<?, ?>> tcur = cur;
        tcur.getDocument().getElement().setLocked(!locked);
    }
}
