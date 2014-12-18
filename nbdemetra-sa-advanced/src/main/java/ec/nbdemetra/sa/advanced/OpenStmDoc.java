/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced;

import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.tss.sa.documents.StmDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "ec.nbdemetra.sa.advanced.OpenStmDoc")
@ActionRegistration(displayName = "#CTL_OpenStmDoc")
@ActionReferences({
    @ActionReference(path = StmDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1300)
 })
@NbBundle.Messages("CTL_OpenStmDoc=Open")
public class OpenStmDoc implements ActionListener {

    private final WsNode context;

    public OpenStmDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<StmDocument> doc = context.getWorkspace().searchDocument(context.lookup(), StmDocument.class);
        StmDocumentManager manager = WorkspaceFactory.getInstance().getManager(StmDocumentManager.class);
        manager.openDocument(doc);
    }
}
