/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package demetra.ui;

import ec.nbdemetra.ui.DemetraUiIcon;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.design.ServiceDefinition;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 * SPI that allows to import/export specific data structures from/to the
 * clipboard.
 *
 * @author Philippe Charles
 * @since 1.3.0
 */
@ServiceDefinition(hasPosition = true)
public interface OldDataTransferSpi extends NamedService {

    @NonNull
    DataFlavor getDataFlavor();

    //<editor-fold defaultstate="collapsed" desc="INamedService impl">
    @Override
    default Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.CLIPBOARD_PASTE_DOCUMENT_TEXT_16);
    }

    @Override
    default Sheet createSheet() {
        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        b.with(String.class).selectConst("DataFlavor", getDataFlavor().getMimeType()).display("Data Flavor").add();
        result.put(b.build());
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Matrix struct">
    @OnEDT
    default boolean canExportMatrix(@NonNull Matrix matrix) {
        return false;
    }

    @OnAnyThread
    @NonNull
    default Object exportMatrix(@NonNull Matrix matrix) throws IOException {
        throw new UnsupportedOperationException();
    }

    @OnEDT
    default boolean canImportMatrix(@NonNull Object obj) {
        return false;
    }

    @OnEDT
    @NonNull
    default Matrix importMatrix(@NonNull Object obj) throws IOException, ClassCastException {
        throw new UnsupportedOperationException();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Table struct">
    @OnEDT
    default boolean canExportTable(@NonNull Table<?> table) {
        return false;
    }

    @OnAnyThread
    @NonNull
    default Object exportTable(@NonNull Table<?> table) throws IOException {
        throw new UnsupportedOperationException();
    }

    @OnEDT
    default boolean canImportTable(@NonNull Object obj) {
        return false;
    }

    @OnEDT
    @NonNull
    default Table<?> importTable(@NonNull Object obj) throws IOException, ClassCastException {
        throw new UnsupportedOperationException();
    }
    //</editor-fold>
}
