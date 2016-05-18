/*
 * Copyright 2013 National Bank of Belgium
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
package ec.util.chart.swing;

import ec.util.chart.ColorScheme;
import static ec.util.chart.swing.SwingColorSchemeSupport.rgbToColor;
import static ec.util.chart.swing.SwingColorSchemeSupport.toHex;
import ec.util.list.swing.JLists;
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.ModernUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.stream.StreamSupport;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Philippe Charles
 */
public final class ColorSchemeDemo {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(ColorSchemeDemo::create)
                .title("Color Scheme Demo")
                .icons(() -> FontAwesome.FA_PAINT_BRUSH.getImages(Color.DARK_GRAY, 16f, 32f, 64f))
                .size(300, 400)
                .logLevel(Level.FINE)
                .launch();
    }

    private static Component create() {
        JPanel result = new JPanel();

        JComboBox<ColorScheme> colorScheme = new JComboBox<>(getColorSchemes());
        colorScheme.setRenderer(JLists.cellRendererOf(ColorSchemeDemo::applyToolTipText));

        final JList<Integer> colors = new JList<>();
        colors.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        colors.setVisibleRowCount(-1);
        colors.setCellRenderer(new ColorRenderer());

        colorScheme.addItemListener(e -> applyColorScheme((ColorScheme) e.getItem(), colors));

        applyColorScheme((ColorScheme) colorScheme.getSelectedItem(), colors);

        result.setLayout(new BorderLayout());
        result.add(colorScheme, BorderLayout.NORTH);
        result.add(ModernUI.withEmptyBorders(new JScrollPane(colors)), BorderLayout.CENTER);
        return result;
    }

    private static void applyColorScheme(ColorScheme colorScheme, JList<Integer> colors) {
        colors.setModel(JLists.modelOf(colorScheme.getAreaColors()));
        colors.setBackground(rgbToColor(colorScheme.getPlotColor()));
        colors.setSelectionBackground(rgbToColor(colorScheme.getGridColor()));
    }

    private static ColorScheme[] getColorSchemes() {
        return StreamSupport.stream(ServiceLoader.load(ColorScheme.class).spliterator(), false).toArray(ColorScheme[]::new);
    }

    private static final class ColorRenderer implements ListCellRenderer<Integer>, Icon {

        private final JLabel renderer = new JLabel(this);
        private final int size = 25;

        @Override
        public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value, int index, boolean isSelected, boolean cellHasFocus) {
            Color color = rgbToColor(value);
            renderer.setForeground(color);
            renderer.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            renderer.setToolTipText(toHex(color));
            return renderer;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(renderer.getForeground());
            g.fillRect(x + 3, y + 3, size - 5, size - 5);
            g.setColor(renderer.getBackground());
            g.drawRect(x + 1, y + 1, size - 2, size - 2);
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

    private static void applyToolTipText(JLabel label, ColorScheme value) {
        label.setToolTipText(value.getName());
    }
}
