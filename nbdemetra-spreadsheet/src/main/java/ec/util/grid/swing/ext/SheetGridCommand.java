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

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tstoolkit.data.Table;
import ec.util.grid.swing.GridModel;
import ec.util.grid.swing.JGrid;
import ec.util.spreadsheet.Cell;
import ec.util.various.swing.JCommand;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Philippe Charles
 */
public abstract class SheetGridCommand extends JCommand<JGrid> {

    @Override
    public void execute(JGrid grid) {
        Table<?> table = toTable(grid);
        Transferable t = TssTransferSupport.getDefault().fromTable(table);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
    }

    // TODO: merge some code with TableGridCommand
    @NonNull
    abstract public Table<?> toTable(@NonNull JGrid grid);

    @NonNull
    public static SheetGridCommand copyAll(boolean rowHeader, boolean columnHeader) {
        return new CopyAllCommand(rowHeader, columnHeader);
    }

    @NonNull
    public static SheetGridCommand copySelection(boolean rowHeader, boolean columnHeader) {
        return new CopySelectionCommand(rowHeader, columnHeader);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static Object toValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.isDate()) {
            return cell.getDate();
        }
        if (cell.isNumber()) {
            return cell.getNumber();
        }
        if (cell.isString()) {
            return cell.getString();
        }
        return null;
    }

    private static Table<?> copy(GridModel model, int firstRow, int firstColumn, int lastRow, int lastColumn, boolean rowHeader, boolean columnHeader) {
        Table<Object> result = new Table<>(lastRow + 1 - firstRow + (columnHeader ? 1 : 0), lastColumn + 1 - firstColumn + (rowHeader ? 1 : 0));
        for (int i = firstRow; i < lastRow + 1; i++) {
            for (int j = firstColumn; j < lastColumn + 1; j++) {
                Cell cell = (Cell) model.getValueAt(i, j);
                result.set(i - firstRow + (columnHeader ? 1 : 0), j - firstColumn + (rowHeader ? 1 : 0), toValue(cell));
            }
        }
        if (rowHeader) {
            for (int i = firstRow; i < lastRow + 1; i++) {
                result.set(i - firstRow + (columnHeader ? 1 : 0), 0, model.getRowName(i));
            }
        }
        if (columnHeader) {
            for (int j = firstColumn; j < lastColumn + 1; j++) {
                result.set(0, j - firstColumn + (rowHeader ? 1 : 0), model.getColumnName(j));
            }
        }
        return result;
    }

    private static Table<?> copy2(GridModel model, Range<Integer> r, Range<Integer> c, boolean rowHeader, boolean columnHeader) {
        if (model.getRowCount() == 0 || model.getColumnCount() == 0) {
            return new Table<>(0, 0);
        }
        int firstRow = r.hasLowerBound() ? (r.lowerBoundType().equals(BoundType.CLOSED) ? r.lowerEndpoint() : (r.lowerEndpoint() + 1)) : 0;
        int lastRow = r.hasUpperBound() ? (r.upperBoundType().equals(BoundType.CLOSED) ? r.upperEndpoint() : (r.upperEndpoint() - 1)) : (model.getRowCount() - 1);
        int firstColumn = c.hasLowerBound() ? (c.lowerBoundType().equals(BoundType.CLOSED) ? c.lowerEndpoint() : (c.lowerEndpoint() + 1)) : 0;
        int lastColumn = c.hasUpperBound() ? (c.upperBoundType().equals(BoundType.CLOSED) ? c.upperEndpoint() : (c.upperEndpoint() - 1)) : (model.getColumnCount() - 1);
        return copy(model, firstRow, firstColumn, lastRow, lastColumn, rowHeader, columnHeader);
    }

    private static final class CopyAllCommand extends SheetGridCommand {

        private final boolean rowHeader;
        private final boolean columnHeader;

        public CopyAllCommand(boolean rowHeader, boolean columnHeader) {
            this.rowHeader = rowHeader;
            this.columnHeader = columnHeader;
        }

        @Override
        public Table<?> toTable(JGrid grid) {
            GridModel model = grid.getModel();
            return copy2(model, Range.<Integer>all(), Range.<Integer>all(), rowHeader, columnHeader);
        }
    }

    private static final class CopySelectionCommand extends SheetGridCommand {

        private final boolean rowHeader;
        private final boolean columnHeader;

        public CopySelectionCommand(boolean rowHeader, boolean columnHeader) {
            this.rowHeader = rowHeader;
            this.columnHeader = columnHeader;
        }

        @Override
        public Table<?> toTable(JGrid grid) {
            ListSelectionModel r = grid.getRowSelectionModel();
            ListSelectionModel c = grid.getColumnSelectionModel();
            return !r.isSelectionEmpty() && !c.isSelectionEmpty()
                    ? copy2(grid.getModel(), Range.closed(r.getMinSelectionIndex(), r.getMaxSelectionIndex()), Range.closed(c.getMinSelectionIndex(), c.getMaxSelectionIndex()), rowHeader, columnHeader)
                    : new Table<>(0, 0);
        }
    }
    //</editor-fold>
}
