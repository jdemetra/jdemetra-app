package demetra.desktop.tramoseats.ui;

import demetra.desktop.Config;
import demetra.desktop.Persistable;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.design.GlobalService;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.util.LazyGlobalService;
import demetra.desktop.util.Persistence;
import demetra.tsprovider.util.ObsFormat;
import demetra.tsprovider.util.ObsFormatHandler;
import demetra.tsprovider.util.PropertyHandler;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.beans.PropertyChangeSupport;
import nbbrd.design.MightBeGenerated;

@GlobalService
public final class TramoSeatsUI implements PropertyChangeSource.WithWeakListeners, Persistable {

    @NonNull
    public static TramoSeatsUI get() {
        return LazyGlobalService.get(TramoSeatsUI.class, TramoSeatsUI::new);
    }

    private TramoSeatsUI() {
    }

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    private final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);


    @SwingProperty
    public static final String OBS_FORMAT_PROPERTY = "obsFormat";
    private static final ObsFormat DEFAULT_OBS_FORMAT = ObsFormat.getSystemDefault();
    private ObsFormat obsFormat = DEFAULT_OBS_FORMAT;

    public ObsFormat getObsFormat() {
        return obsFormat;
    }

    public void setObsFormat(ObsFormat obsFormat) {
        ObsFormat old = this.obsFormat;
        this.obsFormat = obsFormat != null ? obsFormat : DEFAULT_OBS_FORMAT;
        broadcaster.firePropertyChange(OBS_FORMAT_PROPERTY, old, this.obsFormat);
    }

    @SwingProperty
    public static final String GROWTH_LAST_YEARS_PROPERTY = "growthLastYears";
    private static final int DEFAULT_GROWTH_LAST_YEARS = 4;
    private Integer growthLastYears = DEFAULT_GROWTH_LAST_YEARS;

    public Integer getGrowthLastYears() {
        return growthLastYears;
    }

    public void setGrowthLastYears(Integer lastYears) {
        Integer old = this.growthLastYears;
        growthLastYears = lastYears != null ? lastYears : DEFAULT_GROWTH_LAST_YEARS;
        broadcaster.firePropertyChange(GROWTH_LAST_YEARS_PROPERTY, old, growthLastYears);
    }

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
            .with(ObsFormatHandler
                            .builder()
                            .locale(PropertyHandler.onLocale("locale", DEFAULT_OBS_FORMAT.getLocale()))
                            .dateTimePattern(PropertyHandler.onString("dateTimePattern", DEFAULT_OBS_FORMAT.getDateTimePattern()))
                            .numberPattern(PropertyHandler.onString("numberPattern", DEFAULT_OBS_FORMAT.getNumberPattern()))
                            .ignoreNumberGrouping(PropertyHandler.onBoolean("ignoreNumberGrouping", DEFAULT_OBS_FORMAT.isIgnoreNumberGrouping()))
                            .build(),
                    TramoSeatsUI::getObsFormat,
                    TramoSeatsUI::setObsFormat
            )
            .with(PropertyHandler.onInteger(GROWTH_LAST_YEARS_PROPERTY, DEFAULT_GROWTH_LAST_YEARS),
                    TramoSeatsUI::getGrowthLastYears,
                    TramoSeatsUI::setGrowthLastYears
            )
            .build();
}
