package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.data.Parameter;
import demetra.data.ParameterType;
import demetra.desktop.util.NbComponents;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Demortier Jeremy
 */
public class ParametersPropertyEditor extends AbstractPropertyEditor {

    private Parameter[] parameters_;

    public enum Type {
        Undefined, Initial, Fixed
    }

    private static ParameterType convert(String val) {
        Type t = Type.valueOf(val);
        if (null == t) {
            return ParameterType.Undefined;
        } else {
            return switch (t) {
                case Initial -> ParameterType.Initial;
                case Fixed -> ParameterType.Fixed;
                default -> ParameterType.Undefined;
            };
        }
    }

    private static Type convert(ParameterType t) {
        if (null == t) {
            return Type.Undefined;
        } else {
            switch (t) {
                case Initial:
                    return Type.Initial;
                case Fixed:
                    return Type.Fixed;
                default:
                    return Type.Undefined;
            }
        }
    }

    public ParametersPropertyEditor() {
        editor = new ParametersEditor();
    }

    void fireChanged(Parameter[] parameters) {
        Parameter[] old = parameters_;
        parameters_ = parameters;
        firePropertyChange(old, parameters);
    }

    @Override
    public Object getValue() {
        return parameters_;
    }

    @Override
    public void setValue(Object value) {

        if (null != value && value instanceof Parameter[]) {
            parameters_ = (Parameter[]) value;
            ((ParametersEditor) editor).setParameters(parameters_);
        }
    }

    class ParametersEditor extends JPanel {

        private Parameter[] nparameters_;

        public ParametersEditor() {
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
                        switch (column) {
                            case 0:
                                return "Value";
                            case 1:
                                return "Fixed";
                            default:
                                return "";
                        }
                    }

                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        if (columnIndex == 0) {
                            return Double.class;
                        } else {
                            return Type.class;
                        }
                    }

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return true;
                    }

                    @Override
                    public int getRowCount() {
                        return nparameters_.length;
                    }

                    @Override
                    public Object getValueAt(int row, int column) {
                        Parameter param = nparameters_[row];

                        return switch (column) {
                            case 0 -> param.getValue();
                            case 1 -> convert(param.getType());
                            default -> null;
                        }; //== ParameterType.Fixed;
                    }

                    @Override
                    public void setValueAt(Object aValue, int row, int column) {
                        Parameter param = nparameters_[row];

                        switch (column) {
                            case 0 -> {
                                double val = Double.parseDouble(aValue.toString());
                                nparameters_[row] = Parameter.of(val, param.getType());
                            }
                            case 1 -> nparameters_[row] = Parameter.of(param.getValue(), convert(aValue.toString()));
                        }

                        fireTableCellUpdated(row, column);
                    }
                });

                //table.getColumnModel().getColumn(0).setCellEditor(new CustomNumberEditor());
                JComboBox cb = new JComboBox(Type.values());
                table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cb));
                table.setFillsViewportHeight(true);
                pane.add(NbComponents.newJScrollPane(table), BorderLayout.CENTER);
                Window ancestor = SwingUtilities.getWindowAncestor(button);
                final JDialog dlg = new JDialog(ancestor, Dialog.ModalityType.TOOLKIT_MODAL);
                dlg.setContentPane(pane);
                dlg.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (table.getCellEditor() != null) {
                            table.getCellEditor().stopCellEditing();
                        }
                        fireChanged(nparameters_);
                    }
                });
                dlg.setMinimumSize(new Dimension(300, 300));
                dlg.setLocationRelativeTo(ancestor);
                dlg.setModal(true);
                SwingUtilities.invokeLater(() -> dlg.setVisible(true));
            });

            setLayout(new BorderLayout());
            add(button, BorderLayout.CENTER);
        }

        public void setParameters(final Parameter[] param) {
            nparameters_ = param.clone();
        }

        public Parameter[] getParameters() {
            return nparameters_;
        }
    }
}

