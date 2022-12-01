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
package demetra.desktop.anomalydetection.ui;

import com.google.common.base.Stopwatch;
import demetra.desktop.DemetraBehaviour;
import demetra.desktop.DemetraIcons;
import demetra.desktop.anomalydetection.AnomalyItem;
import demetra.desktop.anomalydetection.ControlNode;
import demetra.desktop.anomalydetection.report.CheckLastReportAction;
import demetra.desktop.components.JTsChart;
import demetra.desktop.components.parts.HasTsCollection.TsUpdateMode;
import demetra.desktop.concurrent.UIExecutors;
import demetra.desktop.notification.MessageType;
import demetra.desktop.notification.NotifyUtil;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.tools.ToolsPersistence;
import demetra.desktop.ui.ActiveView;
import demetra.desktop.ui.ActiveViewManager;
import demetra.desktop.util.NbComponents;
import demetra.timeseries.TsCollection;
import demetra.tramo.TramoSpec;
import ec.util.list.swing.JLists;
import jdplus.regsarima.regular.CheckLast;
import jdplus.tramo.TramoKernel;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

/**
 * Top Component for Check Last batch processing
 *
 * @author Mats Maggi
 */
@ConvertAsProperties(dtd = "-//demetra.desktop.anomalydetection.ui/CheckLastList//EN",
        autostore = false)
@TopComponent.Description(preferredID = "CheckLastTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "demetra.desktop.anomalydetection.ui.CheckLastTopComponent")
@ActionReference(path = "Menu/Statistical methods/Anomaly Detection")
@TopComponent.OpenActionRegistration(displayName = "#CTL_CheckLastTopComponentAction")
@NbBundle.Messages({
        "CTL_CheckLastTopComponentAction=Check Last",
        "CTL_CheckLastTopComponent=Check Last Batch Window",
        "HINT_CheckLastTopComponent=This is a Check Last Batch Window"
})
@lombok.extern.java.Log
public final class CheckLastTopComponent extends TopComponent implements ExplorerManager.Provider, MultiViewElement, ActiveView {

    public static final String DEFAULT_SPECIFICATION_PROPERTY = "specificationProperty";
    public static final String STATE_PROPERTY = "state";
    // Main Components
    private final JSplitPane visualRepresentation;
    private final JSplitPane tsInformation;
    private final JToolBar toolBarRepresentation;
    // ToolBar stuff
    private JButton runButton;
    private JLabel itemsLabel;
    private JLabel defSpecLabel;
    private JButton prefButton;
    private JButton reportButton;
    // Visual Stuff
    private final JTsCheckLastList list;
    private final JCheckLastSummary summary;
    private final JTsChart chart;
    // Thread Stuff
    private ProgressHandle progressHandle;
    private SwingWorker<Void, AnomalyItem> worker;
    // Properties
    private Node n;
    private final ExplorerManager mgr = new ExplorerManager();

    public CheckLastTopComponent() {
        setName("Check Last Batch");
        list = new JTsCheckLastList();
        summary = new JCheckLastSummary();
        toolBarRepresentation = createToolBar();

        list.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case JTsCheckLastList.COLOR_VALUES_PROPERTY:
                    refreshNode();
                    break;
                case JTsCheckLastList.COLLECTION_CHANGE_PROPERTY:
                    onCollectionChange();
                    break;
                case JTsCheckLastList.ITEM_SELECTION_PROPERTY:
                    onSelectionChange();
                    break;
                case JTsCheckLastList.LAST_CHECKS_PROPERTY:
                    onNbCheckLastChange();
                    break;
            }
        });

        addPropertyChangeListener(evt -> {
            String p = evt.getPropertyName();
            if (p.equals(STATE_PROPERTY)) {
                onStateChange();
            }
        });

        chart = new JTsChart();
        chart.setTsUpdateMode(TsUpdateMode.None);
        chart.setLegendVisible(false);

        tsInformation = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, summary, chart);
        tsInformation.setResizeWeight(0.5);

        visualRepresentation = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, list, tsInformation);
        visualRepresentation.setResizeWeight(0.6);

        setLayout(new BorderLayout());
        add(toolBarRepresentation, BorderLayout.NORTH);
        add(visualRepresentation, BorderLayout.CENTER);

        refreshNode();
        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
    }

    private void refreshNode() {
        n = ControlNode.onComponentOpened(mgr, list);

        try {
            mgr.setSelectedNodes(new Node[]{n});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Create Tool Bar">
    private JToolBar createToolBar() {
        JToolBar toolBar = NbComponents.newInnerToolbar();
        toolBar.setFloatable(false);
        toolBar.addSeparator();
        toolBar.add(Box.createRigidArea(new Dimension(5, 0)));
        runButton = toolBar.add(new AbstractAction("", DemetraIcons.COMPILE_16) {
            @Override
            public void actionPerformed(ActionEvent e) {
                start(false);
            }
        });
        runButton.setDisabledIcon(ImageUtilities.createDisabledIcon(runButton.getIcon()));
        runButton.setToolTipText("Run the Check Last processing");
        runButton.setEnabled(false);
        toolBar.addSeparator();
        itemsLabel = (JLabel) toolBar.add(new JLabel("No items"));
        toolBar.addSeparator();

        toolBar.add(createLastButton(list));
        toolBar.addSeparator();

        prefButton = toolBar.add(new AbstractAction("", DemetraIcons.PREFERENCES) {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PropertySheetDialogBuilder().title("Properties").editNode(n);
            }
        });
        prefButton.setToolTipText("Open the properties dialog");
        toolBar.addSeparator();

        toolBar.add(createSpecDropDownButton(list));
        defSpecLabel = (JLabel) toolBar.add(new JLabel(list.getSpec() == null ? "" : list.getSpec().display()));

        toolBar.add(Box.createHorizontalGlue());
        toolBar.addSeparator();

        reportButton = toolBar.add(new AbstractAction("", DemetraIcons.DOCUMENT_16) {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        reportButton.setText("Generate Report");
        reportButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        reportButton.setToolTipText("Generate report of the check last processing");
        reportButton.setEnabled(false);

        return toolBar;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Create Spec Change Button">
    private JButton createSpecDropDownButton(final JTsCheckLastList view) {
        final JPopupMenu addPopup = new JPopupMenu();
        TramoSpec[] specs = TramoSpec.allSpecifications();
        for (int i = 0; i < specs.length; ++i) {
            String name = specs[i].display();
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name);
            menuItem.setName(name);
            menuItem.addActionListener(new AbstractAction(name) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    TramoSpec s = TramoSpec.TR0;
                    JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
                    try {
                        s = (TramoSpec) TramoSpec.class.getDeclaredField(source.getText()).get(TramoSpec.DEFAULT);
                    } catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
                        log.log(Level.SEVERE, "Tramo Specification " + source.getText() + " can't be accessed !");
                    }
                    view.setSpec(s);
                }
            });
            menuItem.setState(i == specs.length - 1);
            menuItem.setEnabled(i != specs.length - 1);
            addPopup.add(menuItem);
        }
        view.addPropertyChangeListener(JTsCheckLastList.SPEC_PROPERTY, evt -> {
            refreshNode();
            for (Component o : addPopup.getComponents()) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) o;
                item.setState(view.getSpec().display().equals(o.getName()));
                item.setEnabled(!item.isSelected());
                defSpecLabel.setText(view.getSpec().display());
                reportButton.setEnabled(false);
            }
        });

        JButton result = DropDownButtonFactory.createDropDownButton(DemetraIcons.BLOG_16, addPopup);
        result.setToolTipText("Specification used for the Check Last processing");
        return result;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Create Number of Check Last Button">
    private JButton createLastButton(final JTsCheckLastList view) {
        final JPopupMenu addPopup = new JPopupMenu();
        for (int i = 1; i < 4; ++i) {
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(Integer.toString(i));
            menuItem.setName(Integer.toString(i));
            menuItem.addActionListener(new AbstractAction(Integer.toString(i)) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    view.setLastChecks(Integer.parseInt(getValue(NAME).toString()));
                }
            });
            menuItem.setState(i == 1);
            menuItem.setEnabled(i != 1);
            addPopup.add(menuItem);
        }
        view.addPropertyChangeListener(JTsCheckLastList.LAST_CHECKS_PROPERTY, evt -> {
            refreshNode();
            for (Component o : addPopup.getComponents()) {
                JCheckBoxMenuItem item = (JCheckBoxMenuItem) o;
                item.setState(view.getLastChecks() == Integer.parseInt(item.getName()));
                item.setEnabled(!item.isSelected());
            }
        });

        JButton result = DropDownButtonFactory.createDropDownButton(DemetraIcons.NB_CHECK_LAST_16, addPopup);
        result.setToolTipText("Number of last observations to check");
        return result;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Component States">
    @Override
    public void componentOpened() {
        tsInformation.setDividerLocation((int) (((double) visualRepresentation.getPreferredSize().height) * 0.8));
    }

    @Override
    public void componentClosed() {
        mgr.setRootContext(Node.EMPTY);
        stop();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
        ActiveViewManager.getInstance().set(null);
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        ActiveViewManager.getInstance().set(this);
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters / Setters">
    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    @Override
    public JComponent getVisualRepresentation() {
        return visualRepresentation;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolBarRepresentation;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    // </editor-fold>

    private void generateReport() {
        CheckLastReportAction.process(list.getItems(), list.getReportParameters());
    }

    // <editor-fold defaultstate="collapsed" desc="Thread stuff">
    private class SwingWorkerImpl extends SwingWorker<Void, AnomalyItem> {

        private int progressCount = 0;
        private int ntasks;

        @Override
        protected Void doInBackground() {
            progressCount = 0;
            List<Callable<Void>> tasks = createTasks();
            if (tasks == null) {
                return null;
            }
            ntasks = tasks.size();

            DemetraBehaviour options = DemetraBehaviour.get();

            ExecutorService executorService = UIExecutors.newFixedThreadPool(options.getBatchPoolSize(), options.getBatchPriority());
            Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                executorService.invokeAll(tasks);
            } catch (InterruptedException ex) {
                log.log(Level.INFO, "Check Last interrupted while processing items");
            }

            if (tasks.size() > 0) {
                NotifyUtil.show("Check Last done !", "Processed " + tasks.size() + " items in " + stopwatch.stop(), MessageType.SUCCESS, null, null, null);
                if (!ActiveViewManager.getInstance().isActive(CheckLastTopComponent.this)) {
                    requestAttention(false);
                }
            }

            log.log(Level.INFO, String.format("Task: %s items in %s by %s executors with priority %s", tasks.size(), stopwatch.stop(), options.getBatchPoolSize(), options.getBatchPriority()));

            executorService.shutdown();

            return null;
        }

        List<Callable<Void>> createTasks() {
            AnomalyItem[] items = list.getItems();
            if (items.length > 0) {
                List<Callable<Void>> result = new ArrayList(items.length);
                for (final AnomalyItem o : items) {
                    if (!o.getData().isEmpty()) {
                        if (!o.isProcessed()) {
                            result.add(() -> {
                                if (isCancelled()) {
                                    return null;
                                }
                                CheckLast c = new CheckLast(TramoKernel.of(list.getSpec(), null), list.getLastChecks());
                                o.process(c);
                                publish(o);
                                return null;
                            });
                        }
                    }
                }
                return result;
            } else {
                return null;
            }
        }

        @Override
        protected void process(List<AnomalyItem> chunks) {
            list.fireTableDataChanged();
            progressCount += chunks.size();
            if (progressHandle != null) {
                if (!chunks.isEmpty()) {
                    progressHandle.progress(100 * progressCount / ntasks);
                }
            }
        }
    }

    protected void onStateChange() {
        switch (getState()) {
            case DONE:
                runButton.setEnabled(true);
                if (!list.getMap().isEmpty()) {
                    reportButton.setEnabled(true);
                }
                makeBusy(false);

                if (progressHandle != null) {
                    progressHandle.finish();
                }
                break;
            case PENDING:
                runButton.setEnabled(true);
                reportButton.setEnabled(false);
                break;
            case STARTED:
                runButton.setEnabled(false);
                reportButton.setEnabled(false);
                progressHandle = ProgressHandle.createHandle("Processing Check Last...", () -> worker.cancel(true));
                progressHandle.start(100);
                break;
        }
    }

    public boolean start(boolean local) {
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void onSelectionChange() {
        OptionalInt singleSelection = JLists.getSelectionIndexStream(list.getTsSelectionModel()).findFirst();
        if (!singleSelection.isPresent()) {
            summary.set(null, null);
            chart.setTsCollection(TsCollection.EMPTY);
        } else {
            demetra.timeseries.Ts single = list.getTsCollection().get(singleSelection.getAsInt());
            AnomalyItem a = list.getMap().get(single.getMoniker());
            if (a.isInvalid() || a.isNotProcessable()) {
                summary.set(null, null);
            } else if (!a.getData().isEmpty()) {
                CheckLast cl = new CheckLast(TramoKernel.of(list.getSpec(), null), list.getLastChecks());
                cl.check(a.getData());
                summary.set(a, cl.getModel());
            }

            chart.setTsCollection(TsCollection.of(single));
            chart.repaint();
        }
        summary.repaint();
    }

    private void onCollectionChange() {
        int nbElements = list.getMap().size();
        itemsLabel.setText(nbElements == 0 ? "No items" : nbElements + (nbElements < 2 ? " item" : " items"));
        summary.set(null, list.getCheckLast().getModel());
        summary.repaint();

        runButton.setEnabled(nbElements != 0);

        reportButton.setEnabled(false);
    }

    private void onNbCheckLastChange() {
        reportButton.setEnabled(false);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Properties I/O">
    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        ToolsPersistence.writeTsCollection(list, p);
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        ToolsPersistence.readTsCollection(list, p);
    }
    // </editor-fold>
}
