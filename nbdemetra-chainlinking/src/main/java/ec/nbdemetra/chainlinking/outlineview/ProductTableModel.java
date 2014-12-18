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

import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.chainlinking.AChainLinking.Product;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Table model used by the input table of the chain linking
 *
 * @author Mats Maggi
 */
public class ProductTableModel extends DefaultTableModel {

    private final List<Product> products;
    private final String[] titles = new String[]{"Name", "Quantity", "Price", "Value"};

    public ProductTableModel(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product p) {
        products.add(p);
        fireTableDataChanged();
    }

    public void removeProduct(int index) {
        products.remove(index);
        fireTableDataChanged();
    }

    public void removeProduct(Product p) {
        products.remove(p);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        if (products == null) {
            return 0;
        }
        return products.size();
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return products.get(rowIndex).getName();
            case 1:
                return products.get(rowIndex).getQuantities();
            case 2:
                return products.get(rowIndex).getPrice();
            case 3:
                return products.get(rowIndex).getValue();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            default:
                return TsData.class;
        }
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        if (column == 0) {
            products.get(row).setName(String.valueOf(aValue));
        } else {
            TsData ts = (TsData) aValue;
            if (ts != null) {
                ts = ts.changeFrequency(TsFrequency.Quarterly, TsAggregationType.Sum, true);
            }
            switch (column) {
                case 1:
                    products.get(row).setQuantities(ts);
                    break;
                case 2:
                    products.get(row).setPrice(ts);
                    break;
                case 3:
                    products.get(row).setValue(ts);
            }
        }

        fireTableCellUpdated(row, column);
    }

}
