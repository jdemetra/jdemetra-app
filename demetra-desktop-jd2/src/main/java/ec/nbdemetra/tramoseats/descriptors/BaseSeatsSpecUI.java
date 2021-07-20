/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.tramoseats.descriptors;

import ec.satoolkit.seats.SeatsSpecification;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;

/**
 *
 * @author Kristof Bayens
 */
public abstract class BaseSeatsSpecUI implements IPropertyDescriptors {

    final SeatsSpecification core;
    final boolean ro_;

    public BaseSeatsSpecUI(SeatsSpecification spec, boolean ro) {
       if (spec == null) {
            throw new AssertionError(IObjectDescriptor.EMPTY);
        }
        core = spec;
        ro_=ro;
    }

    public SeatsSpecification getCore() {
        return core;
    }
}
