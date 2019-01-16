/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.openide.util.NbCollections;

/**
 *
 * @author Jean Palate
 */
public class IdsTree {

    public static void fill(JTree tree, List<Id> items) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();

        HashMap<Id, DefaultMutableTreeNode> nodes = new HashMap<>();
        for (Id id : items) {
            Id[] path = id.path();
            DefaultMutableTreeNode prev = null;
            for (int i = 0; i < path.length; ++i) {
                DefaultMutableTreeNode cur = nodes.get(path[i]);
                if (cur == null) {
                    cur = new DefaultMutableTreeNode(path[i].tail());
                    if (prev == null) {
                        root.add(cur);
                    } else {
                        prev.add(cur);
                    }
                    nodes.put(path[i], cur);
                }
                prev = cur;
            }
        }

        DefaultTreeModel model = new DefaultTreeModel(root);
        tree.setModel(model);
    }

    public static Id translate(DefaultMutableTreeNode node) {
        Object[] path = node.getUserObjectPath();
        if (path == null || path.length<=1) {
            return null;
        }
        String[] ids = new String[path.length-1];
        for (int i = 0; i < ids.length; ++i) {
            ids[i] = (String) path[i+1];
        }
        return new LinearId(ids);
    }

     public static Id translate(TreePath tpath) {

        Object[] path = tpath.getPath();
        if (path == null || path.length<=1) {
            return null;
        }
        String[] ids = new String[path.length-1];
        for (int i = 0; i < ids.length; ++i) {
            DefaultMutableTreeNode cur=(DefaultMutableTreeNode) path[i+1];
            ids[i] = (String) cur.getUserObject();
        }
        return new LinearId(ids);
    }

     static DefaultMutableTreeNode search(DefaultTreeModel model, Id pview) {

        DefaultMutableTreeNode cur = (DefaultMutableTreeNode) model.getRoot();
        if (cur == null) {
            return null;
        }
        int pos = 0;
        while (pos < pview.getCount()) {
            boolean ok = false;
            for (Object node : NbCollections.iterable(cur.children())) {
                if (node instanceof DefaultMutableTreeNode
                        && pview.get(pos).equals(((DefaultMutableTreeNode) node).getUserObject())) {
                    cur = (DefaultMutableTreeNode) node;
                    ++pos;
                    ok = true;
                    break;
                }
            }
            if (ok) {
                return null;
            }
        }
        return cur;
    }
}