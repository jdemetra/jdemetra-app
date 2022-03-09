package internal.extra.sdmx.web;

import demetra.desktop.notification.MessageUtil;
import java.net.ProxySelector;
import java.util.function.BiConsumer;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import nbbrd.net.proxy.SystemProxySelector;
import nl.altindag.ssl.SSLFactory;
import org.openide.awt.StatusDisplayer;
import sdmxdl.DataRepository;
import sdmxdl.ext.Cache;
import sdmxdl.kryo.KryoFileFormat;
import sdmxdl.util.ext.FileCache;
import sdmxdl.util.ext.FileFormat;
import sdmxdl.util.ext.VerboseCache;
import sdmxdl.web.MonitorReports;
import sdmxdl.web.Network;
import sdmxdl.web.SdmxWebManager;

@lombok.experimental.UtilityClass
public class SdmxWebFactory {

    public static SdmxWebManager createManager() {
        return SdmxWebManager.ofServiceLoader()
                .toBuilder()
                .eventListener((src, msg) -> StatusDisplayer.getDefault().setStatusText(msg))
                .network(getNetworkFactory())
                .cache(getCache())
                .build();
    }

    private static Network getNetworkFactory() {
        SSLFactory sslFactory = SSLFactory
                .builder()
                .withDefaultTrustMaterial()
                .withSystemTrustMaterial()
                .build();

        return new Network() {
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
        };
    }

    private static Cache getCache() {
        FileCache fileCache = getFileCache(false);
        return getVerboseCache(fileCache, true);
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
}
