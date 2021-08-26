package ec.ui.view;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import demetra.ui.TsManager;
import demetra.ui.components.parts.HasTsCollection.TsUpdateMode;
import demetra.ui.util.NbComponents;
import ec.tstoolkit.data.DescriptiveStatistics;
import demetra.ui.components.JTsGrid.Mode;
import ec.ui.interfaces.IDisposable;
import demetra.ui.components.JTsChart;
import demetra.ui.components.JTsGrid;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.openide.util.NbCollections;
import demetra.ui.datatransfer.DataTransfer;
import demetra.ui.design.SwingComponent;
import ec.tstoolkit.timeseries.simplets.TsData;

/**
 *
 * @author Demortier Jeremy
 */
@SwingComponent
public final class TsProperties extends JComponent implements IDisposable {

    private final JTsChart chart_;
    private final JTsGrid grid_;
    private final JLabel labelSeries_;
    private final JLabel labelSource_;
    private final JTree tree_;

    public TsProperties() {
        super();

        labelSeries_ = new JLabel();
        labelSource_ = new JLabel();
        Box header = Box.createHorizontalBox();
        header.add(new JLabel("Series: "));
        header.add(labelSeries_);
        header.add(Box.createRigidArea(new Dimension(20, 20)));
        header.add(new JLabel("Source: "));
        header.add(labelSource_);
        header.add(Box.createHorizontalGlue());

        tree_ = new JTree();
//    DefaultMutableTreeNode root = new DefaultMutableTreeNode();
//    DefaultMutableTreeNode metaNode = new DefaultMutableTreeNode("Metadata");
//    root.add(metaNode);
//    DefaultMutableTreeNode statNode = new DefaultMutableTreeNode("Statistics");
//    root.add(statNode);
        tree_.setModel(null);
        tree_.setRootVisible(false);
        chart_ = new JTsChart();
        chart_.setTsUpdateMode(TsUpdateMode.None);
        chart_.setLegendVisible(false);
        JSplitPane leftPane = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, NbComponents.newJScrollPane(tree_), chart_);
        leftPane.setResizeWeight(.5d);

        grid_ = new JTsGrid();
        grid_.setMode(Mode.SINGLETS);
        grid_.setTsUpdateMode(TsUpdateMode.None);
        JSplitPane splitPane = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, grid_);
        splitPane.setResizeWeight(.5d);

        this.setLayout(new BorderLayout());
        this.add(header, BorderLayout.NORTH);
        this.add(splitPane, BorderLayout.CENTER);
        tree_.setTransferHandler(new TsHandler());
    }

    public void setTs(demetra.timeseries.Ts ts) {
        ts = ts.load(demetra.timeseries.TsInformationType.All, TsManager.getDefault());

        TsCollection col = TsCollection.of(ts);

        chart_.setTsCollection(col);
        grid_.setTsCollection(col);

        labelSeries_.setText(ts.getName());
        labelSource_.setText(ts.getMoniker().getSource());

        TsData tsData = TsConverter.fromTsData(ts.getData()).get();

        DescriptiveStatistics ds = new DescriptiveStatistics(tsData);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode metaNode = new DefaultMutableTreeNode("Metadata");
        for (String name : ts.getMeta().keySet()) {
            metaNode.add(new DefaultMutableTreeNode(name + " = " + ts.getMeta().get(name)));
        }
        root.add(metaNode);
        DefaultMutableTreeNode statNode = new DefaultMutableTreeNode("Statistics");
        statNode.setAllowsChildren(true);
        statNode.add(new DefaultMutableTreeNode("Time span: "
                + tsData.getDomain().getStart()
                + " to "
                + tsData.getDomain().getEnd()));
        statNode.add(new DefaultMutableTreeNode("Number of observations: "
                + ds.getDataCount()));
        statNode.add(new DefaultMutableTreeNode("Number of missing values: "
                + ds.getMissingValuesCount()));
        statNode.add(new DefaultMutableTreeNode("Min: "
                + ds.getMin()));
        statNode.add(new DefaultMutableTreeNode("Max: "
                + ds.getMax()));
        statNode.add(new DefaultMutableTreeNode("Average: "
                + ds.getAverage()));
        statNode.add(new DefaultMutableTreeNode("Median: "
                + ds.getMedian()));
        statNode.add(new DefaultMutableTreeNode("Stdev: "
                + ds.getStdev()));
        root.add(statNode);
        root.setAllowsChildren(true);

        tree_.setModel(new DefaultTreeModel(root, true));
        tree_.invalidate();
        expandAll(tree_, new TreePath(root));
        tree_.setRootVisible(false);
    }

    private void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Object n : NbCollections.iterable(node.children())) {
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path);
            }
        }
        tree.expandPath(parent);
    }

    @Override
    public void dispose() {
    }

    class TsHandler extends TransferHandler {

        TsHandler() {
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return DataTransfer.getDefault().canImport(support.getDataFlavors());
        }

        @Override
        public boolean importData(TransferSupport support) {
            DataTransfer.getDefault().toTs(support.getTransferable()).ifPresent(TsProperties.this::setTs);
            return super.importData(support);
        }
    }
}
