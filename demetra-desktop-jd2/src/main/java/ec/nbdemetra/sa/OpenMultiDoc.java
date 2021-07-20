/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "ec.nbdemetra.sa.OpenMultiDoc")
@ActionRegistration(displayName = "#CTL_OpenMultiDoc")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.ITEMPATH, position = 1600, separatorBefore = 1300)
})
@Messages("CTL_OpenMultiDoc=Open")
public final class OpenMultiDoc implements ActionListener {

    private final WsNode context;

    public OpenMultiDoc(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<MultiProcessingDocument> doc = context.getWorkspace().searchDocument(context.lookup(), MultiProcessingDocument.class);
        MultiProcessingManager mgr=WorkspaceFactory.getInstance().getManager(MultiProcessingManager.class);
        mgr.openDocument(doc);
    }
}
