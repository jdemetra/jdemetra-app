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
package ec.util.grid.swing;

import static ec.util.chart.swing.SwingColorSchemeSupport.isDark;
import static ec.util.chart.swing.SwingColorSchemeSupport.toHex;
import ec.util.grid.swing.XTableDemo.ColorIcon;
import static ec.util.grid.swing.XTableCommand.*;
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.ModernUI;
import ec.util.various.swing.PopupMouseAdapter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Philippe Charles
 */
public final class XTableDemo extends JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(XTableDemo.class)
                .title("XTable Demo")
                .icons(new Callable<List<? extends Image>>() {
                    @Override
                    public List<? extends Image> call() throws Exception {
                        return FontAwesome.FA_TABLE.getImages(Color.BLACK, 16f, 32f, 64f);
                    }
                })
                .logLevel(Level.FINE)
                .launch();
    }

    public XTableDemo() {
        XTable table = new XTable();
        table.addMouseListener(PopupMouseAdapter.fromMenu(createMenu(table)));

        applyNoDataMessage("<html><center><b><font size=+2>No data</font></b><br>(See popup menu for options)").executeSafely(table);

        setLayout(new BorderLayout());
        add(ModernUI.withEmptyBorders(new JScrollPane(table)));
    }

    private JMenu createMenu(XTable table) {
        JMenu result = new JMenu();

        JMenuItem item;

        item = new JCheckBoxMenuItem(applyModel(createModel()).toAction(table));
        item.setText("Fill");
        result.add(item);

        item = new JCheckBoxMenuItem(applyModel(new DefaultTableModel()).toAction(table));
        item.setText("Clear");
        result.add(item);

        item = new JCheckBoxMenuItem(applyDefaultRenderer(Color.class, createCustomRenderer()).toAction(table));
        item.setText("Custom renderer");
        result.add(item);

        item = new JCheckBoxMenuItem(applyDefaultRenderer(Color.class, new DefaultTableCellRenderer()).toAction(table));
        item.setText("Default renderer");
        result.add(item);

        item = new JCheckBoxMenuItem(applyNoDataMessage("Hello world").toAction(table));
        item.setText("Change no-data message");
        result.add(item);

        item = new JMenuItem(applyColumnWidthAsPercentages(.3, .7).toAction(table));
        item.setText("Change columns width");
        result.add(item);

        return result;
    }

    static TableModel createModel() {
        DefaultTableModel result = new DefaultTableModel(new String[]{"Color", "Name"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Color.class : String.class;
            }
        };
        result.addRow(new Object[]{Color.RED, "Red"});
        result.addRow(new Object[]{Color.GREEN, "Green"});
        result.addRow(new Object[]{Color.BLUE, "Blue"});
        result.addRow(new Object[]{Color.ORANGE, "Orange"});
        result.addRow(new Object[]{Color.BLACK, "Black"});
        return result;
    }

    static DefaultTableCellRenderer createCustomRenderer() {
        return new DefaultTableCellRenderer() {
            final JToolTip toolTip = super.createToolTip();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Color color = (Color) value;
                setText("Red=" + color.getRed() + ", Green=" + color.getGreen() + ", Blue=" + color.getBlue());
                setToolTipText(toHex(color));
                setIcon(new ColorIcon(color));
                toolTip.setBackground(color);
                toolTip.setForeground(isDark(color) ? Color.WHITE : Color.BLACK);
                return this;
            }

            @Override
            public JToolTip createToolTip() {
                return toolTip;
            }
        };
    }

    static class ColorIcon implements Icon {

        private static final int SIZE = 10;
        private final Color color;

        public ColorIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.black);
            g.drawRect(x, y, SIZE - 1, SIZE - 1);
            g.setColor(color);
            g.fillRect(x + 1, y + 1, SIZE - 2, SIZE - 2);
        }

        @Override
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }
}
