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

import ec.tss.TsFactory;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.Test;
import org.openide.util.Lookup;
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
        TssTransferSupport empty = new TssTransferSupport(Lookup.EMPTY, NOPLogger.NOP_LOGGER, false);

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
        assertThatThrownBy(() -> empty.toTsData(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.isTssTransferable(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> empty.canImport((DataFlavor[]) null)).isInstanceOf(NullPointerException.class);

        assertThat(empty.fromMatrix(new Matrix(1, 1)).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTable(new Table<>(1, 1)).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTs(TsFactory.instance.createTs()).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTsCollection(TsFactory.instance.createTsCollection()).getTransferDataFlavors()).isEmpty();
        assertThat(empty.fromTsData(TsData.random(TsFrequency.Yearly)).getTransferDataFlavors()).isEmpty();
    }

    @Test
    public void testInvalidHandler() {
        Transferable t = new StringSelection("hello");

        assertThat(of(new InvalidHandler() {
            @Override
            public boolean canExportMatrix(Matrix matrix) {
                return true;
            }
        }).toMatrix(t)).isNull();

        assertThat(of(new InvalidHandler() {
            @Override
            public boolean canExportMatrix(Matrix matrix) {
                return true;
            }

            @Override
            public String getName() {
                throw new RuntimeException();
            }
        }).toMatrix(t)).isNull();

        assertThat(of(new InvalidHandler() {
            @Override
            public DataFlavor getDataFlavor() {
                throw new RuntimeException();
            }
        }).toMatrix(t)).isNull();
    }

    private static TssTransferSupport of(TssTransferHandler handler) {
        return new TssTransferSupport(Lookups.singleton(handler), NOPLogger.NOP_LOGGER, false);
    }

    private static class InvalidHandler extends TssTransferHandler {

        @Override
        public DataFlavor getDataFlavor() {
            return DataFlavor.stringFlavor;
        }

        @Override
        public String getName() {
            return "xxx";
        }
    }
}
