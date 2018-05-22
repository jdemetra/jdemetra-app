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
import demetra.ui.components.HasTsData;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasTsDataImpl implements HasTsData {

    @lombok.NonNull
    private final PropertyChangeSource.Broadcaster broadcaster;
    private TsData tsData = getDefaultValue();

    @Override
    public TsData getTsData() {
        return tsData;
    }

    @Override
    public void setTsData(TsData tsData) {
        TsData old = this.tsData;
        this.tsData = tsData != null ? tsData : getDefaultValue();
        broadcaster.firePropertyChange(TS_DATA_PROPERTY, old, this.tsData);
    }

    private static TsData getDefaultValue() {
        return new TsData(TsFrequency.Monthly, 2000, 0, 0);
    }
}
