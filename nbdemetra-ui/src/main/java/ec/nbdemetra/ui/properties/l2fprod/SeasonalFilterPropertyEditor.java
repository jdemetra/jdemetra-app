/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import ec.nbdemetra.ui.NbComponents;
import ec.satoolkit.x11.SeasonalFilterOption;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author Jean Palate
 */
public class SeasonalFilterPropertyEditor extends AbstractPropertyEditor {

    private SeasonalFilterOption[] filters_;

    public SeasonalFilterPropertyEditor() {
        editor = new SeasonalFilterEditor();
    }
    
    void fireChanged(SeasonalFilterOption[] filters) {
        SeasonalFilterOption[] old = filters_;
        filters_ = filters;
        firePropertyChange(old, filters_);
    }

    @Override
    public Object getValue() {
        return filters_;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof SeasonalFilterOption[]) {
            filters_ = (SeasonalFilterOption[]) value;
            ((SeasonalFilterEditor) editor).setFilters(filters_);
        }
    }

    class SeasonalFilterEditor extends JPanel {

        private SeasonalFilterOption[] nfilters_;

        public SeasonalFilterEditor() {
            final JButton button = new JButton("...");
            button.addActionListener(event -> {
                JPanel pane = new JPanel(new BorderLayout());
                final JTable table = new JTable(
                        new DefaultTableModel() {
                            
                            @Override
                            public int getColumnCount() {
                                return 2;
                            }
                            
                            @Override
                            public String getColumnName(int column) {
                                if (column == 0) {
                                    return "Period";
                                } else {
                                    return "Filter";
                                }
                            }
                            
                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                if (columnIndex == 0) {
                                    return String.class;
                                } else {
                                    return SeasonalFilterOption.class;
                                }
                            }
                            
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return column == 1;
                            }
                            
                            @Override
                            public int getRowCount() {
                                return nfilters_.length;
                            }
                            
                            @Override
                            public Object getValueAt(int row, int column) {
                                if (column == 0) {
                                    return TsPeriod.formatPeriod(TsFrequency.valueOf(nfilters_.length), row);
                                } else {
                                    return nfilters_[row];
                                }
                            }
                            
                            @Override
                            public void setValueAt(Object aValue, int row, int column) {
                                if (column == 1) {
                                    nfilters_[row] = (SeasonalFilterOption) aValue;
                                }
                                fireTableCellUpdated(row, column);
                            }
                        });
                
                table.setDefaultEditor(SeasonalFilterOption.class, new CustomFilterEditor());
                table.setFillsViewportHeight(true);
                pane.add(NbComponents.newJScrollPane(table), BorderLayout.CENTER);
                
                JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(button), Dialog.ModalityType.TOOLKIT_MODAL);
                dlg.setContentPane(pane);
                dlg.addWindowListener(new WindowAdapter() {
                    
                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (table.getCellEditor() != null) {
                            table.getCellEditor().stopCellEditing();
                        }
                        fireChanged(nfilters_);
                    }
                });
                dlg.setMinimumSize(new Dimension(300, 300));
                dlg.setModal(true);
                dlg.setVisible(true);
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
            });

            setLayout(new BorderLayout());
            add(button, BorderLayout.CENTER);
        }

        public void setFilters(final SeasonalFilterOption[] param) {
            nfilters_ = param.clone();
        }
    }
}

class CustomFilterEditor extends AbstractCellEditor implements TableCellEditor {

    private final JComboBox cb_;

    public CustomFilterEditor() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(SeasonalFilterOption.values());
        cb_ = new JComboBox(model);
    }

    @Override
    public Object getCellEditorValue() {
        return cb_.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        cb_.setSelectedItem(value);
        return cb_;
    }
}
