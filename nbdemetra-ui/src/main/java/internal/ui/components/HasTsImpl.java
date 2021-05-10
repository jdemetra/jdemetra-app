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

import demetra.timeseries.Ts;
import demetra.timeseries.TsMoniker;
import demetra.ui.NextTsManager;
import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.components.HasTs;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasTsImpl implements HasTs, NextTsManager.UpdateListener {

    @lombok.NonNull
    private final PropertyChangeSource.Broadcaster broadcaster;

    private Ts ts = null;

    public HasTsImpl register(NextTsManager manager) {
        manager.addWeakUpdateListener(this);
        return this;
    }

    @Override
    public Ts getTs() {
        return ts;
    }

    @Override
    public void setTs(Ts ts) {
        Ts old = this.ts;
        this.ts = ts;
        broadcaster.firePropertyChange(TS_PROPERTY, old, this.ts);
    }

    @Override
    public void accept(NextTsManager manager, TsMoniker moniker) {
        if (ts != null) {
            if (moniker.equals(ts.getMoniker())) {
                setTs(manager.lookupTs2(moniker));
            }
        }
    }
}
