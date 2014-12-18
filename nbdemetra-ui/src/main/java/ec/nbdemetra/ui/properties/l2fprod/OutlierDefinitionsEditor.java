/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import ec.nbdemetra.ui.NbComponents;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Demortier Jeremy
 */
public class OutlierDefinitionsEditor extends AbstractPropertyEditor {

    private OutlierDefinition[] definitions_;

    public OutlierDefinitionsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int first, last, frequency;
                if (UserInterfaceContext.INSTANCE.getDomain() != null) {
                    first = UserInterfaceContext.INSTANCE.getDomain().getStart().getYear();
                    last = UserInterfaceContext.INSTANCE.getDomain().getEnd().getYear();
                    frequency = UserInterfaceContext.INSTANCE.getDomain().getFrequency().intValue();
                } else {
                    first = 1980;
                    last = GregorianCalendar.getInstance().get(Calendar.YEAR);
                    frequency = 12;
                }

                final JPanel pane = new JPanel(new BorderLayout());
                pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                OutliersModel model = new OutliersModel(first, last, frequency,
                        definitions_ != null ? Arrays.asList(definitions_) : Collections.<OutlierDefinition>emptyList());
                final JTable table = new JTable(model);
                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                table.setCellSelectionEnabled(true);
                table.setDefaultEditor(OutlierType.class, new OutlierTypeEditor());
                table.setDefaultRenderer(OutlierType.class, new OutlierTypeRenderer());

                pane.add(NbComponents.newJScrollPane(table), BorderLayout.CENTER);

                JPanel subPane = new JPanel();
                BoxLayout subLayout = new BoxLayout(subPane, BoxLayout.LINE_AXIS);
                subPane.setLayout(subLayout);
                subPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
                subPane.add(Box.createHorizontalGlue());
                subPane.add(new JButton(new AbstractAction("Clear") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        OutliersModel model = (OutliersModel) table.getModel();
                        table.setModel(new OutliersModel(model.getFirstYear(), model.getLastYear(), model.getFreq(),
                                Collections.<OutlierDefinition>emptyList()));
                    }
                }));
                pane.add(subPane, BorderLayout.SOUTH);

                if (null == UserInterfaceContext.INSTANCE.getDomain()) {
                    JPanel topPane = new JPanel();
                    BoxLayout topLayout = new BoxLayout(topPane, BoxLayout.LINE_AXIS);
                    topPane.setLayout(topLayout);
                    topPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));

                    topPane.add(new JLabel("First year:"));
                    topPane.add(Box.createHorizontalStrut(10));
                    final JSpinner spinnerFirst = new JSpinner(new SpinnerNumberModel(first, 1950, last, 1));
                    spinnerFirst.setEditor(new JSpinner.NumberEditor(spinnerFirst, "#"));
                    topPane.add(spinnerFirst);
                    topPane.add(Box.createHorizontalStrut(20));
                    topPane.add(new JLabel("Last year:"));
                    topPane.add(Box.createHorizontalStrut(10));
                    final JSpinner spinnerLast = new JSpinner(new SpinnerNumberModel(last, 1950, last, 1));
                    spinnerLast.setEditor(new JSpinner.NumberEditor(spinnerLast, "#"));
                    topPane.add(spinnerLast);
                    topPane.add(Box.createHorizontalStrut(20));
                    topPane.add(new JLabel("Frequency:"));
                    topPane.add(Box.createHorizontalStrut(10));
                    final JComboBox comboFreq = new JComboBox(new Integer[]{1, 4, 12});
                    comboFreq.setSelectedItem(frequency);
                    topPane.add(comboFreq);

                    spinnerFirst.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            if ((Integer) spinnerFirst.getValue() > (Integer) spinnerLast.getValue()) {
                                spinnerLast.setValue(spinnerFirst.getValue());
                            } else {
                                table.setModel(new OutliersModel((Integer) spinnerFirst.getValue(),
                                        (Integer) spinnerLast.getValue(),
                                        (Integer) comboFreq.getSelectedItem(),
                                        definitions_ != null ? Arrays.asList(definitions_) : Collections.<OutlierDefinition>emptyList()));
                            }
                        }
                    });

                    spinnerLast.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            if ((Integer) spinnerFirst.getValue() > (Integer) spinnerLast.getValue()) {
                                spinnerFirst.setValue(spinnerLast.getValue());
                            } else {
                                table.setModel(new OutliersModel((Integer) spinnerFirst.getValue(),
                                        (Integer) spinnerLast.getValue(),
                                        (Integer) comboFreq.getSelectedItem(),
                                        definitions_ != null ? Arrays.asList(definitions_) : Collections.<OutlierDefinition>emptyList()));
                            }
                        }
                    });

                    comboFreq.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                table.setModel(new OutliersModel((Integer) spinnerFirst.getValue(),
                                        (Integer) spinnerLast.getValue(),
                                        (Integer) comboFreq.getSelectedItem(),
                                        definitions_ != null ? Arrays.asList(definitions_) : Collections.<OutlierDefinition>emptyList()));
                            }
                        }
                    });

                    pane.add(topPane, BorderLayout.NORTH);
                }

                final JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(editor));
                dialog.setContentPane(pane);
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        TableCellEditor cellEditor = table.getCellEditor();
                        if (cellEditor != null) {
                            cellEditor.stopCellEditing();
                        }
                        OutlierDefinition[] old = definitions_;
                        setValue(Iterables.toArray(((OutliersModel) table.getModel()).getDefinitions(), OutlierDefinition.class));
                        OutlierDefinitionsEditor.this.firePropertyChange(old, definitions_);
                    }
                });
                dialog.pack();
                dialog.setModal(true);
                dialog.setVisible(true);
            }
        });

    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            definitions_ = new OutlierDefinition[0];
        } else if (value instanceof OutlierDefinition[]) {
            definitions_ = (OutlierDefinition[]) value;
        }
    }

    @Override
    public Object getValue() {
        return definitions_;
    }
}

class OutliersModel extends DefaultTableModel {

    private final int firstYear_;
    private final int lastYear_;
    private final int freq_;
    private final HashMap<Day, OutlierDefinition> defs_;

    public List<OutlierDefinition> getDefinitions() {
        return Lists.newArrayList(defs_.values());
    }

    public int getFirstYear() {
        return firstYear_;
    }

    public int getLastYear() {
        return lastYear_;
    }

    public int getFreq() {
        return freq_;
    }

    public OutliersModel(int first, int last, int freq, List<OutlierDefinition> defs) {
        firstYear_ = first;
        lastYear_ = last;
        freq_ = freq;
        defs_ = new HashMap<>();
        for (OutlierDefinition def : defs) {
            defs_.put(def.position, def);
        }
    }

    @Override
    public int getColumnCount() {
        return freq_ + 1;
    }

    @Override
    public String getColumnName(int column) {
        switch (freq_) {
            case 12:
                switch (column) {
                    case 1:
                        return "January";
                    case 2:
                        return "February";
                    case 3:
                        return "March";
                    case 4:
                        return "April";
                    case 5:
                        return "May";
                    case 6:
                        return "June";
                    case 7:
                        return "July";
                    case 8:
                        return "August";
                    case 9:
                        return "September";
                    case 10:
                        return "October";
                    case 11:
                        return "November";
                    case 12:
                        return "December";
                    default:
                        return "";
                }
            case 4:
                switch (column) {
                    case 1:
                        return "I";
                    case 2:
                        return "II";
                    case 3:
                        return "III";
                    case 4:
                        return "IV";
                    default:
                        return "";
                }
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        } else {
            return OutlierType.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

    @Override
    public int getRowCount() {
        return 1 + lastYear_ - firstYear_;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return firstYear_ + row;
        }

        final Day day = new TsPeriod(TsFrequency.valueOf(freq_), firstYear_ + row, column - 1).firstday();
        if (defs_.containsKey(day)) {
            return defs_.get(day).type;
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        final Day day = new TsPeriod(TsFrequency.valueOf(freq_), firstYear_ + row, column - 1).firstday();
        if (null != aValue) {
            defs_.put(day, new OutlierDefinition(day, (OutlierType) aValue, true));
        } else {
            defs_.remove(day);
        }
    }
}

class OutlierTypeRenderer extends DefaultTableCellRenderer {

    private JLabel label_;

    public OutlierTypeRenderer() {
        label_ = new JLabel();
        label_.setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (null != value) {
            OutlierType oType = (OutlierType) value;
            label_.setText(oType.toString());
            label_.setBackground(ColorChooser.getColor(oType));
            label_.setForeground(ColorChooser.getForeColor(oType));
            label_.setOpaque(true);
        } else {
            label_.setBackground(Color.white);
            label_.setText("");
        }
        return label_;
    }
}

class OutlierTypeEditor extends AbstractCellEditor implements TableCellEditor {

    private JComboBox combo_;

    public OutlierTypeEditor() {
        combo_ = new JComboBox(new OutlierType[]{OutlierType.AO, OutlierType.LS, OutlierType.TC, OutlierType.SO, null});
        combo_.setRenderer(new OutlierComboRenderer());
        combo_.setSelectedIndex(-1);
        combo_.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    fireEditingStopped();
                    combo_.setSelectedIndex(-1);
                }
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return combo_.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return combo_;
    }
}

class OutlierComboRenderer extends JLabel implements ListCellRenderer {

    public OutlierComboRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (null == value) {
            setBackground(Color.white);
            setForeground(Color.white);
            setText(" ");

        } else {
            OutlierType type = (OutlierType) value;
            setBackground(ColorChooser.getColor(type));
            setForeground(ColorChooser.getForeColor(type));
            setText(type.toString());
        }
        return this;
    }
}
