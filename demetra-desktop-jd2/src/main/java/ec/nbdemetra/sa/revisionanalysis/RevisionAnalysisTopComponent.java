/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.sa.revisionanalysis;

import com.google.common.base.Stopwatch;
import demetra.bridge.TsConverter;
import demetra.desktop.components.JTsTable;
import ec.nbdemetra.ui.ActiveViewManager;
import demetra.desktop.DemetraIcons;
import demetra.desktop.util.NbComponents;
import demetra.desktop.util.PopupMenuAdapter;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.ui.JSpecSelectionComponent;
import ec.nbdemetra.ws.ui.WorkspaceTopComponent;
import ec.satoolkit.ISaSpecification;
import ec.tss.Ts;
import ec.tss.sa.revisions.RevisionAnalysisDocument;
import ec.tss.sa.revisions.RevisionAnalysisSpec;
import ec.ui.utils.LoadingPanel;
import ec.ui.view.AbstractDocumentViewer;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.PopupMenuEvent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.DropDownButtonFactory;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top Component of the Revision analysis. Input series can be added in a table
 * which can be processed using parameters available in a property sheet
 *
 * @author Mats Maggi
 */
@ConvertAsProperties(dtd = "-//ec.nbdemetra.sa//RevisionAnalysis//EN",
        autostore = false)
@TopComponent.Description(preferredID = "RevisionAnalysisTopComponent",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@NbBundle.Messages({
    "CTL_RevisionAnalysisTopComponent=Revisions Analysis Window",
    "HINT_RevisionAnalysisTopComponent=This is a Revisions Analysis window"
})
public class RevisionAnalysisTopComponent extends WorkspaceTopComponent<RevisionAnalysisDocument> implements ExplorerManager.Provider {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevisionAnalysisTopComponent.class);
    public static final String STATE_PROPERTY = "state";

    // Main components
    private final JSplitPane visualRepresentation;
    private final JTsTable inputList;
    private final RevisionAnalysisDocumentView raView;
    private final LoadingPanel loadingPanel;
    // Toolbar
    private final JToolBar toolBarRepresentation;
    private final JLabel itemsLabel;
    private final JButton runButton;
    private final JLabel defSpecLabel;
    // Currently used specification (parameters)
    private final RevisionAnalysisSpec curSpec;
    // Properties root node
    private Node node;
    // Thread stuff
    private SwingWorker<Void, Ts> worker;
    private boolean active;
    private ProgressHandle progressHandle;

    private static RevisionAnalysisDocumentManager manager() {
        return WorkspaceFactory.getInstance().getManager(RevisionAnalysisDocumentManager.class);
    }

    public RevisionAnalysisTopComponent() {
        this(manager().create(WorkspaceFactory.getInstance().getActiveWorkspace()));
    }

    public RevisionAnalysisTopComponent(WorkspaceItem<RevisionAnalysisDocument> item) {
        super(item);
        setName(getDocument().getDisplayName());
        setToolTipText(Bundle.HINT_RevisionAnalysisTopComponent());

        toolBarRepresentation = NbComponents.newInnerToolbar();
        toolBarRepresentation.setFloatable(false);
        toolBarRepresentation.add(Box.createRigidArea(new Dimension(5, 0)));
        runButton = toolBarRepresentation.add(new AbstractAction("", DemetraIcons.COMPILE_16) {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });
        runButton.setDisabledIcon(ImageUtilities.createDisabledIcon(runButton.getIcon()));
        runButton.setEnabled(false);

        addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals(STATE_PROPERTY)) {
                onStateChange();
            }
        });

        toolBarRepresentation.addSeparator();
        itemsLabel = (JLabel) toolBarRepresentation.add(new JLabel("No items"));

        inputList = new JTsTable();
        inputList.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case JTsTable.TS_COLLECTION_PROPERTY:
                    onCollectionChange();
                    break;
            }
        });

        toolBarRepresentation.addSeparator();
        JPopupMenu specPopup = new JPopupMenu();
        final JButton specButton = (JButton) toolBarRepresentation.add(DropDownButtonFactory.createDropDownButton(DemetraIcons.BLOG_16, specPopup));
        specButton.setFocusPainted(false);
        specPopup.add(new JSpecSelectionComponent()).addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String p = evt.getPropertyName();
                if (p.equals(JSpecSelectionComponent.SPECIFICATION_PROPERTY) && evt.getNewValue() != null) {
                    ISaSpecification spec = (ISaSpecification) evt.getNewValue();
                    curSpec.setSaSpecification(spec);
                    defSpecLabel.setText(spec == null ? "" : spec.toLongString());
                } else if (p.equals(JSpecSelectionComponent.ICON_PROPERTY) && evt.getNewValue() != null) {
                    specButton.setIcon(ImageUtilities.image2Icon((Image) evt.getNewValue()));
                }
            }
        });
        specPopup.addPopupMenuListener(new PopupMenuAdapter() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                ((JSpecSelectionComponent) ((JPopupMenu) e.getSource()).getComponent(0)).setSpecification(curSpec.getSaSpecification());
            }
        });

        raView = new RevisionAnalysisDocumentView();
        raView.setDocument(getDocument().getElement());

        loadingPanel = new LoadingPanel(raView);

        curSpec = getDocument().getElement().getSpecification().clone();

        defSpecLabel = (JLabel) toolBarRepresentation.add(new JLabel());
        defSpecLabel.setText(curSpec.getSaSpecification() == null ? "" : curSpec.getSaSpecification().toLongString());

        visualRepresentation = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, inputList, loadingPanel);
        visualRepresentation.setOneTouchExpandable(true);

        inputList.setTsCollection(TsConverter.toTsCollection(getDocument().getElement().getInput()));

        setLayout(new BorderLayout());
        add(toolBarRepresentation, BorderLayout.NORTH);
        add(visualRepresentation, BorderLayout.CENTER);

        refreshNode();
    }

    private void refreshNode() {
        node = RevisionAnalysisControlNode.onComponentOpened(getExplorerManager(), this);

        try {
            getExplorerManager().setSelectedNodes(new Node[]{node});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void onCollectionChange() {
        int nbElements = inputList.getTsCollection().size();
        itemsLabel.setText(nbElements == 0 ? "No items" : nbElements + (nbElements < 2 ? " item" : " items"));

        runButton.setEnabled(nbElements != 0);
        clear();
        getDocument().getElement().setTsCollection(TsConverter.fromTsCollection(inputList.getTsCollection()));
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        SwingUtilities.invokeLater(() -> {
            visualRepresentation.setDividerLocation(.4);
            visualRepresentation.setResizeWeight(.4);
        });
    }

    @Override
    public void componentClosed() {
        getExplorerManager().setRootContext(Node.EMPTY);
        stop();
        super.componentClosed();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        ActiveViewManager.getInstance().set(this);
        active = true;
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
        ActiveViewManager.getInstance().set(null);
        active = false;
    }

    @Override
    public boolean fill(JMenu menu) {
        return false;
    }

    @Override
    public Node getNode() {
        return node;
    }

    public RevisionAnalysisSpec getSpecification() {
        return curSpec;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return ActiveViewManager.getInstance().getExplorerManager();
    }

    @Override
    public boolean hasContextMenu() {
        return false;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String getContextPath() {
        return RevisionAnalysisDocumentManager.CONTEXTPATH;
    }

    private class SwingWorkerImpl extends SwingWorker {

        @Override
        protected Void doInBackground() throws Exception {
            Stopwatch stopwatch = Stopwatch.createStarted();
            RevisionAnalysisDocument doc = getDocument().getElement();
            doc.setTsCollection(TsConverter.fromTsCollection(inputList.getTsCollection()));
            doc.setSpecification(curSpec.clone());
            doc.getResults();

            StatusDisplayer.getDefault().setStatusText("Processed " + inputList.getTsCollection().size() + " items in " + stopwatch.stop());
            if (!active) {
                requestAttention(false);
            }

            return null;
        }
    }

    protected void onStateChange() {
        switch (getState()) {
            case DONE:
                makeBusy(false);

                loadingPanel.setLoading(false);
                raView.refresh();

                if (progressHandle != null) {
                    progressHandle.finish();
                }

                break;
            case PENDING:
                runButton.setEnabled(true);
                break;
            case STARTED:
                runButton.setEnabled(false);
                loadingPanel.setLoading(true);
                progressHandle = ProgressHandle.createHandle("Processing Time Series...", () -> {
                    runButton.setEnabled(true);
                    return worker.cancel(true);
                });
                progressHandle.start();
                break;
        }
    }

    public boolean start() {
        makeBusy(true);

        worker = new SwingWorkerImpl();
        worker.addPropertyChangeListener(evt -> firePropertyChange(STATE_PROPERTY, null, worker.getState()));
        worker.execute();
        return true;
    }

    public SwingWorker.StateValue getState() {
        return worker != null ? worker.getState() : SwingWorker.StateValue.PENDING;
    }

    public boolean stop() {
        return worker != null && worker.cancel(true);
    }

    protected void clear() {
        runButton.setEnabled(true);
        raView.getDocument().setTsCollection(null);
        raView.refresh();
        stop();
    }

    static class RevisionAnalysisDocumentView extends AbstractDocumentViewer<RevisionAnalysisDocument> {

        @Override
        protected IProcDocumentView<RevisionAnalysisDocument> getView(RevisionAnalysisDocument doc) {
            return RevisionAnalysisViewFactory.getDefault().create(doc);
        }
    }
}
