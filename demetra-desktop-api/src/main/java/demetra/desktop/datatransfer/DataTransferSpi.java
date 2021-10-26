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
package demetra.desktop.datatransfer;

import demetra.desktop.NamedService;
import demetra.desktop.util.NetBeansServiceBackend;
import demetra.timeseries.TsCollection;
import demetra.util.Table;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import nbbrd.service.ServiceSorter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import demetra.math.matrices.Matrix;

/**
 * SPI that allows to import/export specific data structures from/to the
 * clipboard.
 *
 * @author Philippe Charles
 * @since 1.3.0
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface DataTransferSpi extends NamedService {

    @ServiceSorter
    int getPosition();

    @NonNull
    DataFlavor getDataFlavor();

    @OnEDT
    boolean canExportTsCollection(@NonNull TsCollection col);

    @OnAnyThread
    @NonNull
    Object exportTsCollection(@NonNull TsCollection col) throws IOException;

    @OnEDT
    boolean canImportTsCollection(@NonNull Object obj);

    @OnEDT
    @NonNull
    TsCollection importTsCollection(@NonNull Object obj) throws IOException, ClassCastException;

    @OnEDT
    boolean canExportMatrix(@NonNull Matrix matrix);

    @OnAnyThread
    @NonNull
    Object exportMatrix(@NonNull Matrix matrix) throws IOException;

    @OnEDT
    boolean canImportMatrix(@NonNull Object obj);

    @OnEDT
    @NonNull
    Matrix importMatrix(@NonNull Object obj) throws IOException, ClassCastException;

    @OnEDT
    boolean canExportTable(@NonNull Table<?> table);

    @OnAnyThread
    @NonNull
    Object exportTable(@NonNull Table<?> table) throws IOException;

    @OnEDT
    boolean canImportTable(@NonNull Object obj);

    @OnEDT
    @NonNull
    Table<?> importTable(@NonNull Object obj) throws IOException, ClassCastException;
}
