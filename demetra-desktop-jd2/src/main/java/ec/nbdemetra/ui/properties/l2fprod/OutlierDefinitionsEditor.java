/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import ec.nbdemetra.ui.DemetraUI;
import demetra.desktop.DemetraIcons;
import demetra.desktop.util.NbComponents;
import ec.nbdemetra.ui.properties.l2fprod.CheckComboBox.CheckListItem;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.text.DateFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Demortier Jeremy
 * @author Mats Maggi
 */
public class OutlierDefinitionsEditor extends AbstractPropertyEditor {

    private Map<Day, List<OutlierDefinition>> definitions_;

    public OutlierDefinitionsEditor() {
        editor = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window ancestor = SwingUtilities.getWindowAncestor(editor);
                switch (DemetraUI.getDefault().getPrespecifiedOutliersEditor()) {
                    case CALENDAR_GRID:
                        int first,
                         last,
                         frequency;
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
                                definitions_ != null ? new HashMap<>(definitions_) : new HashMap<>());
                        final JTable table = new JTable(model);
                        table.getTableHeader().setReorderingAllowed(false);
                        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        table.setCellSelectionEnabled(true);
                        table.setDefaultEditor(OutlierType[].class, new OutlierTypeEditor());
                        table.setDefaultRenderer(OutlierType[].class, new OutlierTypeRenderer());
                        table.setRowHeight(table.getRowHeight() * 2);

                        pane.add(NbComponents.newJScrollPane(table), BorderLayout.CENTER);

                        JPanel subPane = new JPanel();
                        BoxLayout subLayout = new BoxLayout(subPane, BoxLayout.LINE_AXIS);
                        subPane.setLayout(subLayout);
                        subPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

                        JButton clearButton = new JButton("Clear", DemetraIcons.BROOM);
                        clearButton.addActionListener(event -> {
                            OutliersModel mdl = (OutliersModel) table.getModel();
                            table.setModel(new OutliersModel(mdl.getFirstYear(), mdl.getLastYear(), mdl.getFreq(),
                                    new HashMap<>()));
                        });
                        clearButton.setFocusPainted(false);
                        subPane.add(clearButton);
                        subPane.add(Box.createHorizontalGlue());

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

                            spinnerFirst.addChangeListener((ChangeEvent e1) -> {
                                if ((Integer) spinnerFirst.getValue() > (Integer) spinnerLast.getValue()) {
                                    spinnerLast.setValue(spinnerFirst.getValue());
                                } else {
                                    table.setModel(new OutliersModel((Integer) spinnerFirst.getValue(),
                                            (Integer) spinnerLast.getValue(),
                                            (Integer) comboFreq.getSelectedItem(),
                                            definitions_ != null ? definitions_ : new HashMap<>()));
                                }
                            });

                            spinnerLast.addChangeListener((ChangeEvent e1) -> {
                                if ((Integer) spinnerFirst.getValue() > (Integer) spinnerLast.getValue()) {
                                    spinnerFirst.setValue(spinnerLast.getValue());
                                } else {
                                    table.setModel(new OutliersModel((Integer) spinnerFirst.getValue(),
                                            (Integer) spinnerLast.getValue(),
                                            (Integer) comboFreq.getSelectedItem(),
                                            definitions_ != null ? definitions_ : new HashMap<>()));
                                }
                            });

                            comboFreq.addItemListener((ItemEvent e1) -> {
                                if (e1.getStateChange() == ItemEvent.SELECTED) {
                                    table.setModel(new OutliersModel((Integer) spinnerFirst.getValue(),
                                            (Integer) spinnerLast.getValue(),
                                            (Integer) comboFreq.getSelectedItem(),
                                            definitions_ != null ? definitions_ : new HashMap<>()));
                                }
                            });

                            pane.add(topPane, BorderLayout.NORTH);
                        }

                        final JDialog dialog = new JDialog(ancestor);
                        dialog.setContentPane(pane);

                        JButton doneButton = new JButton("Done");
                        doneButton.addActionListener(event -> {
                            dialog.setVisible(false);
                            closeDialog(table);
                        });
                        doneButton.setFocusPainted(false);
                        subPane.add(doneButton);

                        pane.add(subPane, BorderLayout.SOUTH);

                        dialog.pack();
                        dialog.setModal(true);
                        dialog.setLocationRelativeTo(ancestor);
                        dialog.setVisible(true);
                        break;

                    case LIST:
                        final ArrayEditorDialog<OutlierDescriptor> arrayEditorDialog = new ArrayEditorDialog<>(ancestor,
                                null != definitions_ ? getDescriptors() : new OutlierDescriptor[]{}, OutlierDescriptor.class);
                        arrayEditorDialog.setTitle("Pre-specified outliers");
                        arrayEditorDialog.setLocationRelativeTo(ancestor);
                        arrayEditorDialog.setVisible(true);
                        if (arrayEditorDialog.isDirty()) {
                            setDescriptors(arrayEditorDialog.getElements());
                        }
                        break;
                }
            }
        });
    }

    private void closeDialog(JTable table) {
        TableCellEditor cellEditor = table.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        Map<Day, List<OutlierDefinition>> old = definitions_;
        Map<Day, List<OutlierDefinition>> modelDefs = ((OutliersModel) table.getModel()).getDefinitions();
        OutlierDefinition[] list = modelDefs
                .values()
                .stream()
                .flatMap(Collection::stream)
                .toArray(OutlierDefinition[]::new);
        setValue(list);
        OutlierDefinitionsEditor.this.firePropertyChange(old, definitions_);
    }

    private void setDescriptors(List<OutlierDescriptor> elements) {
        Map<Day, List<OutlierDefinition>> old = definitions_;
        definitions_ = new HashMap<>();
        for (OutlierDescriptor element : elements) {
            Day key = element.getPosition();
            if (!definitions_.containsKey(key) || definitions_.get(key) == null) {
                definitions_.put(key, new ArrayList<>());
            }
            definitions_.get(key).add(element.getCore());
        }
        firePropertyChange(old, definitions_);
    }

    private OutlierDescriptor[] getDescriptors() {
        return definitions_
                .values()
                .stream().flatMap(Collection::stream)
                .map(OutlierDescriptor::new)
                .toArray(OutlierDescriptor[]::new);
    }

    @Override
    public void setValue(Object value) {
        definitions_ = new HashMap<>();
        if (value instanceof OutlierDefinition[]) {
            OutlierDefinition[] outs = ((OutlierDefinition[]) value);
            for (OutlierDefinition out : outs) {
                Day key = out.getPosition();
                if (!definitions_.containsKey(key) || definitions_.get(key) == null) {
                    definitions_.put(key, new ArrayList<>());
                }
                // makes copies
                definitions_.get(key).add(new OutlierDefinition(key, out.getType()));
            }
        }
    }

    @Override
    public Object getValue() {
        return definitions_
                .values()
                .stream().flatMap(Collection::stream)
                .toArray(OutlierDefinition[]::new);
    }

    static class OutliersModel extends DefaultTableModel {

        private final int firstYear_;
        private final int lastYear_;
        private final int freq_;
        private final Map<Day, List<OutlierDefinition>> defs_;
        private final String[] months;

        public Map<Day, List<OutlierDefinition>> getDefinitions() {
            return defs_;
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

        public OutliersModel(int first, int last, int freq, Map<Day, List<OutlierDefinition>> defs) {
            this.firstYear_ = first;
            this.lastYear_ = last;
            this.freq_ = freq;
            this.defs_ = defs;
            this.months = new DateFormatSymbols().getShortMonths();
        }

        @Override
        public int getColumnCount() {
            return freq_ + 1;
        }

        @Override
        public String getColumnName(int column) {
            switch (freq_) {
                case 12:
                    if (column > 0) {
                        return months[column - 1];
                    } else {
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
                return OutlierType[].class;
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
                return defs_.get(day).stream().map(OutlierDefinition::getType).toArray();
            } else {
                return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            final Day day = new TsPeriod(TsFrequency.valueOf(freq_), firstYear_ + row, column - 1).firstday();
            if (aValue != null) {
                defs_.put(day, new ArrayList<>());

                Object[] values = (Object[]) aValue;
                for (Object o : values) {
                    OutlierDefinition toAdd = new OutlierDefinition(day, (OutlierType) o);
                    if (!defs_.get(day).contains(toAdd)) {
                        defs_.get(day).add(toAdd);
                    }
                }
            }
        }
    }

    static class OutlierTypeRenderer extends DefaultTableCellRenderer {

        private final JPanel panel_;

        public OutlierTypeRenderer() {
            panel_ = new JPanel();

        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            panel_.setToolTipText(null);
            panel_.removeAll();
            setHorizontalAlignment(JLabel.CENTER);
            if (null != value) {
                Object[] input = (Object[]) value;
                OutlierType[] oType = Arrays.copyOf(input, input.length, OutlierType[].class);
                panel_.setLayout(new GridLayout(oType.length > 2 ? 2 : 1, 2));
                panel_.setToolTipText(Arrays.stream(oType).map(Enum::toString).collect(Collectors.joining(", ")));

                for (OutlierType t : oType) {
                    JLabel label = new JLabel(t.toString());
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setOpaque(true);
                    label.setFont(label.getFont().deriveFont(10.0f));
                    label.setBackground(ColorChooser.getColor(t.toString()));
                    label.setForeground(ColorChooser.getForeColor(t.toString()));
                    panel_.add(label);
                }

                panel_.setOpaque(true);
            } else {
                panel_.setBackground(Color.WHITE);
            }
            return panel_;
        }
    }

    static class OutlierTypeEditor extends AbstractCellEditor implements TableCellEditor {

        private final CheckComboBox box;

        public OutlierTypeEditor() {
            OutlierType[] items = {
                null,
                OutlierType.AO,
                OutlierType.LS,
                OutlierType.TC,
                OutlierType.SO
            };
            box = new CheckComboBox(items);
        }

        @Override
        public Object getCellEditorValue() {
            return box.getSelectedItems();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            box.uncheckAllItems();
            if (value != null) {
                box.addSelectedItems(Arrays.stream((Object[]) value).map(o -> (OutlierType) o).toArray(OutlierType[]::new));
            }

            return box;
        }
    }

    class CheckListRenderer extends JCheckBox implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean hasFocus) {
            CheckListItem item = (CheckListItem) value;
            setEnabled(list.isEnabled());
            setSelected(item.isSelected());
            setFont(list.getFont());
            setBackground(ColorChooser.getColor(item.getType().toString()));
            setForeground(ColorChooser.getForeColor(item.getType().toString()));
            setText(value.toString());
            return this;
        }
    }

    public enum PrespecificiedOutliersEditor {
        CALENDAR_GRID("Calendar-like Grid"),
        LIST("List of outliers");

        private final String description;

        PrespecificiedOutliersEditor(String d) {
            description = d;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
