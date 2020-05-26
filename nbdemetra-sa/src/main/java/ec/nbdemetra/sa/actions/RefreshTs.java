/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import demetra.bridge.TsConverter;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.nbdemetra.ws.ui.WorkspaceTsTopComponent;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "ec.nbdemetra.sa.actions.RefreshTs")
@ActionRegistration(displayName = "#CTL_RefreshTs", lazy = false)
@ActionReferences({
    @ActionReference(path = WorkspaceFactory.TSCONTEXTPATH, position = 1310)
    ,
    @ActionReference(path = "Shortcuts", name = "R")
})
@Messages("CTL_RefreshTs=Refresh Data")
public final class RefreshTs extends AbstractViewAction<WorkspaceTsTopComponent> {

    public RefreshTs() {
        super(WorkspaceTsTopComponent.class);
        putValue(NAME, Bundle.CTL_RefreshTs());
        refreshAction();
    }

    @Override
    protected void refreshAction() {
        WorkspaceTsTopComponent top = context();
        enabled = top != null && top.getTs() != null && TsConverter.fromTs(top.getTs()).isFrozen();
    }

    @Override
    protected void process(WorkspaceTsTopComponent cur) {
        WorkspaceTsTopComponent top = context();
        if (top != null) {
            demetra.timeseries.Ts s = top.getTs();
            top.setTs(TsConverter.toTs(TsConverter.fromTs(s).unfreeze()));
        }
    }
}
