/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop;

import demetra.desktop.util.NetBeansServiceBackend;
import demetra.timeseries.TsCollection;
import ec.util.various.swing.OnEDT;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 *
 * @author Thomas Witthohn
 * @since 2.1.0
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface TsActionsSaveSpi extends NamedService {

    @OnEDT
    void save(@NonNull List<TsCollection> input);
}
