/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.ui.actions;

import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.desktop.tramo.ui.TramoDocumentManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jdplus.tramo.TramoDocument;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "demetra.desktop.tramo.ui.actions.OpenTramoDoc")
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
