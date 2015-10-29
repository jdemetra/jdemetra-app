/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.sa.advanced.descriptors.mixedfrequencies;

import ec.nbdemetra.sa.advanced.descriptors.*;
import ec.satoolkit.special.PreprocessingSpecification;
import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesSpecification;
import ec.tstoolkit.descriptors.IPropertyDescriptors;

/**
 *
 * @author Jean Palate
 */
public abstract class BaseSpecUI  implements IPropertyDescriptors {

    final MixedFrequenciesSpecification core;

    public BaseSpecUI(MixedFrequenciesSpecification spec){
        core = spec;
    }

}
