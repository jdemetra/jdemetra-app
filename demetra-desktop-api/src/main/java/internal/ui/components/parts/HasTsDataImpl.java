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
package internal.ui.components.parts;

import demetra.timeseries.TsData;
import demetra.ui.beans.PropertyChangeBroadcaster;
import demetra.ui.components.parts.HasTsData;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasTsDataImpl implements HasTsData {

    @lombok.NonNull
    private final PropertyChangeBroadcaster broadcaster;
    private TsData tsData = null;

    @Override
    public TsData getTsData() {
        return tsData;
    }

    @Override
    public void setTsData(TsData tsData) {
        TsData old = this.tsData;
        this.tsData = tsData;
        broadcaster.firePropertyChange(TS_DATA_PROPERTY, old, this.tsData);
    }
}
