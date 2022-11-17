package demetra.desktop.x13.ui;

import demetra.desktop.Config;
import demetra.desktop.Persistable;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.design.GlobalService;
import demetra.desktop.util.LazyGlobalService;
import demetra.desktop.util.Persistence;
import demetra.desktop.x13.diagnostics.X13DiagnosticsFactoryBuddy;
import demetra.sa.SaDiagnosticsFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import jdplus.x13.X13Factory;
import jdplus.x13.X13Results;
import nbbrd.design.MightBeGenerated;
import org.openide.util.Lookup;

@GlobalService
public final class X13UI implements PropertyChangeSource.WithWeakListeners, Persistable {
    
    public static void setDiagnostics(){
        Lookup.getDefault().lookupAll(X13DiagnosticsFactoryBuddy.class).stream().forEach(X13DiagnosticsFactoryBuddy::commit);
        // updates the diagnostics factories of the main processor
        Stream<SaDiagnosticsFactory<?, X13Results>> map = Lookup.getDefault()
                .lookupAll(X13DiagnosticsFactoryBuddy.class)
                .stream()
                .map(buddy->(SaDiagnosticsFactory<?, X13Results>) buddy.createFactory());
        List<SaDiagnosticsFactory<?, X13Results>> factories=new ArrayList();
        map.forEach(fac->factories.add(fac));
        X13Factory.getInstance().resetDiagnosticFactories(factories);
    }

    @NonNull
    public static X13UI get() {
        return LazyGlobalService.get(X13UI.class, X13UI::new);
    }

    private X13UI() {
    }

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    private final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);



    @Override
    public Config getConfig() {
        return PERSISTENCE.loadConfig(this);
    }

    @Override
    public void setConfig(Config config) {
        PERSISTENCE.storeConfig(this, config);
    }

    @MightBeGenerated
    private static final Persistence<X13UI> PERSISTENCE = Persistence
            .builderOf(X13UI.class)
            .name("demetra-ui")
            .version("3.0.0")
            .build();
}
