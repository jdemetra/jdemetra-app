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
import demetra.ui.actions.Reloadable;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "ec.nbdemetra.ui.actions.ReloadAction")
@ActionRegistration(displayName = "#ReloadAction", lazy = false)
@NbBundle.Messages({"ReloadAction=Reload"})
public final class ReloadAction extends AbilityAction<Reloadable> {

    public ReloadAction() {
        super(Reloadable.class);
    }

    @Override
    protected void performAction(Stream<Reloadable> items) {
        items.forEach(Reloadable::reload);
    }

    @Override
    public String getName() {
        return Bundle.ReloadAction();
    }
}
