/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.businesscycle;

import ec.nbdemetra.ws.*;
import ec.nbdemetra.ws.nodes.WsNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "ec.nbdemetra.ws.actions.NewObject")
@ActionRegistration(displayName = "#CTL_NewObject")
@ActionReferences({
    @ActionReference(path = HodrickPrescottManager.PATH, position = 1600, separatorBefore = 1300)
})
@Messages("CTL_NewObject=New")
public class NewObject implements ActionListener {

    private final WsNode context;

    public NewObject(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        IWorkspaceItemManager mgr=WorkspaceFactory.getInstance().getManager(context.lookup());
        if (mgr != null){
            Workspace ws=context.getWorkspace();
            mgr.create(ws);
        }
    }
}
