/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.actions;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.ui.ActiveViewManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
id = "ec.nbdemetra.sa.actions.DefaultSpecification")
@ActionRegistration(displayName = "#CTL_DefaultSpecification")
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 1000, separatorAfter=1005),
    @ActionReference(path = "Shortcuts", name = "D")
})
@Messages("CTL_DefaultSpecification=Default specification...")
public final class DefaultSpecification implements ActionListener {

    private final SaBatchUI context;

    public DefaultSpecification() {
        this.context = ActiveViewManager.getInstance().getLookup().lookup(SaBatchUI.class);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (context != null) {
            context.editDefaultSpecification();
        }
    }
}
