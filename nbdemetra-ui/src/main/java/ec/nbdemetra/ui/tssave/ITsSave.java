/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tssave;

import ec.nbdemetra.ui.ns.INamedService;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsInformationType;
import ec.tstoolkit.design.ServiceDefinition;
import ec.util.various.swing.OnEDT;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 *
 * @author Thomas Witthohn
 * @since 2.1.0
 */
@ServiceDefinition
public interface ITsSave extends INamedService {

    @OnEDT
    void save(@Nonnull Ts[] input);

    /**
     *
     * @param input
     * @since 2.2.0
     */
    @OnEDT
    default void save(@Nonnull TsCollection[] input) {
        Function<TsCollection, Stream<Ts>> toStream = o -> {
            o.load(TsInformationType.Definition);
            return o.stream();
        };
        save(Stream.of(input).flatMap(toStream).toArray(Ts[]::new));
    }
}
