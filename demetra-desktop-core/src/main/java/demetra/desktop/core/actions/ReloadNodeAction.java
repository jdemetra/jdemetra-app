/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core.actions;

import demetra.desktop.actions.AbilityNodeAction;
import demetra.desktop.actions.Reloadable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

import java.util.stream.Stream;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = ReloadNodeAction.ID)
@ActionRegistration(displayName = "#ReloadNodeAction", lazy = false)
@NbBundle.Messages({"ReloadNodeAction=Reload"})
public final class ReloadNodeAction extends AbilityNodeAction<Reloadable> {

    public static final String ID = "demetra.desktop.core.actions.ReloadNodeAction";

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
