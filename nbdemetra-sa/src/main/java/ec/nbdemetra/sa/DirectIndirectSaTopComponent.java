/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ec.nbdemetra.sa.composite.DirectIndirectViewFactory;
import ec.nbdemetra.ui.ActiveViewManager;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.IActiveView;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ws.ui.SpecSelectionComponent;
import ec.satoolkit.ISaSpecification;
import ec.satoolkit.benchmarking.MultiSaBenchmarkingSpec;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.Ts;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.TsStatus;
import ec.tss.sa.composite.MultiSaDocument;
import ec.tss.sa.composite.MultiSaSpecification;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.chart.JTsChart;
import ec.ui.list.JTsList;
import ec.ui.view.AbstractDocumentViewer;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ThreadFactory;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//ec.nbdemetra.sa//DirectIndirectSa//EN",
autostore = false)
@TopComponent.Description(preferredID = "DirectIndirectSaTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "ec.nbdemetra.sa.DirectIndirectSaTopComponent")
@ActionReference(path = "Menu/Statistical methods/Seasonal Adjustment/Tools", position = 350)
@TopComponent.OpenActionRegistration(displayName = "#CTL_DirectIndirectSaAction",
preferredID = "DirectIndirectSaTopComponent")
@Messages({
    "CTL_DirectIndirectSaAction=Direct-Indirect Seasonal Adjustment",
    "CTL_DirectIndirectSaTopComponent=Direct-Indirect Seasonal Adjustment Window",
    "HINT_DirectIndirectSaTopComponent=This is a Direct-Indirect Seasonal Adjustment window"
})
public final class DirectIndirectSaTopComponent extends TopComponent implements IActiveView, ExplorerManager.Provider {

    // CONSTANTS
    private static final Logger LOGGER = LoggerFactory.getLogger(SaBatchUI.class);
    private static final int NBR_EXECUTORS = Runtime.getRuntime().availableProcessors();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).setPriority(Thread.MIN_PRIORITY).build();
    // PROPERTIES DEFINITIONS
    public static final String DEFAULT_SPECIFICATION_PROPERTY = "specificationProperty";
    public static final String PROCESSING_PROPERTY = "processing";
    public static final String STATE_PROPERTY = "state";
    // PROPERTIES
    // main components
    private final JSplitPane visualRepresentation;
    private final JSplitPane mainPane;
    private final JTsList inputList;
    private final JTsChart saChart;
    private final JToolBar toolBarRepresentation;
    // toolBar stuff
    private final JButton runButton;
    private final JLabel defSpecLabel;
    private Node node;
    private MultiSaDocumentView diView;
    private MultiSaSpecification curSpec;
    private SwingWorker worker;

    public DirectIndirectSaTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(DirectIndirectSaTopComponent.class, "CTL_DirectIndirectSaTopComponent"));
        setToolTipText(NbBundle.getMessage(DirectIndirectSaTopComponent.class, "HINT_DirectIndirectSaTopComponent"));

        curSpec = new MultiSaSpecification();
        curSpec.setDefaultSpecification(TramoSeatsSpecification.RSAfull);
        curSpec.setTotalSpecification(TramoSeatsSpecification.RSAfull);

        toolBarRepresentation = NbComponents.newInnerToolbar();
        toolBarRepresentation.setFloatable(false);
        toolBarRepresentation.addSeparator();
        toolBarRepresentation.add(Box.createRigidArea(new Dimension(5, 0)));
        runButton = toolBarRepresentation.add(new AbstractAction("", DemetraUiIcon.COMPILE_16) {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });
        runButton.setDisabledIcon(ImageUtilities.createDisabledIcon(runButton.getIcon()));
        toolBarRepresentation.addSeparator();
        toolBarRepresentation.addSeparator();
        toolBarRepresentation.addSeparator();
        defSpecLabel = (JLabel) toolBarRepresentation.add(new JLabel());
        defSpecLabel.setText(curSpec.getDefaultSpecification() == null ? "" : curSpec.getDefaultSpecification().toLongString());
        defSpecLabel.setToolTipText("Double click to change the default specification.");
        defSpecLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    editDefaultSpecification();
                }
            }
        });
        toolBarRepresentation.add(Box.createHorizontalGlue());
        toolBarRepresentation.addSeparator();
        inputList = new JTsList();
        initList();
        saChart = new JTsChart();
        mainPane = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputList, saChart);
        diView = new MultiSaDocumentView();
        diView.setDocument(new MultiSaDocument());
        visualRepresentation = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, mainPane, diView);
        visualRepresentation.setOneTouchExpandable(true);

        setLayout(new BorderLayout());
        add(toolBarRepresentation, BorderLayout.NORTH);
        add(visualRepresentation, BorderLayout.CENTER);

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String p = evt.getPropertyName();
//                if (p.equals(DEFAULT_SPECIFICATION_PROPERTY)) {
//                    onDefaultSpecificationChange();
//                }
//                else if (p.equals(PROCESSING_PROPERTY)) {
//                    onProcessingChange();
//                }
//                else if (p.equals(STATE_PROPERTY)) {
//                    onStateChange();
//                }
//                else if (p.equals(SELECTION_PROPERTY)) {
//                    onSelectionChange();
//                }
            }
        });


        node = new InternalNode();
        associateLookup(ExplorerUtils.createLookup(ActiveViewManager.getInstance().getExplorerManager(), getActionMap()));
    }

    private void initList() {
        inputList.addPropertyChangeListener(JTsList.COLLECTION_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                TsData sum = null;
                for (Ts s : inputList.getTsCollection()) {
                    if (s.hasData() == TsStatus.Undefined) {
                        s.load(TsInformationType.Data);
                    }
                    sum = TsData.add(sum, s.getTsData());
                }
                Ts t = TsFactory.instance.createTs("Total", null, sum);
                saChart.getTsCollection().replace(t);
                clear();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainPane.setDividerLocation(.5);
                mainPane.setResizeWeight(.5);
                visualRepresentation.setDividerLocation(.4);
                visualRepresentation.setResizeWeight(.4);
            }
        });
    }

    @Override
    public void componentClosed() {
        stop();
        // TODO add custom code on component closing
        //diView.dispose();
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

    private void start() {
        if (worker != null && !worker.isDone()) {
            NotifyDescriptor nd = new NotifyDescriptor.Message("Please, wait for the end of the processing.");
            DialogDisplayer.getDefault().notify(nd);
            return;
        }
        this.makeBusy(true);
        MultiSaDocument doc = diView.getDocument();
        doc.setSpecification(curSpec.clone());
        doc.setTsCollection(inputList.getTsCollection());
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                diView.getDocument().getResults();
                return null;
            }

            @Override
            public void done() {
                DirectIndirectSaTopComponent.this.makeBusy(false);
                diView.refresh();
            }
        };
        worker.execute();
    }

    private void stop() {
        if (worker != null) {
            worker.cancel(true);
        }
    }

    private void clear() {
        diView.getDocument().setTsCollection(null);
        diView.refresh();
    }

    private void setDefaultSpecification(ISaSpecification spec) {
        curSpec.setDefaultSpecification(spec);
        curSpec.setTotalSpecification(spec);
        defSpecLabel.setText(spec == null ? "" : spec.toLongString());
    }

    private void editDefaultSpecification() {
        SpecSelectionComponent c = new SpecSelectionComponent();
        c.setSpecification(curSpec.getDefaultSpecification());
        DialogDescriptor dd = c.createDialogDescriptor("Choose active specification");
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            setDefaultSpecification(c.getSpecification());
            start();
        }
    }

    class InternalNode extends AbstractNode {

        InternalNode() {
            super(Children.LEAF);
            setDisplayName("Direct-Indirect SA");
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();
            Set bench = Sheet.createPropertiesSet();
            bench.setName("Benchmarking");
            bench.setDisplayName("Benchmarking");
            Property<Double> rho = new Property(Double.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return curSpec.getBenchmarkingSpecification().getRho();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    double rho = (Double) t;
                    curSpec.getBenchmarkingSpecification().setRho(rho);
                    clear();
                }
            };
            rho.setName("Rho");
            bench.put(rho);

            Property<Double> lambda = new Property(Double.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return curSpec.getBenchmarkingSpecification().getLambda();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    double lambda = (Double) t;
                    curSpec.getBenchmarkingSpecification().setLambda(lambda);
                    clear();
                }
            };
            lambda.setName("Lambda");
            bench.put(lambda);

            Property<MultiSaBenchmarkingSpec.ConstraintType> ctype = new Property(MultiSaBenchmarkingSpec.ConstraintType.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return curSpec.getBenchmarkingSpecification().getContemporaneousConstraintType();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    MultiSaBenchmarkingSpec.ConstraintType c = (MultiSaBenchmarkingSpec.ConstraintType) t;
                    curSpec.getBenchmarkingSpecification().setContemporaneousConstraintType(c);
                    clear();
                }
            };

            ctype.setName("Contemporaneous constraint");
            bench.put(ctype);

            Property<Boolean> annual = new Property(Boolean.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return curSpec.getBenchmarkingSpecification().isAnnualConstraint();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    boolean a = (Boolean) t;
                    curSpec.getBenchmarkingSpecification().setAnnualConstraint(a);
                    clear();
                }
            };
            annual.setName("Annual constraint");
            bench.put(annual);

            sheet.put(bench);
            return sheet;
        }
    }
}

class MultiSaDocumentView extends AbstractDocumentViewer<MultiSaDocument> {

    @Override
    protected IProcDocumentView<MultiSaDocument> getView(MultiSaDocument doc) {
        return DirectIndirectViewFactory.getDefault().create(doc);
    }
}
