package ec.ui.view.tsprocessing;

import demetra.desktop.components.JExceptionPanel;
import demetra.desktop.util.NbComponents;
import ec.nbdemetra.ui.DocumentUIServices;
import ec.nbdemetra.ui.DocumentUIServices.UIFactory;
import demetra.desktop.nodes.DecoratedNode;
import ec.nbdemetra.ui.IdNodes;
import ec.tstoolkit.algorithm.IActiveProcDocument;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.utilities.Arrays2;
import ec.tstoolkit.utilities.Id;
import ec.ui.interfaces.IDisposable;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.UUID;

/**
 *
 * @author Demortier Jeremy
 */
public class DefaultProcessingViewer<D extends IActiveProcDocument> extends JComponent implements IDisposable, ExplorerManager.Provider {

    public static final String BUTTONS = "Buttons", BUTTON_APPLY = "Apply", BUTTON_RESTORE = "Restore", BUTTON_SAVE = "Save",
            DIRTY_SPEC_PROPERTY = "dirtySpecProperty";

    public enum Type {

        NONE, APPLY, APPLY_RESTORE_SAVE
    }

    protected final Type type_;
    protected final UUID m_identifier;
    // visual components
    protected final JSplitPane splitter;
    protected final JPanel specPanel;
    protected final BeanTreeView m_tree;
    protected final ExplorerManager em;
    protected final JToolBar toolBar;
    protected final JComponent emptyView;
    // document-related data
    protected IProcDocumentView<D> m_procView;
    protected IObjectDescriptor<? extends IProcSpecification> specDescriptor;
    protected IProcSpecification originalSpec;
    // other
    protected int specWidth_ = 300;

    private boolean dirty;

    protected DefaultProcessingViewer(Type type) {
        this.type_ = type;
        this.m_identifier = UUID.randomUUID();

        this.dirty = false;

        this.specPanel = new JPanel(new BorderLayout());
        specPanel.setVisible(false);

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

    public UUID getIdentifier() {
        return m_identifier;
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
            UIFactory factory = DocumentUIServices.getDefault().getFactory(doc.getClass());
            if (factory == null) {
                return;
            }

            m_procView = factory.getDocumentView(doc);
            initSpecView(factory, doc);
        } finally {
            buildTree();
            refreshHeader();
        }
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
        IProcDocument doc = getDocument();
        if (doc == null) {
            return;
        }
        UIFactory factory = DocumentUIServices.getDefault().getFactory(doc.getClass());
        if (factory == null) {
            return;
        }
        initSpecView(factory, getDocument());
    }

    private void initSpecView(UIFactory factory, D document) {
        specDescriptor = factory.getSpecificationDescriptor(document);
        if (specDescriptor == null) {
            return;
        }
        if (type_ == Type.APPLY) {
            initApplySpecView(factory);
        } else if (type_ == Type.APPLY_RESTORE_SAVE) {
            initAllSpecView(factory);
        }
    }

    private void initApplySpecView(UIFactory factory) {
        Action[] commands = {
            new AbstractAction("Apply") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    IActiveProcDocument doc = getDocument();
                    IProcSpecification pspec = specDescriptor.getCore();
                    doc.setSpecification(pspec.clone());
                    setDirty(BUTTON_APPLY);
                    DefaultProcessingViewer.this.firePropertyChange(BUTTON_APPLY, null, null);
                    refreshView();
                    if (isHeaderVisible()) {
                        refreshHeader();
                    }
                }
            }};
        setPropertiesPanel(commands, factory.getSpecView(specDescriptor), specWidth_);
    }

    private void initAllSpecView(final UIFactory factory) {
        final IActiveProcDocument doc = getDocument();

        Action[] commands = {
            new AbstractAction(BUTTON_APPLY) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    IProcSpecification pspec = specDescriptor.getCore();
                    doc.setSpecification(pspec.clone());
                    setDirty(BUTTON_APPLY);
                    DefaultProcessingViewer.this.firePropertyChange(BUTTON_APPLY, null, null);
                    refreshView();
                    if (isHeaderVisible()) {
                        refreshHeader();
                    }
                }
            },
            new AbstractAction(BUTTON_RESTORE) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doc.setSpecification(originalSpec);
                    setDirty(BUTTON_RESTORE);
                    DefaultProcessingViewer.this.firePropertyChange(BUTTON_RESTORE, null, null);
                    refreshView();
                    if (isHeaderVisible()) {
                        refreshHeader();
                    }
                }
            },
            new AbstractAction(BUTTON_SAVE) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Apply & Save
                    IProcSpecification pspec = specDescriptor.getCore();
                    doc.setSpecification(pspec.clone());
                    setDirty(BUTTON_SAVE);
                    DefaultProcessingViewer.this.firePropertyChange(BUTTON_SAVE, null, null);
                    refreshView();
                    if (isHeaderVisible()) {
                        refreshHeader();
                    }
                }
            }};

        specDescriptor = factory.getSpecificationDescriptor(doc);
        setPropertiesPanel(commands, factory.getSpecView(specDescriptor), specWidth_);
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

    public void refreshAll() {
        m_procView.refresh();
        // refresh properties
        D doc = getDocument();
        UIFactory factory = DocumentUIServices.getDefault().getFactory(doc.getClass());
        if (factory != null) {
            initSpecView(factory, doc);
        }

        Node[] sel = em.getSelectedNodes();
        if (!Arrays2.isNullOrEmpty(sel)) {
            Id curid = sel[0].getLookup().lookup(Id.class);
            if (curid != null) {
                showComponent(curid);
            }
        } else {
            selectPreferredView();
        }
        if (isHeaderVisible()) {
            refreshHeader();
        }
    }

    public void refreshHeader() {
        // do nothing
    }

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
        m_tree.setTransferHandler(handler);
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
        m_tree.setTransferHandler(null);
        Component old = splitter.getBottomComponent();
        if (old instanceof IDisposable) {
            ((IDisposable) old).dispose();
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
