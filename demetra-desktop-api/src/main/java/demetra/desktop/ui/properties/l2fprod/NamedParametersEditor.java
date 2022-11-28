package demetra.desktop.ui.properties.l2fprod;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import demetra.data.Parameter;
import demetra.desktop.util.NbComponents;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Demortier Jeremy
 */
public class NamedParametersEditor extends AbstractPropertyEditor {

    private NamedParameters nparameters;

    public NamedParametersEditor() {
        editor = new JButton(new AbstractAction("...") {

            @Override
            public void actionPerformed(ActionEvent e) {
                Window ancestor = SwingUtilities.getWindowAncestor(editor);
                final ParametersEditorDlg dialog = new ParametersEditorDlg(ancestor, nparameters);
                dialog.setTitle("Coefficients");
                dialog.setVisible(true);
                if (dialog.dirty) {
                    fireChanged();
                }
            }
        });
    }

    void fireChanged() {
        NamedParameters old=nparameters;
        nparameters=new NamedParameters().addAll(old.all());
        firePropertyChange(old, nparameters);
    }

    @Override
    public Object getValue() {
        return nparameters;
    }

    @Override
    public void setValue(Object value) {

        if (null != value && value instanceof NamedParameters) {
            nparameters = (NamedParameters) value;
        }
    }

    @Override
    public String getAsText() {
        return "";
    }
}

class ParametersEditorDlg extends JDialog {

    NamedParameters parameters;
    Parameter[] initial;

    boolean dirty = false;

    public ParametersEditorDlg(final Window owner, NamedParameters parameters) {

        super(owner);
        this.parameters = parameters;
        this.initial = parameters.parameters();

        final JPanel pane = new JPanel(new BorderLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        final JTable table = new JTable(
                new DefaultTableModel() {

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public String getColumnName(int column) {
                return switch (column) {
                    case 0 ->
                        "Variable";
                    case 1 ->
                        "Fixed";
                    case 2 ->
                        "Value";
                    default ->
                        "";
                };
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 ->
                        String.class;
                    case 1 ->
                        Boolean.class;
                    case 2 ->
                        Double.class;
                    default ->
                        null;
                };
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }

            @Override
            public int getRowCount() {
                return parameters.size();
            }

            @Override
            public Object getValueAt(int row, int column) {

                return switch (column) {
                    case 0 ->
                        parameters.get(row).getName();
                    case 1 ->
                        parameters.get(row).getObject().isFixed();
                    case 2 ->
                        parameters.get(row).getObject().getValue();
                    default ->
                        null;
                }; //== ParameterType.Fixed;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                switch (column) {
                    case 2 -> {
                        double val = Double.parseDouble(aValue.toString());
                        Parameter p = parameters.get(row).getObject();
                        if (p.isFixed()) {
                            p = Parameter.fixed(val);
                        } else if (val == 0) {
                            p = Parameter.undefined();
                        } else {
                            p = Parameter.initial(val);
                        }
                        parameters.set(row, p);
                    }
                    case 1 -> {
                        Boolean f = (Boolean) aValue;
                        Parameter p = parameters.get(row).getObject();
                        if (f) {
                            p = Parameter.fixed(p.getValue());
                        } else {
                            p = Parameter.initial(p.getValue());
                        }
                        parameters.set(row, p);
                    }
                }

                fireTableCellUpdated(row, column);
            }
        });
        table.setFillsViewportHeight(true);
        final JPanel buttonPane = new JPanel();
        BoxLayout layout = new BoxLayout(buttonPane, BoxLayout.LINE_AXIS);
        buttonPane.setLayout(layout);
        final JButton restoreButton = new JButton("Restore");
        restoreButton.setPreferredSize(new Dimension(80, 27));
        restoreButton.setFocusPainted(false);
        restoreButton.setEnabled(true);
        restoreButton.addActionListener(ev -> {
            try {
                parameters.set(initial);
                dirty = false;
                SwingUtilities.invokeLater(() -> {
                    table.updateUI();
                });

            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        });
        buttonPane.add(restoreButton);

        buttonPane.add(Box.createGlue());
        final JButton okButton = new JButton("Ok");
        okButton.setPreferredSize(new Dimension(80, 27));
        okButton.addActionListener(ev -> {
            dirty = true;
            ParametersEditorDlg.this.setVisible(false);
        });
        okButton.setFocusPainted(false);
        buttonPane.add(okButton);
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(80, 27));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(ev -> {
            parameters.set(initial);
            dirty = false;
            ParametersEditorDlg.this.setVisible(false);
        });
        buttonPane.add(cancelButton);

        buttonPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        pane.add(buttonPane, BorderLayout.SOUTH);

        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pane.add(NbComponents.newJScrollPane(table), BorderLayout.CENTER);
        setMinimumSize(new Dimension(150, 200));
        setContentPane(pane);
        pack();
        setLocationRelativeTo(owner);
        setModal(true);
    }

    public NamedParameters getParameters() {
        return parameters;
    }
}
