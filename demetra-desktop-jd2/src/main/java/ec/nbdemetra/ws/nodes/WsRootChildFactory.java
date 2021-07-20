/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.nodes;

import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.tstoolkit.utilities.Id;
import java.util.Arrays;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Philippe Charles
 */
public class WsRootChildFactory extends ChildFactory<Id> {

    private final Workspace workspace_;

    public WsRootChildFactory(Workspace ws) {
        workspace_ = ws;
    }

    @Override
    protected Node createNodeForKey(Id id) {
        if (ManagerWsNode.isManager(id)) {
            return new ManagerWsNode(workspace_, id);
        } else {
            return new DummyWsNode(workspace_, id);
        }
    }

    @Override
    protected boolean createKeys(List<Id> list) {
        list.addAll(Arrays.asList(WorkspaceFactory.getInstance().getTree().roots()));
        return true;
    }

}
