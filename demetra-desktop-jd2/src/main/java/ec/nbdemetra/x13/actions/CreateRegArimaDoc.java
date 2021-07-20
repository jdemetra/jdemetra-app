/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.actions;

import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.nbdemetra.x13.RegArimaSpecificationManager;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "ec.nbdemetra.x13.actions.CreateRegArimaDoc")
@ActionRegistration(displayName = "#CTL_CreateRegArimaDoc")
@ActionReferences({
    @ActionReference(path = RegArimaSpecificationManager.ITEMPATH, position = 1600, separatorBefore = 1300)
})
@Messages("CTL_CreateRegArimaDoc=Create Document")
public final class CreateRegArimaDoc implements ActionListener {

    private final WsNode context;

    public CreateRegArimaDoc(WsNode context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<RegArimaSpecification> xdoc = context.getWorkspace().searchDocument(context.lookup(), RegArimaSpecification.class);
         if (xdoc == null||xdoc.getElement() == null) {
            return;
        }
        RegArimaSpecificationManager mgr = (RegArimaSpecificationManager) WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        if (mgr != null) {
            mgr.createDocument(context.getWorkspace(), xdoc);
        }
    }
}
