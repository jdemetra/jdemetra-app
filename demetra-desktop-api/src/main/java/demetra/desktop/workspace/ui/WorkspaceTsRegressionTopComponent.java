/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.ui;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.TsManager;
import demetra.desktop.ui.processing.DefaultProcessingViewer;
import demetra.desktop.ui.processing.TsRegressionProcessingViewer;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.timeseries.MultiTsDocument;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public abstract class WorkspaceTsRegressionTopComponent<T extends MultiTsDocument<?, ?>> extends WorkspaceTopComponent<T> {

    private TsRegressionProcessingViewer<?, ?> panel;

    protected WorkspaceTsRegressionTopComponent(WorkspaceItem<T> doc) {
        super(doc);
    }

    @Override
    public void refresh() {
        panel.onDocumentChanged();
    }

    protected abstract TsRegressionProcessingViewer initViewer();

    @Override
    public void componentOpened() {
        super.componentOpened();
        panel = initViewer();
        add(panel);
        panel.initialize();
        panel.doLayout();
        panel.refreshHeader();
        WorkspaceItem<T> d = getDocument();
        panel.addPropertyChangeListener((PropertyChangeEvent arg0) -> {
            switch (arg0.getPropertyName()) {
                case DefaultProcessingViewer.INPUT_CHANGED -> {
                    Object nval = arg0.getNewValue();
                    if (nval instanceof List) {
                        setTs((List<Ts>) nval);
                    }
                }
                case DefaultProcessingViewer.SPEC_CHANGED -> {
                    WorkspaceFactory.Event ev = new WorkspaceFactory.Event(d.getOwner(), d.getId(), WorkspaceFactory.Event.ITEMCHANGED, WorkspaceTsRegressionTopComponent.this);
                    WorkspaceFactory.getInstance().notifyEvent(ev);
                }
            }
        });

        TsDynamicProvider.onDocumentOpened(panel.getDocument());
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        if (panel != null) {
            panel.removeListeners();
            panel.dispose();
        }
        TsDynamicProvider.onDocumentClosing(panel.getDocument());
        super.componentClosed();
    }

    @Override
    public boolean hasContextMenu() {
        return true;
    }

    public void setTs(List<Ts> lts) {

        List<Ts> clts = new ArrayList<>();

        for (Ts ts : lts) {
            Ts cts;
            if (TsManager.isDynamic(ts)) {
                cts = ts.freeze();
            } else {
                cts = ts.load(TsInformationType.All, TsManager.get());
            }
            clts.add(cts);
        }
        panel.getDocument().set(clts);
        panel.updateButtons(null);
        WorkspaceItem<T> d = getDocument();
        d.setDirty();
        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(d.getOwner(), d.getId(), WorkspaceFactory.Event.ITEMCHANGED, this);
        WorkspaceFactory.getInstance().notifyEvent(ev);

    }
}
