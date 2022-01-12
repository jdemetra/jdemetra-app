/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.ui;

import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.nodes.ItemWsNode;
import demetra.desktop.workspace.nodes.WsNode;
import demetra.desktop.workspace.nodes.WsRootNode;
import demetra.util.Id;
import javax.swing.ActionMap;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jean Palate
 */
@ConvertAsProperties(dtd = "-//demetra.desktop.workspace.ui//Workspace//EN",
        autostore = false)
@TopComponent.Description(preferredID = "WorkspaceTopComponent",
        iconBase = "ec/nbdemetra/ui/table.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "demetra.desktop.workspace.ui.DemetraWsUI")
@ActionReference(path = "Menu/Window" /*
         * , position = 444
         */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_WsAction",
        preferredID = "WorkspaceTopComponent")
@NbBundle.Messages({
    "CTL_WsAction=Workspace",
    "CTL_WsTopComponent=Workspace",
    "HINT_WsTopComponent=This is a Workspace window"
})
public class DemetraWsUI extends TopComponent implements ExplorerManager.Provider, LookupListener {

    private final ExplorerManager mgr = new ExplorerManager();
    private final InstanceContent content = new InstanceContent();
    private final Lookup.Result<WorkspaceFactory.Event> result;

    public DemetraWsUI() {
        initComponents();
        setName(Bundle.CTL_WsTopComponent());
        setToolTipText(Bundle.HINT_WsTopComponent());

        ((BeanTreeView) jScrollPane1).setRootVisible(true);
        ((BeanTreeView) jScrollPane1).setDropTarget(true);

        ActionMap map = this.getActionMap();
        map.put("delete", ExplorerUtils.actionDelete(mgr, true));
        associateLookup(ExplorerUtils.createLookup(mgr, map));
        result = WorkspaceFactory.getInstance().getLookup().lookupResult(WorkspaceFactory.Event.class);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.openide.explorer.view.BeanTreeView tree = new org.openide.explorer.view.BeanTreeView();
        jScrollPane1 = tree;

        setLayout(new java.awt.BorderLayout());
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        result.addLookupListener(this);
        //mgr.setRootContext(new AbstractNode(Children.create(new RootChildFactory(), true)));
        mgr.setRootContext(new WsRootNode(WorkspaceFactory.getInstance().getActiveWorkspace()));
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
        mgr.setRootContext(new AbstractNode(Children.LEAF));
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    @Override
    public void resultChanged(final LookupEvent le) {
        for (WorkspaceFactory.Event ev : result.allInstances()) {
            switch (ev.info) {
                case WorkspaceFactory.Event.NEW:
                    processNew(ev);
                    break;
                case WorkspaceFactory.Event.OPEN:
                    processOpen(ev);
                    break;
                case WorkspaceFactory.Event.SAVE:
                    processSave(ev);
                    break;
                case WorkspaceFactory.Event.SAVEAS:
                    processSaveAs(ev);
                    break;
                case WorkspaceFactory.Event.ITEMADDED:
                    processItemAdded(ev);
                    break;
                case WorkspaceFactory.Event.REMOVINGITEM:
                    processRemovingItem(ev);
                    break;
                case WorkspaceFactory.Event.ITEMRENAMED:
                case WorkspaceFactory.Event.ITEMCOMMENTS:
                    refreshItem(ev.id);
                    break;
                default:
                    mgr.setRootContext(new WsRootNode(WorkspaceFactory.getInstance().getActiveWorkspace()));
                    break;
            }
        }
    }

    private void refreshItem(Id id) {
        WsNode node = search(id);
        if (node != null) {
            node.updateUI();
        }
    }

    private void processSave(WorkspaceFactory.Event ev) {
//        MruList.getWorkspacesInstance().add(ev.workspace.getSourceId());
    }

    private void processSaveAs(WorkspaceFactory.Event ev) {
//        MruList.getWorkspacesInstance().add(ev.workspace.getSourceId());
        mgr.getRootContext().setDisplayName(ev.workspace.getName());
    }

    private void processNew(WorkspaceFactory.Event ev) {
        mgr.setRootContext(new WsRootNode(ev.workspace));
    }

    private void processOpen(WorkspaceFactory.Event ev) {
        mgr.setRootContext(new WsRootNode(ev.workspace));
//       MruList.getWorkspacesInstance().add(ev.workspace.getSourceId());
    }

    private void processItemAdded(WorkspaceFactory.Event ev) {
        Node managerNode = search(ev.id.parent());
        if (managerNode != null) {
            managerNode.getChildren().add(new Node[]{new ItemWsNode(ev.workspace, ev.id)});
        }
    }

    private void processRemovingItem(WorkspaceFactory.Event ev) {
        ItemWsNode itemNode = (ItemWsNode) search(ev.id);
        if (itemNode != null) {
            Node parent = itemNode.getParentNode();
            if (parent != null) {
                parent.getChildren().remove(new Node[]{itemNode});
            }
        }
    }

    private WsNode search(Id id) {
        Node node = mgr.getRootContext();
        return search(id, node);
    }

    private WsNode search(Id id, Node node) {
        if (node instanceof WsNode) {
            WsNode wnode = (WsNode) node;
            if (wnode.lookup().equals(id)) {
                return wnode;
            }
        }
        for (Node child : node.getChildren().snapshot()) {
            WsNode wnode = search(id, child);
            if (wnode != null) {
                return wnode;
            }
        }
        return null;
    }
}
