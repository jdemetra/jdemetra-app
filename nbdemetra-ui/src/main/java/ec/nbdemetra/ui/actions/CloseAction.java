/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.actions;

import ec.nbdemetra.ui.tsproviders.DataSourceNode;
import org.netbeans.api.actions.Closable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "File", id = "ec.nbdemetra.ui.actions.CloseAction")
@ActionRegistration(displayName = "#CloseAction", lazy = false)
@ActionReferences({
    @ActionReference(path = DataSourceNode.ACTION_PATH, position = 1330, separatorBefore = 1300)
})
@Messages("CloseAction=Close")
public final class CloseAction extends AbilityAction<Closable> {

    public CloseAction() {
        super(Closable.class);
    }

    @Override
    protected void performAction(Iterable<Closable> items) {
        for (Closable o : items) {
            o.close();
        }
    }

    @Override
    public String getName() {
        return Bundle.CloseAction();
    }
}
