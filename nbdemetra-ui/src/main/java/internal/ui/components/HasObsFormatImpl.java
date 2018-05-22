/*
 * Copyright 2018 National Bank of Belgium
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
package internal.ui.components;

import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.components.HasObsFormat;
import ec.tss.tsproviders.utils.DataFormat;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasObsFormatImpl implements HasObsFormat {

    @lombok.NonNull
    private final PropertyChangeSource.Broadcaster broadcaster;
    private DataFormat dataFormat = null;

    @Override
    public DataFormat getDataFormat() {
        return dataFormat;
    }

    @Override
    public void setDataFormat(DataFormat dataFormat) {
        DataFormat old = this.dataFormat;
        this.dataFormat = dataFormat;
        broadcaster.firePropertyChange(DATA_FORMAT_PROPERTY, old, this.dataFormat);
    }
}
