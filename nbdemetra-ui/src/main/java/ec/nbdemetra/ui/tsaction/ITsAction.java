/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tsaction;

import ec.nbdemetra.ui.ns.INamedService;
import ec.tss.Ts;
import ec.tstoolkit.design.ServiceDefinition;

/**
 *
 * @author Philippe Charles
 */
@ServiceDefinition
public interface ITsAction extends INamedService {

    void open(Ts ts);
}
