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
package ec.ui;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.ui.interfaces.ITsDataView;
import static ec.ui.interfaces.ITsDataView.TS_DATA_PROPERTY;

/**
 *
 * @author Philippe Charles
 */
public abstract class ATsDataView extends ATsControl implements ITsDataView {

    // DEFAULT PROPERTIES
    protected static final TsData DEFAULT_TS_DATA = new TsData(TsFrequency.Monthly, 2000, 0, 0);

    // PROPERTIES
    protected TsData tsData;

    public ATsDataView() {
        this.tsData = DEFAULT_TS_DATA;
        enableProperties();
    }

    private void enableProperties() {
        this.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case TS_DATA_PROPERTY:
                    onTsDataChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handler">
    abstract protected void onTsDataChange();
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public void setTsData(TsData tsData) {
        TsData old = this.tsData;
        this.tsData = tsData != null ? tsData : DEFAULT_TS_DATA;
        firePropertyChange(TS_DATA_PROPERTY, old, this.tsData);
    }

    @Override
    public TsData getTsData() {
        return tsData;
    }
    //</editor-fold>
}
