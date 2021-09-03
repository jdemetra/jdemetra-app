package ec.nbdemetra.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.desktop.util.NbComponents;
import ec.tstoolkit.Parameter;
import ec.tstoolkit.ParameterType;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Demortier Jeremy
 */
public class ParametersPropertyEditor extends AbstractPropertyEditor {

    private Parameter[] parameters_;
    
    public enum Type{Undefined, Initial, Fixed}

    private static ParameterType convert(String val){
        Type t=Type.valueOf(val);
        if (t == Type.Initial)
            return ParameterType.Initial;
        else if (t== Type.Fixed)
            return ParameterType.Fixed;
        else
            return ParameterType.Undefined;
    }
    
    private static Type convert(ParameterType t){
         if (t == ParameterType.Initial)
            return Type.Initial;
        else if (t== ParameterType.Fixed)
            return Type.Fixed;
        else
            return Type.Undefined;
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
                                if (columnIndex == 0)
                                    return Double.class;
                                else
                                    return Type.class;
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
                                
                                switch (column) {
                                    case 0:
                                        return param.getValue();
                                    case 1:
                                        return convert(param.getType()) ;//== ParameterType.Fixed;
                                    default:
                                        return null;
                                }
                            }
                            
                            @Override
                            public void setValueAt(Object aValue, int row, int column) {
                                Parameter param = nparameters_[row];
                                
                                switch (column) {
                                    case 0:
                                        double val = Double.parseDouble(aValue.toString());
                                        param.setValue(val);
                                        break;
                                    case 1:
                                        param.setType(convert(aValue.toString()));
                                        //boolean fixed = Boolean.parseBoolean(aValue.toString());
                                        //param.setType(fixed ? ParameterType.Fixed : ParameterType.Undefined);
                                        break;
                                }
                                
                                fireTableCellUpdated(row, column);
                            }
                        });
                
                //table.getColumnModel().getColumn(0).setCellEditor(new CustomNumberEditor());
                JComboBox cb=new JComboBox(Type.values());
                table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cb));
                table.setFillsViewportHeight(true);
                pane.add(NbComponents.newJScrollPane(table), BorderLayout.CENTER);
                
                final JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(button), Dialog.ModalityType.TOOLKIT_MODAL);
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
                dlg.setModal(true);
                SwingUtilities.invokeLater(() -> dlg.setVisible(true));
            });

            setLayout(new BorderLayout());
            add(button, BorderLayout.CENTER);
        }

        public void setParameters(final Parameter[] param) {
            nparameters_ = Parameter.clone(param);
        }

        public Parameter[] getParameters() {
            return nparameters_;
        }
    }
}

//class CustomNumberEditor extends DefaultCellEditor {
//
//    private final JSpinner spinner_;
//    private JSpinner.NumberEditor editor;
//    private JTextField textField;
//    private boolean valueSet;
//
//    public CustomNumberEditor() {
//        super(new JTextField());
//        SpinnerNumberModel model = new SpinnerNumberModel(0, -2, 2, 0.05);
//        spinner_ = new JSpinner(model);
//        editor = ((JSpinner.NumberEditor) spinner_.getEditor());
//        textField = editor.getTextField();
//        textField.addFocusListener(new FocusListener() {
//
//            @Override
//            public void focusGained(FocusEvent fe) {
//                System.err.println("Got focus");
//                //textField.setSelectionStart(0);
//                //textField.setSelectionEnd(1);
//                SwingUtilities.invokeLater(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        if (valueSet) {
//                            textField.setCaretPosition(1);
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void focusLost(FocusEvent fe) {
//            }
//        });
//        textField.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                stopCellEditing();
//            }
//        });
//    }
//
//    @Override
//    public Object getCellEditorValue() {
//        return spinner_.getValue();
//    }
//
//    @Override
//    public Component getTableCellEditorComponent(JTable table, Object val, boolean isSelected, int row, int column) {
//        if (!valueSet) {
//            spinner_.setValue(val);
//        }
//        SwingUtilities.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                textField.requestFocus();
//            }
//        });
//        return spinner_;
//    }
//
//    @Override
//    public boolean isCellEditable(EventObject eo) {
//        System.err.println("isCellEditable");
//        if (eo instanceof KeyEvent) {
//            KeyEvent ke = (KeyEvent) eo;
//            System.err.println("key event: " + ke.getKeyChar());
//            textField.setText(String.valueOf(ke.getKeyChar()));
//            //textField.select(1,1);
//            //textField.setCaretPosition(1);
//            //textField.moveCaretPosition(1);
//            valueSet = true;
//        }
//        else {
//            valueSet = false;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean stopCellEditing() {
//        System.err.println("Stopping edit");
//        try {
//            editor.commitEdit();
//            spinner_.commitEdit();
//        }
//        catch (java.text.ParseException e) {
//            JOptionPane.showMessageDialog(null,
//                    "Invalid val, discarding.");
//        }
//        return super.stopCellEditing();
//    }
//}
class CustomNumberEditor extends DefaultCellEditor {

    public CustomNumberEditor() {
        super(new JFormattedTextField());
        final JFormattedTextField editor = (JFormattedTextField) super.getComponent();
        editor.setHorizontalAlignment(SwingConstants.RIGHT);
        editor.setBorder(null);
        Locale myLocale = Locale.getDefault(); // better still
        NumberFormat fmt = NumberFormat.getInstance(myLocale);
        editor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(fmt)));
        super.delegate = new EditorDelegate() {

            @Override
            public void setValue(Object value) {
                editor.setValue(value != null ? ((Number) value).doubleValue() : value);
            }

            @Override
            public Object getCellEditorValue() {
                Object val = editor.getValue();
                return val != null ? ((Number) val).doubleValue() : val;
            }

            @Override
            public boolean stopCellEditing() {
                try {
                    editor.commitEdit();
                }
                catch (java.text.ParseException e) {
                }
                return super.stopCellEditing();
            }
        };
    }
}