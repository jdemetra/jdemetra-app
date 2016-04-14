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
package ec.nbdemetra.ui.demo;

import com.google.common.collect.Lists;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.awt.ExceptionPanel;
import ec.tss.TsFactory;
import ec.tstoolkit.utilities.Id;
import ec.ui.interfaces.ITsList;
import ec.ui.list.JTsList;
import ec.ui.view.tsprocessing.IdsTree;
import ec.util.various.swing.BasicSwingLauncher;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.openide.util.Lookup;

/**
 *
 * @author Philippe Charles
 */
public final class ComponentsDemo extends JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(ComponentsDemo.class)
                .title("Components Demo")
                .logLevel(Level.FINE)
                .launch();
    }

    public ComponentsDemo() {
        initStaticResources();

        final Map<Id, Component> demoData = lookupComponents();

        final JPanel main = new JPanel(new BorderLayout());
        final JTree tree = new JTree();
        tree.setRootVisible(false);
        tree.setCellRenderer(new IdRenderer(demoData));

        IdsTree.fill(tree, Lists.newArrayList(demoData.keySet()));
        expandAll(tree);

        tree.getSelectionModel().addTreeSelectionListener(event -> {
            TreePath p = tree.getSelectionPath();
            if (p != null) {
                main.removeAll();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
                Id id = IdsTree.translate(node);
                Component c = demoData.get(id);
                main.add(c != null ? c : new JPanel());
                main.validate();
                main.repaint();
            }
        });

        JTsList dragDrop = new JTsList();
        dragDrop.setShowHeader(false);
        dragDrop.setInformation(new ITsList.InfoType[]{ITsList.InfoType.TsIdentifier, ITsList.InfoType.Data});
        dragDrop.setPreferredSize(new Dimension(200, 200));
        dragDrop.setTsAction(DemoTsActions.DO_NOTHING);

        JSplitPane left = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, NbComponents.newJScrollPane(tree), dragDrop);
        JSplitPane splitPane = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, main);
        splitPane.getLeftComponent().setPreferredSize(new Dimension(200, 400));
        
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
    }

    private static void initStaticResources() {
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultBarPainter(new StandardBarPainter());
        UIManager.put("Nb.Editor.Toolbar.border", BorderFactory.createLineBorder(Color.WHITE));
        TsFactory.instance.add(new FakeTsProvider());
    }
    
    private static void expandAll(JTree tree) {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }

    private static SortedMap<Id, Component> lookupComponents() {
        TreeMap<Id, Component> result = new TreeMap();
        for (DemoComponentFactory o : Lookup.getDefault().lookupAll(DemoComponentFactory.class)) {
            for (Entry<Id, Callable<Component>> e : o.getComponents().entrySet()) {
                result.put(e.getKey(), configure(create(e.getValue())));
            }
        }
        return result;
    }

    private static Component create(Callable<? extends Component> factory) {
        try {
            return factory.call();
        } catch (Exception ex) {
            ExceptionPanel panel = new ExceptionPanel();
            panel.setException(ex);
            return panel;
        }
    }

    private static Component configure(Component c) {
        if (c instanceof ExceptionPanel || c instanceof ReflectComponent) {
            return c;
        }
        JToolBar toolBar = NbComponents.newInnerToolbar();
        for (DemoComponentHandler o : Lookup.getDefault().lookupAll(DemoComponentHandler.class)) {
            if (o.canHandle(c)) {
                o.configure(c);
                o.fillToolBar(toolBar, c);
            }
        }
        if (toolBar.getComponentCount() > 0) {
            JPanel result = new JPanel(new BorderLayout());
            result.add(c, BorderLayout.CENTER);
            result.add(toolBar, BorderLayout.NORTH);
            return result;
        }
        return c;
    }

    private static final class IdRenderer extends DefaultTreeCellRenderer {

        private final Map<Id, Component> demoData;

        public IdRenderer(Map<Id, Component> demoData) {
            this.demoData = demoData;
        }
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel result = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Id id = IdsTree.translate(node);
                if (id != null) {
                    Component c = demoData.get(id);
                    if (c != null) {
                        result.setText((c instanceof ExceptionPanel ? "[!] " : "") + id.tail());
                    } else {
                        result.setText(id.tail());
                    }
                }
                result.setIcon(null);
            }
            return result;
        }
    }
}
