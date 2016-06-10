/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tssave;

import ec.nbdemetra.ui.ns.INamedService;
import ec.tss.Ts;
import ec.tstoolkit.design.ServiceDefinition;
import ec.util.various.swing.OnEDT;
import javax.annotation.Nonnull;

/**
 *
 * @author Thomas Witthohn
 * @since 2.1.0
 */
@ServiceDefinition
public interface ITsSave extends INamedService {

    @OnEDT
    void save(@Nonnull Ts[] ts);
}
