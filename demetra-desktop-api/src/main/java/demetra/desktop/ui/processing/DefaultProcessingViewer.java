package demetra.desktop.ui.processing;

import com.l2fprod.common.propertysheet.PropertySheetPanel;
import demetra.desktop.components.JExceptionPanel;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.interfaces.Disposable;
import demetra.desktop.nodes.DecoratedNode;
import demetra.desktop.ui.IdNodes;
import demetra.desktop.ui.processing.DocumentUIServices.UIFactory;
import demetra.desktop.util.NbComponents;
import demetra.processing.ProcDocument;
import demetra.processing.ProcSpecification;
import demetra.util.Arrays2;
import demetra.util.Id;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Demortier Jeremy
 * @param <S>
 * @param <D>
 */
public class DefaultProcessingViewer<S extends ProcSpecification, D extends ProcDocument<S, ?, ?>> extends JComponent implements Disposable, ExplorerManager.Provider {

    public static final String BUTTONS = "Buttons", BUTTON_APPLY = "Apply", BUTTON_RESTORE = "Restore", BUTTON_SAVE = "Save",
            DIRTY_SPEC_PROPERTY = "dirtySpecProperty";

    public enum Type {

        NONE, APPLY, APPLY_RESTORE_SAVE
    }

    private final UIFactory<S, D> factory;
    protected final Type type_;
    // visual components
    protected final JSplitPane splitter;
    protected final JPanel specPanel;
    protected final BeanTreeView tree;
    protected final ExplorerManager em;
    protected final JToolBar toolBar;
    protected final JComponent emptyView;
    // document-related data
    protected IProcDocumentView<D> m_procView;
    protected IObjectDescriptor<S> specDescriptor;
    protected S originalSpec;
    // other
    protected int specWidth_ = 300;
    private boolean dirty;

    protected DefaultProcessingViewer(UIFactory<S, D> factory, Type type) {
        this.factory = factory;
        this.type_ = type;
 
        this.dirty = false;

        this.specPanel = new JPanel(new BorderLayout());
        specPanel.setVisible(false);

        this.tree = new BeanTreeView();
        tree.setRootVisible(false);
        tree.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        this.em = new ExplorerManager();
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case ExplorerManager.PROP_SELECTED_NODES:
                    Node[] nodes = (Node[]) evt.getNewValue();
                    if (nodes.length > 0) {
                        Id id = nodes[0].getLookup().lookup(Id.class);
                        showComponent(id);
                    }
                    break;
                case BUTTON_APPLY:
                case BUTTON_RESTORE:
                    refreshAll();
                    updateDocument();
                    break;
            }
        });

        this.emptyView = new JPanel(new BorderLayout());

        this.splitter = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, tree, emptyView);
        splitter.setDividerLocation(200);
        splitter.setResizeWeight(.20);

        this.toolBar = NbComponents.newInnerToolbar();
        toolBar.add(Box.createHorizontalGlue());
        toolBar.addSeparator();
        toolBar.add(new JToggleButton(new AbstractAction("Specifications") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSpecificationsVisible(!isSpecificationsVisible());
            }
        }));

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(splitter, BorderLayout.CENTER);
        add(specPanel, BorderLayout.EAST);
    }

    // GETTERS/SETTERS >
    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }

    public int getSpecWidth() {
        return specWidth_;
    }

    public void setSpecWidth(int value) {
        specWidth_ = value;
    }

    public D getDocument() {
        return m_procView == null ? null : m_procView.getDocument();
    }

    public void setDocument(D doc) {
        dirty = false;

        try {
            if (m_procView != null) {
                m_procView.dispose();
                m_procView = null;
            }
            if (doc == null) {
                originalSpec = null;
                return;
            }
            originalSpec = doc.getSpecification();
            // initialize all items
            m_procView = factory.getDocumentView(doc);
            initSpecView(doc);
        } finally {
            buildTree();
            refreshHeader();
        }
    }

    public void updateDocument() {
        dirty = true;
     }

    public boolean isHeaderVisible() {
        return toolBar.isVisible();
    }

    public void setHeaderVisible(boolean visible) {
        toolBar.setVisible(visible);
        if (visible) {
            refreshHeader();
        }
    }

    public boolean isSpecificationsVisible() {
        return specPanel.isVisible();
    }

    public void setSpecificationsVisible(boolean visible) {
        specPanel.setVisible(visible);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    // < GETTERS/SETTERS

    public void initSpecView() {
        // initialize all items
        ProcDocument doc = getDocument();
        if (doc == null) {
            return;
        }
        initSpecView(getDocument());
    }

    private void initSpecView(D document) {
        specDescriptor = factory.getSpecificationDescriptor(document);
        if (specDescriptor == null) {
            return;
        }
        if (type_ == Type.APPLY) {
            initApplySpecView();
        } else if (type_ == Type.APPLY_RESTORE_SAVE) {
            initAllSpecView();
        }
    }

    private void initApplySpecView() {
        D doc = getDocument();
        Action[] commands = {
            new AbstractAction(BUTTON_APPLY) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    S pspec = specDescriptor.getCore();
                    doc.set(pspec);
                    setDirty(BUTTON_APPLY);
                    DefaultProcessingViewer.this.firePropertyChange(BUTTON_APPLY, null, null);
                }
            }};
        PropertySheetPanel specView = factory.getSpecView(specDescriptor);
        setPropertiesPanel(commands, specView, specWidth_);
    }

    private void initAllSpecView() {
        final D doc = getDocument();

        Action[] commands = {
            new AbstractAction(BUTTON_APPLY) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    S pspec = specDescriptor.getCore();
                    doc.set(pspec);
                    setDirty(BUTTON_APPLY);
                    DefaultProcessingViewer.this.firePropertyChange(BUTTON_APPLY, null, null);
                }
            },
            new AbstractAction(BUTTON_RESTORE) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doc.set(originalSpec);
                    setDirty(BUTTON_RESTORE);
                    DefaultProcessingViewer.this.firePropertyChange(BUTTON_RESTORE, null, null);
                }
            },
            new AbstractAction(BUTTON_SAVE) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Apply & Save
                    setDirty(BUTTON_SAVE);
                    refreshAll();
                    DefaultProcessingViewer.this.firePropertyChange(BUTTON_SAVE, null, null);
                }
            }};
        PropertySheetPanel specView = factory.getSpecView(specDescriptor);
        setPropertiesPanel(commands, specView, specWidth_);
    }

    private void setPropertiesPanel(final Action[] commands, final JComponent pane, int width) {

        specPanel.removeAll();
        specPanel.add(pane, BorderLayout.CENTER);
        pane.addPropertyChangeListener(DocumentUIServices.SPEC_PROPERTY, evt -> setDirty(null));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setName(BUTTONS);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        for (int i = 0; i < commands.length; ++i) {
            JButton applyButton = new JButton(commands[i]);
            applyButton.setEnabled(false);
            buttonPanel.add(applyButton);
            if (i < commands.length - 1) {
                buttonPanel.add(Box.createRigidArea(new Dimension(2, 0)));
            }
        }
        specPanel.add(buttonPanel, BorderLayout.SOUTH);
        specPanel.setPreferredSize(new Dimension(width, 100));
        specPanel.validate();
    }
    
    /**
     * Refresh all parts of the view
     */
    public void refreshAll() {
        refreshView();
        if (isHeaderVisible()) {
            refreshHeader();
        }
    }

    public void refreshHeader() {
        // do nothing
    }

    /**
     * Refresh the views panel
     */
    public void refreshView() {
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

    protected void setTreeTransferHandler(TransferHandler handler) {
        tree.setTransferHandler(handler);
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
        if (oldView instanceof Disposable) {
            ((Disposable) oldView).dispose();
        }

        JComponent newView;
        try {
            newView = id == null ? emptyView : m_procView.getView(id);
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
                if (node != null) {
                    em.setSelectedNodes(new Node[]{node});
                    ((DecoratedNode) node).setHtmlDecorator(DecoratedNode.Html.BOLD);
                }
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
        tree.setTransferHandler(null);
        Component old = splitter.getBottomComponent();
        if (old instanceof Disposable) {
            ((Disposable) old).dispose();
        }
        splitter.setBottomComponent(emptyView);
        removeAll();
    }

    public void setDirty(String sourceButton) {
        setDirty(specPanel, sourceButton);
    }

    private void setDirty(Container c, String sourceButton) {
        for (Component o : c.getComponents()) {
            if (o instanceof JButton) {
                JButton button = (JButton) o;
                String command = button.getActionCommand();
                if (sourceButton == null) {
                    // In case of change
                    button.setEnabled(true);
                } else {
                    switch (sourceButton) {
                        case BUTTON_APPLY:
                            button.setEnabled(command.equals(BUTTON_RESTORE) || command.equals(BUTTON_SAVE));
                            dirty = true;
                            break;
                        case BUTTON_RESTORE:
                        case BUTTON_SAVE:
                            button.setEnabled(false);
                            dirty = false;
                            break;
                    }
                }
            } else if (o instanceof Container && BUTTONS.equals(o.getName())) {
                setDirty((Container) o, sourceButton);
            }
        }
    }
}
