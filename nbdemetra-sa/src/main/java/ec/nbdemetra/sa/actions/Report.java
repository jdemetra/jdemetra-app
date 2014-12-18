/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.ISaReportFactory;
import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.ReportSelectionDialog;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.sa.SaReportManager;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.tss.sa.SaProcessing;
import java.util.List;

import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "ec.nbdemetra.sa.actions.Report")
@ActionRegistration(displayName = "#CTL_Report", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 2010, separatorBefore = 1999),
    @ActionReference(path = "Shortcuts", name = "R")
})
@Messages("CTL_Report=Report...")
public final class Report extends AbstractViewAction<SaBatchUI> {

    public Report() {
        super(SaBatchUI.class);
        putValue(NAME, Bundle.CTL_Report());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = this.context();
        enabled = ui != null && ui.getCurrentProcessing() != null && ui.getCurrentProcessing().isProcessed()
                && !SaReportManager.getInstance().getFactories().isEmpty()
                && !ui.isTableEmpty();
    }

    @Override
    protected void process(SaBatchUI ui) {
        SaProcessing processing = ui.getCurrentProcessing();
        if (processing == null || !processing.isProcessed()) {
            return;
        }

        List<ISaReportFactory> factories = SaReportManager.getInstance().getFactories();
        if (factories.isEmpty()) {
            return;
        }
        if (factories.size() == 1) {
            factories.get(0).createReport(processing);
        } else {
            ReportSelectionDialog dlg = new ReportSelectionDialog();
            dlg.setVisible(true);
            dlg.setLocationRelativeTo(null);
            
            ISaReportFactory reportFactory = dlg.getReportFactory();

            if (reportFactory != null) {
                reportFactory.createReport(processing);
            }
        }
    }
}
