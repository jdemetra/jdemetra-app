/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.actions;

import ec.nbdemetra.ws.WorkspaceFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "File",
id = "ec.nbdemetra.ws.actions.SaveWorkspaceAs")
@ActionRegistration(displayName = "#CTL_SaveWorkspaceAs")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 310)
})
@Messages("CTL_SaveWorkspaceAs=Save Workspace As...")
public final class SaveWorkspaceAs implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
         WorkspaceFactory.getInstance().getActiveWorkspace().saveAs();
    }
}
