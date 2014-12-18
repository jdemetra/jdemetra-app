/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.actions;

import ec.nbdemetra.ui.IReloadable;
import ec.nbdemetra.ui.tsproviders.CollectionNode;
import ec.nbdemetra.ui.tsproviders.DataSourceNode;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "ec.nbdemetra.ui.actions.ReloadAction")
@ActionRegistration(displayName = "#ReloadAction", lazy = false)
@ActionReferences({
    @ActionReference(path = DataSourceNode.ACTION_PATH, position = 1340, separatorBefore = 1300),
    @ActionReference(path = CollectionNode.ACTION_PATH, position = 1340, separatorBefore = 1300)
})
@NbBundle.Messages({"ReloadAction=Reload"})
public final class ReloadAction extends AbilityAction<IReloadable> {

    public ReloadAction() {
        super(IReloadable.class);
    }

    @Override
    protected void performAction(Iterable<IReloadable> items) {
        for (IReloadable o : items) {
            o.reload();
        }
    }

    @Override
    public String getName() {
        return Bundle.ReloadAction();
    }
}
