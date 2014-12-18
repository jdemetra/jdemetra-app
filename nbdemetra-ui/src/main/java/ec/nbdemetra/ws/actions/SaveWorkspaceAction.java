/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.actions;

import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "File", 
id = "ec.nbdemetra.ws.actions.SaveWorkspaceAction")
@ActionRegistration(displayName = "#CTL_SaveWorkspaceAction", lazy=false)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 300, separatorBefore=299)
})
@Messages("CTL_SaveWorkspaceAction=Save Workspace")
public final class SaveWorkspaceAction extends AbstractAction{ // implements ContextAwareAction{
    
    public SaveWorkspaceAction(){
        super(Bundle.CTL_SaveWorkspaceAction());
        refreshAction();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WorkspaceFactory.getInstance().getActiveWorkspace().save();
    }
    
    @Override
    public boolean isEnabled(){
        refreshAction();
        return super.isEnabled();
    }
    
    public void refreshAction(){
        Workspace activeWorkspace = WorkspaceFactory.getInstance().getActiveWorkspace();
        enabled = activeWorkspace != null && activeWorkspace.isDirty();
    }
  
//    @Override
//    public Action createContextAwareInstance(Lookup lkp) {
//        refreshAction();
//        return this;
//    }

}
