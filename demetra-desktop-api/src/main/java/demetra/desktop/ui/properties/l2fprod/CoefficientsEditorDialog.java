/*
 * Copyright 2017 National Bank of Belgium
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
package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheet;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;
import demetra.desktop.DemetraIcons;
import demetra.desktop.util.NbComponents;
import ec.util.list.swing.JLists;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Mats Maggi
 */
public class CoefficientsEditorDialog extends JDialog {

    private boolean dirty;
    private Coefficients coefficients;
    private String current;
    private final List<String> availableNames = new ArrayList<>();
    private static final String AVAILABLE_NAMES = "availableNames";

    public CoefficientsEditorDialog(final Window owner, Coefficients elements) {
        super(owner);

        coefficients = new Coefficients(elements);

        final JPanel pane = new JPanel(new BorderLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        Set<String> keys = coefficients.getFixedCoefficients().keySet();

        final JList list = new JList(JLists.modelOf(keys.toArray(new String[0])));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = NbComponents.newJScrollPane(list);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.add(scroll, BorderLayout.CENTER);

        final JPanel buttonPane = new JPanel();
        BoxLayout layout = new BoxLayout(buttonPane, BoxLayout.LINE_AXIS);
        buttonPane.setLayout(layout);
        final JButton addButton = new JButton(DemetraIcons.LIST_ADD_16);
        addButton.setPreferredSize(new Dimension(30, 30));
        addButton.setFocusPainted(false);
        addButton.addActionListener(evt -> {
            NewCoefficientDialog ncd = new NewCoefficientDialog(this);
            ncd.setTitle("Add new variable");
            ncd.setVisible(true);
            if (ncd.getSelection() != null) {
                String[] split = ncd.getSelection().split("#");
                if (split.length == 1) {
                    dirty = true;
                    coefficients.getFixedCoefficients().put(split[0], new double[1]);
                } else if (split.length == 2) {
                    dirty = true;
                    coefficients.getFixedCoefficients().put(split[0], new double[Integer.parseInt(split[1])]);
                }
                SwingUtilities.invokeLater(() -> {
                    list.setModel(JLists.modelOf(coefficients.getFixedCoefficients().keySet().toArray(new String[keys.size()])));
                    list.invalidate();
                    updateAvailableNames();
                });
            }
        });
        buttonPane.add(addButton);

        final JButton deleteButton = new JButton(DemetraIcons.LIST_REMOVE_16);
        deleteButton.setEnabled(false);
        deleteButton.setPreferredSize(new Dimension(30, 30));
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(event -> {
            try {
                if (current == null) {
                    return;
                }
                dirty = true;
                coefficients.getFixedCoefficients().remove(current);
                SwingUtilities.invokeLater(() -> {
                    list.setModel(JLists.modelOf(coefficients.getFixedCoefficients().keySet().toArray(new String[keys.size()])));
                    list.invalidate();
                    updateAvailableNames();
                });

            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });
        buttonPane.add(deleteButton);
        
        final JButton clearButton = new JButton(DemetraIcons.BROOM);
        clearButton.setToolTipText("Clear");
        clearButton.setPreferredSize(new Dimension(30, 30));
        clearButton.setFocusPainted(false);
        clearButton.setEnabled(false);
        clearButton.addActionListener(event -> {
            try {
                if (list.getModel() != null) {
                    dirty = true;
                    coefficients.getFixedCoefficients().clear();
                    SwingUtilities.invokeLater(() -> {
                        list.setModel(JLists.modelOf(coefficients.getFixedCoefficients().keySet().toArray(new String[keys.size()])));
                        list.invalidate();
                        updateAvailableNames();
                    });
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });
        buttonPane.add(clearButton);

        final JButton editButton = new JButton(DemetraIcons.PREFERENCES);
        editButton.setEnabled(false);
        editButton.setPreferredSize(new Dimension(30, 30));
        editButton.setFocusPainted(false);
        editButton.addActionListener(event -> {
            final EditorDialog dialog = new EditorDialog(SwingUtilities.getWindowAncestor(this),
                    coefficients.getFixedCoefficients().get(current));
            dialog.setTitle(current);
            dialog.setVisible(true);
            if (dialog.isDirty()) {
                dirty = true;
                coefficients.getFixedCoefficients().put(current, dialog.getElements());
            }
        });
        buttonPane.add(editButton);

        buttonPane.add(Box.createGlue());
        final JButton okButton = new JButton("Done");
        okButton.setPreferredSize(new Dimension(60, 27));
        okButton.setFocusPainted(false);
        okButton.addActionListener(event -> CoefficientsEditorDialog.this.setVisible(false));
        buttonPane.add(okButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        pane.add(buttonPane, BorderLayout.SOUTH);
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        list.addListSelectionListener(event -> {
            if (list.getSelectedValue() != null) {
                deleteButton.setEnabled(true);
                editButton.setEnabled(true);
                current = (String) list.getSelectedValue();
            } else {
                deleteButton.setEnabled(false);
                editButton.setEnabled(false);
                current = null;
            }
        });
        
        list.addPropertyChangeListener(evt -> clearButton.setEnabled(list.getModel() != null && list.getModel().getSize() > 0));

        addPropertyChangeListener(AVAILABLE_NAMES, (PropertyChangeEvent pce) -> addButton.setEnabled(!availableNames.isEmpty()));
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                super.windowClosing(we);
                coefficients = elements;
            }
        });

        updateAvailableNames();

        setMinimumSize(new Dimension(200, 200));
        setContentPane(pane);
        setLocationRelativeTo(owner);
        pack();
        setModal(true);
    }

    private void updateAvailableNames() {
        List<String> old = new ArrayList<>(availableNames);
        availableNames.clear();
        Set<String> keys = coefficients.getFixedCoefficients().keySet();
        for (String all : coefficients.getAllNames()) {
            String[] tokens = all.split("#"); //all.replaceAll("\\#\\d+", "");
            if (!keys.contains(tokens[0])) {
                availableNames.add(all);
            }
        }
        firePropertyChange(AVAILABLE_NAMES, null, availableNames);
    }

    public Coefficients getCoefficients() {
        return coefficients;
    }

    public boolean isDirty() {
        return dirty;
    }

    private class NewCoefficientDialog extends JDialog {

        private final JComboBox combo;

        public NewCoefficientDialog(JDialog owner) {
            final JPanel pane = new JPanel(new BorderLayout());
            pane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

            combo = new JComboBox();
            combo.setModel(new DefaultComboBoxModel(availableNames.toArray()));

            pane.add(combo, BorderLayout.CENTER);

            final JPanel buttonPane = new JPanel();
            BoxLayout layout = new BoxLayout(buttonPane, BoxLayout.LINE_AXIS);
            buttonPane.setLayout(layout);

            buttonPane.add(Box.createGlue());
            final JButton okButton = new JButton("Done");
            okButton.setPreferredSize(new Dimension(60, 27));
            okButton.setFocusPainted(false);
            okButton.addActionListener(event -> NewCoefficientDialog.this.setVisible(false));
            buttonPane.add(okButton);
            buttonPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
            pane.add(buttonPane, BorderLayout.SOUTH);
            pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            setContentPane(pane);
            setLocationRelativeTo(owner);
            pack();
            setModal(true);
        }

        public String getSelection() {
            return (String) combo.getSelectedItem();
        }
    }

    private class EditorDialog extends JDialog {

        private final List<Double> elements_;
        private boolean dirty_;

        public double[] getElements() {
            return elements_.stream().mapToDouble(Double::doubleValue).toArray();
        }

        public boolean isDirty() {
            return dirty_;
        }

        public EditorDialog(final Window owner, double[] elements) {
            super(owner);
            this.elements_ = new ArrayList<>();
            Arrays.stream(elements).map(Double::new).forEach(elements_::add);

            final JPanel pane = new JPanel(new BorderLayout());
            pane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

            final JList list = new JList(JLists.modelOf(elements_));
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setPreferredSize(new Dimension(150, 200));
            JScrollPane scroll = NbComponents.newJScrollPane(list);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            pane.add(scroll, BorderLayout.WEST);

            final PropertySheetTableModel model = new PropertySheetTableModel();
            final PropertySheetPanel psp = new PropertySheetPanel(new PropertySheetTable(model));
            psp.setToolBarVisible(false);
            psp.setEditorFactory(CustomPropertyEditorRegistry.INSTANCE.getRegistry());
            psp.setRendererFactory(CustomPropertyRendererFactory.INSTANCE.getRegistry());
            psp.setDescriptionVisible(false);
            psp.setMode(PropertySheet.VIEW_AS_CATEGORIES);
            pane.add(psp, BorderLayout.CENTER);
            psp.setPreferredSize(new Dimension(250, 200));
            psp.setBorder(BorderFactory.createEtchedBorder());

            list.addListSelectionListener(event -> {
                if (list.getSelectedValue() != null) {
                    DefaultProperty def = new DefaultProperty();
                    if (list.getModel().getSize() > 1) {
                        def.setName(current + "[" + (list.getSelectedIndex() + 1) + "]");
                    } else {
                        def.setName(current);
                    }

                    def.setDisplayName(def.getName());
                    def.setType(Double.class);
                    def.setCategory("");
                    def.setEditable(true);
                    def.setValue(list.getSelectedValue());
                    def.addPropertyChangeListener(evt -> {
                        if (evt.getNewValue() == null) {
                            return;
                        }
                        elements_.set(list.getSelectedIndex(), ((Number) evt.getNewValue()).doubleValue());
                    });
                    model.setProperties(new Property[]{def});

                } else {
                    model.setProperties(new Property[]{});
                }
            });

            psp.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (dirty_) {
                        list.setModel(JLists.modelOf(elements_));
                        list.invalidate();
                    }
                }
            });

            model.addPropertyChangeListener(evt -> {
                dirty_ = true;
                try {
                    Double o = (Double) list.getSelectedValue();
                    if (o != null) {
                        DefaultProperty def = new DefaultProperty();
                        def.setName(current);
                        def.setDisplayName(current);
                        def.setType(Double.class);
                        def.setCategory("");
                        def.setEditable(true);
                        def.setValue(o);
                        def.addPropertyChangeListener(e -> {
                            if (evt.getNewValue() == null) {
                                return;
                            }

                            elements_.set(list.getSelectedIndex(), ((Number) e.getNewValue()).doubleValue());
                        });
                        model.setProperties(new Property[]{def});
                    }
                } catch (RuntimeException err) {
                } finally {
                    model.fireTableStructureChanged();
                }
            });

            final JPanel buttonPane = new JPanel();
            BoxLayout layout = new BoxLayout(buttonPane, BoxLayout.LINE_AXIS);
            buttonPane.setLayout(layout);

            buttonPane.add(Box.createGlue());
            final JButton okButton = new JButton("Done");
            okButton.setPreferredSize(new Dimension(60, 27));
            okButton.setFocusPainted(false);
            okButton.addActionListener(event -> EditorDialog.this.setVisible(false));
            buttonPane.add(okButton);
            buttonPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
            pane.add(buttonPane, BorderLayout.SOUTH);
            pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            setMinimumSize(new Dimension(400, 200));
            setContentPane(pane);
            setLocationRelativeTo(null);
            pack();
            setModal(true);
        }
    }
}
