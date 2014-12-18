/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.actions;

import ec.nbdemetra.tramoseats.TramoDocumentManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.tss.modelling.documents.TramoDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "ec.nbdemetra.tramoseats.actions.OpenTramoDoc")
@ActionRegistration(displayName = "#CTL_OpenTramoDoc")
@ActionReferences({
    @ActionReference(path = TramoDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1590)
})
@NbBundle.Messages("CTL_OpenTramoDoc=Open")
public class OpenTramoDoc implements ActionListener {

    private final WsNode context;

    public OpenTramoDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<TramoDocument> doc = context.getWorkspace().searchDocument(context.lookup(), TramoDocument.class);
        TramoDocumentManager mgr = WorkspaceFactory.getInstance().getManager(TramoDocumentManager.class);
        mgr.openDocument(doc);
    }
}
