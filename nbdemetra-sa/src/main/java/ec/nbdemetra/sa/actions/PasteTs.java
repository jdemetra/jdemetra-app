/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import demetra.bridge.TsConverter;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.nbdemetra.ws.ui.WorkspaceTsTopComponent;
import ec.tss.Ts;
import ec.tss.datatransfer.DataTransfers;
import ec.tss.datatransfer.TssTransferSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "ec.nbdemetra.sa.actions.PasteTs")
@ActionRegistration(displayName = "#CTL_PasteTs", lazy = false)
@ActionReferences({
    @ActionReference(path = WorkspaceFactory.TSCONTEXTPATH, position = 1310),
    @ActionReference(path = "Shortcuts", name = "P")
})
@Messages("CTL_PasteTs=Paste")
public final class PasteTs extends AbstractViewAction<WorkspaceTsTopComponent> {

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
            Ts s = TssTransferSupport.getDefault().toTs(DataTransfers.systemClipboardAsTransferable());
            if (s == null) {
                NotifyDescriptor nd = new NotifyDescriptor.Message("Unable to paste ts");
                DialogDisplayer.getDefault().notify(nd);
            } else {
                top.setTs(TsConverter.toTs(s));
            }
        }
    }
}
