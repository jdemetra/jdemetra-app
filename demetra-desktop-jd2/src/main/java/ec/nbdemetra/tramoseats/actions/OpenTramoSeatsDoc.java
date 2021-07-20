/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.actions;

import ec.nbdemetra.tramoseats.TramoSeatsDocumentManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.tss.sa.documents.TramoSeatsDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "ec.nbdemetra.tramoseats.actions.OpenTramoSeatsDoc")
@ActionRegistration(displayName = "#CTL_OpenTramoSeatsDoc")
@ActionReferences({
    @ActionReference(path = TramoSeatsDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1590)
})
@NbBundle.Messages("CTL_OpenTramoSeatsDoc=Open")
public class OpenTramoSeatsDoc implements ActionListener {

    private final WsNode context;

    public OpenTramoSeatsDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<TramoSeatsDocument> doc = context.getWorkspace().searchDocument(context.lookup(), TramoSeatsDocument.class);
        TramoSeatsDocumentManager mgr = WorkspaceFactory.getInstance().getManager(TramoSeatsDocumentManager.class);
        mgr.openDocument(doc);
    }
}
