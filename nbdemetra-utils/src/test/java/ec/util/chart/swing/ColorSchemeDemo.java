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
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.ModernUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.AbstractListModel;
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
public final class ColorSchemeDemo extends JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(ColorSchemeDemo.class)
                .title("Color Scheme Demo")
                .icons(new Callable<List<? extends Image>>() {
                    @Override
                    public List<? extends Image> call() throws Exception {
                        return FontAwesome.FA_PAINT_BRUSH.getImages(Color.DARK_GRAY, 16f, 32f, 64f);
                    }
                })
                .size(300, 400)
                .logLevel(Level.FINE)
                .launch();
    }

    public ColorSchemeDemo() {
        JComboBox<ColorScheme> colorScheme = new JComboBox<>(getColorSchemes());

        final JList<Integer> colors = new JList<>();
        colors.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        colors.setVisibleRowCount(-1);
        colors.setCellRenderer(new ColorRenderer());

        colorScheme.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                applyColorScheme((ColorScheme) e.getItem(), colors);
            }
        });

        applyColorScheme((ColorScheme) colorScheme.getSelectedItem(), colors);

        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, colorScheme);
        add(BorderLayout.CENTER, ModernUI.withEmptyBorders(new JScrollPane(colors)));
    }

    private static void applyColorScheme(ColorScheme colorScheme, JList<Integer> colors) {
        colors.setModel(new ColorModel(colorScheme));
        colors.setBackground(rgbToColor(colorScheme.getPlotColor()));
        colors.setSelectionBackground(rgbToColor(colorScheme.getGridColor()));
    }

    private static ColorScheme[] getColorSchemes() {
        List<ColorScheme> result = new ArrayList<>();
        for (ColorScheme o : ServiceLoader.load(ColorScheme.class)) {
            result.add(o);
        }
        return result.toArray(new ColorScheme[result.size()]);
    }

    private static final class ColorModel extends AbstractListModel<Integer> {

        final List<Integer> tmp;

        public ColorModel(ColorScheme cs) {
            this.tmp = cs.getAreaColors();
        }

        @Override
        public int getSize() {
            return tmp.size();
        }

        @Override
        public Integer getElementAt(int i) {
            return tmp.get(i);
        }
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
}
