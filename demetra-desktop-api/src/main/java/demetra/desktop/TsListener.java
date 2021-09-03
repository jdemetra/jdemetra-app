package demetra.desktop;

import ec.util.various.swing.OnEDT;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.EventListener;

/**
 *
 */
public interface TsListener extends EventListener {

    @OnEDT
    void tsUpdated(@NonNull TsEvent event);
}
