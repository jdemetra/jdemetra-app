/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.ui;

import ec.nbdemetra.ui.ActiveViewManager;
import ec.nbdemetra.ui.Menus;
import ec.nbdemetra.ui.TsTopComponent;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.documents.TsDocument;
import javax.swing.Action;
import javax.swing.JMenu;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public abstract class WorkspaceTsTopComponent<T extends TsDocument<?, ?>> extends TsTopComponent {

    private final WorkspaceItem<T> doc;

    protected abstract String getContextPath();

    protected WorkspaceTsTopComponent(WorkspaceItem<T> doc) {
        this.doc = doc;
        associateLookup(ExplorerUtils.createLookup(ActiveViewManager.getInstance().getExplorerManager(), getActionMap()));
    }

    public WorkspaceItem<T> getDocument() {
        return doc;
    }
    
    @Override
    public boolean hasContextMenu(){
        return true;
    }

    @Override
    public boolean fill(JMenu menu) {
        Menus.fillMenu(menu, WorkspaceFactory.TSCONTEXTPATH, getContextPath());
        return true;
    }
    
    @Override
    public Node getNode(){
        return null;
    }

    @Override
    public Action[] getActions() {
        return Menus.createActions(super.getActions(), WorkspaceFactory.TSCONTEXTPATH, getContextPath());
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        doc.setView(this);
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
//        ActiveViewManager.getInstance().unregister(this);
        doc.setView(null);
        super.componentClosed();
    }
    
    @Override
    public void componentActivated(){
        ActiveViewManager.getInstance().set(this);
    }
    
    @Override
    public void componentDeactivated(){
        ActiveViewManager.getInstance().set(null);
    }
}
