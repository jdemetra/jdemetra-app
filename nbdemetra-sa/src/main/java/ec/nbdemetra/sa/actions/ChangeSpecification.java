/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.nbdemetra.ws.ui.SpecSelectionComponent;
import ec.satoolkit.ISaSpecification;
import ec.tss.sa.SaItem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "ec.nbdemetra.sa.actions.ChangeSpecification")
@ActionRegistration(displayName = "#CTL_ChangeSpecification", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1400),
    @ActionReference(path = MultiProcessingManager.LOCALPATH, position = 1400)
})
@Messages("CTL_ChangeSpecification=Specification...")
public final class ChangeSpecification extends AbstractViewAction<SaBatchUI> {

    public ChangeSpecification() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_ChangeSpecification());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() > 0;
    }

    @Override
    protected void process(SaBatchUI cur) {
        cur.stop();
        SaItem[] selection = cur.getSelection();
        ISaSpecification spec = null;
        // find unique spec
        for (SaItem o : selection) {
            if (spec == null) {
                spec = o.getDomainSpecification();
            } else if (!spec.equals(o.getDomainSpecification())) {
                spec = null;
                break;
            }
        }
        SpecSelectionComponent c = new SpecSelectionComponent();
        c.setSpecification(spec);
        DialogDescriptor dd = c.createDialogDescriptor("Choose active specification");
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            for (int i = 0; i < selection.length; ++i) {
                SaItem o = selection[i];
                SaItem n = new SaItem(c.getSpecification(), o.getTs());
                n.setMetaData(o.getMetaData());
                cur.getCurrentProcessing().replace(o, n);
            }
            cur.redrawAll();
        }
    }
}
