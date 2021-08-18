/*
 * Copyright 2017 National Bank of Belgium
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
package demetra.ui.datatransfer;

import demetra.math.matrices.MatrixType;
import demetra.timeseries.TsUnit;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.util.Table;
import internal.ui.Providers;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Logger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.Test;
import org.openide.util.datatransfer.ExTransferable;

/**
 *
 * @author Philippe Charles
 */
public class DataTransferTest {

    @Test
    @SuppressWarnings("null")
    public void testEmpty() {
        DataTransfer empty = of();

        assertThatThrownBy(() -> empty.fromTs(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.fromTsCollection(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.fromTsData(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.fromMatrix(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.fromTable(null)).isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> empty.toTs(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTsCollection(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTsCollectionStream(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTsData(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toMatrix(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTable(null)).isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> empty.isTssTransferable(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.canImport((Transferable) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.canImport((DataFlavor[]) null)).isInstanceOf(NullPointerException.class);

        assertThat(empty.fromTs(Ts.builder().build()).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTsCollection(demetra.timeseries.TsCollection.EMPTY).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTsData(demetra.timeseries.TsData.random(TsUnit.YEAR, 0)).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromMatrix(MatrixType.of(new double[]{3.14}, 1, 1)).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTable(new Table<>(1, 1)).getTransferDataFlavors()).isEmpty();
    }

    @Test
    public void testContent() {
        LocalObjectTransferable<demetra.timeseries.TsCollection> col = new LocalObjectTransferable<>(demetra.timeseries.TsCollection.EMPTY);
        Transferable multi = new ExTransferable.Multi(new Transferable[]{col});

        DataTransfer local = of(new LocalObjectDataTransfer());
        assertThat(local.canImport(col)).isTrue();
        assertThat(local.canImport(multi)).isTrue();
        assertThat(local.toTsCollection(col)).contains(col.getValue());
        assertThat(local.toTsCollection(multi)).isEmpty();
        assertThat(local.toTsCollectionStream(col)).containsExactly(col.getValue());
        assertThat(local.toTsCollectionStream(multi)).containsExactly(col.getValue());

        DataTransfer empty = of();
        assertThat(empty.canImport(col)).isFalse();
        assertThat(empty.canImport(multi)).isFalse();
        assertThat(empty.toTsCollection(col)).isEmpty();
        assertThat(empty.toTsCollectionStream(col)).isEmpty();

        DataTransfer valid = of(new CustomHandler(DataFlavor.stringFlavor));
        assertThat(valid.canImport(col)).isFalse();
        assertThat(valid.canImport(multi)).isFalse();
        assertThat(valid.toTsCollection(col)).isEmpty();
        assertThat(valid.toTsCollectionStream(col)).isEmpty();

        DataTransfer invalid = of(new CustomHandler(LocalObjectDataTransfer.DATA_FLAVOR));
        assertThat(invalid.canImport(col)).isTrue();
        assertThat(invalid.canImport(multi)).isTrue();
        assertThat(invalid.toTsCollection(col)).isEmpty();
        assertThat(invalid.toTsCollectionStream(col)).isEmpty();
    }

    @Test
    public void testInvalidHandler() {
        Transferable t = new StringSelection("hello");

        assertThat(of(new CustomHandler(DataFlavor.stringFlavor) {
            @Override
            public boolean canExportTsCollection(TsCollection o) {
                return true;
            }
        }).toTsCollection(t)).isEmpty();

        assertThat(of(new CustomHandler(DataFlavor.stringFlavor) {
            @Override
            public boolean canExportTsCollection(TsCollection o) {
                return true;
            }

            @Override
            public String getName() {
                throw new RuntimeException();
            }
        }).toTsCollection(t)).isEmpty();

        assertThat(of(new CustomHandler(DataFlavor.stringFlavor) {
            @Override
            public DataFlavor getDataFlavor() {
                throw new RuntimeException();
            }
        }).toTsCollection(t)).isEmpty();

        assertThat(of(new CustomHandler(DataFlavor.stringFlavor) {
            @Override
            public boolean canExportMatrix(MatrixType matrix) {
                return true;
            }
        }).toMatrix(t)).isEmpty();

        assertThat(of(new CustomHandler(DataFlavor.stringFlavor) {
            @Override
            public boolean canExportMatrix(MatrixType matrix) {
                return true;
            }

            @Override
            public String getName() {
                throw new RuntimeException();
            }
        }).toMatrix(t)).isEmpty();

        assertThat(of(new CustomHandler(DataFlavor.stringFlavor) {
            @Override
            public DataFlavor getDataFlavor() {
                throw new RuntimeException();
            }
        }).toMatrix(t)).isEmpty();
    }

    private static DataTransfer of(DataTransferSpi... handlers) {
        return new DataTransfer(Providers.of(java.util.Arrays.asList(handlers)), Logger.getAnonymousLogger(), false);
    }

    private static class CustomHandler implements DataTransferSpi {

        private final DataFlavor df;

        public CustomHandler(DataFlavor df) {
            this.df = df;
        }

        @Override
        public int getPosition() {
            return 0;
        }

        @Override
        public DataFlavor getDataFlavor() {
            return df;
        }

        @Override
        public String getName() {
            return "xxx";
        }

        @Override
        public boolean canExportTsCollection(TsCollection col) {
            return false;
        }

        @Override
        public Object exportTsCollection(TsCollection col) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean canImportTsCollection(Object obj) {
            return false;
        }

        @Override
        public TsCollection importTsCollection(Object obj) throws IOException, ClassCastException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean canExportMatrix(MatrixType matrix) {
            return false;
        }

        @Override
        public Object exportMatrix(MatrixType matrix) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean canImportMatrix(Object obj) {
            return false;
        }

        @Override
        public MatrixType importMatrix(Object obj) throws IOException, ClassCastException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean canExportTable(Table<?> table) {
            return false;
        }

        @Override
        public Object exportTable(Table<?> table) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean canImportTable(Object obj) {
            return false;
        }

        @Override
        public Table<?> importTable(Object obj) throws IOException, ClassCastException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class LocalObjectTransferable<T> extends ExTransferable.Single {

        private final T data;

        public LocalObjectTransferable(T data) {
            super(LocalObjectDataTransfer.DATA_FLAVOR);
            this.data = data;
        }

        @Override
        protected Object getData() throws IOException, UnsupportedFlavorException {
            return data;
        }

        T getValue() {
            return data;
        }
    }
}
