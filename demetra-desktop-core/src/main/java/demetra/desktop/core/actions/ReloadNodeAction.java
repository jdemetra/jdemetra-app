/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core.actions;

import demetra.ui.actions.AbilityNodeAction;
import java.util.stream.Stream;

import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import demetra.ui.actions.Reloadable;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "demetra.desktop.core.actions.ReloadNodeAction")
@ActionRegistration(displayName = "#ReloadNodeAction", lazy = false)
@NbBundle.Messages({"ReloadNodeAction=Reload"})
public final class ReloadNodeAction extends AbilityNodeAction<Reloadable> {

    public ReloadNodeAction() {
        super(Reloadable.class);
    }

    @Override
    protected void performAction(Stream<Reloadable> items) {
        items.forEach(Reloadable::reload);
    }

    @Override
    public String getName() {
        return Bundle.ReloadNodeAction();
    }
}
