/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view.tsprocessing;

import ec.satoolkit.ComponentDescriptor;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.ucarima.WienerKolmogorovEstimators;

/**
 *
 * @author Jean Palate
 */
public class WkInformation {
    public WienerKolmogorovEstimators estimators;
    public ComponentDescriptor[] descriptors;
    public TsFrequency frequency;
}
