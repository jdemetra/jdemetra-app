/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.ui.actions;

import demetra.desktop.regarima.documents.RegArimaSpecManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.regarima.RegArimaSpec;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "demetra.desktop.regarima.ui.spec.actions.EditRegArimaSpec")
@ActionRegistration(displayName = "#CTL_EditRegArimaSpec")
@ActionReferences({
    @ActionReference(path = RegArimaSpecManager.ITEMPATH, position = 1000, separatorAfter=1090)
})
@NbBundle.Messages("CTL_EditRegArimaSpec=Open")
public class EditRegArimaSpec implements ActionListener {

    private final WsNode context;

    public EditRegArimaSpec(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final WorkspaceItem<RegArimaSpec> xdoc = context.getWorkspace().searchDocument(context.lookup(), RegArimaSpec.class);
        if (xdoc == null || xdoc.getElement() == null) {
            return;
        }
        RegArimaSpecManager mgr = (RegArimaSpecManager) WorkspaceFactory.getInstance().getManager(xdoc.getFamily());
        if (mgr != null) {
            mgr.edit(xdoc);
        }
    }
}
