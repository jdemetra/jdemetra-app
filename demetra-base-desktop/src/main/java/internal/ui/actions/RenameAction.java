/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package internal.ui.actions;

import demetra.ui.actions.AbilityAction;
import java.util.stream.Stream;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import demetra.ui.actions.Renameable;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "ec.nbdemetra.ui.actions.RenameAction")
@ActionRegistration(displayName = "#RenameAction", lazy = false)
@NbBundle.Messages({"RenameAction=Rename..."})
public final class RenameAction extends AbilityAction<Renameable> {

    public RenameAction() {
        super(Renameable.class);
    }

    @Override
    protected void performAction(Stream<Renameable> items) {
        items.forEach(Renameable::rename);
    }

    @Override
    public String getName() {
        return Bundle.RenameAction();
    }
}
