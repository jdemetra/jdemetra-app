/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.actions;

import ec.nbdemetra.tramoseats.TramoSpecificationManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "ec.nbdemetra.tramoseats.actions.CreateTramoDoc")
@ActionRegistration(displayName = "#CTL_CreateTramoDoc")
@ActionReferences({
    @ActionReference(path = TramoSpecificationManager.ITEMPATH, position = 1620, separatorBefore = 1300)
})
@Messages("CTL_CreateTramoDoc=Create Document")
public final class CreateTramoDoc implements ActionListener {

    private final WsNode context;

    public CreateTramoDoc(WsNode context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<TramoSpecification> xdoc = context.getWorkspace().searchDocument(context.lookup(), TramoSpecification.class);
        if (xdoc == null||xdoc.getElement() == null) {
            return;
        }
        TramoSpecificationManager mgr = (TramoSpecificationManager) WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        if (mgr != null) {
            mgr.createDocument(context.getWorkspace(), xdoc);
        }
    }
}

