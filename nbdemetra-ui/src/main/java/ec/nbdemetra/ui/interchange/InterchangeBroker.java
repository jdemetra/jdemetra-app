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
package ec.nbdemetra.ui.interchange;

import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.ns.INamedService;
import ec.tstoolkit.design.ServiceDefinition;
import ec.util.various.swing.OnEDT;
import java.awt.Image;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import org.openide.util.ImageUtilities;

/**
 * Service that performs import/export of configs.
 *
 * @author Philippe Charles
 * @since 1.5.1
 */
@ServiceDefinition(hasPosition = true)
public abstract class InterchangeBroker implements INamedService {

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.CLIPBOARD_PASTE_DOCUMENT_TEXT_16);
    }

    @OnEDT
    public boolean canImport(@Nonnull List<? extends Importable> importables) {
        return false;
    }

    @OnEDT
    public void performImport(@Nonnull List<? extends Importable> importables) throws IOException, IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @OnEDT
    public boolean canExport(@Nonnull List<? extends Exportable> exportables) {
        return false;
    }

    @OnEDT
    public void performExport(@Nonnull List<? extends Exportable> exportables) throws IOException {
        throw new UnsupportedOperationException();
    }
}
