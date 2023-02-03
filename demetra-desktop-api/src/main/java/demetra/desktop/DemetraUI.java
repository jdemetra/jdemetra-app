package demetra.desktop;

import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.design.GlobalService;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.ui.properties.l2fprod.OutlierDescriptorsEditor;
import demetra.desktop.util.LazyGlobalService;
import demetra.desktop.util.Persistence;
import demetra.tsprovider.util.ObsFormat;
import demetra.tsprovider.util.ObsFormatHandler;
import demetra.tsprovider.util.PropertyHandler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.beans.PropertyChangeSupport;
import nbbrd.design.MightBeGenerated;

@GlobalService
public final class DemetraUI implements PropertyChangeSource.WithWeakListeners, Persistable {

    @NonNull
    public static DemetraUI get() {
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

    @SwingProperty
    public static final String LOW_LEVEL_OPTIONS_PROPERTY = "lowLevelOptions";
    private static final boolean DEFAULT_LOW_LEVEL_OPTIONS = false;
    private boolean lowLevelOptions = DEFAULT_LOW_LEVEL_OPTIONS;

    public boolean isLowLevelOptions() {
        return lowLevelOptions;
    }

    public void setLowLevelOptions(boolean lowOptions) {
        boolean old = this.lowLevelOptions;
        lowLevelOptions = lowOptions;
        broadcaster.firePropertyChange(LOW_LEVEL_OPTIONS_PROPERTY, old, lowLevelOptions);
    }

    @SwingProperty
    public static final String PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY = "prespecifiedOutliersEditor";
    private static final OutlierDescriptorsEditor.PrespecificiedOutliersEditor DEFAULT_PRESPECIFIED_OUTLIERS_EDITOR = OutlierDescriptorsEditor.PrespecificiedOutliersEditor.LIST;
    private OutlierDescriptorsEditor.PrespecificiedOutliersEditor prespecifiedOutliersEditor = DEFAULT_PRESPECIFIED_OUTLIERS_EDITOR;

    public OutlierDescriptorsEditor.PrespecificiedOutliersEditor getPrespecifiedOutliersEditor() {
        return prespecifiedOutliersEditor;
    }

    public void setPrespecifiedOutliersEditor(OutlierDescriptorsEditor.PrespecificiedOutliersEditor prespecifiedOutliersEditor) {
        OutlierDescriptorsEditor.PrespecificiedOutliersEditor old = this.prespecifiedOutliersEditor;
        this.prespecifiedOutliersEditor = prespecifiedOutliersEditor != null ? prespecifiedOutliersEditor : DEFAULT_PRESPECIFIED_OUTLIERS_EDITOR;
        broadcaster.firePropertyChange(PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY, old, this.prespecifiedOutliersEditor);
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
            .with(
                    PropertyHandler.onString(COLOR_SCHEME_NAME_PROPERTY, DEFAULT_COLOR_SCHEME_NAME),
                    DemetraUI::getColorSchemeName,
                    DemetraUI::setColorSchemeName
            )
            .with(
                    PropertyHandler.onBoolean(POPUP_MENU_ICONS_VISIBLE_PROPERTY, DEFAULT_POPUP_MENU_ICONS_VISIBLE),
                    DemetraUI::isPopupMenuIconsVisible,
                    DemetraUI::setPopupMenuIconsVisible
            )
            .with(
                    PropertyHandler.onInteger(HTML_ZOOM_RATIO_PROPERTY, DEFAULT_HTML_ZOOM_RATIO),
                    DemetraUI::getHtmlZoomRatio,
                    DemetraUI::setHtmlZoomRatio
            )
            .with(
                    ObsFormatHandler
                            .builder()
                            .locale(PropertyHandler.onLocale("locale", DEFAULT_OBS_FORMAT.getLocale()))
                            .dateTimePattern(PropertyHandler.onString("dateTimePattern", DEFAULT_OBS_FORMAT.getDateTimePattern()))
                            .numberPattern(PropertyHandler.onString("numberPattern", DEFAULT_OBS_FORMAT.getNumberPattern()))
                            .ignoreNumberGrouping(PropertyHandler.onBoolean("ignoreNumberGrouping", DEFAULT_OBS_FORMAT.isIgnoreNumberGrouping()))
                            .build(),
                    DemetraUI::getObsFormat,
                    DemetraUI::setObsFormat
            )
            .with(
                    PropertyHandler.onInteger(GROWTH_LAST_YEARS_PROPERTY, DEFAULT_GROWTH_LAST_YEARS),
                    DemetraUI::getGrowthLastYears,
                    DemetraUI::setGrowthLastYears
            )
            .with(
                    PropertyHandler.onEnum(PRESPECIFIED_OUTLIERS_EDITOR_PROPERTY, DEFAULT_PRESPECIFIED_OUTLIERS_EDITOR),
                    DemetraUI::getPrespecifiedOutliersEditor,
                    DemetraUI::setPrespecifiedOutliersEditor
            )
            .with(
                    PropertyHandler.onBoolean(LOW_LEVEL_OPTIONS_PROPERTY, DEFAULT_LOW_LEVEL_OPTIONS),
                    DemetraUI::isLowLevelOptions,
                    DemetraUI::setLowLevelOptions
            )
            .build();
}
