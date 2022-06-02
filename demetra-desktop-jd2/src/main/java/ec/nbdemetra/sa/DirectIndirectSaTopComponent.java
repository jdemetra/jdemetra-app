/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import demetra.desktop.TsManager;
import ec.nbdemetra.sa.composite.DirectIndirectViewFactory;
import ec.nbdemetra.ui.ActiveViewManager;
import demetra.desktop.DemetraIcons;
import ec.nbdemetra.ui.IActiveView;
import demetra.desktop.util.NbComponents;
import ec.nbdemetra.ws.ui.JSpecSelectionComponent;
import ec.satoolkit.ISaSpecification;
import ec.satoolkit.benchmarking.MultiSaBenchmarkingSpec;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tss.sa.composite.MultiSaDocument;
import ec.tss.sa.composite.MultiSaSpecification;
import demetra.desktop.components.JTsChart;
import demetra.desktop.components.JTsTable;
import ec.ui.view.AbstractDocumentViewer;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
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
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

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
    "CTL_DirectIndirectSaAction=Direct-Indirect Seasonal Adjustment"
})
public final class DirectIndirectSaTopComponent extends TopComponent implements IActiveView, ExplorerManager.Provider {

    // PROPERTIES DEFINITIONS
    public static final String DEFAULT_SPECIFICATION_PROPERTY = "specificationProperty";
    public static final String PROCESSING_PROPERTY = "processing";
    public static final String STATE_PROPERTY = "state";
    // PROPERTIES
    // main components
    private final JSplitPane visualRepresentation;
    private final JSplitPane mainPane;
    private final JTsTable inputList;
    private final JTsChart saChart;
    private final JToolBar toolBarRepresentation;
    // toolBar stuff
    private final JButton runButton;
    private final JLabel defSpecLabel;
    private final Node node;
    private final MultiSaDocumentView diView;
    private final MultiSaSpecification curSpec;
    private SwingWorker worker;

    @Messages({
        "directIndirectSaTopComponent.setName=Direct-Indirect Seasonal Adjustment Window",
        "directIndirectSaTopComponent.setToolTipText=This is a Direct-Indirect Seasonal Adjustment window"
    })
    public DirectIndirectSaTopComponent() {
        initComponents();
        setName(Bundle.directIndirectSaTopComponent_setName());
        setToolTipText(Bundle.directIndirectSaTopComponent_setToolTipText());

        curSpec = new MultiSaSpecification();
        curSpec.setDefaultSpecification(TramoSeatsSpecification.RSAfull);
        curSpec.setTotalSpecification(TramoSeatsSpecification.RSAfull);

        toolBarRepresentation = NbComponents.newInnerToolbar();
        toolBarRepresentation.setFloatable(false);
        toolBarRepresentation.addSeparator();
        toolBarRepresentation.add(Box.createRigidArea(new Dimension(5, 0)));
        runButton = toolBarRepresentation.add(new AbstractAction("", DemetraIcons.COMPILE_16) {
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
        inputList = new JTsTable();
        inputList.setFreezeOnImport(true);
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

        addPropertyChangeListener(evt -> {
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
        });

        node = new InternalNode();
        associateLookup(ExplorerUtils.createLookup(ActiveViewManager.getInstance().getExplorerManager(), getActionMap()));
    }

    private void initList() {
        inputList.addPropertyChangeListener(JTsTable.TS_COLLECTION_PROPERTY, evt -> {
            demetra.timeseries.TsData sum = inputList.getTsCollection()
                    .load(demetra.timeseries.TsInformationType.Data, TsManager.get())
                    .stream()
                    .map(demetra.timeseries.Ts::getData)
                    .reduce(null, demetra.timeseries.TsData::add);
            demetra.timeseries.Ts t = demetra.timeseries.Ts.builder().name("Total").data(sum).build();
            saChart.setTsCollection(TsCollection.of(t));
            clear();
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
        SwingUtilities.invokeLater(() -> {
            mainPane.setDividerLocation(.5);
            mainPane.setResizeWeight(.5);
            visualRepresentation.setDividerLocation(.4);
            visualRepresentation.setResizeWeight(.4);
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
        doc.setTsCollection(TsConverter.fromTsCollection(inputList.getTsCollection()));
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
        JSpecSelectionComponent c = new JSpecSelectionComponent();
        c.setSpecification(curSpec.getDefaultSpecification());
        DialogDescriptor dd = c.createDialogDescriptor("Choose active specification");
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            setDefaultSpecification(c.getSpecification());
            start();
        }
    }

    class InternalNode extends AbstractNode {

        @Messages({
            "directIndirectSaTopComponent.internalNode.setDisplayName=Direct-Indirect SA"
        })
        InternalNode() {
            super(Children.LEAF);
            setDisplayName(Bundle.directIndirectSaTopComponent_internalNode_setDisplayName());
        }

        @Override
        @Messages({
            "directIndirectSaTopComponent.benchmarking.setName=Benchmarking",
            "directIndirectSaTopComponent.benchmarking.displayName=Benchmarking",
            "directIndirectSaTopComponent.rho.name=Rho",
            "directIndirectSaTopComponent.rho.desc=The value of the AR(1) parameter (set between 0 and 1). The default value of 1 is equivalent to Denton Benchmarking.",
            "directIndirectSaTopComponent.lambda.name=Lambda",
            "directIndirectSaTopComponent.lambda.desc=A parameter in the function used for benchmarking that relates to the weights in the regression equation; it is typically equal to 0, 1/2 or 1.",
            "directIndirectSaTopComponent.contemporaneousConstraints.name=Contemporaneous constraints",
            "directIndirectSaTopComponent.contemporaneousConstraints.desc=A constraint imposed for each period when comparing the sum of seasonally adjusted series (indirect SA) with the SA of the sum (direct SA) (None- no constraint; Fixed – the seasonally adjusted components are modified so that their sum is equal to the direct SA; Free – the sum of the benchmarked SA series is equal to the benchmarked direct SA series, which implies that all the SA series, including the direct SA, may be modified).",
            "directIndirectSaTopComponent.annualConstraints.name=Annual constraint",
            "directIndirectSaTopComponent.annualConstraints.desc=Each SA series is benchmarked on its annual totals computed on the raw data."
        })
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();
            Set bench = Sheet.createPropertiesSet();
            bench.setName(Bundle.directIndirectSaTopComponent_benchmarking_setName());
            bench.setDisplayName(Bundle.directIndirectSaTopComponent_benchmarking_displayName());
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
            rho.setName(Bundle.directIndirectSaTopComponent_rho_name());
            rho.setShortDescription(Bundle.directIndirectSaTopComponent_rho_desc());
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
            lambda.setName(Bundle.directIndirectSaTopComponent_lambda_name());
            lambda.setShortDescription(Bundle.directIndirectSaTopComponent_lambda_desc());
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

            ctype.setName(Bundle.directIndirectSaTopComponent_contemporaneousConstraints_name());
            ctype.setShortDescription(Bundle.directIndirectSaTopComponent_contemporaneousConstraints_desc());
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
            annual.setName(Bundle.directIndirectSaTopComponent_annualConstraints_name());
            annual.setShortDescription(Bundle.directIndirectSaTopComponent_annualConstraints_desc());
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
