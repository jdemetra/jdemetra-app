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
package ec.ui.list;

import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.awt.ActionMaps;
import ec.nbdemetra.ui.awt.InputMaps;
import ec.nbdemetra.ui.awt.KeyStrokes;
import ec.nbdemetra.ui.tsaction.ITsAction;
import ec.tss.DynamicTsVariable;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.timeseries.regression.ITsVariable;
import ec.tstoolkit.timeseries.regression.TsVariable;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.chart.TsSparklineCellRenderer;
import ec.ui.interfaces.ITsActionAble;
import ec.util.chart.swing.Charts;
import ec.util.grid.swing.XTable;
import ec.util.various.swing.JCommand;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
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
public class JTsVariableList extends JComponent implements ITsActionAble {

    public static final String DELETE_ACTION = "delete";
    public static final String CLEAR_ACTION = "clear";
    public static final String SELECT_ALL_ACTION = "selectAll";
    public static final String RENAME_ACTION = "rename";
    private static final String OPEN_ACTION = "open";

    private final XTable table;
    private final TsVariables variables;
    private ITsAction tsAction;

    public JTsVariableList(TsVariables vars) {
        this.variables = vars;
        this.table = buildTable();
        this.tsAction = null;

        registerActions();
        registerInputs();
        enableOpenOnDoubleClick();
        enablePopupMenu();

        setLayout(new BorderLayout());
        add(NbComponents.newJScrollPane(table), BorderLayout.CENTER);
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public ITsAction getTsAction() {
        return tsAction;
    }

    @Override
    public void setTsAction(ITsAction tsAction) {
        ITsAction old = this.tsAction;
        this.tsAction = tsAction;
        firePropertyChange(TS_ACTION_PROPERTY, old, this.tsAction);
    }
    //</editor-fold>

    protected JPopupMenu buildPopupMenu() {
        ActionMap actionMap = getActionMap();

        JMenu result = new JMenu();

        JMenuItem item;

        item = new JMenuItem(actionMap.get(OPEN_ACTION));
        item.setText("Open");
        item.setAccelerator(KeyStrokes.OPEN.get(0));
        item.setFont(item.getFont().deriveFont(Font.BOLD));
        result.add(item);

        item = buildOpenWithMenu();
        item.setText("Open with");
        result.add(item);

        item = new JMenuItem(actionMap.get(RENAME_ACTION));
        item.setText("Rename");
        result.add(item);

        result.addSeparator();

        item = new JMenuItem(actionMap.get(DELETE_ACTION));
        item.setText("Remove");
        item.setAccelerator(KeyStrokes.DELETE.get(0));
        result.add(item);

        item = new JMenuItem(actionMap.get(CLEAR_ACTION));
        item.setText("Clear");
        item.setAccelerator(KeyStrokes.CLEAR.get(0));
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

    private XTable buildTable() {
        final XTable result = new XTable();
        result.setNoDataRenderer(new XTable.DefaultNoDataRenderer("Drop data here", "Drop data here"));

        result.setDefaultRenderer(TsData.class, new TsSparklineCellRenderer());
        result.setDefaultRenderer(TsPeriod.class, new TsPeriodTableCellRenderer());
        result.setDefaultRenderer(TsFrequency.class, new TsFrequencyTableCellRenderer());
        result.setDefaultRenderer(String.class, new MultiLineNameTableCellRenderer());

        result.setModel(new CustomTableModel());
        XTable.setWidthAsPercentages(result, .1, .2, .1, .1, .1, .1, .3);

        result.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(result.getModel());
        result.setRowSorter(sorter);
        result.setDragEnabled(true);
        result.setTransferHandler(new TsVariableTransferHandler());
        result.setFillsViewportHeight(true);

        return result;
    }

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
            String name=variables.nextName();
            TsVariable var;
            if (s.getMoniker().isAnonymous()) {
                var=new TsVariable(s.getName(), s.getTsData());
            } else {
                var=new DynamicTsVariable(s.getName(), s.getMoniker(), s.getTsData());
            }
            var.setName(name);
            variables.set(name, var);
        }
        ((CustomTableModel) table.getModel()).fireTableStructureChanged();
    }

    private static final class MultiLineNameTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String text = (String) value;
            if (text.isEmpty()) {
                result.setText(" ");
                result.setToolTipText(null);
            } else if (text.startsWith("<html>")) {
                result.setText(text);
                result.setToolTipText(text);
            } else {
                result.setText(MultiLineNameUtil.join(text));
                result.setToolTipText(MultiLineNameUtil.toHtml(text));
            }
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
                case 1:
                    return String.class;
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

    private static final class VarName extends NotifyDescriptor.InputLine {

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
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && !textField.getInputVerifier().verify(textField)) {
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

    private void registerActions() {
        ActionMap am = getActionMap();
        am.put(OPEN_ACTION, OpenCommand.INSTANCE.toAction(this));
        am.put(RENAME_ACTION, RenameCommand.INSTANCE.toAction(this));
        am.put(DELETE_ACTION, DeleteCommand.INSTANCE.toAction(this));
        am.put(CLEAR_ACTION, ClearCommand.INSTANCE.toAction(this));
        ActionMaps.copyEntries(am, false, table.getActionMap());
    }

    private void registerInputs() {
        InputMap im = getInputMap();
        KeyStrokes.putAll(im, KeyStrokes.OPEN, OPEN_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.DELETE, DELETE_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.CLEAR, CLEAR_ACTION);
        InputMaps.copyEntries(im, false, table.getInputMap());
    }

    private void enableOpenOnDoubleClick() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!Charts.isPopup(e) && Charts.isDoubleClick(e)) {
                    ActionMaps.performAction(getActionMap(), OPEN_ACTION, e);
                }
            }
        });
    }

    private void enablePopupMenu() {
        table.setComponentPopupMenu(buildPopupMenu());
    }

    private JMenu buildOpenWithMenu() {
        JMenu result = new JMenu(OpenWithCommand.INSTANCE.toAction(this));

        for (ITsAction o : DemetraUI.getDefault().getTsActions()) {
            JMenuItem item = new JMenuItem(new OpenWithItemCommand(o).toAction(this));
            item.setName(o.getName());
            item.setText(o.getDisplayName());
            result.add(item);
        }

        return result;
    }

    //<editor-fold defaultstate="collapsed" desc="Commands">
    private static TsVariable getSelectedVariable(JTsVariableList c) {
        if (c.table.getSelectedRowCount() == 1) {
            int idx = c.table.convertRowIndexToModel(c.table.getSelectedRow());
            ITsVariable result = c.variables.get(c.variables.getNames()[idx]);
            if (result instanceof TsVariable) {
                return (TsVariable) result;
            }
        }
        return null;
    }

    private static Ts toTs(TsVariable variable) {
        return variable instanceof DynamicTsVariable
        ? TsFactory.instance.createTs(variable.getDescription(), ((DynamicTsVariable) variable).getMoniker(), TsInformationType.All)
        : TsFactory.instance.createTs(variable.getDescription(), null, variable.getTsData());
    }

    private static final class OpenCommand extends JCommand<JTsVariableList> {

        public static final OpenCommand INSTANCE = new OpenCommand();

        @Override
        public void execute(JTsVariableList c) throws Exception {
            TsVariable variable = getSelectedVariable(c);
            ITsAction tsAction = c.tsAction != null ? c.tsAction : DemetraUI.getDefault().getTsAction();
            tsAction.open(toTs(variable));
        }

        @Override
        public boolean isEnabled(JTsVariableList c) {
            return getSelectedVariable(c) != null;
        }

        @Override
        public ActionAdapter toAction(JTsVariableList c) {
            return super.toAction(c).withWeakListSelectionListener(c.table.getSelectionModel());
        }
    }

    private static final class OpenWithCommand extends JCommand<JTsVariableList> {

        public static final OpenWithCommand INSTANCE = new OpenWithCommand();

        @Override
        public void execute(JTsVariableList c) throws Exception {
            // do nothing
        }

        @Override
        public boolean isEnabled(JTsVariableList c) {
            return c.table.getSelectedRowCount() == 1;
        }

        @Override
        public JCommand.ActionAdapter toAction(JTsVariableList c) {
            return super.toAction(c).withWeakListSelectionListener(c.table.getSelectionModel());
        }
    }

    private static final class OpenWithItemCommand extends JCommand<JTsVariableList> {

        private final ITsAction tsAction;

        public OpenWithItemCommand(@Nonnull ITsAction tsAction) {
            this.tsAction = tsAction;
        }

        @Override
        public void execute(JTsVariableList c) throws Exception {
            tsAction.open(toTs(getSelectedVariable(c)));
        }
    }

    private static final class RenameCommand extends JCommand<JTsVariableList> {

        public static final RenameCommand INSTANCE = new RenameCommand();

        @Override
        public void execute(JTsVariableList c) throws java.lang.Exception {
            int[] sel = c.table.getSelectedRows();
            if (sel.length != 1) {
                return;
            }

            String oldName = c.names(sel)[0], newName;
            VarName nd = new VarName(c.variables, "New name:", "Please enter the new name", oldName);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            newName = nd.getInputText();
            if (newName.equals(oldName)) {
                return;
            }
            c.variables.rename(oldName, newName);
            ((CustomTableModel) c.table.getModel()).fireTableStructureChanged();
        }

        @Override
        public boolean isEnabled(JTsVariableList c) {
            return c.table.getSelectedRowCount() == 1;
        }

        @Override
        public ActionAdapter toAction(JTsVariableList c) {
            return super.toAction(c).withWeakListSelectionListener(c.table.getSelectionModel());
        }
    }

    private static final class DeleteCommand extends JCommand<JTsVariableList> {

        public static final DeleteCommand INSTANCE = new DeleteCommand();

        @Override
        public void execute(JTsVariableList c) throws java.lang.Exception {
            int[] sel = c.table.getSelectedRows();
            if (sel.length == 0) {
                return;
            }
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation("Are you sure you want to delete the selected items?", NotifyDescriptor.OK_CANCEL_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                return;
            }

            String[] n = c.names(sel);
            for (int i = 0; i < n.length; ++i) {
                c.variables.remove(n[i]);
            }
            ((CustomTableModel) c.table.getModel()).fireTableStructureChanged();
        }

        @Override
        public boolean isEnabled(JTsVariableList c) {
            return c.table.getSelectedRowCount() > 0;
        }

        @Override
        public ActionAdapter toAction(JTsVariableList c) {
            return super.toAction(c).withWeakListSelectionListener(c.table.getSelectionModel());
        }
    }

    private static final class ClearCommand extends JCommand<JTsVariableList> {

        public static final ClearCommand INSTANCE = new ClearCommand();

        @Override
        public void execute(JTsVariableList c) throws java.lang.Exception {
            c.variables.clear();
            ((CustomTableModel) c.table.getModel()).fireTableStructureChanged();
        }
    }
    //</editor-fold>
}
