/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.ui.ActiveViewAction;
import demetra.sa.EstimationPolicyType;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "SaProcessing",
        id = "demetra.desktop.sa.multiprocessing.actions.LocalRefreshCurrent")
@ActionRegistration(displayName = "#CTL_LocalRefreshCurrent", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.LOCALPATH + LocalRefresh.PATH, position = 1201)
})
@NbBundle.Messages("CTL_LocalRefreshCurrent=Current [AO]")
public final class LocalRefreshCurrent extends ActiveViewAction<SaBatchUI> {

    public LocalRefreshCurrent() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_LocalRefreshCurrent());
    }

    @Override
    protected void process(SaBatchUI ui) {
        ui.refresh(EstimationPolicyType.Current, true, false);
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getElement().isRefreshable() && ui.getSelectionCount() > 0;
    }
}
