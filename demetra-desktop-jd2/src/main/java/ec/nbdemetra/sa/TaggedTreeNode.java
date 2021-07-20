/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.tss.sa.RegArimaReport;
import ec.tstoolkit.algorithm.AlgorithmDescriptor;
import java.util.Map;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Philippe Charles
 */
class TaggedTreeNode extends DefaultMutableTreeNode {

    private Object tag_;

    public TaggedTreeNode() {
        super();
    }

    public TaggedTreeNode(Object userObject) {
        super(userObject);
    }

    public Object getTag() {
        return tag_;
    }

    public void setTag(Object value) {
        tag_ = value;
    }

    static void fillTree(JTree tree, Map<Integer, Map<AlgorithmDescriptor, RegArimaReport>> reports) {
        tree.getSelectionModel().clearSelection();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.setUserObject(REPORTS);
        if (!reports.isEmpty()) {
            for (Map.Entry<Integer, Map<AlgorithmDescriptor, RegArimaReport>> item : reports.entrySet()) {
                TaggedTreeNode fnode = new TaggedTreeNode(freqName(item.getKey()));
                fnode.setTag(item.getKey());
                for (AlgorithmDescriptor ritem : item.getValue().keySet()) {
                    TaggedTreeNode mnode = new TaggedTreeNode(ritem.name);
                    mnode.setTag(ritem);
                    fnode.add(mnode);
                }
                root.add(fnode);
            }
        }
        tree.setModel(new DefaultTreeModel(root));
    }
    private static final String TRS = "tramoseats", X12 = "x12", X13 = "x13", REPORTS = "Reports", ANALYSIS = "Analysis";
    private static final int[] FREQ = new int[]{2, 3, 4, 6, 12};
    private static final String[] SFREQ = new String[]{"Half-Year", "Quadri-monthly", "Quarterly", "Bi-montlhy", "Monthly"};

    static String freqName(int ifreq) {
        for (int i = 0; i < FREQ.length; ++i) {
            if (ifreq == FREQ[i]) {
                return SFREQ[i];
            }
        }
        return "";
    }
}
