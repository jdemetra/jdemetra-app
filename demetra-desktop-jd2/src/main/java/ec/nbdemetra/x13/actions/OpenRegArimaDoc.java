/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.actions;

import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.nbdemetra.x13.RegArimaDocumentManager;
import ec.tss.modelling.documents.RegArimaDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "ec.nbdemetra.x13.actions.OpenRegArimaDoc")
@ActionRegistration(displayName = "#CTL_OpenRegArimaDoc")
@ActionReferences({
    @ActionReference(path = RegArimaDocumentManager.ITEMPATH, position = 1000, separatorAfter=1090)
})
@NbBundle.Messages("CTL_OpenRegArimaDoc=Open")
public class OpenRegArimaDoc implements ActionListener {

    private final WsNode context;

    public OpenRegArimaDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<RegArimaDocument> doc = context.getWorkspace().searchDocument(context.lookup(), RegArimaDocument.class);
        RegArimaDocumentManager mgr = WorkspaceFactory.getInstance().getManager(RegArimaDocumentManager.class);
        mgr.openDocument(doc);
    }
}
