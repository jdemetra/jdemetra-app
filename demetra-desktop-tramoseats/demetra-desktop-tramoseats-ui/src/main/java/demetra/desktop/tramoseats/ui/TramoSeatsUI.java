package demetra.desktop.tramoseats.ui;

import demetra.desktop.Config;
import demetra.desktop.Persistable;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.design.GlobalService;
import demetra.desktop.tramoseats.diagnostics.TramoSeatsDiagnosticsFactoryBuddy;
import demetra.desktop.util.LazyGlobalService;
import demetra.desktop.util.Persistence;
import demetra.sa.SaDiagnosticsFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import jdplus.tramoseats.TramoSeatsFactory;
import jdplus.tramoseats.TramoSeatsResults;
import nbbrd.design.MightBeGenerated;
import org.openide.util.Lookup;

@GlobalService
public final class TramoSeatsUI implements PropertyChangeSource.WithWeakListeners, Persistable {
    
    public static void setDiagnostics(){
        Lookup.getDefault().lookupAll(TramoSeatsDiagnosticsFactoryBuddy.class).stream().forEach(TramoSeatsDiagnosticsFactoryBuddy::commit);
        // updates the diagnostics factories of the main processor
        Stream<SaDiagnosticsFactory<?, TramoSeatsResults>> map = Lookup.getDefault()
                .lookupAll(TramoSeatsDiagnosticsFactoryBuddy.class)
                .stream()
                .map(buddy->(SaDiagnosticsFactory<?, TramoSeatsResults>) buddy.createFactory());
        List<SaDiagnosticsFactory<?, TramoSeatsResults>> factories=new ArrayList();
        map.forEach(fac->factories.add(fac));
        TramoSeatsFactory.getInstance().resetDiagnosticFactories(factories);
    }

    @NonNull
    public static TramoSeatsUI get() {
        return LazyGlobalService.get(TramoSeatsUI.class, TramoSeatsUI::new);
    }

    private TramoSeatsUI() {
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
    private static final Persistence<TramoSeatsUI> PERSISTENCE = Persistence
            .builderOf(TramoSeatsUI.class)
            .name("demetra-ui")
            .version("3.0.0")
            .build();
}
