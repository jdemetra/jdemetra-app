package demetra.desktop;

import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.concurrent.ThreadPoolSize;
import demetra.desktop.concurrent.ThreadPriority;
import demetra.desktop.design.GlobalService;
import demetra.desktop.design.MightBeGenerated;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.util.LazyGlobalService;
import demetra.desktop.util.Persistence;
import demetra.tsprovider.util.ObsFormat;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.beans.PropertyChangeSupport;

@GlobalService
public final class DemetraUI implements PropertyChangeSource.WithWeakListeners, Persistable {

    @NonNull
    public static DemetraUI getDefault() {
        return LazyGlobalService.get(DemetraUI.class, DemetraUI::new);
    }

    private DemetraUI() {
    }

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    private final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

    @SwingProperty
    public static final String COLOR_SCHEME_NAME_PROPERTY = "colorSchemeName";
    private static final String DEFAULT_COLOR_SCHEME_NAME = "Smart";
    private String colorSchemeName = DEFAULT_COLOR_SCHEME_NAME;

    @NonNull
    public String getColorSchemeName() {
        return colorSchemeName;
    }

    public void setColorSchemeName(@Nullable String colorSchemeName) {
        String old = this.colorSchemeName;
        this.colorSchemeName = colorSchemeName != null ? colorSchemeName : DEFAULT_COLOR_SCHEME_NAME;
        broadcaster.firePropertyChange(COLOR_SCHEME_NAME_PROPERTY, old, this.colorSchemeName);
    }

    @SwingProperty
    public static final String POPUP_MENU_ICONS_VISIBLE_PROPERTY = "popupMenuIconsVisible";
    private static final boolean DEFAULT_POPUP_MENU_ICONS_VISIBLE = false;
    private boolean popupMenuIconsVisible = DEFAULT_POPUP_MENU_ICONS_VISIBLE;

    public boolean isPopupMenuIconsVisible() {
        return popupMenuIconsVisible;
    }

    public void setPopupMenuIconsVisible(boolean visible) {
        boolean old = this.popupMenuIconsVisible;
        this.popupMenuIconsVisible = visible;
        broadcaster.firePropertyChange(POPUP_MENU_ICONS_VISIBLE_PROPERTY, old, this.popupMenuIconsVisible);
    }

    @SwingProperty
    public static final String HTML_ZOOM_RATIO_PROPERTY = "htmlZoomRatio";
    private static final int DEFAULT_HTML_ZOOM_RATIO = 100;
    private int htmlZoomRatio = DEFAULT_HTML_ZOOM_RATIO;

    public int getHtmlZoomRatio() {
        return this.htmlZoomRatio;
    }

    public void setHtmlZoomRatio(int htmlZoomRatio) {
        int old = this.htmlZoomRatio;
        this.htmlZoomRatio = htmlZoomRatio >= 10 && htmlZoomRatio <= 200 ? htmlZoomRatio : DEFAULT_HTML_ZOOM_RATIO;
        broadcaster.firePropertyChange(HTML_ZOOM_RATIO_PROPERTY, old, this.htmlZoomRatio);
    }

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
    private static final Persistence<DemetraUI> PERSISTENCE = Persistence
            .builderOf(DemetraUI.class)
            .name("demetra-ui")
            .version("3.0.0")
            .onString(
                    COLOR_SCHEME_NAME_PROPERTY,
                    DEFAULT_COLOR_SCHEME_NAME,
                    DemetraUI::getColorSchemeName,
                    DemetraUI::setColorSchemeName
            )
            .onBoolean(
                    POPUP_MENU_ICONS_VISIBLE_PROPERTY,
                    DEFAULT_POPUP_MENU_ICONS_VISIBLE,
                    DemetraUI::isPopupMenuIconsVisible,
                    DemetraUI::setPopupMenuIconsVisible
            )
            .onInt(
                    HTML_ZOOM_RATIO_PROPERTY,
                    DEFAULT_HTML_ZOOM_RATIO,
                    DemetraUI::getHtmlZoomRatio,
                    DemetraUI::setHtmlZoomRatio
            )
            .onConverter(
                    new ObsFormatConverter(DEFAULT_OBS_FORMAT, "locale", "datePattern", "numberPattern"),
                    DemetraUI::getObsFormat,
                    DemetraUI::setObsFormat
            )
            .onInt(
                    GROWTH_LAST_YEARS_PROPERTY,
                    DEFAULT_GROWTH_LAST_YEARS,
                    DemetraUI::getGrowthLastYears,
                    DemetraUI::setGrowthLastYears
            )
            .build();

    private static final class ObsFormatConverter implements Config.Converter<ObsFormat> {

        private final ObsFormat defaultValue;

        public ObsFormatConverter(ObsFormat defaultValue, String locale, String datePattern, String numberPattern) {
            this.defaultValue = defaultValue;
        }

        @Override
        public ObsFormat getDefaultValue() {
            return defaultValue;
        }

        @Override
        public ObsFormat get(Config config) {
            // TODO
            return defaultValue;
        }

        @Override
        public void set(Config.Builder builder, ObsFormat value) {
            // TODO
        }
    }
}
