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

import demetra.timeseries.TsUnit;
import demetra.tsprovider.Ts;
import demetra.tsprovider.TsCollection;
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
import org.openide.util.lookup.Lookups;

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
        assertThatThrownBy(() -> empty.toTs(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTsCollection(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTsCollectionStream(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTsData(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.isTssTransferable(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.canImport((Transferable) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.canImport((DataFlavor[]) null)).isInstanceOf(NullPointerException.class);

        assertThat(empty.fromTs(Ts.builder().build()).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTsCollection(demetra.tsprovider.TsCollection.EMPTY).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTsData(demetra.timeseries.TsData.random(TsUnit.YEAR, 0)).getTransferDataFlavors()).isEmpty();
    }

    @Test
    public void testContent() {
        LocalObjectTransferable<demetra.tsprovider.TsCollection> col = new LocalObjectTransferable<>(demetra.tsprovider.TsCollection.EMPTY);
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
    }

    private static DataTransfer of(DataTransferSpi... handlers) {
        return new DataTransfer(Lookups.fixed((Object[]) handlers), Logger.getAnonymousLogger(), false);
    }

    private static class CustomHandler implements DataTransferSpi {

        private final DataFlavor df;

        public CustomHandler(DataFlavor df) {
            this.df = df;
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
