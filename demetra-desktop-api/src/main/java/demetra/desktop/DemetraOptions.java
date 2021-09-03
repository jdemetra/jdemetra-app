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
public final class DemetraOptions implements PropertyChangeSource.WithWeakListeners, Persistable {

    @NonNull
    public static DemetraOptions getDefault() {
        return LazyGlobalService.get(DemetraOptions.class, DemetraOptions::new);
    }

    private DemetraOptions() {
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
    public static final String SHOW_UNAVAILABLE_TS_PROVIDERS_PROPERTY = "showUnavailableTsProviders";
    private static final boolean DEFAULT_SHOW_UNAVAILABLE_TS_PROVIDERS = false;
    private boolean showUnavailableTsProviders = DEFAULT_SHOW_UNAVAILABLE_TS_PROVIDERS;

    public boolean isShowUnavailableTsProviders() {
        return showUnavailableTsProviders;
    }

    public void setShowUnavailableTsProviders(boolean show) {
        boolean old = this.showUnavailableTsProviders;
        this.showUnavailableTsProviders = show;
        broadcaster.firePropertyChange(SHOW_UNAVAILABLE_TS_PROVIDERS_PROPERTY, old, this.showUnavailableTsProviders);
    }

    @SwingProperty
    public static final String SHOW_TS_PROVIDER_NODES_PROPERTY = "showTsProviderNodes";
    private static final boolean DEFAULT_SHOW_TS_PROVIDER_NODES = true;
    private boolean showTsProviderNodes = DEFAULT_SHOW_TS_PROVIDER_NODES;

    public boolean isShowTsProviderNodes() {
        return showTsProviderNodes;
    }

    public void setShowTsProviderNodes(boolean show) {
        boolean old = this.showTsProviderNodes;
        this.showTsProviderNodes = show;
        broadcaster.firePropertyChange(SHOW_TS_PROVIDER_NODES_PROPERTY, old, this.showTsProviderNodes);
    }

    @SwingProperty
    public static final String TS_ACTION_NAME_PROPERTY = "tsActionName";
    private static final String DEFAULT_TS_ACTION_NAME = "ChartGridTsAction";
    private String tsActionName = DEFAULT_TS_ACTION_NAME;

    public String getTsActionName() {
        return tsActionName;
    }

    public void setTsActionName(String tsActionName) {
        String old = this.tsActionName;
        this.tsActionName = tsActionName != null ? tsActionName : DEFAULT_TS_ACTION_NAME;
        broadcaster.firePropertyChange(TS_ACTION_NAME_PROPERTY, old, this.tsActionName);
    }

    @SwingProperty
    public static final String PERSIST_TOOLS_CONTENT_PROPERTY = "persistToolsContent";
    private static final boolean DEFAULT_PERSIST_TOOLS_CONTENT = false;
    private boolean persistToolsContent = DEFAULT_PERSIST_TOOLS_CONTENT;

    public boolean isPersistToolsContent() {
        return persistToolsContent;
    }

    public void setPersistToolsContent(boolean persistToolsContent) {
        boolean old = this.persistToolsContent;
        this.persistToolsContent = persistToolsContent;
        broadcaster.firePropertyChange(PERSIST_TOOLS_CONTENT_PROPERTY, old, this.persistToolsContent);
    }

    @SwingProperty
    public static final String PERSIST_OPENED_DATA_SOURCES_PROPERTY = "persistOpenedDataSources";
    private static final boolean DEFAULT_PERSIST_OPENED_DATA_SOURCES = false;
    private boolean persistOpenedDataSources = DEFAULT_PERSIST_OPENED_DATA_SOURCES;

    public boolean isPersistOpenedDataSources() {
        return persistOpenedDataSources;
    }

    public void setPersistOpenedDataSources(boolean persistOpenedDataSources) {
        boolean old = this.persistOpenedDataSources;
        this.persistOpenedDataSources = persistOpenedDataSources;
        broadcaster.firePropertyChange(PERSIST_OPENED_DATA_SOURCES_PROPERTY, old, this.persistOpenedDataSources);
    }

    @SwingProperty
    public static final String BATCH_POOL_SIZE_PROPERTY = "batchPoolSize";
    private static final ThreadPoolSize DEFAULT_BATCH_POOL_SIZE = ThreadPoolSize.ALL_BUT_ONE;
    private ThreadPoolSize batchPoolSize = DEFAULT_BATCH_POOL_SIZE;

    public ThreadPoolSize getBatchPoolSize() {
        return batchPoolSize;
    }

    public void setBatchPoolSize(ThreadPoolSize batchPoolSize) {
        ThreadPoolSize old = this.batchPoolSize;
        this.batchPoolSize = batchPoolSize != null ? batchPoolSize : DEFAULT_BATCH_POOL_SIZE;
        broadcaster.firePropertyChange(BATCH_POOL_SIZE_PROPERTY, old, this.batchPoolSize);
    }

    @SwingProperty
    public static final String BATCH_PRIORITY_PROPERTY = "batchPriority";
    private static final ThreadPriority DEFAULT_BATCH_PRIORITY = ThreadPriority.NORMAL;
    private ThreadPriority batchPriority = DEFAULT_BATCH_PRIORITY;

    public ThreadPriority getBatchPriority() {
        return batchPriority;
    }

    public void setBatchPriority(ThreadPriority batchPriority) {
        ThreadPriority old = this.batchPriority;
        this.batchPriority = batchPriority != null ? batchPriority : DEFAULT_BATCH_PRIORITY;
        broadcaster.firePropertyChange(BATCH_PRIORITY_PROPERTY, old, this.batchPriority);
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
    private static final ObsFormat DEFAULT_OBS_FORMAT = ObsFormat.DEFAULT;
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
    private static final Persistence<DemetraOptions> PERSISTENCE = Persistence
            .builderOf(DemetraOptions.class)
            .name("INSTANCE")
            .version("VERSION")
            .onString(
                    COLOR_SCHEME_NAME_PROPERTY,
                    DEFAULT_COLOR_SCHEME_NAME,
                    DemetraOptions::getColorSchemeName,
                    DemetraOptions::setColorSchemeName
            )
            .onBoolean(
                    SHOW_UNAVAILABLE_TS_PROVIDERS_PROPERTY,
                    DEFAULT_SHOW_UNAVAILABLE_TS_PROVIDERS,
                    DemetraOptions::isShowUnavailableTsProviders,
                    DemetraOptions::setShowUnavailableTsProviders
            )
            .onBoolean(
                    SHOW_TS_PROVIDER_NODES_PROPERTY,
                    DEFAULT_SHOW_TS_PROVIDER_NODES,
                    DemetraOptions::isShowTsProviderNodes,
                    DemetraOptions::setShowTsProviderNodes
            )
            .onString(
                    TS_ACTION_NAME_PROPERTY,
                    DEFAULT_TS_ACTION_NAME,
                    DemetraOptions::getTsActionName,
                    DemetraOptions::setTsActionName
            )
            .onBoolean(
                    PERSIST_TOOLS_CONTENT_PROPERTY,
                    DEFAULT_PERSIST_TOOLS_CONTENT,
                    DemetraOptions::isPersistToolsContent,
                    DemetraOptions::setPersistToolsContent
            )
            .onBoolean(
                    PERSIST_OPENED_DATA_SOURCES_PROPERTY,
                    DEFAULT_PERSIST_OPENED_DATA_SOURCES,
                    DemetraOptions::isPersistOpenedDataSources,
                    DemetraOptions::setPersistOpenedDataSources
            )
            .onEnum(
                    BATCH_POOL_SIZE_PROPERTY,
                    DEFAULT_BATCH_POOL_SIZE,
                    DemetraOptions::getBatchPoolSize,
                    DemetraOptions::setBatchPoolSize
            )
            .onEnum(
                    BATCH_PRIORITY_PROPERTY,
                    DEFAULT_BATCH_PRIORITY,
                    DemetraOptions::getBatchPriority,
                    DemetraOptions::setBatchPriority
            )
            .onBoolean(
                    POPUP_MENU_ICONS_VISIBLE_PROPERTY,
                    DEFAULT_POPUP_MENU_ICONS_VISIBLE,
                    DemetraOptions::isPopupMenuIconsVisible,
                    DemetraOptions::setPopupMenuIconsVisible
            )
            .onInt(
                    HTML_ZOOM_RATIO_PROPERTY,
                    DEFAULT_HTML_ZOOM_RATIO,
                    DemetraOptions::getHtmlZoomRatio,
                    DemetraOptions::setHtmlZoomRatio
            )
            .onConverter(
                    new ObsFormatConverter(DEFAULT_OBS_FORMAT, "locale", "datePattern", "numberPattern"),
                    DemetraOptions::getObsFormat,
                    DemetraOptions::setObsFormat
            )
            .onInt(
                    GROWTH_LAST_YEARS_PROPERTY,
                    DEFAULT_GROWTH_LAST_YEARS,
                    DemetraOptions::getGrowthLastYears,
                    DemetraOptions::setGrowthLastYears
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
