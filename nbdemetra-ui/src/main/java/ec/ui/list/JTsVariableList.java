/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.list;

import ec.nbdemetra.ui.MonikerUI;
import ec.nbdemetra.ui.NbComponents;
import ec.tss.DynamicTsVariable;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsIdentifier;
import ec.tss.TsInformationType;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.timeseries.regression.ITsVariable;
import ec.tstoolkit.timeseries.regression.TsVariable;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.chart.TsSparklineCellRenderer;
import ec.util.grid.swing.XTable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Jean Palate
 */
public class JTsVariableList extends JComponent {

    public static final String DELETE_ACTION = "delete";
    public static final String CLEAR_ACTION = "clear";
    public static final String SELECT_ALL_ACTION = "selectAll";
    public static final String RENAME_ACTION = "rename";
    private final ListTableSelectionListener listTableListener;
    private final XTable table;
    private final TsVariables variables;
    private final RenameAction rename;
    private final ClearAction clear;
    private final DeleteAction remove;

    public JTsVariableList(TsVariables vars) {
        this.variables = vars;
        this.table = buildTable();
        rename = new RenameAction();
        remove = new DeleteAction();
        clear = new ClearAction();
        this.listTableListener = new ListTableSelectionListener();
        table.getSelectionModel().addListSelectionListener(listTableListener);
        table.setComponentPopupMenu(buildPopupMenu());

        setLayout(new BorderLayout());
        add(NbComponents.newJScrollPane(table), BorderLayout.CENTER);
    }

    protected JPopupMenu buildPopupMenu() {
        JMenu result = new JMenu();

        JMenuItem item;

        item = new JMenuItem(rename);
        item.setText("Rename");
        result.add(item);

        result.addSeparator();

        item = new JMenuItem(remove);
        item.setText("Remove");
        result.add(item);

        item = new JMenuItem(clear);
        item.setText("Clear");
        result.add(item);

        return result.getPopupMenu();
    }

    private String[] names(int[] pos) {
        String[] n = new String[pos.length];
        CustomTableModel model = (CustomTableModel) table.getModel();
        for (int i = 0; i < pos.length; ++i) {
            n[i] = model.names[pos[i]];
        }
        return n;
    }

    private class RenameAction extends AbstractAction {

        public static final String RENAME_TITLE = "Please enter the new name",
                NAME_MESSAGE = "New name:";

        public RenameAction() {
            super(RENAME_ACTION);
            enabled = false;

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int[] sel = table.getSelectedRows();
            if (sel.length != 1) {
                return;
            }

            String oldName = names(sel)[0], newName;
            VarName nd = new VarName(variables, NAME_MESSAGE, RENAME_TITLE, oldName);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            newName = nd.getInputText();
            if (newName.equals(oldName)) {
                return;
            }
            variables.rename(oldName, newName);
            ((CustomTableModel) table.getModel()).fireTableStructureChanged();
            JTsVariableList.this.firePropertyChange(RENAME_ACTION, oldName, newName);
        }
    }

    private class DeleteAction extends AbstractAction {

        public static final String DELETE_MESSAGE = "Are you sure you want to delete the selected items?";

        public DeleteAction() {
            super(DELETE_ACTION);
            enabled = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int[] sel = table.getSelectedRows();
            if (sel.length == 0) {
                return;
            }
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(DELETE_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }

            String[] n = names(sel);
            for (int i = 0; i < n.length; ++i) {
                variables.remove(n[i]);
            }
            ((CustomTableModel) table.getModel()).fireTableStructureChanged();
            JTsVariableList.this.firePropertyChange(DELETE_ACTION, null, null);
        }
    }

    private class ClearAction extends AbstractAction {

        public ClearAction() {
            super(CLEAR_ACTION);
            enabled = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            variables.clear();
            ((CustomTableModel) table.getModel()).fireTableStructureChanged();
            JTsVariableList.this.firePropertyChange(CLEAR_ACTION, null, null);
        }
    }

    private XTable buildTable() {
        final XTable result = new XTable();
        result.setNoDataRenderer(new XTable.DefaultNoDataRenderer("Drop data here", "Drop data here"));

        result.setDefaultRenderer(TsData.class, new TsSparklineCellRenderer());
        result.setDefaultRenderer(TsPeriod.class, new TsPeriodTableCellRenderer());
        result.setDefaultRenderer(TsFrequency.class, new TsFrequencyTableCellRenderer());
        result.setDefaultRenderer(TsIdentifier.class, new TsIdentifierTableCellRenderer());

        result.setModel(new CustomTableModel());
        XTable.setWidthAsPercentages(result, .1, .2, .1, .1, .1, .1, .3);

        result.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(result.getModel());
        result.setRowSorter(sorter);
        result.setDragEnabled(true);
        result.setTransferHandler(new TsVariableTransferHandler());
        result.setFillsViewportHeight(true);
//        fillActionMap(result.getActionMap());
//        fillInputMap(result.getInputMap());

        return result;
    }

//    protected void fillActionMap(ActionMap am) {
//        for (Object o : getActionMap().keys()) {
//            am.put(o, getActionMap().get(o));
//        }
//    }
//
//    protected void fillInputMap(InputMap im) {
//        for (KeyStroke o : getInputMap().keys()) {
//            im.put(o, getInputMap().get(o));
//        }
//    }
//    
    public class TsVariableTransferHandler extends TransferHandler {

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return null;
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            boolean result = TssTransferSupport.getDefault().canImport(support.getDataFlavors());
            if (result && support.isDrop()) {
                support.setDropAction(COPY);
            }
            return result;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            TsCollection col = TssTransferSupport.getDefault().toTsCollection(support.getTransferable());
            if (col != null) {
                col.query(TsInformationType.All);
                if (!col.isEmpty()) {
                    appendTsVariables(col);
                }
                return true;
            }
            return false;
        }
    }

    public void appendTsVariables(TsCollection coll) {
        for (Ts s : coll) {
            if (s.getMoniker().isAnonymous()) {
                variables.set(variables.nextName(), new TsVariable(s.getName(), s.getTsData()));
            } else {
                variables.set(variables.nextName(), new DynamicTsVariable(s.getName(), s.getMoniker(), s.getTsData()));
            }
        }
        ((CustomTableModel) table.getModel()).fireTableStructureChanged();
    }

    private static class TsIdentifierTableCellRenderer extends DefaultTableCellRenderer {

        final MonikerUI monikerUI = MonikerUI.getDefault();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            TsIdentifier identifier = (TsIdentifier) value;
            result.setText(identifier.getName());
            result.setIcon(monikerUI.getIcon(identifier.getMoniker()));
            return result;
        }
    }
    private static final String[] information = new String[]{"Name", "Description", "Type", "Frequency", "Start", "End", "Data"};

    private class CustomTableModel extends AbstractTableModel {

        private String[] names;

        @Override
        public void fireTableStructureChanged() {
            names = variables.getNames();
            super.fireTableStructureChanged();
            updateMenus();
        }

        public CustomTableModel() {
            names = variables.getNames();
        }

        @Override
        public int getRowCount() {
            return names.length;
        }

        @Override
        public int getColumnCount() {
            return 7;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return names[rowIndex];
            }

            ITsVariable var = variables.get(names[rowIndex]);
            switch (columnIndex) {
                case 0:
                    return names[rowIndex];
                case 1:
                    return var.getDescription();
                case 2:
                    return var.getClass().getSimpleName();
                case 3:
                    return var.getDefinitionFrequency();
                case 4: {
                    TsDomain d = var.getDefinitionDomain();
                    if (d != null) {
                        return d.getStart();
                    }
                }
                case 5: {
                    TsDomain d = var.getDefinitionDomain();
                    if (d != null) {
                        return d.getLast();
                    }
                }
                case 6: {
                    TsDomain d = var.getDefinitionDomain();
                    if (d != null) {
                        List<DataBlock> data = Collections.singletonList(new DataBlock(d.getLength()));
                        var.data(d, data);
                        return new TsData(d.getStart(), data.get(0));
                    }
                }
                default:
                    return null;
            }
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getColumnName(int column) {
            return information[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 4:
                    return TsPeriod.class;
                case 5:
                    return TsPeriod.class;
                case 6:
                    return TsData.class;

            }
            return super.getColumnClass(columnIndex);
        }
    }

    private void updateMenus() {
        clear.setEnabled(!variables.isEmpty());
        int selectedRows = table.getSelectedRowCount();
        rename.setEnabled(selectedRows == 1);
        remove.setEnabled(selectedRows > 0);
    }

    private class ListTableSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                ListSelectionModel model = (ListSelectionModel) e.getSource();
                updateMenus();
            }
        }
    }

    private class VarName extends NotifyDescriptor.InputLine {

        VarName(final TsVariables vars, String title, String text, final String oldname) {
            super(title, text, NotifyDescriptor.QUESTION_MESSAGE, NotifyDescriptor.OK_CANCEL_OPTION);

            setInputText(oldname);
        textField.addKeyListener(new KeyListener() {
                // To handle VK_ENTER !!!
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && ! textField.getInputVerifier().verify(textField)){
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
            textField.setInputVerifier(new InputVerifier() {
                @Override
                public boolean verify(JComponent input) {
                    JTextField txt = (JTextField) input;
                    String name = txt.getText();
                    if (name.equals(oldname)) {
                        return true;
                    }
                    if (vars.contains(name)) {
                        NotifyDescriptor nd = new NotifyDescriptor.Message(name + " is in use. You should choose another name!");
                        DialogDisplayer.getDefault().notify(nd);
                        return false;
                    }
                    if (!vars.getNameValidator().accept(name)) {
                        NotifyDescriptor nd = new NotifyDescriptor.Message(vars.getNameValidator().getLastError());
                        DialogDisplayer.getDefault().notify(nd);
                        return false;
                    }
                    return true;
                }
            });
        }
    }
}
