/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.ui.util.NbComponents;
import ec.satoolkit.x11.SigmavecOption;
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
 * @author Christiane Hofer
 */
public class SigmavecPropertyEditor  extends AbstractPropertyEditor {

    private SigmavecOption[] groups_;

    public SigmavecPropertyEditor() {
        editor = new SigmavecEditor();
    }
    
    void fireChanged(SigmavecOption[] groups) {
        SigmavecOption[] old = groups_;
        groups_ = groups;
        firePropertyChange(old, groups_);
    }

    @Override
    public Object getValue() {
        return groups_;
    }

    @Override
    public void setValue(Object value) {
        if (null != value && value instanceof SigmavecOption[]) {
            groups_ = (SigmavecOption[]) value;
            ((SigmavecEditor) editor).setGroups(groups_);
        }
    }

    class SigmavecEditor extends JPanel {

        private SigmavecOption[] ngroups_;

        public SigmavecEditor() {
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
                                    return "Group";
                                }
                            }
                            
                            @Override
                            public Class<?> getColumnClass(int columnIndex) {
                                if (columnIndex == 0) {
                                    return String.class;
                                } else {
                                    return SigmavecOption.class;
                                }
                            }
                            
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return column == 1;
                            }
                            
                            @Override
                            public int getRowCount() {
                                return ngroups_.length;
                            }
                            
                            @Override
                            public Object getValueAt(int row, int column) {
                                if (column == 0) {
                                    return TsPeriod.formatPeriod(TsFrequency.valueOf(ngroups_.length), row);
                                } else {
                                    return ngroups_[row];
                                }
                            }
                            
                            @Override
                            public void setValueAt(Object aValue, int row, int column) {
                                if (column == 1) {
                                    ngroups_[row] = (SigmavecOption) aValue;
                                }
                                fireTableCellUpdated(row, column);
                            }
                        });
                
                table.setDefaultEditor(SigmavecOption.class, new CustomSigmavecEditor());
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
                        fireChanged(ngroups_);
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

        public void setGroups(final SigmavecOption[] param) {
            ngroups_ = param.clone();
        }
    }
}

class CustomSigmavecEditor extends AbstractCellEditor implements TableCellEditor {

    private final JComboBox cb_;

    public CustomSigmavecEditor() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(SigmavecOption.values());
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
    

