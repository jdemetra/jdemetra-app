package demetra.ui;

import ec.util.various.swing.OnEDT;
import java.util.EventListener;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 */
public interface TsListener extends EventListener {

    @OnEDT
    void tsUpdated(@NonNull TsEvent event);
}
