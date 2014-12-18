/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui;

import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.ui.interfaces.ITsDataView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
        this.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String p = evt.getPropertyName();
                if (p.equals(TS_DATA_PROPERTY)) {
                    onTsDataChange();
                }
            }
        });
    }

    // EVENT HANDLERS > 
    abstract protected void onTsDataChange();
    // < EVENT HANDLERS 

    // GETTERS/SETTERS >
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
    // < GETTERS/SETTERS
}
