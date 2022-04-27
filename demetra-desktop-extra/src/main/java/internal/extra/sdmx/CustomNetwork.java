package internal.extra.sdmx;

import internal.http.curl.CurlHttpURLConnection;
import java.net.ProxySelector;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import nbbrd.net.proxy.SystemProxySelector;
import nl.altindag.ssl.SSLFactory;
import sdmxdl.web.Network;
import sdmxdl.web.URLConnectionFactory;

@lombok.Value
@lombok.Builder(toBuilder = true)
public final class CustomNetwork implements Network {

    boolean curlBackend;
    boolean autoProxy;
    boolean defaultTrustMaterial;
    boolean systemTrustMaterial;

    @lombok.NonNull
    @lombok.Getter(lazy = true)
    private final ProxySelector lazyProxySelector = initProxySelector();

    @lombok.NonNull
    @lombok.Getter(lazy = true)
    private final SSLFactory lazySSLFactory = initSSLFactory();

    private ProxySelector initProxySelector() {
        return autoProxy ? SystemProxySelector.ofServiceLoader() : ProxySelector.getDefault();
    }

    private SSLFactory initSSLFactory() {
        SSLFactory.Builder result = SSLFactory.builder();
        if (defaultTrustMaterial) {
            result.withDefaultTrustMaterial();
        }
        if (systemTrustMaterial) {
            result.withSystemTrustMaterial();
        }
        return result.build();
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return getLazySSLFactory().getHostnameVerifier();
    }

    @Override
    public ProxySelector getProxySelector() {
        return getLazyProxySelector();
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory() {
        return getLazySSLFactory().getSslSocketFactory();
    }

    @Override
    public URLConnectionFactory getURLConnectionFactory() {
        return curlBackend ? CurlHttpURLConnection::of : URLConnectionFactory.getDefault();
    }
}
