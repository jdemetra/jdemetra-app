/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.ui;

import ec.nbdemetra.ui.ActiveViewManager;
import ec.nbdemetra.ui.IActiveView;
import ec.nbdemetra.ui.Menus;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jean Palate
 */
public abstract class WorkspaceTopComponent<T> extends TopComponent implements ExplorerManager.Provider, IActiveView, LookupListener {

    private final WorkspaceItem<T> doc;
    protected Lookup.Result<WorkspaceFactory.Event> result;

    protected abstract String getContextPath();

    protected WorkspaceTopComponent(WorkspaceItem<T> doc) {
        this.doc = doc;
        result = WorkspaceFactory.getInstance().getLookup().lookupResult(WorkspaceFactory.Event.class);
        associateLookup(ExplorerUtils.createLookup(ActiveViewManager.getInstance().getExplorerManager(), getActionMap()));
    }

    public WorkspaceItem<T> getDocument() {
        return doc;
    }

    @Override
    public String getName(){
        return doc == null ? super.getName() : doc.getDisplayName();
    }

    @Override
    public boolean hasContextMenu() {
        return true;
    }

    @Override
    public Node getNode() {
        return null;
    }

    @Override
    public Action[] getActions() {
        return Menus.createActions(super.getActions(), getContextPath());
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return ActiveViewManager.getInstance().getExplorerManager();
    }

    @Override
    public boolean fill(JMenu menu) {
        if (doc != null) {
            Menus.fillMenu(menu, getContextPath());
        }
        return true;
    }

    @Override
    public void componentOpened() {
        result.addLookupListener(this);
        if (doc != null && doc.getView() == null) {
            doc.setView(this);
        }
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        if (doc != null) {
            doc.setView(null);
        }
        result.removeLookupListener(this);
    }

    public void refresh() {
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (doc == null) {
            return;
        }
        Collection<? extends WorkspaceFactory.Event> all = result.allInstances();
        if (!all.isEmpty()) {
            for (WorkspaceFactory.Event ev : all) {
                if (ev.info == WorkspaceFactory.Event.REMOVINGITEM) {
                    WorkspaceItem<?> wdoc = ev.workspace.searchDocument(ev.id);
                    if (wdoc == doc) {
                        SwingUtilities.invokeLater(doc::closeView);
                    }
                } else if (ev.info == WorkspaceFactory.Event.ITEMCHANGED) {
                    if (ev.source != this) {
                        WorkspaceItem<?> wdoc = ev.workspace.searchDocument(ev.id);
                        if (wdoc == doc) {
                            SwingUtilities.invokeLater(this::refresh);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void componentActivated() {
        if (doc != null) {
            ActiveViewManager.getInstance().set(this);
        }
    }

    @Override
    public void componentDeactivated() {
        if (doc != null) {
            ActiveViewManager.getInstance().set(null);
        }
    }
}
