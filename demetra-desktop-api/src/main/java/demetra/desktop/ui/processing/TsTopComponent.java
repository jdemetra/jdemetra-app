/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.ui.processing;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.TsManager;
import demetra.desktop.components.parts.HasTs;
import demetra.desktop.ui.ActiveView;
import demetra.desktop.ui.ActiveViewManager;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import java.util.Collection;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;

/**
 *
 * @author PALATEJ
 */
public abstract class TsTopComponent extends TopComponent implements HasTs, ActiveView, ExplorerManager.Provider, LookupListener{

    @Override
    public ExplorerManager getExplorerManager() {
        return ActiveViewManager.getInstance().getExplorerManager();
    }
    
    protected Lookup.Result<WorkspaceFactory.Event> result;
    protected TsProcessingViewer<?, ?> panel;

    protected TsTopComponent() {
        result = WorkspaceFactory.getInstance().getLookup().lookupResult(WorkspaceFactory.Event.class);
    }

    public void refresh() {
        panel.refreshAll();
    }

    @Override
    public boolean fill(JMenu menu) {
        return true;
    }

    @Override
    public void componentOpened() {
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
    }

    @Override
    public demetra.timeseries.Ts getTs() {
        return panel.getDocument().getInput();
    }

    @Override
    public void setTs(demetra.timeseries.Ts ts) {
        Ts loadedTs = ts.load(TsInformationType.All, TsManager.getDefault());
        panel.getDocument().set(loadedTs);
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
