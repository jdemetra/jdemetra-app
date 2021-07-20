/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.variables.actions;

import ec.nbdemetra.ui.variables.VariablesDocumentManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.nodes.WsNode;
import ec.tstoolkit.timeseries.regression.TsVariables;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Tools",
id = "ec.nbdemetra.variables.actions.OpenAction")
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
        WorkspaceItem<TsVariables> doc = context.getWorkspace().searchDocument(context.lookup(), TsVariables.class);
        VariablesDocumentManager mgr = WorkspaceFactory.getInstance().getManager(VariablesDocumentManager.class);
        mgr.openDocument(doc);
    }
}
