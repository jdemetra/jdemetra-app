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
import ec.util.list.swing.JLists;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Arrays;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *
 * @author Philippe Charles
 */
public final class StandardSwingColorDemo {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(StandardSwingColorDemo::create)
                .title("Standard Swing Colors")
                .size(300, 200)
                .launch();
    }

    private static Component create() {
        StandardSwingColor[] values = StandardSwingColor.values();
        Arrays.sort(values, (l, r) -> l.key().compareTo(r.key()));

        JList<StandardSwingColor> list = new JList<>(values);
        list.setCellRenderer(JLists.cellRendererOf(StandardSwingColorDemo::applyColor));

        return ModernUI.withEmptyBorders(new JScrollPane(list));
    }

    private static void applyColor(JLabel label, StandardSwingColor value) {
        Color color = value.value();
        if (color != null) {
            label.setText(value.key() + " (" + SwingColorSchemeSupport.toHex(color).toUpperCase() + ")");
            label.setIcon(new ColorIcon(color, label.getFont().getSize()));
        } else {
            label.setText(value.key() + " (null)");
            label.setIcon(null);
        }
    }

    private static final class ColorIcon implements Icon {

        private final Color color;
        private final int size;

        public ColorIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color savedColor = g.getColor();
            g.setColor(c.getForeground());
            g.fillRect(x, y, getIconWidth(), getIconHeight());
            g.setColor(color);
            g.fillRect(x + 1, y + 1, getIconWidth() - 2, getIconHeight() - 2);
            g.setColor(savedColor);
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
