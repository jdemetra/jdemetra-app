/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.variables.actions;

import ec.nbdemetra.ui.variables.VariablesDocumentManager;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.timeseries.regression.ITsVariable;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.utilities.IDynamicObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Tools",
id = "ec.nbdemetra.ui.variables.actions.RefreshAllAction")
@ActionRegistration(displayName = "#CTL_RefreshAllAction")
@ActionReferences({
    @ActionReference(path = VariablesDocumentManager.PATH, position = 1700, separatorBefore = 1699)
})
@Messages("CTL_RefreshAllAction=Refresh all")
public final class RefreshAllAction implements ActionListener {

    public RefreshAllAction() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Warning
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(WARNING_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
        if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
            return;
        }

        List<WorkspaceItem<TsVariables>> documents = WorkspaceFactory.getInstance().getActiveWorkspace().searchDocuments(TsVariables.class);
        for (WorkspaceItem<TsVariables> document : documents) {
            for (ITsVariable var : document.getElement().variables()) {
                if (var instanceof IDynamicObject) {
                    IDynamicObject dvar = (IDynamicObject) var;
                    dvar.refresh();
                }
            }
        }
    }
    
    public static final String WARNING_MESSAGE = "Refreshing variables may modify some estimations. Are you sure you want to continue?";
}
