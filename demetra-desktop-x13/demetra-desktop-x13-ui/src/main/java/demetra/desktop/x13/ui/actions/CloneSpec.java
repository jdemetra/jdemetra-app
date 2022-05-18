/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.x13.ui.actions;

import demetra.desktop.x13.documents.X13SpecManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.x13.X13Spec;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "demetra.desktop.x13.ui.actions.CloneSpec")
@ActionRegistration(displayName = "#CTL_CloneSpec")
@ActionReferences({
    @ActionReference(path = X13SpecManager.ITEMPATH, position = 1700),
})
@Messages("CTL_CloneSpec=Clone")
public final class CloneSpec implements ActionListener {
    
   private final WsNode context;

    public CloneSpec(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
         WorkspaceItem<X13Spec> xdoc = 
                 context.getWorkspace().searchDocument(context.lookup(), X13Spec.class);
        if (xdoc == null) {
            return;
        }
        WorkspaceItemManager mgr=WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        WorkspaceItem<X13Spec> ndoc = WorkspaceItem.newItem(xdoc.getFamily(), mgr.getNextItemName(null), xdoc.getElement());
        context.getWorkspace().add(ndoc);
    }
}
