package demetra.desktop.tramoseats.ui;

import demetra.desktop.Config;
import demetra.desktop.Persistable;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.design.GlobalService;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.sa.output.OutputSelection;
import demetra.desktop.tramoseats.diagnostics.TramoSeatsDiagnosticsFactoryBuddy;
import demetra.desktop.util.LazyGlobalService;
import demetra.desktop.util.Persistence;
import demetra.sa.SaDiagnosticsFactory;
import demetra.tsprovider.util.PropertyHandler;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import jdplus.tramoseats.TramoSeatsFactory;
import jdplus.tramoseats.TramoSeatsResults;
import nbbrd.design.MightBeGenerated;
import org.openide.util.Lookup;

@GlobalService
public final class TramoSeatsUI implements PropertyChangeSource.WithWeakListeners, Persistable {

    public static void setDiagnostics() {
        Lookup.getDefault().lookupAll(TramoSeatsDiagnosticsFactoryBuddy.class).stream().forEach(TramoSeatsDiagnosticsFactoryBuddy::commit);
        // updates the diagnostics factories of the main processor
        Stream<SaDiagnosticsFactory<?, TramoSeatsResults>> map = Lookup.getDefault()
                .lookupAll(TramoSeatsDiagnosticsFactoryBuddy.class)
                .stream()
                .map(buddy -> (SaDiagnosticsFactory<?, TramoSeatsResults>) buddy.createFactory());
        List<SaDiagnosticsFactory<?, TramoSeatsResults>> factories = new ArrayList();
        map.forEach(fac -> factories.add(fac));
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

    private final List<String> selectedComponents = new ArrayList<>();
    private final List<String> selectedDiagnostics = new ArrayList<>();
    private int defaultSeriesParameter=DEFAULT_SERIES_PARAMETER;

    @SwingProperty
    public static final String DEFAULT_SERIES_PARAMETER_PROPERTY = "defaultSeriesParameter";
    private static final int DEFAULT_SERIES_PARAMETER = -2;

    public int getDefaultSeriesParameter() {
        return defaultSeriesParameter;
    }

    public void setDefaultSeriesParameter(int value) {
        defaultSeriesParameter = value;
    }

    public void setSelectedDiagnostics(List<String> diags) {
        selectedDiagnostics.clear();
        selectedDiagnostics.addAll(diags);
    }

    public List<String> getSelectedDiagnostics() {
        return Collections.unmodifiableList(selectedDiagnostics);
    }

    public List<String> allDiagnostics() {
        return OutputSelection.matrixItems(Collections.singletonList(TramoSeatsFactory.getInstance()));
    }

    public void setSelectedComponents(List<String> cmps) {
        selectedComponents.clear();
        selectedComponents.addAll(cmps);
    }

    public List<String> getSelectedComponents() {
        return Collections.unmodifiableList(selectedComponents);
    }

    public List<String> allComponents() {
        return OutputSelection.seriesItems(Collections.singletonList(TramoSeatsFactory.getInstance()));
    }

    @Override
    public Config getConfig() {
        return PERSISTENCE.loadConfig(this);
    }

    @Override
    public void setConfig(Config config) {
        PERSISTENCE.storeConfig(this, config);
    }

    private static final String COMPONENTS = "series", DIAGS = "matrix";

    @MightBeGenerated
    private static final Persistence<TramoSeatsUI> PERSISTENCE = Persistence
            .builderOf(TramoSeatsUI.class)
            .name("demetra-tramoseats")
            .version("3.0.0")
            .with(
                    PropertyHandler.onStringList(COMPONENTS, Collections.emptyList(), ','),
                    TramoSeatsUI::getSelectedComponents,
                    TramoSeatsUI::setSelectedComponents
            )
            .with(
                    PropertyHandler.onStringList(DIAGS, Collections.emptyList(), ','),
                    TramoSeatsUI::getSelectedDiagnostics,
                    TramoSeatsUI::setSelectedDiagnostics
            )
            .with(
                    PropertyHandler.onInteger(DEFAULT_SERIES_PARAMETER_PROPERTY, DEFAULT_SERIES_PARAMETER),
                    TramoSeatsUI::getDefaultSeriesParameter,
                    TramoSeatsUI::setDefaultSeriesParameter
            )
            .build();
}
