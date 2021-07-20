/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.satoolkit.x11.X11Specification;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;

/**
 *
 * @author Kristof Bayens
 */
public abstract class BaseX11SpecUI implements IPropertyDescriptors {

    final X11Specification core;
    final boolean ro_;

    public BaseX11SpecUI(X11Specification spec, boolean ro) {
        if (spec == null) {
            throw new AssertionError(IObjectDescriptor.EMPTY);
        }
        core = spec;
        ro_ = ro;
    }

    public X11Specification getCore() {
        return core;
    }
}
