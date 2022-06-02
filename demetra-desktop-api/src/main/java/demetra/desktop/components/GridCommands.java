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
package demetra.desktop.components;

import demetra.data.Range;
import demetra.desktop.datatransfer.DataTransferManager;
import ec.util.grid.swing.GridModel;
import ec.util.grid.swing.JGrid;
import ec.util.various.swing.JCommand;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.ListSelectionModel;
import demetra.util.Table;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class GridCommands {

    @NonNull
    public static JCommand<JGrid> copyAll(boolean rowHeader, boolean columnHeader) {
        return new CopyAllCommand(rowHeader, columnHeader);
    }

    @NonNull
    public static JCommand<JGrid> copySelection(boolean rowHeader, boolean columnHeader) {
        return new CopySelectionCommand(rowHeader, columnHeader);
    }

    @NonNull
    public static JCommand<JGrid> copyRange(Range<Integer> rowRange, Range<Integer> columnRange, boolean rowHeader, boolean columnHeader) {
        return new CopyRangeCommand(rowRange, columnRange, rowHeader, columnHeader);
    }

    private static abstract class TableGridCommand extends JCommand<JGrid> {

        @Override
        public void execute(JGrid grid) {
            Table<?> table = toTable(grid);
            Transferable t = DataTransferManager.get().fromTable(table);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
        }

        @NonNull
        abstract public Table<?> toTable(@NonNull JGrid grid);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static Table<?> copy(GridModel model, int firstRow, int firstColumn, int lastRow, int lastColumn, boolean rowHeader, boolean columnHeader) {
        Table<Object> result = new Table<>(lastRow + 1 - firstRow + (columnHeader ? 1 : 0), lastColumn + 1 - firstColumn + (rowHeader ? 1 : 0));
        for (int i = firstRow; i < lastRow + 1; i++) {
            for (int j = firstColumn; j < lastColumn + 1; j++) {
                result.set(i - firstRow + (columnHeader ? 1 : 0), j - firstColumn + (rowHeader ? 1 : 0), model.getValueAt(i, j));
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
        int firstRow = r.start().equals(Integer.MIN_VALUE) ? 0 : r.start();
        int lastRow = r.end().equals(Integer.MAX_VALUE) ? (model.getRowCount() - 1) : (r.end() - 1);
        int firstColumn = c.start().equals(Integer.MIN_VALUE) ? 0 : c.start();
        int lastColumn = c.end().equals(Integer.MAX_VALUE) ? (model.getColumnCount() - 1) : (c.end() - 1);
        return copy(model, firstRow, firstColumn, lastRow, lastColumn, rowHeader, columnHeader);
    }

    private static final class CopyRangeCommand extends TableGridCommand {

        private final Range<Integer> rowRange;
        private final Range<Integer> columnRange;
        private final boolean rowHeader;
        private final boolean columnHeader;

        public CopyRangeCommand(Range<Integer> rowRange, Range<Integer> columnRange, boolean rowHeader, boolean columnHeader) {
            this.rowRange = rowRange;
            this.columnRange = columnRange;
            this.rowHeader = rowHeader;
            this.columnHeader = columnHeader;
        }

        @Override
        public Table<?> toTable(JGrid grid) {
            return copy2(grid.getModel(), rowRange, columnRange, rowHeader, columnHeader);
        }
    }

    private static final class CopyAllCommand extends TableGridCommand {

        private final boolean rowHeader;
        private final boolean columnHeader;

        public CopyAllCommand(boolean rowHeader, boolean columnHeader) {
            this.rowHeader = rowHeader;
            this.columnHeader = columnHeader;
        }

        @Override
        public Table<?> toTable(JGrid grid) {
            GridModel model = grid.getModel();
            return copy2(model, ALL, ALL, rowHeader, columnHeader);
        }

        private static final Range<Integer> ALL = Range.of(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static final class CopySelectionCommand extends TableGridCommand {

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
                    ? copy2(grid.getModel(), Range.of(r.getMinSelectionIndex(), r.getMaxSelectionIndex() + 1), Range.of(c.getMinSelectionIndex(), c.getMaxSelectionIndex() + 1), rowHeader, columnHeader)
                    : new Table<>(0, 0);
        }
    }
    //</editor-fold>
}
