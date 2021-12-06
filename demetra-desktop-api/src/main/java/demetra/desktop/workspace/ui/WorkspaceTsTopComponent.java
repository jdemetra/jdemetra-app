/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.ui;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.TsManager;
import demetra.desktop.ui.ActiveViewManager;
import demetra.desktop.ui.Menus;
import demetra.desktop.ui.processing.TsProcessingViewer;
import demetra.desktop.ui.processing.TsTopComponent;
import demetra.desktop.ui.properties.l2fprod.UserInterfaceContext;
import demetra.timeseries.TsDocument;
import demetra.desktop.workspace.WorkspaceItem;
import javax.swing.Action;
import javax.swing.JMenu;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import java.util.Collection;
import javax.swing.SwingUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public abstract class WorkspaceTsTopComponent<T extends TsDocument<?, ?>> extends TsTopComponent implements LookupListener{

    private final WorkspaceItem<T> doc;
    protected Lookup.Result<WorkspaceFactory.Event> result;
    protected TsProcessingViewer<?, ?> panel;

    protected abstract String getContextPath();

    protected WorkspaceTsTopComponent(WorkspaceItem<T> doc) {
        this.doc = doc;
        result = WorkspaceFactory.getInstance().getLookup().lookupResult(WorkspaceFactory.Event.class);
        associateLookup(ExplorerUtils.createLookup(ActiveViewManager.getInstance().getExplorerManager(), getActionMap()));
    }

    public WorkspaceItem<T> getDocument() {
        return doc;
    }
    
    public void updateUserInterfaceContext() {
        if (doc == null)
            return;
        T element = doc.getElement();
        if (element == null) {
            UserInterfaceContext.INSTANCE.setDomain(null);
        } else {
            Ts s = element.getInput();
            if (s == null) {
                UserInterfaceContext.INSTANCE.setDomain(null);
            } else {
                UserInterfaceContext.INSTANCE.setDomain(s.getData().getDomain());
            }
        }
    }


    @Override
    public void componentActivated(){
        super.componentActivated();
        updateUserInterfaceContext();
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
    
    public void refresh() {
        panel.refreshAll();
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        doc.setView(this);
        result.addLookupListener(this);
        TsDynamicProvider.OnDocumentOpened(panel.getDocument());
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        if (panel != null) {
            panel.dispose();
        }
        result.removeLookupListener(this);
        TsDynamicProvider.OnDocumentClosing(panel.getDocument());
        doc.setView(null);
        super.componentClosed();
    }

    @Override
    public demetra.timeseries.Ts getTs() {
        return panel.getDocument().getInput();
    }

    @Override
    public void setTs(demetra.timeseries.Ts ts) {
        Ts loadedTs = ts.load(TsInformationType.All, TsManager.getDefault());
        panel.getDocument().set(loadedTs);
        panel.initSpecView();
        panel.refreshAll();
        panel.updateDocument();
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends WorkspaceFactory.Event> all = result.allInstances();
        if (!all.isEmpty()) {
            for (WorkspaceFactory.Event ev : all) {
                if (ev.info == WorkspaceFactory.Event.REMOVINGITEM) {
                    WorkspaceItem<?> wdoc = ev.workspace.searchDocument(ev.id);
                    if (wdoc.getElement() == panel.getDocument()) {
                        SwingUtilities.invokeLater(this::close);
                    }
                } else if (ev.info == WorkspaceFactory.Event.ITEMCHANGED) {
                    if (ev.source != this) {
                        WorkspaceItem<?> wdoc = ev.workspace.searchDocument(ev.id);
                        if (wdoc.getElement() == panel.getDocument()) {
                            SwingUtilities.invokeLater(this::refresh);
                        }
                    }
                }
            }
        }
    }

}
