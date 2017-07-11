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
package ec.nbdemetra.chainlinking.outlineview;

import ec.nbdemetra.chainlinking.html.AnnualOverlapHtml;
import ec.nbdemetra.chainlinking.outlineview.nodes.ChainLinkingRenderer;
import ec.nbdemetra.chainlinking.outlineview.nodes.ChainLinkingRowModel;
import ec.nbdemetra.chainlinking.outlineview.nodes.ChainLinkingTreeModel;
import ec.nbdemetra.chainlinking.outlineview.nodes.ChainLinkingTreeModel.ProductNode;
import ec.nbdemetra.chainlinking.outlineview.nodes.CustomOutlineCellRenderer;
import ec.nbdemetra.ui.ComponentFactory;
import ec.nbdemetra.ui.NbComponents;
import ec.tss.html.HtmlUtil;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.chainlinking.AChainLinking.Product;
import ec.tstoolkit.timeseries.simplets.chainlinking.AnnualOverlap;
import ec.ui.AHtmlView;
import ec.util.grid.swing.XTable;
import ec.util.various.swing.ModernUI;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeModel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.OutlineModel;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Top component of the Chain Linking.
 *
 * @author Mats Maggi
 */
@ConvertAsProperties(dtd = "-//ec.nbdemetra.sa//ChainLinking//EN",
        autostore = false)
@TopComponent.Description(preferredID = "ChainLinkingTopComponent",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@ActionID(category = "Window", id = "ec.nbdemetra.chainlinking.outlineview.ChainLinkingTopComponent")
@ActionReference(path = "Menu/Tools", position = 333)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ChainLinkingAction")
@NbBundle.Messages({
    "CTL_ChainLinkingAction=Chain Linking",
    "CTL_ChainLinkingTopComponent=Chain Linking Window",
    "HINT_ChainLinkingTopComponent=This is a Chain Linking window"
})
public class ChainLinkingTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final XOutline outline;
    private final JSplitPane visualRepresentation;
    private final JSplitPane inputs;
    private final AnnualOverlap ao;
    private final AHtmlView htmlResultsView;
    private final AHtmlView htmlDetailedView;
    private final CustomOutlineCellRenderer defaultCellRenderer;
    private final ChainLinkingChart chart;
    private final JTabbedPane tabbedPane;
    private final DecimalFormat df2;
    private final ExplorerManager mgr = new ExplorerManager();

    // Properties root node
    private Node node;

    public ChainLinkingTopComponent() {
        setName(Bundle.CTL_ChainLinkingAction());
        setToolTipText(Bundle.HINT_ChainLinkingTopComponent());
        setLayout(new BorderLayout());

        df2 = new DecimalFormat();
        df2.setMaximumFractionDigits(2);
        df2.setMinimumFractionDigits(2);

        tabbedPane = new JTabbedPane();

        outline = new XOutline();
        outline.addPropertyChangeListener("Delete", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                int response = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(outline), "Are you sure you want to delete this product ?", "Delete ?", JOptionPane.OK_CANCEL_OPTION);
                if (response == JOptionPane.OK_OPTION) {
                    Product p = ((ProductNode) evt.getNewValue()).getProduct();
                    for (int i = 0; i < ao.getInput().size(); i++) {
                        if (ao.getInput().get(i).equals(p)) {
                            ao.getInput().remove(i);
                            ao.process();
                            refreshModel();
                            break;
                        }
                    }
                }
            }
        });

        ao = new AnnualOverlap();

        defaultCellRenderer = new CustomOutlineCellRenderer();

        chart = new ChainLinkingChart();
        htmlResultsView = ComponentFactory.getDefault().newHtmlView();
        htmlDetailedView = ComponentFactory.getDefault().newHtmlView();

        refreshModel();

        JScrollPane p = ModernUI.withEmptyBorders(new JScrollPane());
        p.setViewportView(outline);

        AddProductPanel addPanel = new AddProductPanel();

        tabbedPane.add("Results", htmlResultsView);
        tabbedPane.add("Detailed", htmlDetailedView);
        tabbedPane.add("Chart", chart);

        addPanel.addPropertyChangeListener(AddProductPanel.PROCESS_PRODUCTS, evt -> {
            ao.setProducts((List<Product>) evt.getNewValue());
            ao.process();
            refreshModel();
        });

        inputs = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, addPanel, p);

        visualRepresentation = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, inputs, tabbedPane);

        add(visualRepresentation, BorderLayout.CENTER);

        refreshNode();
    }

    private void refreshModel() {
        TreeModel treeMdl = new ChainLinkingTreeModel(ao.getInput());
        OutlineModel mdl = DefaultOutlineModel.createOutlineModel(treeMdl, new ChainLinkingRowModel(), true);
        outline.setRenderDataProvider(new ChainLinkingRenderer());
        outline.setDefaultRenderer(String.class, defaultCellRenderer);
        outline.setModel(mdl);
        outline.getColumnModel().getColumn(0).setHeaderValue("Basic data");

        XTable.setWidthAsPercentages(outline, .6, .2, .2, .2);
        refreshChart();
        refreshHtmlView();
    }

    private void refreshChart() {
        if (ao.getResults() == null) {
            chart.setResult(null);
        } else {
            chart.setResult(ao.getResults().get(AnnualOverlap.CHAIN_LINKED_INDEXES, TsData.class));
        }
    }

    private void refreshNode() {
        node = ChainLinkingControlNode.onComponentOpened(mgr, this);

        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));
        try {
            mgr.setSelectedNodes(new Node[]{node});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    private void refreshHtmlView() {
        if (ao.getResults() == null) {
            htmlResultsView.loadContent("");
            htmlDetailedView.loadContent("");
        } else {
            AnnualOverlapHtml htmlResults = new AnnualOverlapHtml(ao.getResults(), false);
            AnnualOverlapHtml htmlDetailed = new AnnualOverlapHtml(ao.getResults(), true);
            htmlResultsView.loadContent(HtmlUtil.toString(htmlResults));
            htmlDetailedView.loadContent(HtmlUtil.toString(htmlDetailed));
        }
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
        SwingUtilities.invokeLater(() -> {
            visualRepresentation.setDividerLocation(.3);
            visualRepresentation.setResizeWeight(.3);
            
            inputs.setDividerLocation(.4);
            inputs.setResizeWeight(.4);
        });
    }

    @Override
    public void componentClosed() {
        getExplorerManager().setRootContext(Node.EMPTY);
        super.componentClosed();
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
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

    public void setRefYear(int year) {
        ao.setRefYear(year);
        ao.process();
        refreshModel();
    }

    public int getRefYear() {
        return ao.getRefYear();
    }
}
