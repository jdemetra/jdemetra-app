/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.actions;

import ec.nbdemetra.ui.INameable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "ec.nbdemetra.ui.actions.RenameAction")
@ActionRegistration(displayName = "#RenameAction", lazy = false)
@NbBundle.Messages({"RenameAction=Rename..."})
public final class RenameAction extends AbilityAction<INameable> {

    public RenameAction() {
        super(INameable.class);
    }

    @Override
    protected void performAction(Iterable<INameable> items) {
        for (INameable o : items) {
            o.rename();
        }
    }

    @Override
    public String getName() {
        return Bundle.RenameAction();
    }
}
