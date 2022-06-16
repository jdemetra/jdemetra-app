package demetra.desktop.extra.sdmx.web;

import demetra.desktop.notification.MessageUtil;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.util.Persistence;
import demetra.tsprovider.util.PropertyHandler;
import internal.extra.sdmx.CustomNetwork;
import internal.extra.sdmx.SdmxAutoCompletion;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import javax.swing.filechooser.FileNameExtensionFilter;
import nbbrd.design.MightBeGenerated;
import org.openide.awt.NotificationDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Sheet;
import sdmxdl.DataRepository;
import sdmxdl.LanguagePriorityList;
import sdmxdl.ext.Cache;
import sdmxdl.format.FileFormat;
import sdmxdl.format.kryo.KryoFileFormat;
import sdmxdl.format.xml.XmlWebSource;
import sdmxdl.provider.ext.FileCache;
import sdmxdl.provider.ext.VerboseCache;
import sdmxdl.web.MonitorReports;
import sdmxdl.web.SdmxWebManager;
import sdmxdl.web.SdmxWebSource;

@lombok.Data
public class SdmxWebConfiguration {

    private static final String SOURCES_PROPERTY = "sources";
    private static final File DEFAULT_SOURCES = null;
    private File sources = DEFAULT_SOURCES;

    private static final String LANGUAGES_PROPERTY = "languages";
    private static final String DEFAULT_LANGUAGES = null;
    private String languages = DEFAULT_LANGUAGES;

    private static final String CURL_BACKEND_PROPERTY = "curlBackend";
    private static final boolean DEFAULT_CURL_BACKEND = false;
    private boolean curlBackend = DEFAULT_CURL_BACKEND;

    private static final String NO_CACHE_PROPERTY = "noCache";
    private static final boolean DEFAULT_NO_CACHE = false;
    private boolean noCache = DEFAULT_NO_CACHE;

    private static final String AUTO_PROXY_PROPERTY = "autoProxy";
    private static final boolean DEFAULT_AUTO_PROXY = false;
    private boolean autoProxy = DEFAULT_AUTO_PROXY;

    private static final String NO_DEFAULT_SSL_PROPERTY = "noDefaultSSL";
    private static final boolean DEFAULT_NO_DEFAULT_SSL = false;
    private boolean noDefaultSSL = DEFAULT_NO_DEFAULT_SSL;

    private static final String NO_SYSTEM_SSL_PROPERTY = "noSystemSSL";
    private static final boolean DEFAULT_NO_SYSTEM_SSL = false;
    private boolean noSystemSSL = DEFAULT_NO_SYSTEM_SSL;

    @MightBeGenerated
    public static SdmxWebConfiguration copyOf(SdmxWebConfiguration bean) {
        SdmxWebConfiguration result = new SdmxWebConfiguration();
        result.sources = bean.sources;
        result.languages = bean.languages;
        result.curlBackend = bean.curlBackend;
        result.noCache = bean.noCache;
        result.autoProxy = bean.autoProxy;
        result.noDefaultSSL = bean.noDefaultSSL;
        result.noSystemSSL = bean.noSystemSSL;
        return result;
    }

    SdmxWebManager toSdmxWebManager() {
        return SdmxWebManager.ofServiceLoader()
                .toBuilder()
                .languages(toLanguages())
                .eventListener(toEventListener())
                .cache(toCache())
                .network(toNetwork())
                .customSources(toSources())
                .build();
    }

    private LanguagePriorityList toLanguages() throws IllegalArgumentException {
        return languages != null ? LanguagePriorityList.parse(languages) : LanguagePriorityList.ANY;
    }

    private BiConsumer<? super SdmxWebSource, ? super String> toEventListener() {
        return (source, message) -> StatusDisplayer.getDefault().setStatusText(message);
    }

    private Cache toCache() {
        if (noCache) {
            return Cache.noOp();
        }
        FileCache fileCache = getFileCache(false);
        return getVerboseCache(fileCache, true);
    }

    private CustomNetwork toNetwork() {
        return CustomNetwork
                .builder()
                .curlBackend(curlBackend)
                .autoProxy(autoProxy)
                .defaultTrustMaterial(!noDefaultSSL)
                .systemTrustMaterial(!noSystemSSL)
                .build();
    }

    private List<SdmxWebSource> toSources() {
        if (sources != null && sources.exists()) {
            try {
                return XmlWebSource.getParser().parseFile(sources);
            } catch (IOException ex) {
                MessageUtil.showException("Cannot load custom sources", ex);
            }
        }
        return Collections.emptyList();
    }

    private static FileCache getFileCache(boolean noCacheCompression) {
        return FileCache
                .builder()
                .repositoryFormat(getRepositoryFormat(noCacheCompression))
                .monitorFormat(getMonitorFormat(noCacheCompression))
                .onIOException(SdmxWebConfiguration::reportIOException)
                .build();
    }

    private static FileFormat<DataRepository> getRepositoryFormat(boolean noCacheCompression) {
        FileFormat<DataRepository> result = FileFormat.of(KryoFileFormat.REPOSITORY, ".kryo");
        return noCacheCompression ? result : FileFormat.gzip(result);
    }

    private static FileFormat<MonitorReports> getMonitorFormat(boolean noCacheCompression) {
        FileFormat<MonitorReports> result = FileFormat.of(KryoFileFormat.MONITOR, ".kryo");
        return noCacheCompression ? result : FileFormat.gzip(result);
    }

    private static void reportIOException(String message, IOException error) {
        NotificationDisplayer.getDefault().notify(message, SdmxAutoCompletion.getDefaultIcon(), "", null);
    }

    private static Cache getVerboseCache(Cache delegate, boolean verbose) {
        if (verbose) {
            BiConsumer<String, Boolean> listener = (key, hit) -> StatusDisplayer.getDefault().setStatusText((hit ? "Hit " : "Miss ") + key);
            return new VerboseCache(delegate, listener, listener);
        }
        return delegate;
    }

    Sheet toSheet() {
        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.withFile()
                .select(this, SOURCES_PROPERTY)
                .display("Sources")
                .description("File that provides data source definitions")
                .filterForSwing(new FileNameExtensionFilter("XML file", "xml"))
                .directories(false)
                .add();
        b.withAutoCompletion()
                .select(this, LANGUAGES_PROPERTY)
                .servicePath(Locale.class.getName())
                .separator(",")
                .display("Languages")
                .description("Language priority list")
                .add();
        result.put(b.build());

        b.reset("Network");
        b.withBoolean()
                .select(this, CURL_BACKEND_PROPERTY)
                .display("Curl backend")
                .description("Use curl backend instead of JDK")
                .add();
        b.withBoolean()
                .select(this, NO_CACHE_PROPERTY)
                .display("No cache")
                .description("Disable caching")
                .add();
        b.withBoolean()
                .select(this, AUTO_PROXY_PROPERTY)
                .display("Auto proxy")
                .description("Enable automatic proxy detection")
                .add();
        b.withBoolean()
                .select(this, NO_DEFAULT_SSL_PROPERTY)
                .display("No default SSL")
                .description("Disable default truststore")
                .add();
        b.withBoolean()
                .select(this, NO_SYSTEM_SSL_PROPERTY)
                .display("No system SSL")
                .description("Disable system truststore")
                .add();
        result.put(b.build());

        return result;
    }

    @MightBeGenerated
    static final Persistence<SdmxWebConfiguration> PERSISTENCE = Persistence
            .builderOf(SdmxWebConfiguration.class)
            .name("INSTANCE")
            .version("VERSION")
            .with(PropertyHandler.onFile(SOURCES_PROPERTY, DEFAULT_SOURCES), SdmxWebConfiguration::getSources, SdmxWebConfiguration::setSources)
            .with(PropertyHandler.onString(LANGUAGES_PROPERTY, DEFAULT_LANGUAGES), SdmxWebConfiguration::getLanguages, SdmxWebConfiguration::setLanguages)
            .with(PropertyHandler.onBoolean(CURL_BACKEND_PROPERTY, DEFAULT_CURL_BACKEND), SdmxWebConfiguration::isCurlBackend, SdmxWebConfiguration::setCurlBackend)
            .with(PropertyHandler.onBoolean(NO_CACHE_PROPERTY, DEFAULT_NO_CACHE), SdmxWebConfiguration::isNoCache, SdmxWebConfiguration::setNoCache)
            .with(PropertyHandler.onBoolean(AUTO_PROXY_PROPERTY, DEFAULT_AUTO_PROXY), SdmxWebConfiguration::isAutoProxy, SdmxWebConfiguration::setAutoProxy)
            .with(PropertyHandler.onBoolean(NO_DEFAULT_SSL_PROPERTY, DEFAULT_NO_DEFAULT_SSL), SdmxWebConfiguration::isNoDefaultSSL, SdmxWebConfiguration::setNoDefaultSSL)
            .with(PropertyHandler.onBoolean(NO_SYSTEM_SSL_PROPERTY, DEFAULT_NO_SYSTEM_SSL), SdmxWebConfiguration::isNoSystemSSL, SdmxWebConfiguration::setNoSystemSSL)
            .build();
}
