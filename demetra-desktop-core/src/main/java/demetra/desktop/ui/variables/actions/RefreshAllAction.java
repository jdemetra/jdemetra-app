/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.variables.actions;

import demetra.desktop.ui.variables.VariablesDocumentManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.timeseries.DynamicTsDataSupplier;
import demetra.timeseries.TsDataSupplier;
import demetra.timeseries.regression.TsDataSuppliers;
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
id = "demetra.desktop.ui.variables.actions.RefreshAllAction")
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

        List<WorkspaceItem<TsDataSuppliers>> documents = WorkspaceFactory.getInstance().getActiveWorkspace().searchDocuments(TsDataSuppliers.class);
        for (WorkspaceItem<TsDataSuppliers> document : documents) {
            for (TsDataSupplier var : document.getElement().variables()) {
                if (var instanceof DynamicTsDataSupplier dvar) {
                    dvar.refresh();
                }
            }
        }
    }
    
    public static final String WARNING_MESSAGE = "Refreshing variables may modify some estimations. Are you sure you want to continue?";
}
