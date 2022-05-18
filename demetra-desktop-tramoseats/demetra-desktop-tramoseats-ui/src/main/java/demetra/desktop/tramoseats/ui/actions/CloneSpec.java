/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.ui.actions;

import demetra.desktop.tramoseats.documents.TramoSeatsSpecManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.tramoseats.TramoSeatsSpec;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "demetra.desktop.tramoseats.ui.actions.CloneSpec")
@ActionRegistration(displayName = "#CTL_CloneSpec")
@ActionReferences({
    @ActionReference(path = TramoSeatsSpecManager.ITEMPATH, position = 1700),
})
@Messages("CTL_CloneSpec=Clone")
public final class CloneSpec implements ActionListener {
    
   private final WsNode context;

    public CloneSpec(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
         WorkspaceItem<TramoSeatsSpec> xdoc = 
                 context.getWorkspace().searchDocument(context.lookup(), TramoSeatsSpec.class);
        if (xdoc == null) {
            return;
        }
        WorkspaceItemManager mgr=WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        WorkspaceItem<TramoSeatsSpec> ndoc = WorkspaceItem.newItem(xdoc.getFamily(), mgr.getNextItemName(null), xdoc.getElement());
        context.getWorkspace().add(ndoc);
    }
}
