/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.x13.ui.actions;

import demetra.desktop.x13.documents.X13SpecManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.x13.X13Spec;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "demetra.desktop.x13.ui.actions.EditX13Spec")
@ActionRegistration(displayName = "#CTL_EditX13Spec")
@ActionReferences({
    @ActionReference(path = X13SpecManager.ITEMPATH, position = 1000, separatorAfter=1090)
})
@NbBundle.Messages("CTL_EditX13Spec=Open")
public class EditX13Spec implements ActionListener {

    private final WsNode context;

    public EditX13Spec(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<X13Spec> xdoc = context.getWorkspace().searchDocument(context.lookup(), X13Spec.class);
        if (xdoc == null || xdoc.getElement() == null) {
            return;
        }
        X13SpecManager mgr = (X13SpecManager) WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        if (mgr != null) {
            mgr.edit(xdoc);
        }
    }
}
