/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tools;

import ec.nbdemetra.ui.ActiveViewManager;
import ec.nbdemetra.ui.IActiveView;
import ec.nbdemetra.ui.MonikerUI;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.tsaction.ITsView2;
import ec.tss.Ts;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tstoolkit.stats.AutoCorrelations;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.grid.JTsGrid;
import ec.ui.interfaces.ITsCollectionView.TsUpdateMode;
import ec.ui.interfaces.ITsGrid.Mode;
import ec.ui.view.AutoCorrelationsView;
import ec.ui.view.PeriodogramView;
import java.awt.BorderLayout;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//ec.nbdemetra.ui.tools//Differencing//EN",
autostore = false)
@TopComponent.Description(preferredID = "DifferencingTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "ec.nbdemetra.ui.tools.DifferencingTopComponent")
@ActionReference(path = "Menu/Tools", position = 334)
@TopComponent.OpenActionRegistration(displayName = "#CTL_DifferencingAction",
preferredID = "DifferencingTopComponent")
@Messages({
    "CTL_DifferencingAction=Differencing",
    "CTL_DifferencingTopComponent=Differencing Window",
    "HINT_DifferencingTopComponent=This is a Differencing window"
})
public final class DifferencingTopComponent extends TopComponent implements ITsView2, IActiveView, ExplorerManager.Provider {

    private final JToolBar toolBar;
    // CONSTANTS
    private static final Font DROP_DATA_FONT = new JLabel().getFont().deriveFont(Font.ITALIC);
    // visual components
    private final JLabel dropDataLabel;
    private final JLabel tsLabel;
    private final JSplitPane splitter1;
    private final JSplitPane splitter2;
    private final PeriodogramView periodogramView;
    private final AutoCorrelationsView acView;
    private final JTsGrid grid;
    private Ts ts_;
    private Node node;
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
        acView = new AutoCorrelationsView();
        acView.setKind(AutoCorrelationsView.ACKind.Partial);
        periodogramView = new PeriodogramView();
        periodogramView.setTransferHandler(null);
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
        associateLookup(ExplorerUtils.createLookup(ActiveViewManager.getInstance().getExplorerManager(), getActionMap()));
    }

//    @Override
//    public void open() {
//        super.open();
//        org.openide.windows.Mode mode = WindowManager.getDefault().findMode("output");
//        if (mode != null && mode.canDock(this)) {
//            mode.dockInto(this);
//        }
//    }

    public void refreshHeader() {
        if (ts_ == null) {
            dropDataLabel.setVisible(true);
            tsLabel.setVisible(false);
        } else {
            dropDataLabel.setVisible(false);
            tsLabel.setText(ts_.getName());
            TsMoniker moniker = ts_.getMoniker();
            tsLabel.setIcon(MonikerUI.getDefault().getIcon(moniker));
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
    @Override
    public void componentOpened() {
        super.componentOpened();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                splitter2.setDividerLocation(.5);
                splitter2.setResizeWeight(.5);
                splitter1.setDividerLocation(.5);
                splitter1.setResizeWeight(.5);
            }
        });
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        //grid.dispose();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
        ActiveViewManager.getInstance().set(this);
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
        ActiveViewManager.getInstance().set(null);
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

    class TsHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return TssTransferSupport.getDefault().canImport(support.getDataFlavors());
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            Ts s = TssTransferSupport.getDefault().toTs(support.getTransferable());
            if (s != null) {
                setTs(s);
                return true;
            }
            return false;
        }
    }

    void showTests() {
        clear();
        TsData s = ts_.getTsData();
        if (isLog) {
            s = s.log();
        }
        if (diffOrder > 0) {
            s = s.delta(1, diffOrder);
        }
        int ifreq = s.getFrequency().intValue();
        if (ifreq > 1 && seasonalDiffOrder > 0) {
            s = s.delta(ifreq, seasonalDiffOrder);
        }
        Ts del = TsFactory.instance.createTs("Differenced series", null, s);
        grid.getTsCollection().replace(del);
        AutoCorrelations ac = new AutoCorrelations(s);
        acView.setLength(ifreq * 3);
        acView.setAutoCorrelations(ac);

        periodogramView.setData("Periodogram", ifreq, s);
    }

    private void clear() {
        acView.reset();
        periodogramView.reset();
        grid.getTsCollection().clear();
    }

    @Override
    public void setTs(Ts s) {
        ts_ = s;
        ts_.load(TsInformationType.All);
        refreshHeader();
        showTests();
    }

    @Override
    public Ts getTs() {
        return ts_;
    }

    @Override
    public boolean fill(JMenu menu) {
        return false;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return ActiveViewManager.getInstance().getExplorerManager();
    }

    @Override
    public boolean hasContextMenu() {
        return false;
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
