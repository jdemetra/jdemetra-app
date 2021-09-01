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
package demetra.desktop.interchange;

import demetra.ui.NamedService;
import demetra.ui.util.NetBeansServiceBackend;
import ec.util.various.swing.OnEDT;
import java.io.IOException;
import java.util.List;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import nbbrd.service.ServiceSorter;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Service that performs import/export of configs.
 *
 * @author Philippe Charles
 * @since 1.5.1
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface InterchangeSpi extends NamedService {

    @ServiceSorter
    int getPosition();

    @OnEDT
    boolean canImport(@NonNull List<? extends Importable> importables);

    @OnEDT
    void performImport(@NonNull List<? extends Importable> importables) throws IOException, IllegalArgumentException;

    @OnEDT
    boolean canExport(@NonNull List<? extends Exportable> exportables);

    @OnEDT
    void performExport(@NonNull List<? extends Exportable> exportables) throws IOException;
}
