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
package ec.tss.datatransfer;

import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.ns.INamedService;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.TsCollection;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.design.ServiceDefinition;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.util.various.swing.OnEDT;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import javax.annotation.Nonnull;
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
public abstract class TssTransferHandler implements INamedService {

    @Nonnull
    abstract public DataFlavor getDataFlavor();

    //<editor-fold defaultstate="collapsed" desc="INamedService impl">
    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.CLIPBOARD_PASTE_DOCUMENT_TEXT_16);
    }

    @Override
    public Sheet createSheet() {
        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        b.with(String.class).selectConst("DataFlavor", getDataFlavor().getMimeType()).display("Data Flavor").add();
        result.put(b.build());
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="TimeSeries struct">
    @OnEDT
    public boolean canExportTsCollection(@Nonnull TsCollection col) {
        return false;
    }

    @OnEDT
    @Nonnull
    public Object exportTsCollection(@Nonnull TsCollection col) throws IOException {
        throw new UnsupportedOperationException();
    }

    @OnEDT
    public boolean canImportTsCollection(@Nonnull Object obj) {
        return false;
    }

    @OnEDT
    @Nonnull
    public TsCollection importTsCollection(@Nonnull Object obj) throws IOException, ClassCastException {
        throw new UnsupportedOperationException();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Matrix struct">
    @OnEDT
    public boolean canExportMatrix(@Nonnull Matrix matrix) {
        return false;
    }

    @OnEDT
    @Nonnull
    public Object exportMatrix(@Nonnull Matrix matrix) throws IOException {
        throw new UnsupportedOperationException();
    }

    @OnEDT
    public boolean canImportMatrix(@Nonnull Object obj) {
        return false;
    }

    @OnEDT
    @Nonnull
    public Matrix importMatrix(@Nonnull Object obj) throws IOException, ClassCastException {
        throw new UnsupportedOperationException();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Table struct">
    @OnEDT
    public boolean canExportTable(@Nonnull Table<?> table) {
        return false;
    }

    @OnEDT
    @Nonnull
    public Object exportTable(@Nonnull Table<?> table) throws IOException {
        throw new UnsupportedOperationException();
    }

    @OnEDT
    public boolean canImportTable(@Nonnull Object obj) {
        return false;
    }

    @OnEDT
    @Nonnull
    public Table<?> importTable(@Nonnull Object obj) throws IOException, ClassCastException {
        throw new UnsupportedOperationException();
    }
    //</editor-fold>
}
