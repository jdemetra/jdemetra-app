/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.ui.actions;

import demetra.desktop.tramoseats.ui.TramoSeatsSpecManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.tramoseats.TramoSeatsSpec;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "demetra.desktop.tramoseats.ui.actions.EditTramoSeatsSpec")
@ActionRegistration(displayName = "#CTL_EditTramoSeatsSpec")
@ActionReferences({
    @ActionReference(path = TramoSeatsSpecManager.ITEMPATH, position = 1000, separatorAfter=1090)
})
@NbBundle.Messages("CTL_EditTramoSeatsSpec=Open")
public class EditTramoSeatsSpec implements ActionListener {

    private final WsNode context;

    public EditTramoSeatsSpec(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<TramoSeatsSpec> xdoc = context.getWorkspace().searchDocument(context.lookup(), TramoSeatsSpec.class);
        if (xdoc == null || xdoc.getElement() == null) {
            return;
        }
        TramoSeatsSpecManager mgr = (TramoSeatsSpecManager) WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        if (mgr != null) {
            mgr.edit(xdoc);
        }
    }
}
