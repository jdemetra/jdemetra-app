/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import demetra.bridge.TsConverter;
import demetra.ui.components.parts.HasTs;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.ui.view.tsprocessing.TsProcessingViewer;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;

/**
 *
 * @author Jean Palate
 */
public abstract class TsTopComponent extends TopComponent implements ExplorerManager.Provider, IActiveView, HasTs, LookupListener {

    @Override
    public ExplorerManager getExplorerManager() {
        return ActiveViewManager.getInstance().getExplorerManager();
    }
    protected Lookup.Result<WorkspaceFactory.Event> result;
    protected TsProcessingViewer panel;

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
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        if (panel != null) {
            panel.dispose();
        }
        result.removeLookupListener(this);
    }

    @Override
    public demetra.timeseries.Ts getTs() {
        return TsConverter.toTs(panel.getDocument().getTs());
    }

    @Override
    public void setTs(demetra.timeseries.Ts ts) {
        panel.getDocument().setTs(TsConverter.fromTs(ts));
        panel.refreshAll();
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends WorkspaceFactory.Event> all = result.allInstances();
        if (!all.isEmpty()) {
            Iterator<? extends WorkspaceFactory.Event> iterator = all.iterator();
            while (iterator.hasNext()) {
                WorkspaceFactory.Event ev = iterator.next();
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
