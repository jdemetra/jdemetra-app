package demetra.desktop;

import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.concurrent.ThreadPoolSize;
import demetra.desktop.concurrent.ThreadPriority;
import demetra.desktop.design.GlobalService;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.util.LazyGlobalService;
import demetra.desktop.util.Persistence;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.beans.PropertyChangeSupport;
import nbbrd.design.MightBeGenerated;

@GlobalService
public final class DemetraBehaviour implements PropertyChangeSource.WithWeakListeners, Persistable {

    @NonNull
    public static DemetraBehaviour get() {
        return LazyGlobalService.get(DemetraBehaviour.class, DemetraBehaviour::new);
    }

    private DemetraBehaviour() {
    }

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    private final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

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

    @Override
    public Config getConfig() {
        return PERSISTENCE.loadConfig(this);
    }

    @Override
    public void setConfig(Config config) {
        PERSISTENCE.storeConfig(this, config);
    }

    @MightBeGenerated
    private static final Persistence<DemetraBehaviour> PERSISTENCE = Persistence
            .builderOf(DemetraBehaviour.class)
            .name("demetra-ui")
            .version("3.0.0")
            .onBoolean(SHOW_UNAVAILABLE_TS_PROVIDERS_PROPERTY,
                    DEFAULT_SHOW_UNAVAILABLE_TS_PROVIDERS,
                    DemetraBehaviour::isShowUnavailableTsProviders,
                    DemetraBehaviour::setShowUnavailableTsProviders
            )
            .onBoolean(SHOW_TS_PROVIDER_NODES_PROPERTY,
                    DEFAULT_SHOW_TS_PROVIDER_NODES,
                    DemetraBehaviour::isShowTsProviderNodes,
                    DemetraBehaviour::setShowTsProviderNodes
            )
            .onString(TS_ACTION_NAME_PROPERTY,
                    DEFAULT_TS_ACTION_NAME,
                    DemetraBehaviour::getTsActionName,
                    DemetraBehaviour::setTsActionName
            )
            .onBoolean(PERSIST_TOOLS_CONTENT_PROPERTY,
                    DEFAULT_PERSIST_TOOLS_CONTENT,
                    DemetraBehaviour::isPersistToolsContent,
                    DemetraBehaviour::setPersistToolsContent
            )
            .onBoolean(PERSIST_OPENED_DATA_SOURCES_PROPERTY,
                    DEFAULT_PERSIST_OPENED_DATA_SOURCES,
                    DemetraBehaviour::isPersistOpenedDataSources,
                    DemetraBehaviour::setPersistOpenedDataSources
            )
            .onEnum(BATCH_POOL_SIZE_PROPERTY,
                    DEFAULT_BATCH_POOL_SIZE,
                    DemetraBehaviour::getBatchPoolSize,
                    DemetraBehaviour::setBatchPoolSize
            )
            .onEnum(BATCH_PRIORITY_PROPERTY,
                    DEFAULT_BATCH_PRIORITY,
                    DemetraBehaviour::getBatchPriority,
                    DemetraBehaviour::setBatchPriority
            )
            .build();
}
