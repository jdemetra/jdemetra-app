/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.ui.actions;

import demetra.desktop.tramo.ui.TramoSpecManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.tramo.TramoSpec;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "demetra.desktop.tramo.ui.actions.EditTramoSpec")
@ActionRegistration(displayName = "#CTL_EditTramoSpec")
@ActionReferences({
    @ActionReference(path = TramoSpecManager.ITEMPATH, position = 1000, separatorAfter=1090)
})
@NbBundle.Messages("CTL_EditTramoSpec=Open")
public class EditTramoSpec implements ActionListener {

    private final WsNode context;

    public EditTramoSpec(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<TramoSpec> xdoc = context.getWorkspace().searchDocument(context.lookup(), TramoSpec.class);
        if (xdoc == null || xdoc.getElement() == null) {
            return;
        }
        TramoSpecManager mgr = (TramoSpecManager) WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        if (mgr != null) {
            mgr.edit(xdoc);
        }
    }
}
