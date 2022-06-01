/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace.ui;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.TsManager;
import demetra.desktop.components.parts.HasTs;
import demetra.desktop.ui.Menus;
import demetra.desktop.ui.processing.DefaultProcessingViewer;
import demetra.desktop.ui.processing.TsProcessingViewer;
import demetra.desktop.ui.properties.l2fprod.UserInterfaceContext;
import demetra.timeseries.TsDocument;
import demetra.desktop.workspace.WorkspaceItem;
import javax.swing.Action;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JMenu;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public abstract class WorkspaceTsTopComponent<T extends TsDocument<?, ?>> extends WorkspaceTopComponent<T> implements HasTs {

    protected TsProcessingViewer<?, ?> panel;

    protected WorkspaceTsTopComponent(WorkspaceItem<T> doc) {
        super(doc);
    }

    public void updateUserInterfaceContext() {
        if (doc == null) {
            return;
        }
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
    public void componentActivated() {
        super.componentActivated();
        updateUserInterfaceContext();
    }

    @Override
    public Action[] getActions() {
        return Menus.createActions(super.getActions(), WorkspaceFactory.TSCONTEXTPATH, getContextPath());
    }

    @Override
    public void refresh() {
        panel.onDocumentChanged();
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        panel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent arg0) {
                switch (arg0.getPropertyName()) {
                    case DefaultProcessingViewer.INPUT_CHANGED:
                        Object nval=arg0.getNewValue();
                        if (nval instanceof Ts){
                            setTs((Ts) nval);
                        }
                        break;
                    case DefaultProcessingViewer.SPEC_CHANGED:
                        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(doc.getOwner(), doc.getId(), WorkspaceFactory.Event.ITEMCHANGED, WorkspaceTsTopComponent.this);
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
    public boolean hasContextMenu(){
        return true;
    }

    @Override
    public boolean fill(JMenu menu) {
        Menus.fillMenu(menu, WorkspaceFactory.TSCONTEXTPATH, getContextPath());
        return true;
    }

    @Override
    public demetra.timeseries.Ts getTs() {
        return panel.getDocument().getInput();
    }

    @Override
    public void setTs(demetra.timeseries.Ts ts) {
        Ts cts;
        if (TsManager.isDynamic(ts)) {
            cts = ts.freeze();
        } else {
            cts = ts.load(TsInformationType.All, TsManager.get());
        }
        panel.getDocument().set(cts);
        panel.updateButtons(null);
        getDocument().setDirty();
        WorkspaceFactory.Event ev = new WorkspaceFactory.Event(doc.getOwner(), doc.getId(), WorkspaceFactory.Event.ITEMCHANGED, this);
        WorkspaceFactory.getInstance().notifyEvent(ev);

    }
}
