/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.star;

import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.tsproviders.DataSourceNode;
import ec.tss.tsproviders.DataSource;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "File", id = "ec.nbdemetra.ui.star.StarAction")
@ActionRegistration(lazy = false, displayName = "NOT-USED")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 800, separatorBefore = 799),
    @ActionReference(path = DataSourceNode.ACTION_PATH, position = 1210, separatorBefore = 1200)
})
public final class StarAction extends SingleNodeAction<DataSourceNode> {

    public StarAction() {
        super(DataSourceNode.class);
    }

    @Messages({
        "starAction.add=Add star",
        "starAction.remove=Remove star"
    })
    @Override
    protected boolean enable(DataSourceNode activatedNode) {
        putValue(NAME, StarList.getInstance().isStarred(activatedNode.getLookup().lookup(DataSource.class)) ? Bundle.starAction_remove() : Bundle.starAction_add());
        activatedNode.refreshAnnotation();
        return true;
    }

    @Override
    protected void performAction(DataSourceNode activatedNode) {
        StarList.getInstance().toggle(activatedNode.getLookup().lookup(DataSource.class));
    }

    @Override
    public String getName() {
        return StarAction.class.getName();
    }
}
