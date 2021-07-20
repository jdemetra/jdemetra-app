/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.actions;

import ec.nbdemetra.tramoseats.TramoSeatsSpecificationManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "ec.nbdemetra.tramoseats.actions.CreateTramoSeatsDoc")
@ActionRegistration(displayName = "#CTL_CreateTramoSeatsDoc")
@ActionReferences({
    @ActionReference(path = TramoSeatsSpecificationManager.ITEMPATH, position = 1620, separatorBefore = 1300)
})
@Messages("CTL_CreateTramoSeatsDoc=Create Document")
public final class CreateTramoSeatsDoc implements ActionListener {

    private final WsNode context;

    public CreateTramoSeatsDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<TramoSeatsSpecification> xdoc = context.getWorkspace().searchDocument(context.lookup(), TramoSeatsSpecification.class);
        if (xdoc == null||xdoc.getElement() == null) {
            return;
        }
        TramoSeatsSpecificationManager mgr = WorkspaceFactory.getInstance().getManager(TramoSeatsSpecificationManager.class);
        if (mgr != null) {
            mgr.createDocument(context.getWorkspace(), xdoc);
        }
    }
}
