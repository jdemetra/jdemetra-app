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
package ec.util.various.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 *
 * @author Philippe Charles
 */
public final class FontAwesomeDemo extends JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(FontAwesomeDemo.class)
                .title("Font Awesome Demo")
                .size(300, 200)
                .icons(() -> FontAwesome.FA_FONT.getImages(Color.BLUE, 16f, 32f, 64f))
                .launch();
    }

    public FontAwesomeDemo() {
        setLayout(new FlowLayout());

        final JComboBox cb = new JComboBox(FontAwesome.values());
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setIcon(((FontAwesome) value).getIcon(getForeground(), 16));
                return this;
            }
        });
        add(cb);

        final JLabel x = new JLabel();
        x.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        cb.addItemListener(evt -> x.setIcon(((FontAwesome) evt.getItem()).getIcon(Color.GREEN.darker(), 100)));
        add(x);

        final JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
        slider.addChangeListener(evt -> {
            JSlider source = (JSlider) evt.getSource();
            if (!source.getValueIsAdjusting()) {
                int angle = source.getValue();
                x.setIcon(((FontAwesome) (FontAwesome) cb.getSelectedItem()).getIcon(Color.GREEN.darker(), 100, angle));
            }
        });
        add(slider);

        cb.setSelectedItem(FontAwesome.FA_DESKTOP);
    }

}
