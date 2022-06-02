/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core.ts.actions;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import demetra.desktop.datatransfer.DataTransferManager;
import demetra.desktop.datatransfer.DataTransfers;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.ui.ActiveViewAction;
import demetra.desktop.workspace.ui.WorkspaceTsTopComponent;
import java.util.Optional;

@ActionID(category = "Processing",
        id = "demetra.desktop.core.ts.actions.PasteTs")
@ActionRegistration(displayName = "#CTL_PasteTs", lazy = false)
@ActionReferences({
    @ActionReference(path = WorkspaceFactory.TSCONTEXTPATH, position = 1310),
    @ActionReference(path = "Shortcuts", name = "P")
})
@Messages("CTL_PasteTs=Paste")
public final class PasteTs extends ActiveViewAction<WorkspaceTsTopComponent> {

    public PasteTs() {
        super(WorkspaceTsTopComponent.class);
        putValue(NAME, Bundle.CTL_PasteTs());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
    }

    @Override
    protected void process(WorkspaceTsTopComponent cur) {
        WorkspaceTsTopComponent top = context();
        if (top != null) {
            Optional<demetra.timeseries.Ts> s = DataTransferManager.get().toTs(DataTransfers.systemClipboardAsTransferable());
            if (!s.isPresent()) {
                NotifyDescriptor nd = new NotifyDescriptor.Message("Unable to paste ts");
                DialogDisplayer.getDefault().notify(nd);
            } else {
                top.setTs(s.get());
            }
        }
    }
}
