/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.nodes;

import ec.nbdemetra.ui.nodes.BasicChildFactory;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.tstoolkit.utilities.Id;
import java.awt.Image;
import java.util.Arrays;
import java.util.List;
import org.openide.nodes.Node;

/**
 *
 * @author Philippe Charles
 */
public class DummyWsNode extends WsNode {

    public DummyWsNode(Workspace ws, Id id) {
        super(createItems(ws, id), ws, id);
    }

    @Override
    public Image getIcon(int type) {
        return super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return super.getOpenedIcon(type);
    }

    @Deprecated
    static class DummyChildFactory extends BasicChildFactory<Id> {

        final Workspace workspace_;
        final Id managerId;

        public DummyChildFactory(Workspace ws, Id managerId) {
            workspace_ = ws;
            this.managerId = managerId;
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
        protected void tryCreateKeys(List<Id> list) throws Exception {
            list.addAll(Arrays.asList(WorkspaceFactory.getInstance().getTree().children(managerId)));
        }
    }
}
