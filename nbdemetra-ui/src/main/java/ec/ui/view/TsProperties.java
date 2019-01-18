package ec.ui.view;

import demetra.bridge.TsConverter;
import demetra.tsprovider.TsCollection;
import demetra.ui.TsManager;
import demetra.ui.components.HasTsCollection.TsUpdateMode;
import ec.nbdemetra.ui.NbComponents;
import ec.tss.Ts;
import ec.tss.TsInformationType;
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
import java.util.Optional;

/**
 *
 * @author Demortier Jeremy
 */
public class TsProperties extends JComponent implements IDisposable {

    private JTsChart chart_;
    private JTsGrid grid_;
    private JLabel labelSeries_, labelSource_;
    private JTree tree_;

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

    public void setTs(Ts ts) {
        TsManager.getDefault().load(ts, TsInformationType.All);

        TsCollection col = TsCollection.builder().data(TsConverter.toTs(ts)).build();

        chart_.setTsCollection(col);
        grid_.setTsCollection(col);

        labelSeries_.setText(ts.getName());
        labelSource_.setText(ts.getMoniker().getSource());

        DescriptiveStatistics ds = new DescriptiveStatistics(ts.getTsData());

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode metaNode = new DefaultMutableTreeNode("Metadata");
        if (ts.getMetaData() != null) {
            for (String name : ts.getMetaData().keySet()) {
                metaNode.add(new DefaultMutableTreeNode(name + " = " + ts.getMetaData().get(name)));
            }
        }
        root.add(metaNode);
        DefaultMutableTreeNode statNode = new DefaultMutableTreeNode("Statistics");
        statNode.setAllowsChildren(true);
        statNode.add(new DefaultMutableTreeNode("Time span: "
                + ts.getTsData().getDomain().getStart()
                + " to "
                + ts.getTsData().getDomain().getEnd()));
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
            Optional<demetra.tsprovider.Ts> s = DataTransfer.getDefault().toTs(support.getTransferable());
            if (s.isPresent()) {
                setTs(TsConverter.fromTs(s.get()));
            }
            return super.importData(support);
        }
    }
}
