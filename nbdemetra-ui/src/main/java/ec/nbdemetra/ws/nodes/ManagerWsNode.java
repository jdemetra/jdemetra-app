/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.nodes;

import ec.nbdemetra.ui.nodes.BasicChildFactory;
import ec.nbdemetra.ws.IWorkspaceItemManager;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.utilities.Id;
import java.awt.Image;
import java.util.List;
import javax.swing.Icon;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
public class ManagerWsNode extends WsNode {


    public ManagerWsNode(Workspace ws, Id id) {
        super(createFinalItems(ws, id), ws, id);
    }

    public IWorkspaceItemManager<?> getManager() {
        return WorkspaceFactory.getInstance().getManager(lookup());
    }

    @Override
    public Image getIcon(int type) {
        Icon result = getManager().getManagerIcon();
        return result != null ? ImageUtilities.icon2Image(result) : super.getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        Icon result = getManager().getManagerIcon();
        return result != null ? ImageUtilities.icon2Image(result) : super.getOpenedIcon(type);
    }

    @Deprecated
    static class ManagerChildFactory extends BasicChildFactory<Id> {

        final Workspace workspace_;
        final Id managerId;

        public ManagerChildFactory(Workspace ws, Id managerId) {
            workspace_ = ws;
            this.managerId = managerId;
        }

        @Override
        protected Node createNodeForKey(Id id) {
            return new ItemWsNode(workspace_, id);
        }

        @Override
        protected void tryCreateKeys(List<Id> list) throws Exception {
            for (WorkspaceItem<?> doc : workspace_.searchDocuments(managerId)) {
                list.add(doc.getId());
            }
        }
    }
    
}
