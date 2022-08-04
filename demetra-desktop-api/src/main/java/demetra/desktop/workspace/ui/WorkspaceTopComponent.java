/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.ui;

import demetra.desktop.ui.ActiveView;
import demetra.desktop.ui.ActiveViewManager;
import demetra.desktop.ui.Menus;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import java.util.Collection;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public abstract class WorkspaceTopComponent<T> extends TopComponent implements ActiveView, ExplorerManager.Provider, LookupListener {

    private WorkspaceItem<T> doc;
    private final Lookup.Result<WorkspaceFactory.Event> wsevent;

    protected WorkspaceTopComponent(WorkspaceItem<T> doc) {
        this.doc = doc;
        this.wsevent = WorkspaceFactory.getInstance().getLookup().lookupResult(WorkspaceFactory.Event.class);
    }

    /**
     * Called when the document is opened if the current document is null The
     * result become the new document
     *
     * @return
     */
    public abstract WorkspaceItem<T> newDocument();

    public WorkspaceItem<T> getDocument() {
        return doc;
    }

    public T getElement() {
        return doc == null ? null : doc.getElement();
    }

    @Override
    public boolean hasContextMenu() {
        return true;
    }

    protected String getContextPath() {
        return null;
    }

    @Override
    public boolean fill(JMenu menu) {
        Menus.fillMenu(menu, getContextPath());
        return true;
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        ActiveViewManager.getInstance().set(this);
    }

    @Override
    public void componentDeactivated() {
        ActiveViewManager.getInstance().set(null);
        super.componentDeactivated();
    }

    @Override
    public Action[] getActions() {
        return Menus.createActions(super.getActions(), getContextPath());
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        if (doc == null) {
            doc = newDocument();
        }
        setDisplayName(doc.getDisplayName());
        setName(doc.getDisplayName());
        doc.setView(this);
        wsevent.addLookupListener(this);
        // TODO add custom code on component opening
     }

    @Override
    public void componentClosed() {
        wsevent.removeLookupListener(this);
        doc.setView(null);
        super.componentClosed();
    }

    public void refresh() {
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends WorkspaceFactory.Event> all = wsevent.allInstances();
        for (WorkspaceFactory.Event ev : all) {
            if (ev.id.equals(doc.getId())) {
                switch (ev.info) {
                    case WorkspaceFactory.Event.ITEMCHANGED -> //if (ev.source != this) {
                        SwingUtilities.invokeLater(this::refresh);
                    //}
                }
            }
        }
    }

}
