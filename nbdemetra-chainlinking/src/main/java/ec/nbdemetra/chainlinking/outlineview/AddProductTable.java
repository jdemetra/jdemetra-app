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
package ec.nbdemetra.chainlinking.outlineview;

import ec.nbdemetra.ui.DemetraUI;
import ec.tss.TsFactory;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.chainlinking.AChainLinking.Product;
import ec.ui.chart.TsSparklineCellRenderer;
import ec.util.grid.swing.XTable;
import ec.util.various.swing.FontAwesome;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DropMode;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;

/**
 * Table where input data are given for the chain linking processing. At least,
 * 2 of the following data must be provided : Ts of quantities, prices and
 * values.
 *
 * @author Mats Maggi
 */
public class AddProductTable extends XTable {

    private final ProductTableModel model;
    private JPopupMenu popupMenu;
    private final DemetraUI demetraUI = DemetraUI.getDefault();

    public AddProductTable() {
        model = new ProductTableModel(new ArrayList<Product>());

        setModel(model);

        createPopupMenu();

        setDragEnabled(true);
        setDropMode(DropMode.USE_SELECTION);
        setTransferHandler(new TsTransferHandler());
        setDefaultRenderer(TsData.class, new TsSparklineCellRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getTableHeader().setReorderingAllowed(false);
        addMouseListener(new MyMouseListener());

        setNoDataRenderer(new XTable.DefaultNoDataRenderer("Click the + button to add a product", ""));

        setToolTipText("Drop the Ts data on the corresponding 'Quantity', 'Price' or 'Value' cell");
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        model.setValueAt(aValue, row, column);
    }

    public void addProduct(Product p) {
        model.addProduct(p);
    }

    public void removeProduct(int index) {
        model.removeProduct(index);
    }

    public List<Product> getProducts() {
        return model.getProducts();
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        JMenuItem remove = new JMenuItem(new AbstractAction("Remove ts data", demetraUI.getPopupMenuIcon(FontAwesome.FA_TRASH_O)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                setValueAt(null, getSelectedRow(), getSelectedColumn());
            }
        });

        popupMenu.add(remove);
    }

    private class MyMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                AddProductTable source = (AddProductTable) e.getSource();
                int col = source.columnAtPoint(e.getPoint());
                int row = source.rowAtPoint(e.getPoint());
                if (row != -1 && col > 0) {
                    DemetraUI demetraUI = DemetraUI.getDefault();
                    TsData tsData = (TsData) source.getValueAt(row, col);
                    if (tsData != null) {
                        String tsName = String.valueOf(source.getValueAt(row, 0));
                        tsName += " " + (col == 1 ? "(Quantity)" : col == 2 ? "(Price)" : "(Value)");
                        demetraUI.getTsAction().open(TsFactory.instance.createTs(tsName, null, tsData));
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {

                AddProductTable source = (AddProductTable) e.getSource();
                int col = source.columnAtPoint(e.getPoint());
                int row = source.rowAtPoint(e.getPoint());
                if (!source.isRowSelected(row)) {
                    source.changeSelection(row, col, false, false);
                }

                if (col > 0 && row != -1) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

    }
}
