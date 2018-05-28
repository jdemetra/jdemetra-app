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
package ec.tss.datatransfer;

import demetra.ui.TsManager;
import ec.tss.TsCollection;
import ec.tss.datatransfer.impl.LocalObjectTssTransferHandler;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
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

/**
 *
 * @author Philippe Charles
 */
public class TssTransferSupportTest {

    @Test
    @SuppressWarnings("null")
    public void testEmpty() {
        TssTransferSupport empty = of();

        assertThat(empty.stream()).isEmpty();

        assertThatThrownBy(() -> empty.fromMatrix(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.fromTable(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.fromTs(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.fromTsCollection(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.fromTsData(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toMatrix(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTable(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTs(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTsCollection(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTsCollectionStream(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.toTsData(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.isTssTransferable(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.canImport((Transferable) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.canImport((DataFlavor[]) null)).isInstanceOf(NullPointerException.class);

        assertThat(empty.fromMatrix(new Matrix(1, 1)).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTable(new Table<>(1, 1)).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTs(TsManager.getDefault().newTsWithName("")).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTsCollection(demetra.tsprovider.TsCollection.EMPTY).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTsData(TsData.random(TsFrequency.Yearly)).getTransferDataFlavors()).isEmpty();
    }

    @Test
    public void testContent() {
        LocalObjectTransferable<TsCollection> col = new LocalObjectTransferable<>(TsManager.getDefault().newTsCollection());
        Transferable multi = new ExTransferable.Multi(new Transferable[]{col});

        TssTransferSupport local = of(new LocalObjectTssTransferHandler());
        assertThat(local.canImport(col)).isTrue();
        assertThat(local.canImport(multi)).isTrue();
        assertThat(local.toTsCollection(col)).isEqualTo(col.getValue());
        assertThat(local.toTsCollection(multi)).isNull();
        assertThat(local.toTsCollectionStream(col)).containsExactly(col.getValue());
        assertThat(local.toTsCollectionStream(multi)).containsExactly(col.getValue());

        TssTransferSupport empty = of();
        assertThat(empty.canImport(col)).isFalse();
        assertThat(empty.canImport(multi)).isFalse();
        assertThat(empty.toTsCollection(col)).isNull();
        assertThat(empty.toTsCollectionStream(col)).isEmpty();

        TssTransferSupport valid = of(new CustomHandler(DataFlavor.stringFlavor));
        assertThat(valid.canImport(col)).isFalse();
        assertThat(valid.canImport(multi)).isFalse();
        assertThat(valid.toTsCollection(col)).isNull();
        assertThat(valid.toTsCollectionStream(col)).isEmpty();

        TssTransferSupport invalid = of(new CustomHandler(LocalObjectTssTransferHandler.DATA_FLAVOR));
        assertThat(invalid.canImport(col)).isTrue();
        assertThat(invalid.canImport(multi)).isTrue();
        assertThat(invalid.toTsCollection(col)).isNull();
        assertThat(invalid.toTsCollectionStream(col)).isEmpty();
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

    private static TssTransferSupport of(TssTransferHandler... handlers) {
        return new TssTransferSupport(Lookups.fixed((Object[]) handlers), NOPLogger.NOP_LOGGER, false);
    }

    private static class CustomHandler extends TssTransferHandler {

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
            super(LocalObjectTssTransferHandler.DATA_FLAVOR);
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
