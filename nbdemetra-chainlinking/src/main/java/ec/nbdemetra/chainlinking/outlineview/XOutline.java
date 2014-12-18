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

import ec.nbdemetra.chainlinking.outlineview.nodes.ChainLinkingTreeModel.CustomNode;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.EventObject;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.swing.outline.Outline;

/**
 * Custom Outline component used to display input data of the Chain Linking.
 *
 * @author Mats Maggi
 */
public class XOutline extends Outline {

    public XOutline() {
        super();

        setRootVisible(false);
        getTableHeader().setReorderingAllowed(false);
        setColumnHidingAllowed(false);
        setRowSorter(null);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFillsViewportHeight(true);
        setDragEnabled(true);
        setComponentPopupMenu(createPopupMenu());
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu result = new JPopupMenu();

        JMenuItem expand = new JMenuItem(new AbstractAction("Expand all") {
            @Override
            public void actionPerformed(ActionEvent e) {
                expandAll();
            }
        });

        JMenuItem collapse = new JMenuItem(new AbstractAction("Collapse all") {
            @Override
            public void actionPerformed(ActionEvent e) {
                collapseAll();
            }
        });

        result.add(expand);
        result.add(collapse);

        return result;
    }

    public void expandAll() {
        TreePath parent = new TreePath(((TreeModel) getModel()).getRoot());
        expandAll(parent);
    }

    public void collapseAll() {
        TreePath parent = new TreePath(((TreeModel) getModel()).getRoot());
        collapseAll(parent);
    }

    private void expandAll(TreePath p) {
        CustomNode node = (CustomNode) p.getLastPathComponent();
        if (node.getChildren() != null && node.getChildren().length >= 0) {
            for (Object n : node.getChildren()) {
                TreePath path = p.pathByAddingChild(n);
                expandAll(path);
            }
        }
        expandPath(p);
    }

    private void collapseAll(TreePath p) {
        CustomNode node = (CustomNode) p.getLastPathComponent();
        if (node.getChildren() != null && node.getChildren().length >= 0) {
            for (Object n : node.getChildren()) {
                TreePath path = p.pathByAddingChild(n);
                collapseAll(path);
            }
        }

        if (p.getParentPath() != null) {
            collapsePath(p);
        }
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        if (getRowCount() == 0 || row == -1) {
            return false;
        } else {
            return super.editCellAt(row, column, e);
        }
    }
}
