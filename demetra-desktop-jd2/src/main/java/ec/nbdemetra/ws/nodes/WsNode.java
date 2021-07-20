/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.nodes;

import ec.nbdemetra.ui.nodes.BasicChildFactory;
import ec.nbdemetra.ui.nodes.BasicNode;
import ec.nbdemetra.ui.nodes.IdNodes;
import ec.nbdemetra.ws.Workspace;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tstoolkit.utilities.Id;
import java.awt.Image;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Jean Palate
 */
public abstract class WsNode extends BasicNode<Id> {

    public static boolean isManager(Id id) {
        return WorkspaceFactory.getInstance().getManager(id) != null;
    }

    public static Children createChildren(Workspace ws, Id id) {
        if (id == null) {
            return createItems(ws);
        }
        if (isManager(id)) {
            return createFinalItems(ws, id);
        } else {
            return createItems(ws, id);
        }
    }

    static Children createFinalItems(Workspace ws, Id managerId) {
        List<WorkspaceItem<?>> items = ws.searchDocuments(managerId);
        Node[] nodes = new Node[items.size()];
        int n = 0;
        for (WorkspaceItem<?> doc : items) {
            nodes[n++] = new ItemWsNode(ws, doc.getId());
        }
        Children.Array children = new Children.Array();
        children.add(nodes);
        return children;
    }

    static Children createItems(Workspace ws) {
        Node[] nodes = roots(ws);
        Children.Array children = new Children.Array();
        children.add(nodes);
        return children;
    }

    static Node[] roots(Workspace ws) {
        Id[] nroots = WorkspaceFactory.getInstance().getTree().roots();
        Node[] nodes = new Node[nroots.length];
        for (int i = 0; i < nroots.length; ++i) {
            if (ManagerWsNode.isManager(nroots[i])) {
                nodes[i] = new ManagerWsNode(ws, nroots[i]);
            } else {
                nodes[i] = new DummyWsNode(ws, nroots[i]);
            }
        }
        return nodes;
    }

    static Children createItems(Workspace ws, Id id) {
        Node[] nodes = items(ws, id);
        Children.Array children = new Children.Array();
        children.add(nodes);
        return children;
    }

    public static Node[] items(Workspace ws, Id id) {
        Id[] nroots = WorkspaceFactory.getInstance().getTree().children(id);
        Node[] nodes = new Node[nroots.length];
        for (int i = 0; i < nroots.length; ++i) {
            if (ManagerWsNode.isManager(nroots[i])) {
                nodes[i] = new ManagerWsNode(ws, nroots[i]);
            } else {
                nodes[i] = new DummyWsNode(ws, nroots[i]);
            }
        }
        return nodes;
    }

    protected final Workspace workspace_;

    public WsNode(Children children, Workspace ws, Id id) {
        super(children, id, WorkspaceFactory.getInstance().getActionsPath(id));
        workspace_ = ws;
    }

    public WsNode(BasicChildFactory<?> factory, Workspace ws, Id id) {
        super(factory, id, WorkspaceFactory.getInstance().getActionsPath(id));
        workspace_ = ws;
    }

    public void updateUI() {
        this.fireDisplayNameChange(null, lookup().tail());
    }

    public void updatePropertySheet() {
        setSheet(createSheet());
    }

    @Override
    public String getDisplayName() {
        return lookup().tail();
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.icon2Image(new IdNodes.IdIcon(lookup()));
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    public Workspace getWorkspace() {
        return workspace_;
    }
}
