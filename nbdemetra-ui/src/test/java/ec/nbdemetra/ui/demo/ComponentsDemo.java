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
import demetra.ui.TsManager;
import demetra.ui.components.JTsTable;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.awt.ExceptionPanel;
import ec.tstoolkit.utilities.Id;
import demetra.ui.components.JTsTable.Column;
import ec.ui.view.tsprocessing.IdsTree;
import ec.util.various.swing.BasicSwingLauncher;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.stream.Collectors;
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
public final class ComponentsDemo {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(ComponentsDemo::create)
                .title("Components Demo")
                .logLevel(Level.FINE)
                .size(900, 600)
                .launch();
    }

    private static Component create() {
        initStaticResources();

        JPanel result = new JPanel();

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

        JTsTable dnd = new JTsTable();
        dnd.setShowHeader(false);
        dnd.setColumns(Arrays.asList(Column.TS_IDENTIFIER, Column.DATA));
        dnd.setPreferredSize(new Dimension(250, 200));
        dnd.setTsAction(DemoTsActions.DO_NOTHING);

        JSplitPane left = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, NbComponents.newJScrollPane(tree), dnd);
        JSplitPane splitPane = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, main);
        splitPane.getLeftComponent().setPreferredSize(new Dimension(250, 400));

        result.setLayout(new BorderLayout());
        result.add(splitPane, BorderLayout.CENTER);
        return result;
    }

    private static void initStaticResources() {
        ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
        BarRenderer.setDefaultBarPainter(new StandardBarPainter());
        UIManager.put("Nb.Editor.Toolbar.border", BorderFactory.createLineBorder(Color.WHITE));
        TsManager.getDefault().register(new FakeTsProvider());
    }

    private static void expandAll(JTree tree) {
        int row = 0;
        while (row < tree.getRowCount()) {
            tree.expandRow(row);
            row++;
        }
    }

    private static SortedMap<Id, Component> lookupComponents() {
        return Lookup.getDefault().lookupAll(DemoComponentFactory.class).stream()
                .flatMap(o -> o.getComponents().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, o -> configure(create(o.getValue())), (l, r) -> l, TreeMap::new));
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
        Lookup.getDefault().lookupAll(DemoComponentHandler.class).forEach(o -> {
            if (o.canHandle(c)) {
                o.configure(c);
                o.fillToolBar(toolBar, c);
            }
        });
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
