package internal.extra.sdmx;

import demetra.desktop.notification.MessageUtil;
import demetra.desktop.util.Caches;
import java.net.ProxySelector;
import java.time.Clock;
import java.util.function.BiConsumer;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import nbbrd.net.proxy.SystemProxySelector;
import nl.altindag.ssl.SSLFactory;
import org.openide.awt.StatusDisplayer;
import sdmxdl.DataRepository;
import sdmxdl.SdmxSource;
import sdmxdl.ext.Cache;
import sdmxdl.file.SdmxFileManager;
import sdmxdl.kryo.KryoFileFormat;
import sdmxdl.util.ext.FileCache;
import sdmxdl.util.ext.FileFormat;
import sdmxdl.util.ext.MapCache;
import sdmxdl.util.ext.VerboseCache;
import sdmxdl.web.MonitorReports;
import sdmxdl.web.Network;
import sdmxdl.web.SdmxWebManager;

@lombok.experimental.UtilityClass
public class SdmxManagerFactory {

    public static SdmxWebManager newWebManager() {
        return SdmxWebManager.ofServiceLoader()
                .toBuilder()
                .eventListener(SdmxManagerFactory::reportOnStatusBar)
                .cache(getCacheForWeb())
                .network(getNetworkFactory())
                .build();
    }

    public static SdmxFileManager newFileManager() {
        return SdmxFileManager.ofServiceLoader()
                .toBuilder()
                .eventListener(SdmxManagerFactory::reportOnStatusBar)
                .cache(getCacheForFile())
                .build();
    }

    private static void reportOnStatusBar(SdmxSource source, String message) {
        StatusDisplayer.getDefault().setStatusText(message);
    }

    private static Cache getCacheForWeb() {
        FileCache fileCache = getFileCache(false);
        return getVerboseCache(fileCache, true);
    }

    private static Cache getCacheForFile() {
        return MapCache.of(
                Caches.softValuesCacheAsMap(),
                Caches.softValuesCacheAsMap(),
                Clock.systemDefaultZone()
        );
    }

    private static Network getNetworkFactory() {
        SSLFactory sslFactory = SSLFactory
                .builder()
                .withDefaultTrustMaterial()
                .withSystemTrustMaterial()
                .build();

        return new SSLFactoryNetwork(sslFactory);
    }

    private static FileCache getFileCache(boolean noCacheCompression) {
        return FileCache
                .builder()
                .repositoryFormat(getRepositoryFormat(noCacheCompression))
                .monitorFormat(getMonitorFormat(noCacheCompression))
                .onIOException(MessageUtil::showException)
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

    private static Cache getVerboseCache(Cache delegate, boolean verbose) {
        if (verbose) {
            BiConsumer<String, Boolean> listener = (key, hit) -> StatusDisplayer.getDefault().setStatusText((hit ? "Hit " : "Miss ") + key);
            return new VerboseCache(delegate, listener, listener);
        }
        return delegate;
    }

    @lombok.AllArgsConstructor
    private static final class SSLFactoryNetwork implements Network {

        @lombok.NonNull
        private final SSLFactory sslFactory;

        @Override
        public HostnameVerifier getHostnameVerifier() {
            return sslFactory.getHostnameVerifier();
        }

        @Override
        public ProxySelector getProxySelector() {
            return SystemProxySelector.ofServiceLoader();
        }

        @Override
        public SSLSocketFactory getSslSocketFactory() {
            return sslFactory.getSslSocketFactory();
        }
    }
}
