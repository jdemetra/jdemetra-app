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
package ec.nbdemetra.anomalydetection.ui;

import com.google.common.base.Stopwatch;
import ec.nbdemetra.anomalydetection.ControlNode;
import ec.nbdemetra.ui.ActiveViewManager;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.IActiveView;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.notification.MessageType;
import ec.nbdemetra.ui.notification.NotifyUtil;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.ui.chart.JTsChart;
import ec.ui.interfaces.ITsCollectionView;
import ec.ui.interfaces.ITsGrid;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top component for the outlier detection of Ts
 *
 * @author Mats Maggi
 */
@TopComponent.Description(preferredID = "OutliersTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "ec.nbdemetra.anomalydetection.ui.OutliersTopComponent")
@ActionReference(path = "Menu/Statistical methods/Anomaly Detection")
@TopComponent.OpenActionRegistration(displayName = "#CTL_OutliersTopComponentAction")
@NbBundle.Messages({
    "CTL_OutliersTopComponentAction=Outliers Detection",
    "CTL_OutliersTopComponent=Outliers Detection Window",
    "HINT_OutliersTopComponent=This is an Outlier Detection Window"
})
public class OutliersTopComponent extends TopComponent implements ExplorerManager.Provider, MultiViewElement, IActiveView {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutliersTopComponent.class);
    public static final String STATE_CHANGED = "Processing state changed";
    private final JTsAnomalyGrid grid;
    private final AnomalyDetectionSummary summary;
    private final ExplorerManager mgr = new ExplorerManager();
    private Node node;
    private final JSplitPane visualRepresentation;
    private final JSplitPane tsInformation;
    private final JTsChart chart;
    private JToolBar toolBar;
    private JButton runButton;
    private JLabel itemsLabel;
    private JButton prefButton;
    private Stopwatch stopwatch;
    private boolean active;
    private int nbElements = 0;

    private void calculateNbItems() {
        if (grid.getTsCollection() != null) {
            nbElements = grid.getTsCollection().getCount();
            itemsLabel.setText(nbElements == 0 ? "No items" : nbElements + (nbElements < 2 ? " item" : " items"));
        } else {
            nbElements = 0;
            itemsLabel.setText(String.valueOf(nbElements));
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public OutliersTopComponent() {
        setName("Outliers Detection");
        grid = new JTsAnomalyGrid();
        summary = new AnomalyDetectionSummary();

        grid.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case JTsAnomalyGrid.COLLECTION_PROPERTY:
                    case JTsAnomalyGrid.TYPES_PROPERTY:
                        refreshSummary();
                        calculateNbItems();
                        runButton.setEnabled(nbElements != 0);
                        break;
                    case JTsAnomalyGrid.SPEC_CHANGE_PROPERTY:
                    case JTsAnomalyGrid.CRITICAL_VALUE_PROPERTY:
                    case ITsGrid.SELECTION_PROPERTY:
                    case JTsAnomalyGrid.TRANSFORMATION_PROPERTY:
                        refreshSummary();
                        break;
                    case STATE_CHANGED:
                        onStateChanged((SwingWorker.StateValue) evt.getNewValue());
                        break;
                }
            }
        });

        chart = new JTsChart();
        chart.setTsUpdateMode(ITsCollectionView.TsUpdateMode.None);
        chart.setLegendVisible(false);

        tsInformation = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, summary, chart);
        tsInformation.setResizeWeight(0.7);

        visualRepresentation = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, grid, tsInformation);
        visualRepresentation.setResizeWeight(0.6);

        toolBar = createToolBar();

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(visualRepresentation, BorderLayout.CENTER);

        node = ControlNode.onComponentOpened(mgr, grid);

        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
        try {
            mgr.setSelectedNodes(new Node[]{node});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void refreshSummary() {
        summary.set(grid.getModelOfSelection());
        refreshChart();
        summary.repaint();
    }

    private void refreshChart() {
        Ts[] selection = grid.getSelection();
        if (selection != null && selection.length == 1) {
            TsCollection col = TsFactory.instance.createTsCollection();
            col.add(selection[0]);
            chart.setTsCollection(col);
        } else {
            chart.setTsCollection(null);
        }

    }

    private void onStateChanged(SwingWorker.StateValue state) {
        switch (state) {
            case STARTED:
                if (stopwatch == null) {
                    stopwatch = Stopwatch.createStarted();
                } else if (!stopwatch.isRunning()) {
                    stopwatch.reset();
                    stopwatch.start();
                }
                makeBusy(true);
                runButton.setEnabled(false);
                break;
            case DONE:
            case PENDING:
                runButton.setEnabled(!grid.getTsCollection().isEmpty());
                makeBusy(false);
                NotifyUtil.show("Done !", "Processed " + grid.getTsCollection().getCount() + " items in " + stopwatch.stop().toString(), MessageType.SUCCESS, null, null, null);
                if (!active) {
                    requestAttention(false);
                }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Creation of Toolbar">
    private JToolBar createToolBar() {
        JToolBar result = NbComponents.newInnerToolbar();
        result.setFloatable(false);
        result.addSeparator();
        result.add(Box.createRigidArea(new Dimension(5, 0)));
        runButton = result.add(new AbstractAction("", DemetraUiIcon.COMPILE_16) {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid.calculateOutliers();
            }
        });
        runButton.setDisabledIcon(ImageUtilities.createDisabledIcon(runButton.getIcon()));
        runButton.setToolTipText("Run Outliers Detection");
        runButton.setEnabled(false);
        result.addSeparator();

        itemsLabel = (JLabel) result.add(new JLabel("No items"));
        result.addSeparator();

        prefButton = result.add(new AbstractAction("", DemetraUiIcon.PREFERENCES) {
            @Override
            public void actionPerformed(ActionEvent e) {
                OpenIdePropertySheetBeanEditor.editNode(node, "Properties", null);
            }
        });
        prefButton.setToolTipText("Open the properties dialog");
        result.addSeparator();

        result.add(createZoomButton());
        result.add(Box.createHorizontalGlue());
        result.addSeparator();

        return result;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Creation of Zoom Button">
    private JButton createZoomButton() {
        final JPopupMenu addPopup = new JPopupMenu();
        int[] zoomValues = {200, 100, 75, 50, 25};
        for (int i = 0; i < zoomValues.length; ++i) {
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(zoomValues[i] + " %");
            menuItem.setName(Integer.toString(zoomValues[i]));
            menuItem.addActionListener(new AbstractAction(Integer.toString(zoomValues[i])) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    grid.setZoomPercentage(Integer.parseInt(getValue(NAME).toString()));
                }
            });
            menuItem.setState(i == 1);
            menuItem.setEnabled(i != 1);
            addPopup.add(menuItem);
        }
        grid.addPropertyChangeListener(ITsGrid.ZOOM_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                for (Component o : addPopup.getComponents()) {
                    JCheckBoxMenuItem item = (JCheckBoxMenuItem) o;
                    item.setState(grid.getZoomPercentage() == Integer.parseInt(o.getName()));
                    item.setEnabled(!item.isSelected());
                }
            }
        });

        JButton result = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.MAGNIFYING_TOOL, addPopup);
        result.setToolTipText("Zoom ratio of the current view");
        return result;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters / Setters">
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

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
        return toolBar;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback mvec) {
        // Do nothing
    }

    @Override
    public CloseOperationState canCloseElement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean fill(JMenu menu) {
        return false;
    }

    @Override
    public boolean hasContextMenu() {
        return false;
    }

    @Override
    public Node getNode() {
        return node;
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
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
        ActiveViewManager.getInstance().set(null);
        active = false;
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        ActiveViewManager.getInstance().set(this);
        active = true;
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
}
