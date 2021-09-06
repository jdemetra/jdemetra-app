/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core.actions;

import demetra.desktop.actions.AbilityNodeAction;
import org.netbeans.api.actions.Closable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

import java.util.stream.Stream;

@ActionID(category = "File", id = CloseNodeAction.ID)
@ActionRegistration(displayName = "#CloseNodeAction", lazy = false)
@Messages("CloseNodeAction=Close")
public final class CloseNodeAction extends AbilityNodeAction<Closable> {

    public static final String ID = "demetra.desktop.core.actions.CloseNodeAction";

    public CloseNodeAction() {
        super(Closable.class);
    }

    @Override
    protected void performAction(Stream<Closable> items) {
        items.forEach(Closable::close);
    }

    @Override
    public String getName() {
        return Bundle.CloseNodeAction();
    }
}
