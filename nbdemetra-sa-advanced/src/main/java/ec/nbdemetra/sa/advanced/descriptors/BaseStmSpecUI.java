/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.sa.advanced.descriptors;

import ec.satoolkit.special.StmSpecification;
import ec.tstoolkit.descriptors.IPropertyDescriptors;

/**
 *
 * @author Jean Palate
 */
public abstract class BaseStmSpecUI implements IPropertyDescriptors {

    final StmSpecification core;

    public BaseStmSpecUI(StmSpecification spec){
        core = spec;
    }
}
