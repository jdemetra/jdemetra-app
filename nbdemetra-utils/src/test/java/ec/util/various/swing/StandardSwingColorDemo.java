/*
 * Copyright 2015 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.util.various.swing;

import ec.util.chart.swing.SwingColorSchemeSupport;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Philippe Charles
 */
public final class StandardSwingColorDemo extends JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(StandardSwingColorDemo.class)
                .title("Standard Swing Colors")
                .size(300, 200)
                .launch();
    }

    public StandardSwingColorDemo() {
        StandardSwingColor[] values = StandardSwingColor.values();
        Arrays.sort(values, new CustomComparator());

        JList<StandardSwingColor> list = new JList<>(values);
        list.setCellRenderer(new CustomRenderer());

        setLayout(new BorderLayout());
        add(ModernUI.withEmptyBorders(new JScrollPane(list)), BorderLayout.CENTER);
    }

    private static final class CustomComparator implements Comparator<StandardSwingColor> {

        @Override
        public int compare(StandardSwingColor l, StandardSwingColor r) {
            return l.key().compareTo(r.key());
        }
    }

    private static final class CustomRenderer implements ListCellRenderer<StandardSwingColor>, Icon {

        private final DefaultListCellRenderer label = new DefaultListCellRenderer();
        private Color color;

        @Override
        public Component getListCellRendererComponent(JList<? extends StandardSwingColor> list, StandardSwingColor value, int index, boolean isSelected, boolean cellHasFocus) {
            label.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            color = value.value();
            label.setText("<html>" + value.key() + " (" + toString(color) + ")");
            label.setIcon(this);
            return label;
        }

        private String toString(Color c) {
            return c != null ? SwingColorSchemeSupport.toHex(color).toUpperCase() : "null";
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color savedColor = g.getColor();
            g.setColor(label.getForeground());
            g.fillRect(x, y, getIconWidth(), getIconHeight());
            g.setColor(color);
            g.fillRect(x + 1, y + 1, getIconWidth() - 2, getIconHeight() - 2);
            g.setColor(savedColor);
        }

        @Override
        public int getIconWidth() {
            return 12;
        }

        @Override
        public int getIconHeight() {
            return 12;
        }
    }
}
