/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.actions;

import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.nbdemetra.x13.X13SpecificationManager;
import ec.satoolkit.x13.X13Specification;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "ec.nbdemetra.x13.actions.CreateX13Doc")
@ActionRegistration(displayName = "#CTL_CreateX13Doc")
@ActionReferences({
    @ActionReference(path = X13SpecificationManager.ITEMPATH, position = 1610, separatorBefore = 1300)
})
@Messages("CTL_CreateX13Doc=Create Document")
public final class CreateX13Doc implements ActionListener {

    private final WsNode context;

    public CreateX13Doc(WsNode context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<X13Specification> xdoc = context.getWorkspace().searchDocument(context.lookup(), X13Specification.class);
         if (xdoc == null||xdoc.getElement() == null) {
            return;
        }
        X13SpecificationManager mgr = (X13SpecificationManager) WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        if (mgr != null) {
            mgr.createDocument(context.getWorkspace(), xdoc);
        }
    }
}
