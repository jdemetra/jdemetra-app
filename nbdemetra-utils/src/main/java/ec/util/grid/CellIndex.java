/*
 * Copyright 2015 National Bank of Belgium
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
package ec.util.grid;

import java.util.Objects;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 *
 * @author Philippe Charles
 */
@Immutable
public final class CellIndex {

    @Nonnull
    public static final CellIndex NULL = new CellIndex(-1, -1);

    @Nonnull
    public static CellIndex valueOf(int row, int column) {
        return NULL.equals(row, column) ? NULL : new CellIndex(row, column);
    }

    private final int row;
    private final int column;

    private CellIndex(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Nonnegative
    public int getRow() {
        return row;
    }

    @Nonnegative
    public int getColumn() {
        return column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof CellIndex && equals((CellIndex) obj));
    }

    private boolean equals(CellIndex that) {
        return equals(that.row, that.column);
    }

    public boolean equals(int row, int column) {
        return this.row == row && this.column == column;
    }

    @Override
    public String toString() {
        return row + "x" + column;
    }
}
