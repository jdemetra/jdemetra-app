/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.descriptors.IPropertyDescriptors;

/**
 *
 * @author Jean Palate
 */
public abstract class BaseTramoSpecUI implements IPropertyDescriptors {

    final TramoSpecification core;
    final boolean ro_;

    public BaseTramoSpecUI(TramoSpecification spec, boolean ro) {
         if (spec == null) {
            throw new AssertionError(IObjectDescriptor.EMPTY);
        }
      core = spec;
        ro_ = ro;
    }

    public TramoSpecification getCore() {
        return core;
    }
    
}
