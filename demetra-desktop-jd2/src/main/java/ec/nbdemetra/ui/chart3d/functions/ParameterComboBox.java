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
package ec.nbdemetra.ui.chart3d.functions;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Panel containing the 2 comboboxes used to select the parameters of the function.
 * @author Mats Maggi
 */
public class ParameterComboBox extends JPanel {

    private Object selected1;
    private Object selected2;
    private JComboBox combo1;
    private JComboBox combo2;
    private String[] elements;
    public static final String PARAMETERS_CHANGED = "Parameters Changed";
    private final JLabel param1Label = new JLabel("X Parameter : ");
    private final JLabel param2Label = new JLabel("Y Parameter : ");
    private ItemListener l1, l2;
    private JButton button;

    public ParameterComboBox(String[] elements) {
        super();
        this.elements = elements;
        setLayout(new BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
        setOpaque(false);
        initComboBoxes();
        initPanels();
    }

    private void initPanels() {
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, javax.swing.BoxLayout.PAGE_AXIS));
        JPanel p1 = new JPanel();
        p1.setOpaque(false);
        p1.setBorder(new EmptyBorder(1, 5, 1, 5));
        p1.setLayout(new BoxLayout(p1, javax.swing.BoxLayout.LINE_AXIS));
        p1.add(param1Label);
        p1.add(combo1);

        JPanel p2 = new JPanel();
        p2.setOpaque(false);
        p2.setBorder(new EmptyBorder(1, 5, 1, 5));
        p2.setLayout(new BoxLayout(p2, javax.swing.BoxLayout.LINE_AXIS));
        p2.add(param2Label);
        p2.add(combo2);

        param1Label.setMaximumSize(new Dimension(50, 18));
        param2Label.setMaximumSize(new Dimension(50, 18));

        mainPanel.add(p1);
        mainPanel.add(p2);

        button = new JButton("OK");
        button.setMaximumSize(new Dimension(50, 18));

        button.addActionListener(event -> firePropertyChange(PARAMETERS_CHANGED, null, null));

        add(mainPanel);
        add(button);
    }

    private void initComboBoxes() {
        selected1 = elements[0];
        selected2 = elements[1];

        combo1 = new JComboBox(elements);
        combo1.setOpaque(false);
        combo1.setSelectedItem(selected1);
        combo1.setMaximumSize(new Dimension(100, 18));
        combo1.setPreferredSize(new Dimension(100, 18));

        combo2 = new JComboBox(elements);
        combo2.setOpaque(false);
        combo2.setSelectedItem(selected2);
        combo2.setMaximumSize(new Dimension(100, 18));
        combo2.setPreferredSize(new Dimension(100, 18));

        combo2.removeItem(selected1);
        combo1.removeItem(selected2);

        l1 = event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                selected1 = event.getItem();
                resetElements();
            }
        };

        l2 = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                selected2 = e.getItem();
                resetElements();
            }
        };

        combo1.addItemListener(l1);
        combo2.addItemListener(l2);
    }

    public void setElements(String[] elements) {
        this.elements = elements;
        selected1 = this.elements[0];
        selected2 = this.elements[1];

        resetElements();
    }

    public void resetElements() {
        combo1.removeItemListener(l1);
        combo2.removeItemListener(l2);

        combo1.removeAllItems();
        combo2.removeAllItems();
        for (String s : elements) {
            combo1.addItem(s);
            combo2.addItem(s);
        }

        combo2.removeItem(selected1);
        combo1.removeItem(selected2);

        combo1.setSelectedItem(selected1);
        combo2.setSelectedItem(selected2);

        combo1.addItemListener(l1);
        combo2.addItemListener(l2);
    }

    public int getSelectedIndex1() {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].equals(String.valueOf(selected1))) {
                return i;
            }
        }
        return -1;
    }

    public int getSelectedIndex2() {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].equals(String.valueOf(selected2))) {
                return i;
            }
        }
        return -1;
    }
}
