/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.descriptors.IPropertyDescriptors;

/**
 *
 * @author Kristof Bayens
 */
public abstract class BaseRegArimaSpecUI implements IPropertyDescriptors {

    final RegArimaSpecification core;
    final boolean ro_;
    final Validator validator;

    public BaseRegArimaSpecUI(RegArimaSpecification spec, boolean ro) {
        core = spec;
        ro_ = ro;
        validator=null;
    }

    public BaseRegArimaSpecUI(RegArimaSpecification spec, boolean ro, Validator validator) {
        core = spec;
        ro_ = ro;
        this.validator=validator;
    }

    public RegArimaSpecification getCore() {
        return core;
    }

}
