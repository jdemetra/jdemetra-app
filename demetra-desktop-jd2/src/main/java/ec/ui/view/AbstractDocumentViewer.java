/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import demetra.desktop.components.JExceptionPanel;
import demetra.desktop.util.NbComponents;
import demetra.desktop.nodes.DecoratedNode;
import ec.nbdemetra.ui.IdNodes;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.utilities.Arrays2;
import ec.tstoolkit.utilities.Id;
import ec.ui.interfaces.IDisposable;
import ec.ui.view.tsprocessing.IProcDocumentView;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.UUID;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractDocumentViewer<D extends IProcDocument> extends JComponent implements IDisposable, ExplorerManager.Provider {

    protected final UUID m_identifier;
    // visual components
    protected final JSplitPane splitter;
    protected final BeanTreeView m_tree;
    protected final ExplorerManager em;
    protected final JComponent emptyView;
    // document-related data
    protected IProcDocumentView<D> m_procView;

    protected AbstractDocumentViewer() {
        this.m_identifier = UUID.randomUUID();

        this.m_tree = new BeanTreeView();
        m_tree.setRootVisible(false);
        m_tree.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        this.em = new ExplorerManager();
        em.addVetoableChangeListener(evt -> {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Node[] nodes = (Node[]) evt.getNewValue();
                if (nodes.length > 0) {
                    Id id = nodes[0].getLookup().lookup(Id.class);
                    showComponent(id);
                }
            }
        });

        this.emptyView = new JPanel(new BorderLayout());

        this.splitter = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, m_tree, emptyView);
        splitter.setDividerLocation(200);
        splitter.setResizeWeight(.20);

        setLayout(new BorderLayout());
        add(splitter, BorderLayout.CENTER);
    }

    // GETTERS/SETTERS >
    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }

    public UUID getIdentifier() {
        return m_identifier;
    }

    public D getDocument() {
        return m_procView == null ? null : m_procView.getDocument();
    }

    protected abstract IProcDocumentView<D> getView(D doc);

    public void setDocument(D doc) {
        try {
            if (m_procView != null) {
                m_procView.dispose();
                m_procView = null;
            }
            m_procView = getView(doc);
        } finally {
            buildTree();
        }
    }

    public void refresh() {
        m_procView.refresh();
        Node[] sel = em.getSelectedNodes();
        if (!Arrays2.isNullOrEmpty(sel)) {
            Id curid = sel[0].getLookup().lookup(Id.class);
            if (curid != null) {
                showComponent(curid);
            }
        } else {
            selectPreferredView();
        }
    }

    private void buildTree() {
        if (m_procView != null) {
            em.setRootContext(new DecoratedNode(IdNodes.getRootNode(m_procView.getItems())));
            selectPreferredView();
        } else {
            em.setRootContext(Node.EMPTY);
            showComponent(null);
        }
    }

    private void showComponent(Id id) {
        Component oldView = splitter.getBottomComponent();
        if (oldView instanceof IDisposable) {
            ((IDisposable) oldView).dispose();
        }

        JComponent newView;
        try {
            newView = id != null ? m_procView.getView(id) : emptyView;
        } catch (RuntimeException ex) {
            newView = JExceptionPanel.create(ex);
        }

        int sep = splitter.getDividerLocation();
        splitter.setBottomComponent(newView != null ? newView : emptyView);
        splitter.setDividerLocation(sep);
    }

    private void selectPreferredView() {
        Id pview = m_procView.getPreferredView();
        if (pview != null) {
            showComponent(pview);
            Node node = IdNodes.findNode(em.getRootContext(), pview);
            try {
                em.setSelectedNodes(new Node[]{node});
                ((DecoratedNode) node).setHtmlDecorator(DecoratedNode.Html.BOLD);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public void dispose() {
        if (m_procView != null) {
            m_procView.dispose();
        }
        m_tree.setTransferHandler(null);
        Component old = splitter.getBottomComponent();
        if (old instanceof IDisposable) {
            ((IDisposable) old).dispose();
        }
        splitter.setBottomComponent(emptyView);
        removeAll();
    }

}