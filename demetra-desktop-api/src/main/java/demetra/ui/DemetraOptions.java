package demetra.ui;

import demetra.tsprovider.util.ObsFormat;
import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.concurrent.ThreadPoolSize;
import demetra.ui.concurrent.ThreadPriority;
import ec.util.chart.ColorScheme;
import ec.util.various.swing.FontAwesome;
import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Icon;
import nbbrd.io.text.BooleanProperty;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.IntProperty;
import nbbrd.io.text.Parser;
import nbbrd.io.text.Property;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.util.Lookup;

@GlobalService
public final class DemetraOptions implements PropertyChangeSource, Persistable {

    private static final DemetraOptions INSTANCE = new DemetraOptions();

    @NonNull
    public static DemetraOptions getDefault() {
        return INSTANCE;
    }

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    protected final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

    public static final String COLOR_SCHEME_NAME_PROPERTY = "colorSchemeName";
    private static final String DEFAULT_COLOR_SCHEME_NAME = "Smart";
    private static final Property<String> COLOR_SCHEME_NAME_CONFIG = Property.of(COLOR_SCHEME_NAME_PROPERTY, DEFAULT_COLOR_SCHEME_NAME, Parser.onString(), Formatter.onString());
    private String colorSchemeName = DEFAULT_COLOR_SCHEME_NAME;

    public String getColorSchemeName() {
        return colorSchemeName;
    }

    public void setColorSchemeName(String colorSchemeName) {
        String old = this.colorSchemeName;
        this.colorSchemeName = colorSchemeName != null ? colorSchemeName : DEFAULT_COLOR_SCHEME_NAME;
        broadcaster.firePropertyChange(COLOR_SCHEME_NAME_PROPERTY, old, this.colorSchemeName);
    }

    public static final String SHOW_UNAVAILABLE_TS_PROVIDERS_PROPERTY = "showUnavailableTsProviders";
    private static final boolean DEFAULT_SHOW_UNAVAILABLE_TS_PROVIDERS = false;
    private static final BooleanProperty SHOW_UNAVAILABLE_TS_PROVIDERS_CONFIG = BooleanProperty.of(SHOW_UNAVAILABLE_TS_PROVIDERS_PROPERTY, DEFAULT_SHOW_UNAVAILABLE_TS_PROVIDERS);
    private boolean showUnavailableTsProviders = DEFAULT_SHOW_UNAVAILABLE_TS_PROVIDERS;

    public boolean isShowUnavailableTsProviders() {
        return showUnavailableTsProviders;
    }

    public void setShowUnavailableTsProviders(boolean show) {
        boolean old = this.showUnavailableTsProviders;
        this.showUnavailableTsProviders = show;
        broadcaster.firePropertyChange(SHOW_UNAVAILABLE_TS_PROVIDERS_PROPERTY, old, this.showUnavailableTsProviders);
    }

    public static final String SHOW_TS_PROVIDER_NODES_PROPERTY = "showTsProviderNodes";
    private static final boolean DEFAULT_SHOW_TS_PROVIDER_NODES = true;
    private static final BooleanProperty SHOW_TS_PROVIDER_NODES_CONFIG = BooleanProperty.of(SHOW_TS_PROVIDER_NODES_PROPERTY, DEFAULT_SHOW_TS_PROVIDER_NODES);
    private boolean showTsProviderNodes = DEFAULT_SHOW_TS_PROVIDER_NODES;

    public boolean isShowTsProviderNodes() {
        return showTsProviderNodes;
    }

    public void setShowTsProviderNodes(boolean show) {
        boolean old = this.showTsProviderNodes;
        this.showTsProviderNodes = show;
        broadcaster.firePropertyChange(SHOW_TS_PROVIDER_NODES_PROPERTY, old, this.showTsProviderNodes);
    }

    public static final String TS_ACTION_NAME_PROPERTY = "tsActionName";
    private static final String DEFAULT_TS_ACTION_NAME = "ChartGridTsAction";
    private static final Property<String> TS_ACTION_NAME_CONFIG = Property.of(TS_ACTION_NAME_PROPERTY, DEFAULT_TS_ACTION_NAME, Parser.onString(), Formatter.onString());
    private String tsActionName = DEFAULT_TS_ACTION_NAME;

    public String getTsActionName() {
        return tsActionName;
    }

    public void setTsActionName(String tsActionName) {
        String old = this.tsActionName;
        this.tsActionName = tsActionName != null ? tsActionName : DEFAULT_TS_ACTION_NAME;
        broadcaster.firePropertyChange(TS_ACTION_NAME_PROPERTY, old, this.tsActionName);
    }

    public static final String PERSIST_TOOLS_CONTENT_PROPERTY = "persistToolsContent";
    private static final boolean DEFAULT_PERSIST_TOOLS_CONTENT = false;
    private static final BooleanProperty PERSIST_TOOLS_CONTENT_CONFIG = BooleanProperty.of(PERSIST_TOOLS_CONTENT_PROPERTY, DEFAULT_PERSIST_TOOLS_CONTENT);
    private boolean persistToolsContent = DEFAULT_PERSIST_TOOLS_CONTENT;

    public boolean isPersistToolsContent() {
        return persistToolsContent;
    }

    public void setPersistToolsContent(boolean persistToolsContent) {
        boolean old = this.persistToolsContent;
        this.persistToolsContent = persistToolsContent;
        broadcaster.firePropertyChange(PERSIST_TOOLS_CONTENT_PROPERTY, old, this.persistToolsContent);
    }

    public static final String PERSIST_OPENED_DATA_SOURCES_PROPERTY = "persistOpenedDataSources";
    private static final boolean DEFAULT_PERSIST_OPENED_DATA_SOURCES = false;
    private static final BooleanProperty PERSIST_OPENED_DATA_SOURCES_CONFIG = BooleanProperty.of(PERSIST_OPENED_DATA_SOURCES_PROPERTY, DEFAULT_PERSIST_OPENED_DATA_SOURCES);
    private boolean persistOpenedDataSources = DEFAULT_PERSIST_OPENED_DATA_SOURCES;

    public boolean isPersistOpenedDataSources() {
        return persistOpenedDataSources;
    }

    public void setPersistOpenedDataSources(boolean persistOpenedDataSources) {
        boolean old = this.persistOpenedDataSources;
        this.persistOpenedDataSources = persistOpenedDataSources;
        broadcaster.firePropertyChange(PERSIST_OPENED_DATA_SOURCES_PROPERTY, old, this.persistOpenedDataSources);
    }

    public static final String BATCH_POOL_SIZE_PROPERTY = "batchPoolSize";
    private static final ThreadPoolSize DEFAULT_BATCH_POOL_SIZE = ThreadPoolSize.ALL_BUT_ONE;
    private static final Property<ThreadPoolSize> BATCH_POOL_SIZE_CONFIG = Property.of(BATCH_POOL_SIZE_PROPERTY, DEFAULT_BATCH_POOL_SIZE, Parser.onEnum(ThreadPoolSize.class), Formatter.onEnum());
    private ThreadPoolSize batchPoolSize = DEFAULT_BATCH_POOL_SIZE;

    public ThreadPoolSize getBatchPoolSize() {
        return batchPoolSize;
    }

    public void setBatchPoolSize(ThreadPoolSize batchPoolSize) {
        ThreadPoolSize old = this.batchPoolSize;
        this.batchPoolSize = batchPoolSize != null ? batchPoolSize : DEFAULT_BATCH_POOL_SIZE;
        broadcaster.firePropertyChange(BATCH_POOL_SIZE_PROPERTY, old, this.batchPoolSize);
    }

    public static final String BATCH_PRIORITY_PROPERTY = "batchPriority";
    private static final ThreadPriority DEFAULT_BATCH_PRIORITY = ThreadPriority.NORMAL;
    private static final Property<ThreadPriority> BATCH_PRIORITY_CONFIG = Property.of(BATCH_PRIORITY_PROPERTY, DEFAULT_BATCH_PRIORITY, Parser.onEnum(ThreadPriority.class), Formatter.onEnum());
    private ThreadPriority batchPriority = DEFAULT_BATCH_PRIORITY;

    public ThreadPriority getBatchPriority() {
        return batchPriority;
    }

    public void setBatchPriority(ThreadPriority batchPriority) {
        ThreadPriority old = this.batchPriority;
        this.batchPriority = batchPriority != null ? batchPriority : DEFAULT_BATCH_PRIORITY;
        broadcaster.firePropertyChange(BATCH_PRIORITY_PROPERTY, old, this.batchPriority);
    }

    public static final String POPUP_MENU_ICONS_VISIBLE_PROPERTY = "popupMenuIconsVisible";
    private static final boolean DEFAULT_POPUP_MENU_ICONS_VISIBLE = false;
    private static final BooleanProperty POPUP_MENU_ICONS_VISIBLE_CONFIG = BooleanProperty.of(POPUP_MENU_ICONS_VISIBLE_PROPERTY, DEFAULT_POPUP_MENU_ICONS_VISIBLE);
    private boolean popupMenuIconsVisible = DEFAULT_POPUP_MENU_ICONS_VISIBLE;

    public boolean isPopupMenuIconsVisible() {
        return popupMenuIconsVisible;
    }

    public void setPopupMenuIconsVisible(boolean visible) {
        boolean old = this.popupMenuIconsVisible;
        this.popupMenuIconsVisible = visible;
        broadcaster.firePropertyChange(POPUP_MENU_ICONS_VISIBLE_PROPERTY, old, this.popupMenuIconsVisible);
    }

    public static final String HTML_ZOOM_RATIO_PROPERTY = "htmlZoomRatio";
    private static final int DEFAULT_HTML_ZOOM_RATIO = 100;
    private static final IntProperty HTML_ZOOM_RATIO_CONFIG = IntProperty.of(HTML_ZOOM_RATIO_PROPERTY, DEFAULT_HTML_ZOOM_RATIO);
    private int htmlZoomRatio = DEFAULT_HTML_ZOOM_RATIO;

    public int getHtmlZoomRatio() {
        return this.htmlZoomRatio;
    }

    public void setHtmlZoomRatio(int htmlZoomRatio) {
        int old = this.htmlZoomRatio;
        this.htmlZoomRatio = htmlZoomRatio >= 10 && htmlZoomRatio <= 200 ? htmlZoomRatio : DEFAULT_HTML_ZOOM_RATIO;
        broadcaster.firePropertyChange(HTML_ZOOM_RATIO_PROPERTY, old, this.htmlZoomRatio);
    }

    public static final String OBS_FORMAT_PROPERTY = "obsFormat";
    private static final ObsFormat DEFAULT_OBS_FORMAT = ObsFormat.DEFAULT;
    private static final Config.Converter<ObsFormat> OBS_FORMAT_CONFIG = new ObsFormatConverter(DEFAULT_OBS_FORMAT, "locale", "datePattern", "numberPattern");
    private ObsFormat obsFormat = DEFAULT_OBS_FORMAT;

    public ObsFormat getObsFormat() {
        return obsFormat;
    }

    public void setObsFormat(ObsFormat obsFormat) {
        ObsFormat old = this.obsFormat;
        this.obsFormat = obsFormat != null ? obsFormat : DEFAULT_OBS_FORMAT;
        broadcaster.firePropertyChange(OBS_FORMAT_PROPERTY, old, this.obsFormat);
    }

    public static final String GROWTH_CHART_LENGTH_PROPERTY = "growthChartLength";
    private static final int DEFAULT_GROWTH_LAST_YEARS = 4;
    private static final IntProperty GROWTH_LAST_YEARS_CONFIG = IntProperty.of(GROWTH_CHART_LENGTH_PROPERTY, DEFAULT_GROWTH_LAST_YEARS);
    private Integer growthLastYears = DEFAULT_GROWTH_LAST_YEARS;

    public Integer getGrowthLastYears() {
        return growthLastYears;
    }

    public void setGrowthLastYears(Integer lastYears) {
        Integer old = this.growthLastYears;
        growthLastYears = lastYears != null ? lastYears : DEFAULT_GROWTH_LAST_YEARS;
        broadcaster.firePropertyChange(GROWTH_CHART_LENGTH_PROPERTY, old, growthLastYears);
    }

    @Override
    public Config getConfig() {
        Config.Builder b = Config.builder(DOMAIN, NAME, VERSION);
        COLOR_SCHEME_NAME_CONFIG.set(b::parameter, getColorSchemeName());
        SHOW_UNAVAILABLE_TS_PROVIDERS_CONFIG.set(b::parameter, isShowUnavailableTsProviders());
        SHOW_TS_PROVIDER_NODES_CONFIG.set(b::parameter, isShowTsProviderNodes());
        TS_ACTION_NAME_CONFIG.set(b::parameter, getTsActionName());
        PERSIST_TOOLS_CONTENT_CONFIG.set(b::parameter, isPersistToolsContent());
        PERSIST_OPENED_DATA_SOURCES_CONFIG.set(b::parameter, isPersistOpenedDataSources());
        BATCH_POOL_SIZE_CONFIG.set(b::parameter, getBatchPoolSize());
        BATCH_PRIORITY_CONFIG.set(b::parameter, getBatchPriority());
        POPUP_MENU_ICONS_VISIBLE_CONFIG.set(b::parameter, isPopupMenuIconsVisible());
        HTML_ZOOM_RATIO_CONFIG.set(b::parameter, getHtmlZoomRatio());
        OBS_FORMAT_CONFIG.set(b, getObsFormat());
        GROWTH_LAST_YEARS_CONFIG.set(b::parameter, getGrowthLastYears());
        return b.build();
    }

    @Override
    public void setConfig(Config config) {
        if (!DOMAIN.equals(config.getDomain())) {
            throw new IllegalArgumentException("Not produced here");
        }
        setColorSchemeName(COLOR_SCHEME_NAME_CONFIG.get(config::getParameter));
        setShowUnavailableTsProviders(SHOW_UNAVAILABLE_TS_PROVIDERS_CONFIG.get(config::getParameter));
        setShowTsProviderNodes(SHOW_TS_PROVIDER_NODES_CONFIG.get(config::getParameter));
        setTsActionName(TS_ACTION_NAME_CONFIG.get(config::getParameter));
        setPersistToolsContent(PERSIST_TOOLS_CONTENT_CONFIG.get(config::getParameter));
        setPersistOpenedDataSources(PERSIST_OPENED_DATA_SOURCES_CONFIG.get(config::getParameter));
        setBatchPoolSize(BATCH_POOL_SIZE_CONFIG.get(config::getParameter));
        setBatchPriority(BATCH_PRIORITY_CONFIG.get(config::getParameter));
        setPopupMenuIconsVisible(POPUP_MENU_ICONS_VISIBLE_CONFIG.get(config::getParameter));
        setHtmlZoomRatio(HTML_ZOOM_RATIO_CONFIG.get(config::getParameter));
        setObsFormat(OBS_FORMAT_CONFIG.get(config));
        setGrowthLastYears(GROWTH_LAST_YEARS_CONFIG.get(config::getParameter));
    }

    private static final String DOMAIN = DemetraOptions.class.getName(), NAME = "INSTANCE", VERSION = "";

    @Deprecated
    public ColorScheme getColorScheme() {
        return getColorSchemes()
                .stream()
                .filter(colorScheme -> colorScheme.getName().equals(getColorSchemeName())
                || colorScheme.getName().equals(DEFAULT_COLOR_SCHEME_NAME))
                .findFirst()
                .orElse(null);
    }

    @Deprecated
    public List<? extends ColorScheme> getColorSchemes() {
        return Lookup.getDefault().lookupAll(ColorScheme.class)
                .stream()
                .sorted(Comparator.comparing(ColorScheme::getDisplayName))
                .collect(Collectors.toList());
    }

    @Deprecated
    public Icon getPopupMenuIcon(Icon icon) {
        return isPopupMenuIconsVisible() ? icon : null;
    }

    @Deprecated
    public Icon getPopupMenuIcon(FontAwesome icon) {
        return isPopupMenuIconsVisible() ? icon.getIcon(Color.BLACK, 13f) : null;
    }

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
