/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.core.tools;

import demetra.data.DoubleSeq;
import demetra.desktop.components.JTsGrid;
import demetra.desktop.components.JTsGrid.Mode;
import demetra.desktop.components.parts.HasTs;
import demetra.desktop.components.parts.HasTsCollection;
import demetra.desktop.components.parts.HasTsCollection.TsUpdateMode;
import demetra.desktop.components.tools.JAutoCorrelationsView;
import demetra.desktop.components.tools.PeriodogramView;
import demetra.desktop.datatransfer.DataTransfer;
import demetra.desktop.tsproviders.DataSourceProviderBuddySupport;
import demetra.desktop.util.NbComponents;
import demetra.desktop.ui.processing.TsTopComponent;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import demetra.timeseries.TsInformationType;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//demetra.desktop.core.tools//Differencing//EN",
        autostore = false)
@TopComponent.Description(preferredID = "DifferencingTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "demetra.desktop.core.tools.DifferencingTopComponent")
@ActionReference(path = "Menu/Tools", position = 334)
@TopComponent.OpenActionRegistration(displayName = "#CTL_DifferencingAction",
        preferredID = "DifferencingTopComponent")
@Messages({
        "CTL_DifferencingAction=Differencing",
        "CTL_DifferencingTopComponent=Differencing Window",
        "HINT_DifferencingTopComponent=This is a Differencing window"
})
public final class DifferencingTopComponent extends TopComponent  implements HasTs, ExplorerManager.Provider {

    private final ExplorerManager mgr = new ExplorerManager();
    private final JToolBar toolBar;
    // CONSTANTS
    private static final Font DROP_DATA_FONT = new JLabel().getFont().deriveFont(Font.ITALIC);
    // visual components
    private final JLabel dropDataLabel;
    private final JLabel tsLabel;
    private final JSplitPane splitter1;
    private final JSplitPane splitter2;
    
    @lombok.experimental.Delegate(types=HasTs.class)
    private final PeriodogramView periodogramView;
    private final JAutoCorrelationsView acView;
    private final JTsGrid grid;
    private final Node node;
    private boolean isLog = false;
    private int diffOrder = 1, seasonalDiffOrder = 1;

    public DifferencingTopComponent() {
        initComponents();
        setName(Bundle.CTL_DifferencingTopComponent());
        setToolTipText(Bundle.HINT_DifferencingTopComponent());
        this.toolBar = NbComponents.newInnerToolbar();
        toolBar.add(Box.createHorizontalGlue());
        toolBar.addSeparator();
        this.dropDataLabel = new JLabel("Drop data here");
        dropDataLabel.setFont(DROP_DATA_FONT);
        this.tsLabel = new JLabel();
        tsLabel.setVisible(false);

        toolBar.add(Box.createHorizontalStrut(3), 0);
        toolBar.add(dropDataLabel, 1);
        toolBar.add(tsLabel, 2);
        acView = new JAutoCorrelationsView();
        acView.setKind(JAutoCorrelationsView.ACKind.Normal);
        periodogramView = new PeriodogramView();
        periodogramView.setTransferHandler(null);
        periodogramView.setDifferencingOrder(0);
        periodogramView.setLogTransformation(false);
        grid = new JTsGrid();
        grid.setMode(Mode.SINGLETS);
        grid.setTsUpdateMode(TsUpdateMode.None);
        this.splitter2 = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, acView, periodogramView);
        splitter2.setOneTouchExpandable(true);
        this.splitter1 = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, grid, splitter2);
        splitter1.setOneTouchExpandable(true);

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(splitter1, BorderLayout.CENTER);
        setTransferHandler(new TsHandler());
        node = new InternalNode();
        
        periodogramView.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case TS_PROPERTY :
                    onTsChange();
                    break;
            }
        });
        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    @Override
    protected void componentOpened() {
        Node node = new InternalNode();
        try {
            mgr.setRootContext(node);
            mgr.setSelectedNodes(new Node[]{node});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
        SwingUtilities.invokeLater(() -> {
            splitter2.setDividerLocation(.5);
            splitter2.setResizeWeight(.5);
            splitter1.setDividerLocation(.5);
            splitter1.setResizeWeight(.5);
        });
    }

    @Override
    protected void componentClosed() {
        mgr.setRootContext(Node.EMPTY);
    }

//    @Override
//    public void open() {
//        super.open();
//        org.openide.windows.Mode mode = WindowManager.getDefault().findMode("output");
//        if (mode != null && mode.canDock(this)) {
//            mode.dockInto(this);
//        }
//    }
//    
    public void refreshHeader() {
        Ts ts=getTs();
        if (ts == null) {
            dropDataLabel.setVisible(true);
            tsLabel.setVisible(false);
        } else {
            dropDataLabel.setVisible(false);
            tsLabel.setText(ts.getName());
            demetra.timeseries.TsMoniker moniker = ts.getMoniker();
            tsLabel.setIcon(DataSourceProviderBuddySupport.getDefault().getIcon(moniker, BeanInfo.ICON_COLOR_16x16, false));
            tsLabel.setToolTipText(tsLabel.getText() + (moniker.getSource() != null ? (" (" + moniker.getSource() + ")") : ""));
            tsLabel.setVisible(true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(100, 100));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "3.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    class TsHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return DataTransfer.getDefault().canImport(support.getDataFlavors());
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            Optional<demetra.timeseries.Ts> s = DataTransfer.getDefault().toTs(support.getTransferable());
            if (s.isPresent()) {
                setTs(s.get());
                return true;
            }
            return false;
        }
    }

    private TsData requestData() {
        Ts ts = getTs();
        if (ts == null) {
            return null;
        }
        if (ts.getType().encompass(TsInformationType.Data)) {
            return ts.getData();
        } else {
            loadAsync(TsInformationType.Data);
            return null;
        }
    }
    
    void onTsChange(){
        showTests();
    }

    void showTests() {
        clear();
        TsData s = requestData();
        if (s == null || s.isEmpty())
            return;
        if (isLog) {
            s = s.log();
        }
        if (diffOrder > 0) {
            s = s.delta(1, diffOrder);
        }
        int ifreq = s.getAnnualFrequency();
        if (ifreq > 1 && seasonalDiffOrder > 0) {
            s = s.delta(ifreq, seasonalDiffOrder);
        }
       
        Ts del = Ts.builder()
                .name("Differenced series")
                .data(s)
                .build();
        grid.setTsCollection(TsCollection.of(del));
        acView.setLength(ifreq * 6);
        acView.setSample(s.getValues());

        periodogramView.setData("Periodogram", ifreq, s.getValues());
    }

    private void clear() {
        acView.setSample(DoubleSeq.empty());
        grid.setTsCollection(TsCollection.EMPTY);
    }

     class InternalNode extends AbstractNode {

        InternalNode() {
            super(Children.LEAF);
            setDisplayName("Differencing");
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();
            Sheet.Set transform = Sheet.createPropertiesSet();
            transform.setName("Transform");
            transform.setDisplayName("Transformation");
            Node.Property<Boolean> log = new Node.Property(Boolean.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return isLog;
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    isLog = (Boolean) t;
                    showTests();
                }
            };

            log.setName("Log");
            transform.put(log);
            Node.Property<Integer> diff = new Node.Property(Integer.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return diffOrder;
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    diffOrder = (Integer) t;
                    showTests();
                }
            };

            diff.setName("Regular Differencing");
            transform.put(diff);
            Node.Property<Integer> sdiff = new Node.Property(Integer.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return seasonalDiffOrder;
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    seasonalDiffOrder = (Integer) t;
                    showTests();
                }
            };

            sdiff.setName("Seasonal Differencing");
            transform.put(sdiff);
            sheet.put(transform);
            return sheet;
        }
    }
}
