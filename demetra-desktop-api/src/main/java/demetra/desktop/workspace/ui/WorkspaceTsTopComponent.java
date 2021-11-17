/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.ui;

import demetra.desktop.ui.ActiveViewManager;
import demetra.desktop.ui.Menus;
import demetra.desktop.ui.TsTopComponent;
import demetra.timeseries.TsDocument;
import demetra.desktop.workspace.WorkspaceItem;
import javax.swing.Action;
import javax.swing.JMenu;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import demetra.desktop.workspace.WorkspaceFactory;

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
