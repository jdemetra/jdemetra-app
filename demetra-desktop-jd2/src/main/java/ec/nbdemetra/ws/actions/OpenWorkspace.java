package ec.nbdemetra.ws.actions;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import ec.nbdemetra.ws.WorkspaceFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

//@ActionID(category = "File",
//id = "ec.nbdemetra.ws.actions.OpenWorkspace")
//@ActionRegistration(displayName = "#CTL_OpenWorkspace")
//@ActionReferences({
//    @ActionReference(path = "Menu/File", position = 200)
//})
//@Messages("CTL_OpenWorkspace=Open Workspace")
public final class OpenWorkspace implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        WorkspaceFactory.getInstance().openWorkspace();
    }
}
