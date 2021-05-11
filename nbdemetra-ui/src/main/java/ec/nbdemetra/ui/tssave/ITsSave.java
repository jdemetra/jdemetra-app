/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tssave;

import demetra.timeseries.TsCollection;
import demetra.ui.NamedService;
import ec.tstoolkit.design.ServiceDefinition;
import ec.util.various.swing.OnEDT;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Thomas Witthohn
 * @since 2.1.0
 */
@ServiceDefinition
public interface ITsSave extends NamedService {

    @OnEDT
    void save(@NonNull List<TsCollection> input);
}
