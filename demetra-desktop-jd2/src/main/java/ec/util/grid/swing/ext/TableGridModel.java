/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.util.grid.swing.ext;

import ec.tstoolkit.data.Table;
import ec.util.grid.swing.AbstractGridModel;

/**
 *
 * @author Philippe Charles
 */
public final class TableGridModel extends AbstractGridModel {

    private final Table<?> table;

    public TableGridModel(Table<?> table) {
        this.table = table;
    }

    @Override
    public int getRowCount() {
        return table.getRowsCount();
    }

    @Override
    public int getColumnCount() {
        return table.getColumnsCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return table.get(rowIndex, columnIndex);
    }
}
