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
package internal.ui;

import ec.tss.datatransfer.impl.LocalObjectDataTransfer;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.Test;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.lookup.Lookups;
import org.slf4j.helpers.NOPLogger;
import demetra.ui.OldDataTransferSpi;

/**
 *
 * @author Philippe Charles
 */
public class DefaultDataTransferTest {

    @Test
    @SuppressWarnings("null")
    public void testEmpty() {
        DefaultDataTransfer empty = of();

        assertThat(empty.getProviders()).isEmpty();

        assertThatThrownBy(() -> empty.fromMatrix(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.fromTable(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toMatrix(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTable(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.canImport((Transferable) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.canImport((DataFlavor[]) null)).isInstanceOf(NullPointerException.class);

        assertThat(empty.fromMatrix(new Matrix(1, 1)).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTable(new Table<>(1, 1)).getTransferDataFlavors()).isEmpty();
    }

    @Test
    public void testContent() {
        LocalObjectTransferable<demetra.tsprovider.TsCollection> col = new LocalObjectTransferable<>(demetra.tsprovider.TsCollection.EMPTY);
        Transferable multi = new ExTransferable.Multi(new Transferable[]{col});

        DefaultDataTransfer local = of(new LocalObjectDataTransfer());
        assertThat(local.canImport(col)).isTrue();
        assertThat(local.canImport(multi)).isTrue();

        DefaultDataTransfer empty = of();
        assertThat(empty.canImport(col)).isFalse();
        assertThat(empty.canImport(multi)).isFalse();

        DefaultDataTransfer valid = of(new CustomHandler(DataFlavor.stringFlavor));
        assertThat(valid.canImport(col)).isFalse();
        assertThat(valid.canImport(multi)).isFalse();

        DefaultDataTransfer invalid = of(new CustomHandler(LocalObjectDataTransfer.DATA_FLAVOR));
        assertThat(invalid.canImport(col)).isTrue();
        assertThat(invalid.canImport(multi)).isTrue();
    }

    @Test
    public void testInvalidHandler() {
        Transferable t = new StringSelection("hello");

        assertThat(of(new CustomHandler(DataFlavor.stringFlavor) {
            @Override
            public boolean canExportMatrix(Matrix matrix) {
                return true;
            }
        }).toMatrix(t)).isNull();

        assertThat(of(new CustomHandler(DataFlavor.stringFlavor) {
            @Override
            public boolean canExportMatrix(Matrix matrix) {
                return true;
            }

            @Override
            public String getName() {
                throw new RuntimeException();
            }
        }).toMatrix(t)).isNull();

        assertThat(of(new CustomHandler(DataFlavor.stringFlavor) {
            @Override
            public DataFlavor getDataFlavor() {
                throw new RuntimeException();
            }
        }).toMatrix(t)).isNull();
    }

    private static DefaultDataTransfer of(OldDataTransferSpi... handlers) {
        return new DefaultDataTransfer(Lookups.fixed((Object[]) handlers), NOPLogger.NOP_LOGGER, false);
    }

    private static class CustomHandler implements OldDataTransferSpi {

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
