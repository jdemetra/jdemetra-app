/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.variables.actions;

import demetra.desktop.ui.variables.VariablesDocumentManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.timeseries.regression.TsDataSuppliers;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Edit",
id = "demetra.desktop.ui.variables.actions.OpenAction")
@ActionRegistration(displayName = "#CTL_OpenAction")
@ActionReferences({
    @ActionReference(path = VariablesDocumentManager.ITEMPATH, position = 1600, separatorBefore = 1590)
})
@NbBundle.Messages("CTL_OpenAction=Open")
public class OpenAction implements ActionListener {

    private final WsNode context;

    public OpenAction(WsNode context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        WorkspaceItem<TsDataSuppliers> doc = context.getWorkspace().searchDocument(context.lookup(), TsDataSuppliers.class);
        VariablesDocumentManager mgr = WorkspaceFactory.getInstance().getManager(VariablesDocumentManager.class);
        mgr.openDocument(doc);
    }
}
