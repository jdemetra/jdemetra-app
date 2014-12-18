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

import ec.nbdemetra.ui.ns.INamedService;
import ec.tss.TsCollection;
import ec.tstoolkit.design.ServiceDefinition;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;

/**
 * Defines a formatter used to transfer data.
 *
 * @author Philippe Charles
 * @deprecated use {@link TssTransferHandler} instead
 */
@ServiceDefinition(hasPosition = true)
@Deprecated
public interface ITsCollectionFormatter extends INamedService {

    DataFlavor getDataFlavor();

    boolean canExportTransferData(TsCollection col);

    Object toTransferData(TsCollection col) throws IOException;

    boolean canImportTransferData(Object obj);

    TsCollection fromTransferData(Object obj) throws IOException, ClassCastException;
}
