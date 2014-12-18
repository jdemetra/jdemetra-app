/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingDocument;
import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.sa.output.OutputPanel;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.satoolkit.ISaSpecification;
import ec.tss.sa.ISaOutputFactory;
import ec.tss.sa.SaItem;
import ec.tss.sa.SaProcessing;
import ec.tss.sa.documents.SaDocument;
import ec.tstoolkit.algorithm.IOutput;
import ec.tstoolkit.utilities.LinearId;
import java.util.List;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.Output")
@ActionRegistration(displayName = "#CTL_Output", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 2000, separatorBefore = 1999),
    @ActionReference(path = "Shortcuts", name = "O")
})
@Messages("CTL_Output=Output...")
public final class Output extends AbstractViewAction<SaBatchUI> {

    public Output() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_Output());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = this.context();
        enabled = ui != null && ui.getCurrentProcessing() != null && ui.getCurrentProcessing().isProcessed();
    }

    @Override
    protected void process(SaBatchUI ui) {
        SaProcessing processing = ui.getCurrentProcessing();
        if (processing == null || !processing.isProcessed()) {
            return;
        }
        final OutputPanel panel = new OutputPanel();

        final DialogDescriptor dd = new DialogDescriptor(panel, "Batch output");

        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            WorkspaceItem<MultiProcessingDocument> doc = ui.getDocument();
            List<ISaOutputFactory> outputs = panel.getFactories();
            LinearId id = new LinearId(doc.getOwner().getName(), doc.getDisplayName());
            for (ISaOutputFactory output : outputs) {
                try {
                    IOutput<SaDocument<ISaSpecification>> sadoc = output.create();
                    sadoc.start(id);
                    for (SaItem cur : processing.toArray()) {
                        SaDocument<ISaSpecification> cdoc = cur.toDocument();
                        sadoc.process(cdoc);
                    }
                    sadoc.end(id);
                    NotifyDescriptor sdesc=new NotifyDescriptor.Message(output.getName()+" successfully generated");
                    DialogDisplayer.getDefault().notify(sdesc);
                } catch (Exception err) {
                    NotifyDescriptor edesc=new NotifyDescriptor.Message("Can't generate output ("+
                            output.getName()+"): "+err.getMessage());
                    DialogDisplayer.getDefault().notify(edesc);
                }
            }
        }
    }
}
