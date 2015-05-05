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

import ec.util.grid.swing.AbstractGridModel;
import ec.util.spreadsheet.Sheet;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
public final class SheetGridModel extends AbstractGridModel {

    private final Sheet sheet;

    public SheetGridModel(@Nonnull Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public int getRowCount() {
        return sheet.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return sheet.getColumnCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return sheet.getCell(rowIndex, columnIndex);
    }
}
