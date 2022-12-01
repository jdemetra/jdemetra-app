/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.ui;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import demetra.desktop.DemetraBehaviour;
import demetra.desktop.DemetraIcons;
import demetra.desktop.TsDynamicProvider;
import demetra.desktop.TsManager;
import demetra.desktop.components.parts.HasTsCollection;
import demetra.desktop.components.parts.HasTsCollectionSupport;
import demetra.desktop.datatransfer.DataTransferManager;
import demetra.desktop.datatransfer.DataTransfers;
import demetra.desktop.notification.MessageType;
import demetra.desktop.notification.NotifyUtil;
import demetra.desktop.sa.multiprocessing.ui.MultiProcessingController.SaProcessingState;
import demetra.desktop.sa.ui.DemetraSaUI;
import demetra.desktop.tsproviders.DataSourceManager;
import demetra.desktop.ui.Menus;
import demetra.desktop.ui.Menus.DynamicPopup;
import demetra.desktop.ui.processing.DefaultProcessingViewer;
import demetra.desktop.ui.processing.TsProcessingViewer;
import demetra.desktop.ui.properties.l2fprod.UserInterfaceContext;
import demetra.desktop.util.ListTableModel;
import demetra.desktop.util.NbComponents;
import demetra.desktop.util.PopupMenuAdapter;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.desktop.workspace.WorkspaceFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.desktop.workspace.ui.JSpecSelectionComponent;
import demetra.processing.ProcQuality;
import demetra.processing.ProcessingLog.InformationType;
import demetra.sa.*;
import demetra.timeseries.*;
import demetra.timeseries.regression.ModellingContext;
import demetra.util.MultiLineNameUtil;
import ec.util.grid.swing.XTable;
import ec.util.table.swing.JTables;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Philippe Charles
 * @author Mats Maggi
 */
@lombok.extern.java.Log
public class SaBatchUI extends AbstractSaProcessingTopComponent implements MultiViewElement, HasTsCollection, ExplorerManager.Provider {

    private static final String REFRESH_MESSAGE = "Are you sure you want to refresh the data?";
    private static final String REFRESH_LOCAL_MESSAGE = "Are you sure you want to refresh the selected items?";
    private static final String DELETE_LOCAL_MESSAGE = "Are you sure you want to delete the selected items?";
    private static final String DELETE_ALL_MESSAGE = "Are you sure you want to delete all items?";
    private static final String RESET_MESSAGE = "Are you sure you want to reset this document?";
    private static final String PASTE_FAILED_MESSAGE = "Unable to paste data?";

    // MultiViewElement >
    @Override
    public JComponent getVisualRepresentation() {
        return visualRepresentation;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolBarRepresentation;
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
    }

    @Override
    public void componentClosed() {
        if (detail.getDocument() != null) {
            TsDynamicProvider.onDocumentClosing((TsDocument) detail.getDocument());
        }
        stop();
        detail.dispose();
        controller.dispose();
        for (PropertyChangeListener listener : this.getPropertyChangeListeners()) {
            this.removePropertyChangeListener(listener);
        }
        super.componentClosed();
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        active = true;
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
        active = false;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }
    // < MultiViewElement

    public int getSelectionCount() {
        return selection.length;
    }

    public enum SaItemPriorityDefinition {

        Log,
        Level
    }

    @Override
    public Action[] getActions() {
        return Menus.createActions(super.getActions(), MultiProcessingManager.CONTEXTPATH);
    }

    @Override
    public boolean hasContextMenu() {
        return true;
    }

    @Override
    public boolean fill(JMenu menu) {
        Menus.fillMenu(menu, MultiProcessingManager.CONTEXTPATH);
        return true;
    }

    // CONSTANTS
    // PROPERTIES DEFINITIONS
    public static final String DEFAULT_SPECIFICATION_PROPERTY = "specificationProperty";
    public static final String PROCESSING_PROPERTY = "processing";
    public static final String SELECTION_PROPERTY = "itemSelection";
    // PROPERTIES
    private SaSpecification defaultSpecification;
    private SaNode[] selection = new SaNode[0];
    // main components
    private final JSplitPane visualRepresentation;
    private final JToolBar toolBarRepresentation;
    // toolBar stuff
    private final JButton runButton;
    private final JLabel statusLabel;
    private final JLabel itemsLabel;
    private final JLabel defSpecLabel;
    private final JToggleButton buttonCollapse;
    // visual stuff
    private final XTable master;
    private final TsProcessingViewer detail;
    // a trier
    private ProgressHandle progressHandle;
    private boolean active;
    private SwingWorker<Void, SaNode> worker;
    private final SaProcessingModel model;
    private final ListTableSelectionListener listTableListener;

    @lombok.experimental.Delegate
    private final HasTsCollection collection;

    private final DeleteActionPanel deleteActionPanel;

    private final ExplorerManager mgr = new ExplorerManager();

    public SaBatchUI(MultiProcessingController controller) {
        super(controller);
        this.collection = HasTsCollectionSupport.of(this::firePropertyChange, TsInformationType.None);
        collection.setTsUpdateMode(TsUpdateMode.Replace);
        this.defaultSpecification = DemetraSaUI.get().getDefaultSaSpec();

        setName(controller.getDocument().getDisplayName());
        setDisplayName(controller.getDocument().getDisplayName());
        toolBarRepresentation = NbComponents.newInnerToolbar();
        toolBarRepresentation.setFloatable(false);
        toolBarRepresentation.addSeparator();
        toolBarRepresentation.add(Box.createRigidArea(new Dimension(5, 0)));
        runButton = toolBarRepresentation.add(new AbstractAction("", DemetraIcons.COMPILE_16) {
            @Override
            public void actionPerformed(ActionEvent e) {
                start(true);
            }
        });
        runButton.setDisabledIcon(ImageUtilities.createDisabledIcon(runButton.getIcon()));
        toolBarRepresentation.addSeparator();
        statusLabel = (JLabel) toolBarRepresentation.add(new JLabel());
        toolBarRepresentation.addSeparator();
        itemsLabel = (JLabel) toolBarRepresentation.add(new JLabel());
        toolBarRepresentation.addSeparator();

        JPopupMenu specPopup = new JPopupMenu();
        final JButton specButton = (JButton) toolBarRepresentation.add(DropDownButtonFactory.createDropDownButton(DemetraIcons.BLOG_16, specPopup));
        JSpecSelectionComponent cmp = new JSpecSelectionComponent();
        cmp.setFamily(SaSpecification.FAMILY);
        specPopup.add(cmp).addPropertyChangeListener(evt -> {
            String p = evt.getPropertyName();
            if (p.equals(JSpecSelectionComponent.SPECIFICATION_PROPERTY) && evt.getNewValue() != null) {
                setDefaultSpecification((SaSpecification) evt.getNewValue());
            } else if (p.equals(JSpecSelectionComponent.ICON_PROPERTY) && evt.getNewValue() != null) {
                specButton.setIcon(ImageUtilities.image2Icon((Image) evt.getNewValue()));
            }
        });
        specPopup.addPopupMenuListener(new PopupMenuAdapter() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                ((JSpecSelectionComponent) ((JPopupMenu) e.getSource()).getComponent(0)).setSpecification(getDefaultSpecification());
            }
        });

        defSpecLabel = (JLabel) toolBarRepresentation.add(new JLabel());
        defSpecLabel.setText(defaultSpecification == null ? "" : defaultSpecification.longDisplay());
        toolBarRepresentation.add(Box.createHorizontalGlue());
        toolBarRepresentation.addSeparator();
        buttonCollapse = (JToggleButton) toolBarRepresentation.add(new JToggleButton("Specifications"));
        buttonCollapse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detail.setSpecificationsVisible(!detail.isSpecificationsVisible());
            }
        });

        model = new SaProcessingModel();
        listTableListener = new ListTableSelectionListener();

        master = buildList();
        // TODO
        detail = new TsProcessingViewer(null, TsProcessingViewer.Type.APPLY_RESTORE_SAVE);
        detail.setHeaderVisible(false);
        detail.addPropertyChangeListener(DefaultProcessingViewer.SPEC_SAVED, evt -> save((TsDocument) detail.getDocument()));
        detail.addPropertyChangeListener(DefaultProcessingViewer.SPEC_CHANGED, evt -> detail.onDocumentChanged());
        visualRepresentation = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, NbComponents.newJScrollPane(master), detail);
        visualRepresentation.setResizeWeight(.60d);
        visualRepresentation.setOneTouchExpandable(true);

        setLayout(new BorderLayout());
        add(toolBarRepresentation, BorderLayout.NORTH);
        add(visualRepresentation, BorderLayout.CENTER);
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case HasTsCollection.DROP_CONTENT_PROPERTY, HasTsCollection.FREEZE_ON_IMPORT_PROPERTY, HasTsCollection.TS_COLLECTION_PROPERTY, HasTsCollection.TS_SELECTION_MODEL_PROPERTY, HasTsCollection.TS_UPDATE_MODE_PROPERTY ->
                    onCollectionChange();
                case DEFAULT_SPECIFICATION_PROPERTY ->
                    onDefaultSpecificationChange();
                case PROCESSING_PROPERTY ->
                    onProcessingChange();
                case SELECTION_PROPERTY ->
                    onSelectionChange();
            }
        });
        master.addMouseListener(new DynamicPopup(MultiProcessingManager.LOCALPATH));
        master.setDropMode(DropMode.ON);
        master.setTransferHandler(HasTsCollectionSupport.newTransferHandler(collection));
        deleteActionPanel = new DeleteActionPanel();
        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    @NbBundle.Messages({
        "undefinedspec.dialog.title=Undefined specification"
    })
    private void onCollectionChange() {
        TsCollection coll = getTsCollection();
        if (coll == null) {
            return;
        }
        Ts[] all = coll.stream().filter(s -> s.getType().encompass(TsInformationType.Data)).toArray(n -> new Ts[n]);
        if (all.length > 0 && defaultSpecification == null) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(Bundle.undefinedspec_dialog_title(), NotifyDescriptor.INFORMATION_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        getElement().add(defaultSpecification, all);
        controller.getDocument().setDirty();
        redrawAll();

    }

    public boolean isTableEmpty() {
        return master.getModel().getRowCount() == 0;
    }

    // EVENT HANDLERS >
    protected void onDefaultSpecificationChange() {
        // do nothing
    }

    public void editDefaultSpecification() {
        JSpecSelectionComponent c = new JSpecSelectionComponent();
        c.setSpecification(getDefaultSpecification());
        DialogDescriptor dd = c.createDialogDescriptor("Choose active specification");
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            setDefaultSpecification((SaSpecification) c.getSpecification());
        }
    }

    protected void onProcessingChange() {
        model.fireTableDataChanged();
//        String ts = getCurrentProcessing().getMeta().get(TsMeta.TIMESTAMP.getKey());
//        statusLabel.setText(ts != null ? ("Saved:" + ts) : "New processing");
    }

    @Override
    protected void onSaProcessingStateChange() {
        super.onSaProcessingStateChange();
        switch (controller.getSaProcessingState()) {
            case DONE:
                runButton.setEnabled(true);
                makeBusy(false);

                if (progressHandle != null) {
                    progressHandle.finish();
                }
                break;
            case PENDING:
                runButton.setEnabled(true);
                break;
            case STARTED:
                runButton.setEnabled(false);
                progressHandle = ProgressHandle.createHandle(controller.getDocument().getDisplayName(), () -> worker.cancel(true));
                progressHandle.start(getElement().getCurrent().size());
                break;
        }
    }

    protected void onSelectionChange() {
        listTableListener.setEnabled(false);
        master.getSelectionModel().clearSelection();
        for (SaNode o : selection) {
            int i = master.convertRowIndexToView(getElement().getCurrent().indexOf(o));
            master.getSelectionModel().addSelectionInterval(i, i);
        }
        if (selection.length > 0) {
            SaNode item = selection[0];
            item.process(ModellingContext.getActiveContext(), true);
            int idx = getElement().getCurrent().indexOf(item);
            model.fireTableRowsUpdated(idx, idx);
            showDetails(item);
        } else {
            showDetails(null);
        }
        listTableListener.setEnabled(true);
    }
    // < EVENT HANDLERS

    // GETTERS/SETTERS >
    public SaSpecification getDefaultSpecification() {
        return defaultSpecification;
    }

    public MultiProcessingDocument getElement() {
        if (controller == null) {
            return null;
        }
        return controller.getDocument().getElement();
    }

    public void setDefaultSpecification(SaSpecification defaultSpecification) {
        SaSpecification old = this.defaultSpecification;
        this.defaultSpecification = defaultSpecification;
        defSpecLabel.setText(defaultSpecification == null ? "" : defaultSpecification.display());
        firePropertyChange(DEFAULT_SPECIFICATION_PROPERTY, old, this.defaultSpecification);
    }

    public void setSelection(SaNode[] selection) {
        SaNode[] old = this.selection;
        this.selection = selection != null ? selection : new SaNode[0];
        firePropertyChange(SELECTION_PROPERTY, old, this.selection);
    }

    public SaNode[] getSelection() {
        return selection.clone();
    }
    // < GETTERS/SETTERS

    public boolean start(boolean all) {
        makeBusy(true);
        worker = new SwingWorkerImpl(all);
        worker.addPropertyChangeListener(evt -> {
            switch (worker.getState()) {
                case DONE:
                    if (progressHandle != null) {
                        progressHandle.finish();
                    }
                    if (controller != null) {
                        controller.setSaProcessingState(SaProcessingState.DONE);
                    }
                    break;
                case PENDING:
                    controller.setSaProcessingState(SaProcessingState.PENDING);
                    break;
                case STARTED:
                    controller.setSaProcessingState(SaProcessingState.STARTED);
                    break;
            }
        });
        worker.execute();
        return true;
    }

    public boolean stop() {
        return worker != null && worker.cancel(true);
    }

    public void setInitialOrder() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        sorter.setComparator(SaProcessingModel.SERIES, SaNodeComparer.Name);
        sorter.setComparator(SaProcessingModel.METHOD, SaNodeComparer.Method);
        sorter.setComparator(SaProcessingModel.PRIORITY, SaNodeComparer.Priority);
        sorter.setComparator(SaProcessingModel.QUALITY, SaNodeComparer.Quality);
        master.setRowSorter(sorter);
    }

    public void refresh(EstimationPolicyType policy, boolean interactive, boolean all) {
        refresh(policy, 0, interactive, all);
    }

    public void refresh(EstimationPolicyType policy, int nback, boolean interactive, boolean all) {
        if (interactive) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(all ? REFRESH_MESSAGE : REFRESH_LOCAL_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
        }
        if (all) {
            controller.getDocument().getElement().refresh(policy, nback, item -> true);
        } else {
            Set<SaNode> sel = Arrays.stream(selection).collect(Collectors.toSet());
            sel.forEach(n -> n.setOutput(n.getOutput().copy()));
            controller.getDocument().getElement().refresh(policy, nback, item -> sel.contains(item));
        }
        showDetails(null);
        controller.getDocument().setDirty();
        start(all);
    }

    public void paste(boolean interactive) {
        Transferable dataobj = DataTransfers.systemClipboardAsTransferable();
        if (dataobj.getTransferDataFlavors().length > 0) {
            if (pasteTs(dataobj)) {
                redrawAll();
                return;
            }
            if (pasteSaProcessing(dataobj)) {
                redrawAll();
                return;
            }
        }
        if (interactive) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(PASTE_FAILED_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

    private boolean pasteTs(Transferable dataobj) {
        long count = DataTransferManager.get()
                .toTsCollectionStream(dataobj)
                .map(col -> col
                .load(demetra.timeseries.TsInformationType.All, TsManager.get())
                .stream()
                .map(Ts::freeze)
                .collect(TsCollection.toTsCollection())
                )
                .peek(col -> getElement().add(defaultSpecification, col.toArray(n -> new Ts[n])))
                .count();
        if (count > 0) {
            controller.setSaProcessingState(SaProcessingState.READY);
            return true;
        } else {
            return false;
        }
    }

    private boolean pasteSaProcessing(Transferable dataobj) {
//        SaItems processing = TransferableXml.read(dataobj, SaItems.class, XmlSaProcessing.class);
//        if (processing != null) {
//            getCurrentProcessing().addAll(processing.stream().map(SaItem::makeCopy).collect(toList()));
//            controller.setSaProcessingState(SaProcessingState.READY);
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }

    public void remove(boolean interactive) {
        SaNode[] items = selection;
        if (items == null) {
            return;
        }
        if (interactive) {
            deleteActionPanel.setItems(items);
            NotifyDescriptor nd = new NotifyDescriptor(deleteActionPanel,
                    "Delete confirmation",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    NotifyDescriptor.YES_OPTION);

            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.YES_OPTION) {
                return;
            }
        }

        List<SaNode> itemsToDelete = new ArrayList<>();
        int[] indexToDelete = deleteActionPanel.getSelectedIndices();
        for (int index : indexToDelete) {
            itemsToDelete.add(items[index]);
        }
        controller.getDocument().getElement().remove(itemsToDelete);

        redrawAll();
        controller.setSaProcessingState(SaProcessingState.READY);
        controller.getDocument().setDirty();
        setSelection(null);
    }

    public void clear(boolean interactive) {
        if (interactive) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(DELETE_ALL_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
        }
        controller.getDocument().getElement().removeAll();
        redrawAll();
        controller.setSaProcessingState(SaProcessingState.READY);
        controller.getDocument().setDirty();
        setSelection(null);
    }

    public void reset(boolean interactive) {
        if (interactive) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(RESET_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
        }
        controller.getDocument().getElement().reset();
        redrawAll();
        controller.setSaProcessingState(SaProcessingState.READY);
        controller.getDocument().setDirty();
        setSelection(null);
    }

    public void copy() {
        SaNode[] items = selection;
        if (items == null) {
            return;
        }
        List<SaNode> litems = Arrays.asList(items);
        copy(litems);
    }

    public void copy(Collection<SaNode> litems) {
//        SaProcessing processing = new SaProcessing();
//        processing.addAll(litems);
//        TransferableXml transferable = new TransferableXml(processing, XmlSaProcessing.class);
//        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
    }

    public void copySeries() {
        SaNode[] items = selection;
        if (items == null) {
            return;
        }
        List<SaNode> litems = Arrays.asList(items);
        copySeries(litems);
    }

    public void copySeries(Collection<SaNode> litems) {
//        demetra.timeseries.TsCollection col = litems.stream()
//                .map(item->item.getDefinition().getTs())
//                .collect(demetra.timeseries.TsCollection.toTsCollection());
//        Transferable transferable = DataTransfer.getDefault().fromTsCollection(col);
//        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
    }

    public void copyComponents(List<String> components) {
        List<demetra.timeseries.Ts> col = new java.util.ArrayList<>();
        for (SaNode item : getSelection()) {
            item.getOutput().compute(ModellingContext.getActiveContext(), false);
            components.stream().forEach((comp) -> {
                TsData tsData = item.getOutput().getEstimation().getResults().getData(comp, TsData.class);
                if (tsData != null) {
                    col.add(Ts.of("[" + comp + "] " + item.getName(), tsData));
                }
            });
        }

        Transferable transferable = DataTransferManager.get().fromTsCollection(TsCollection.of(col));
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
    }

    public void cut() {
        SaNode[] items = selection;
        if (items == null) {
            return;
        }
        List<SaNode> litems = Arrays.asList(items);
        cut(litems);
    }

    public void cut(Collection<SaNode> litems) {
//        SaProcessing processing = new SaProcessing();
//        processing.addAll(litems);
//        TransferableXml transferable = new TransferableXml(processing, XmlSaProcessing.class);
//        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
//        getCurrentProcessing().removeAll(litems);
//        redrawAll();
//        controller.setSaProcessingState(SaProcessingState.READY);
    }

    private XTable buildList() {
        final XTable result = new XTable();
        result.setModel(model);
        result.setDragEnabled(false);
        result.setDropMode(DropMode.ON);
        result.setFillsViewportHeight(true);
        result.setNoDataRenderer(new XTable.DefaultNoDataRenderer("Drop data here", "Drop data here"));

        final ListSelectionModel lsmodel = result.getSelectionModel();
        lsmodel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lsmodel.addListSelectionListener(listTableListener);

        JTables.setWidthAsPercentages(result, .35, .1, .1, .1, .05, .07, .07, .06);
        result.setAutoCreateColumnsFromModel(false);
        result.getColumnModel().getColumn(SaProcessingModel.SERIES).setCellRenderer(new SeriesRenderer());
        result.getColumnModel().getColumn(SaProcessingModel.METHOD).setCellRenderer(new MethodRenderer());
        result.getColumnModel().getColumn(SaProcessingModel.ESTIMATION).setCellRenderer(new EstimationRenderer());
        result.getColumnModel().getColumn(SaProcessingModel.STATUS).setCellRenderer(new StatusRenderer());
        result.getColumnModel().getColumn(SaProcessingModel.PRIORITY).setCellRenderer(new PriorityRenderer());
        result.getColumnModel().getColumn(SaProcessingModel.QUALITY).setCellRenderer(new QualityRenderer());
        result.getColumnModel().getColumn(SaProcessingModel.WARNINGS).setCellRenderer(new WarningsRenderer());
        result.getColumnModel().getColumn(SaProcessingModel.COMMENTS).setCellRenderer(new CommentsRenderer());

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(result.getModel());
        sorter.setComparator(SaProcessingModel.SERIES, SaNodeComparer.Name);
        sorter.setComparator(SaProcessingModel.METHOD, SaNodeComparer.Method);
        sorter.setComparator(SaProcessingModel.PRIORITY, SaNodeComparer.Priority);
        sorter.setComparator(SaProcessingModel.QUALITY, SaNodeComparer.Quality);
        result.setRowSorter(sorter);

        result.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = result.getSelectedRow();
                if (e.getClickCount() > 1 && row != -1) {
                    SaNode item = model.getValues().get(result.getRowSorter().convertRowIndexToModel(row));
//                    TsActions.getDefault().openWith(item..getDefinition().getTs(), DemetraUI.getDefault().getTsActionName());
                }
            }
        });

        return result;
    }

    private void refreshInfo() {
//        String ts = getCurrentProcessing().getMeta().get(TsMeta.TIMESTAMP);
//        if (!Strings.isNullOrEmpty(ts)) {
//            if (getCurrentProcessing().isDirty()) {
//                statusLabel.setText("Unsaved");
//            } else {
//                statusLabel.setText("Saved:" + ts);
//            }
//        } else {
//            statusLabel.setText("New processing");
//        }
    }

    public void redrawAll() {
        int n = getElement().getCurrent().size();
        itemsLabel.setText(n + (n < 2 ? " item" : " items"));
        model.fireTableDataChanged();
    }

    private void showDetails(SaNode item) {
        if (item == null) {
            detail.setSpecificationsVisible(false);
            buttonCollapse.setSelected(false);
            buttonCollapse.setEnabled(false);
            TsDocument doc = (TsDocument) detail.getDocument();
            if (doc != null) {
                doc.set((Ts) null);
                detail.onDocumentChanged();
                updateUserInterfaceContext(null);
            }
        } else {
            SaItem output = item.getOutput();
            SaSpecification cspec = output.getDefinition().activeSpecification();
            Ts ts = output.getDefinition().getTs();
            TsDocument doc = (TsDocument) detail.getDocument();
            if (doc != null && cspec.getClass().isInstance(doc.getSpecification())) {
                // same document. To be updated
                doc.setAll(cspec, ts, output.getEstimation().getResults());
                detail.onDocumentChanged();
            } else {
                DocumentUIServices ui = DocumentUIServices.forSpec(cspec.getClass());
                if (ui == null) {
                    showDetails(null);
                } else {
                    Class dclass = ui.getDocumentType();
                    WorkspaceItemManager mgr = WorkspaceItemManager.forItem(dclass);
                    if (mgr == null) {
                        showDetails(null);
                    } else {
                        TsDocument tmp = (TsDocument) mgr.createNewObject();
                        if (doc != null) {
                            TsDynamicProvider.onDocumentClosing(doc);
                        }
                        TsDynamicProvider.onDocumentOpened(tmp);
                        tmp.setAll(cspec, ts, output.getEstimation().getResults());
                        detail.setDocument(tmp, ui);
                    }
                }
            }
            detail.onDocumentChanged();
            buttonCollapse.setEnabled(true);
            detail.setSpecificationsVisible(buttonCollapse.isSelected());
            updateUserInterfaceContext(ts);
        }
    }

    private void updateUserInterfaceContext(Ts s) {
        if (s == null) {
            UserInterfaceContext.INSTANCE.setDomain(null);
        } else {
            UserInterfaceContext.INSTANCE.setDomain(s.getData().getDomain());
        }
    }

    void save(TsDocument doc) {
        if (selection.length == 0) {
            return;
        }
        SaNode node = selection[0];
        SaItem item = node.getOutput();

        SaSpecification spec = (SaSpecification) doc.getSpecification();
        SaDefinition def = SaDefinition.builder()
                .domainSpec(spec)
                .ts(doc.getInput())
                .policy(EstimationPolicyType.Interactive)
                .build();

        SaEstimation estimation = doc instanceof HasSaEstimation ? ((HasSaEstimation) doc).getEstimation() : null;
//        
        SaItem nitem = SaItem.builder()
                .name(item.getName())
                .meta(item.getMeta())
                .definition(def)
                .estimation(estimation)
                .build();

        node.setOutput(nitem);
        master.repaint();
//        model.fireTableDataChanged();

        controller.getDocument().setDirty();
//        setSelection(new SaNode[]{node});
    }

    public void clearPriority(List<SaItem> items) {
        setPriority(items, 0);
    }

    public void setPriority(List<SaItem> items, SaItemPriorityDefinition def) {
        if (def == SaItemPriorityDefinition.Level) {
            setLevelPriority(items);
        } else {
            setLogPriority(items);
        }
    }

    public void setLevelPriority(List<SaItem> items) {
//        int n = items.size();
//        if (n == 0) {
//            return;
//        }
//        double maxavg = 0;
//        double[] avg = new double[n];
//
//        int i = 0;
//        for (SaItem item : items) {
//            if (item.getTsData() != null) {
//                DescriptiveStatistics stats = new DescriptiveStatistics(item.getTsData().getValues());
//                double cur = stats.getAverage();
//                if (cur > maxavg) {
//                    maxavg = cur;
//
//                }
//                avg[i] = cur;
//            }
//            ++i;
//        }
//
//        i = 0;
//        for (SaItem item : items) {
//            item.setPriority((int) Math.floor(avg[i++] / maxavg * 10));
//        }
//        redrawAll();
    }

    public void setLogPriority(List<SaItem> items) {
//        int n = items.size();
//        if (n == 0) {
//            return;
//        }
//        double maxavg = 0;
//        double[] avg = new double[n];
//
//        int i = 0;
//        for (SaItem item : items) {
//            if (item.getTsData() != null) {
//                DescriptiveStatistics stats = new DescriptiveStatistics(item.getTsData().getValues());
//                double cur = stats.getAverage();
//                if (cur > 0) {
//                    cur = Math.log10(cur);
//
//                }
//                if (cur < 0) {
//                    cur = 0;
//
//                }
//                if (cur > maxavg) {
//                    maxavg = cur;
//
//                }
//                avg[i] = cur;
//            }
//            ++i;
//        }
//        if (maxavg == 0) {
//            return;
//
//        }
//        i = 0;
//        for (SaItem item : items) {
//            item.setPriority((int) (avg[i++] / maxavg * 10));
//        }
//        redrawAll();
    }

    public void setPriority(List<SaItem> items, int p) {
        if (items.isEmpty()) {
            return;
        }
        MultiProcessingDocument element = getElement();
        Set<SaItem> all = new HashSet(items);
//        element.replace(item-> all.contains(item), item->item.withPriority(p));
        redrawAll();
    }

    private class ListTableSelectionListener implements ListSelectionListener {

        boolean enabled = true;

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (enabled && !e.getValueIsAdjusting()) {
                if (detail.isDirty()) {
                    NotifyDescriptor nd = new NotifyDescriptor.Confirmation("There are unsaved changes in the previously selected item's spec."
                            + "\nDo you want to save them ?", "Unsaved changes", NotifyDescriptor.YES_NO_OPTION);
                    if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.YES_OPTION) {
                        save((TsDocument) detail.getDocument());
                    }
                    detail.setDirty(false);
                }

                setSelection(Arrays.stream(master.getSelectedRows())
                        .mapToObj(i -> getElement().getCurrent().get(master.convertRowIndexToModel(i)))
                        .toArray(SaNode[]::new));
            }
        }
    }

    abstract static class SimpleRenderer<T> extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setText(getText((T) value));
            label.setToolTipText(getToolTipText((T) value));
            label.setIcon(getIcon((T) value));
            if (!isSelected) {
                label.setForeground(getColor((T) value));
            }
            return label;
        }

        protected String getText(T item) {
            return item.toString();
        }

        protected String getToolTipText(T item) {
            return null;
        }

        protected Color getColor(T item) {
            return null;
        }

        protected Icon getIcon(T item) {
            return null;
        }
    }

    static class SeriesRenderer extends SimpleRenderer<SaNode> {

        @Override
        protected String getText(SaNode item) {
            String name = item.getName();
            return MultiLineNameUtil.join(name);
        }

        @Override
        protected String getToolTipText(SaNode item) {
            String name = item.getName();
            return name != null && !name.isEmpty() ? MultiLineNameUtil.toHtml(name) : null;
        }

        @Override
        public Icon getIcon(SaNode item) {
            return DataSourceManager.get().getIcon(item.getMoniker(), BeanInfo.ICON_COLOR_16x16, false);
        }
    }

    static class MethodRenderer extends SimpleRenderer<SaNode> {

        @Override
        protected String getText(SaNode item) {
            SaSpecification currentDomainSpec = item.domainSpec();
            List<WorkspaceItem<SaSpecification>> x = WorkspaceFactory.getInstance().getActiveWorkspace().searchDocuments(SaSpecification.class);
            Optional<WorkspaceItem<SaSpecification>> y = x.stream().filter(workspaceItem -> workspaceItem.getElement().equals(currentDomainSpec)).findFirst();
            return y.isPresent() ? y.get().getDisplayName() : currentDomainSpec.display();
        }

        @Override
        protected Color getColor(SaNode item) {
            SaSpecification spec = item.getSpec();
            DocumentUIServices ui = DocumentUIServices.forSpec(spec.getClass());
            return ui == null ? null : ui.getColor();
        }
    }

    static class EstimationRenderer extends SimpleRenderer<SaNode> {

        @Override
        protected String getText(SaNode item) {
            if (item.getOutput() == null) {
                return "";
            }
            EstimationPolicyType policy = item.getOutput().getDefinition().getPolicy();
            return switch (policy) {
                case Fixed ->
                    "Fixed model";
                case FixedParameters ->
                    "Reg. coefficients";
                case FreeParameters ->
                    "Arima parameters";
                case LastOutliers ->
                    "Last outliers";
                case Outliers_StochasticComponent ->
                    "Arima model";
                case Complete ->
                    "Concurrent";
                case None ->
                    "New";
                default ->
                    policy.name();
            };
        }
    }

    static class StatusRenderer extends SimpleRenderer<SaNode> {

        @Override
        protected String getText(SaNode item) {
            return item.getStatus().toString();
        }

        @Override
        protected Color getColor(SaNode item) {
            return switch (item.getStatus()) {
                case Unprocessed ->
                    Color.GRAY;
                case Pending ->
                    Color.ORANGE;
                case Valid ->
                    null;
                default ->
                    Color.RED;
            };
        }
    }

    static class PriorityRenderer extends SimpleRenderer<SaNode> {

        @Override
        protected String getText(SaNode item) {
            int priority = item.getOutput() == null ? 0 : item.getOutput().getPriority();
            return priority > 0 ? Integer.toString(priority) : null;
        }
    }

    static class QualityRenderer extends SimpleRenderer<SaNode> {

        @Override
        protected String getText(SaNode item) {
            if (!item.isProcessed()) {
                return null;
            }
            ProcQuality quality = item.results().getQuality();
            return quality != null ? quality.name() : null;
        }

        @Override
        protected Color getColor(SaNode item) {
            return item.isProcessed() ? getColor(item.results().getQuality()) : null;
        }

        Color getColor(ProcQuality quality) {
            switch (quality) {
                case Error -> {
                    return Color.RED.darker().darker();
                }
                case Severe -> {
                    return Color.RED.darker();
                }
                case Bad -> {
                    return Color.RED;
                }
                case Uncertain -> {
                    return Color.ORANGE.darker();
                }
                case Good -> {
                    return Color.GREEN.darker();
                }
                case Accepted -> {
                    return Color.GRAY;
                }
                case Undefined -> {
                    return null;
                }
            }
            throw new RuntimeException();
        }
    }

    static class WarningsRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setText("");
            SaNode item = (SaNode) value;
            if (!item.isProcessed()) {
                return label;
            }
            String[] warnings = item.results().getLog().
                    all().stream()
                    .filter(log -> log.getType() == InformationType.Warning)
                    .map(info -> info.getMsg()).toArray(n -> new String[n]);
            if (warnings.length == 0) {
                return label;
            }
            char[] tmp = new char[warnings.length];
            Arrays.fill(tmp, '!');
            label.setText(String.valueOf(tmp));
            label.setToolTipText(Joiner.on(". ").join(warnings));
            return label;
        }
    }

    static class CommentsRenderer extends SimpleRenderer<SaNode> {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        }

        @Override
        protected String getText(SaNode item) {
            return null;
        }

        @Override
        protected String getToolTipText(SaNode item) {
            if (item.getOutput() == null) {
                return null;
            }
            String comment = item.getOutput().getComment();
            if (comment != null && !comment.isEmpty()) {
                return MultiLineNameUtil.toHtml(comment);
            } else {
                return null;
            }
        }

        @Override
        protected Color getColor(SaNode item) {
            return item.getOutput() == null ? null : item.getOutput().getDefinition().getTs().isFrozen() ? Color.gray : null;
        }

        @Override
        public Icon getIcon(SaNode item) {
            if (item.getOutput() == null) {
                return null;
            }
            String comment = item.getOutput().getComment();
            if (comment != null && !comment.isEmpty()) {
                return DemetraIcons.COMMENT;
            } else {
                return null;
            }
        }
    }

    class SaProcessingModel extends ListTableModel<SaNode> {

        static final int SERIES = 0, METHOD = 1, ESTIMATION = 2, STATUS = 3, PRIORITY = 4, QUALITY = 5, WARNINGS = 6, COMMENTS = 7;
        final List<String> columnNames = Arrays.asList("Series", "Method", "Estimation", "Status", "Priority", "Quality", "Warnings", "Comments");

        @Override
        protected List<String> getColumnNames() {
            return columnNames;
        }

        @Override
        protected List<SaNode> getValues() {
            MultiProcessingDocument element = getElement();
            return element == null ? Collections.emptyList() : element.getCurrent();
        }

        @Override
        protected Object getValueAt(SaNode row, int columnIndex) {
            return row;
        }
    }

    private class SwingWorkerImpl extends SwingWorker<Void, SaNode> {

        private final SaNode[] items;
        private final ModellingContext context = ModellingContext.getActiveContext();

        public SwingWorkerImpl(boolean all) {
            if (all) {
                List<SaNode> current = getElement().getCurrent();
                items = current.stream().filter(o -> !o.getOutput().isProcessed())
                        .peek(o -> o.setStatus(SaNode.Status.Pending))
                        .toArray(SaNode[]::new);
            } else {
                SaNode[] sel = getSelection();
                items = Arrays.stream(sel).filter(o -> !o.getOutput().isProcessed())
                        .peek(o -> o.setStatus(SaNode.Status.Pending))
                        .toArray(SaNode[]::new);
            }
        }

        @Override
        protected Void doInBackground() {
            List<Callable<String>> tasks = createTasks();
            if (tasks == null) {
                return null;
            }

            DemetraBehaviour options = DemetraBehaviour.get();
            int nThread = options.getBatchPoolSize().getSize();
            int priority = options.getBatchPriority().getPriority();

            ExecutorService executorService = Executors.newFixedThreadPool(nThread, new ThreadFactoryBuilder().setDaemon(true).setPriority(priority).build());
            Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                executorService.invokeAll(tasks);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.log(Level.INFO, "While processing SaItems", ex);
            }

            if (!tasks.isEmpty()) {
                if (worker != null && !worker.isCancelled()) {
                    NotifyUtil.show("SA Processing done !", "Processed " + tasks.size() + " items in " + stopwatch.stop(), MessageType.SUCCESS, null, null, null);
                }

                if (!active) {
                    requestAttention(false);
                }
            }

            log.log(Level.INFO, String.format("Task: %s items in %s by %s executors with priority %s", tasks.size(), stopwatch.stop(), nThread, priority));
            executorService.shutdown();
            return null;

        }

        private List<Callable<String>> createTasks() {
            List<Callable<String>> result = new ArrayList(items.length);
            for (final SaNode o : items) {
                result.add((Callable<String>) () -> {
                    o.process(context, true);
                    publish(o);
                    SaEstimation result1 = o.getOutput().getEstimation();
                    String rslt = result1 == null ? " failed" : " processed";
                    return rslt;
                });
            }
            return result;
        }

        int progressCount = 0;

        @Override
        protected void process(List<SaNode> chunks) {
            model.fireTableDataChanged();
            progressCount += chunks.size();
            if (progressHandle != null) {
                if (!chunks.isEmpty()) {
                    progressHandle.progress(chunks.get(chunks.size() - 1).getName(), progressCount);
                } else {
                    progressHandle.progress(progressCount);
                }
            }
        }
    }

}
