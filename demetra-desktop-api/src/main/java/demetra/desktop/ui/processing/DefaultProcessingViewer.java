package demetra.desktop.ui.processing;

import com.l2fprod.common.propertysheet.PropertySheetPanel;
import demetra.desktop.TsDynamicProvider;
import demetra.desktop.components.JExceptionPanel;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.interfaces.Disposable;
import demetra.desktop.nodes.DecoratedNode;
import demetra.desktop.ui.IdNodes;
import demetra.desktop.util.NbComponents;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.processing.ProcDocument;
import demetra.processing.ProcSpecification;
import demetra.util.Arrays2;
import demetra.util.Id;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

    public static final String SPEC_CHANGED = "spec_changed", SPEC_SAVED = "spec_saved", INPUT_CHANGED = "input_changed", DOC_CHANGED = "doc_changed";

    public enum Type {

        NONE, APPLY, APPLY_RESTORE_SAVE
    }

    private DocumentUIServices<S, D> factory;
    private final Type type_;
    // visual components
    private final JSplitPane splitter;
    private final JPanel specPanel;
    private final BeanTreeView tree;
    private final transient ExplorerManager explorerManager;
    protected final JToolBar toolBar;
    protected final JComponent emptyView;
    // document-related data
    private IProcDocumentView<D> procView;
    private IObjectDescriptor<S> specDescriptor;
    private S originalSpec;
    // other
    private int specWidth = 300;
    private boolean dirty;

    protected DefaultProcessingViewer(DocumentUIServices<S, D> factory, Type type) {
        this.factory = factory;
        this.type_ = type;

        this.dirty = false;

        this.specPanel = new JPanel(new BorderLayout());
        specPanel.setVisible(false);

        this.tree = new BeanTreeView();
        tree.setRootVisible(false);
        tree.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.explorerManager = new ExplorerManager();
        explorerManager.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            switch (evt.getPropertyName()) {
                case ExplorerManager.PROP_SELECTED_NODES -> {
                    Node[] nodes = (Node[]) evt.getNewValue();
                    if (nodes.length > 0) {
                        Id id = nodes[0].getLookup().lookup(Id.class);
                        showComponent(id);
                    }
                }
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
        return this.explorerManager;
    }

    public int getSpecWidth() {
        return specWidth;
    }

    public void setSpecWidth(int value) {
        specWidth = value;
    }

    public D getDocument() {
        return procView == null ? null : procView.getDocument();
    }

    /**
     * Set a new document and update the graphical interface
     *
     * @param doc
     */
    public void setDocument(D doc) {
        dirty = false;

        try {
            if (procView != null) {
                procView.dispose();
                procView = null;
            }
            if (doc == null) {
                originalSpec = null;
                return;
            }
            originalSpec = doc.getSpecification();
            // initialize all items
            procView = factory.getDocumentView(doc);
            initSpecView(doc);
        } finally {
            buildTree();
            refreshHeader();
        }
    }

    public void setDocument(D doc, DocumentUIServices<S, D> factory) {
        boolean newlayout = this.factory != factory;
        if (!newlayout) {
            setDocument(doc);
            return;
        }
        dirty = false;

        try {
            this.factory = factory;
            if (procView != null) {
                procView.dispose();
                procView = null;
            }
            if (doc == null) {
                originalSpec = null;
                return;
            }
            originalSpec = doc.getSpecification();
            // initialize all items
            procView = factory.getDocumentView(doc);
            initSpecView(doc);
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
                    updateButtons(BUTTON_APPLY);
                    updateResults();
                }
            }};
        PropertySheetPanel specView = factory.getSpecView(specDescriptor);
        setPropertiesPanel(commands, specView, specWidth);
    }

    private void initAllSpecView() {
        final D doc = getDocument();

        Action[] commands = {
            new AbstractAction(BUTTON_APPLY) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    S pspec = specDescriptor.getCore();
//                    S ospec = doc.getSpecification();
                    doc.set(pspec);
                    updateButtons(BUTTON_APPLY);
                    dirty = true;
                    updateResults();
                }
            },
            new AbstractAction(BUTTON_RESTORE) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    S ospec = doc.getSpecification();
                    doc.set(originalSpec);
                    initSpecView(doc);
                    updateButtons(BUTTON_RESTORE);
                    dirty = false;
                    DefaultProcessingViewer.this.firePropertyChange(SPEC_CHANGED, ospec, originalSpec);
                }
            },
            new AbstractAction(BUTTON_SAVE) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Apply & Save
                    updateButtons(BUTTON_SAVE);
                    dirty = false;
                    originalSpec = doc.getSpecification();
                    DefaultProcessingViewer.this.firePropertyChange(SPEC_SAVED, null, null);
                }
            }};
        PropertySheetPanel specView = factory.getSpecView(specDescriptor);
        setPropertiesPanel(commands, specView, specWidth);
    }

    private void setPropertiesPanel(final Action[] commands, final JComponent pane, int width) {

        specPanel.removeAll();
        specPanel.add(pane, BorderLayout.CENTER);
        pane.addPropertyChangeListener(DocumentUIServices.SPEC_PROPERTY, evt -> updateButtons(null));

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
     * Called when the content of the document has changed
     */
    public void onDocumentChanged() {
        refreshAll();
        TsDynamicProvider.onDocumentChanged(getDocument());
    }

    /**
     * Refresh all parts of the view
     */
    public void refreshAll() {
        refreshView();
        if (isHeaderVisible()) {
            refreshHeader();
        }
        initSpecView();
    }
    
    /**
     * Refresh all parts of the view
     */
    public void updateResults() {
        refreshView();
        if (isHeaderVisible()) {
            refreshHeader();
        }
        TsDynamicProvider.onDocumentChanged(getDocument());
    }
    

    public void refreshHeader() {
        // do nothing
    }

    /**
     * Refresh the views panel
     */
    private void refreshView() {
        procView.refresh();
        Node[] sel = explorerManager.getSelectedNodes();
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
        if (procView != null) {
            explorerManager.setRootContext(new DecoratedNode(IdNodes.getRootNode(procView.getItems())));
            selectPreferredView();
        } else {
            explorerManager.setRootContext(Node.EMPTY);
            showComponent(null);
        }
    }

    private void showComponent(Id id) {
        Component oldView = splitter.getBottomComponent();
        if (oldView instanceof Disposable disposable) {
            disposable.dispose();
        }

        JComponent newView;
        try {
            newView = id == null ? emptyView : procView.getView(id);
        } catch (RuntimeException ex) {
            newView = JExceptionPanel.create(ex);
        }

        int sep = splitter.getDividerLocation();
        splitter.setBottomComponent(newView != null ? newView : emptyView);
        splitter.setDividerLocation(sep);
    }

    private void selectPreferredView() {
        Id pview = procView.getPreferredView();
        if (pview != null) {
            showComponent(pview);
            Node node = IdNodes.findNode(explorerManager.getRootContext(), pview);
            try {
                if (node != null) {
                    explorerManager.setSelectedNodes(new Node[]{node});
                    ((DecoratedNode) node).setHtmlDecorator(DecoratedNode.Html.BOLD);
                }
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public void dispose() {
        if (procView != null) {
            procView.dispose();
        }
        tree.setTransferHandler(null);
        Component old = splitter.getBottomComponent();
        if (old instanceof Disposable) {
            ((Disposable) old).dispose();
        }
        splitter.setBottomComponent(emptyView);
        removeAll();
    }

    public void removeListeners() {
        PropertyChangeListener[] listeners = this.getPropertyChangeListeners();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; ++i) {
                this.removePropertyChangeListener(listeners[i]);
            }
        }
    }

    public void updateButtons(String sourceButton) {
        updateButtons(specPanel, sourceButton);
    }

    private void updateButtons(Container c, String sourceButton) {
        for (Component o : c.getComponents()) {
            if (o instanceof JButton) {
                JButton button = (JButton) o;
                String command = button.getActionCommand();
                if (sourceButton == null) {
                    // Internal change
                    button.setEnabled(command.equals(BUTTON_APPLY) || command.equals(BUTTON_RESTORE));
                } else {
                    switch (sourceButton) {
                        case BUTTON_APPLY:
                            button.setEnabled(command.equals(BUTTON_RESTORE) || command.equals(BUTTON_SAVE));
                            break;
                        case BUTTON_RESTORE:
                        case BUTTON_SAVE:
                            button.setEnabled(false);
                            break;
                    }
                }
            } else if (o instanceof Container && BUTTONS.equals(o.getName())) {
                updateButtons((Container) o, sourceButton);
            }
        }
    }
}
