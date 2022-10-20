/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.ui.ActiveViewAction;
import demetra.sa.EstimationPolicyType;
import static javax.swing.Action.NAME;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "SaProcessing",
id = "demetra.desktop.sa.multiprocessing.actions.LocalRefreshMovingAverageParameters")
@ActionRegistration(displayName = "#CTL_LocalRefreshMovingAverageParameters", lazy=true)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.LOCALPATH+LocalRefreshPartial.PATH, position = 1230)
})
@NbBundle.Messages("CTL_LocalRefreshMovingAverageParameters=+ Moving average parameters")
public final class LocalRefreshMovingAverageParameters extends ActiveViewAction<SaBatchUI> {

    public LocalRefreshMovingAverageParameters() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_LocalRefreshMovingAverageParameters());
    }

    @Override
    protected void process(SaBatchUI ui) {
        ui.refresh(EstimationPolicyType.FixedAutoRegressiveParameters, true, false);
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && !ui.getElement().isNew() && ui.getSelectionCount() > 0;
    }
}