/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.interfaces;

import ec.tstoolkit.timeseries.simplets.TsData;

/**
 *
 * @author Philippe Charles
 */
public interface ITsDataView extends ITsControl {
    
    public static final String TS_DATA_PROPERTY = "tsData";

    void setTsData(TsData tsData);
    
    TsData getTsData();
}
