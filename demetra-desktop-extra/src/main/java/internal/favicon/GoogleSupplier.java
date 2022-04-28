package internal.favicon;

import com.google.common.net.InternetDomainName;
import internal.util.http.HttpClient;
import internal.util.http.HttpRequest;
import internal.util.http.HttpResponse;
import internal.util.http.HttpResponseException;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

public final class GoogleSupplier implements FaviconSupplier {

    @Override
    public String getName() {
        return "Google";
    }

    @Override
    public Image getFaviconOrNull(URL url, HttpClient client) throws IOException {
        try ( HttpResponse response = client.requestGET(getFaviconRequest(url))) {
            try ( InputStream stream = response.getBody()) {
                return ImageIO.read(stream);
            }
        } catch (HttpResponseException ex) {
            if (isDefaultFavicon(ex)) {
                return null;
            }
            throw ex;
        }
    }

    private HttpRequest getFaviconRequest(URL url) throws MalformedURLException {
        InternetDomainName domainName = InternetDomainName.from(url.getHost());
        return HttpRequest
                .builder()
                .query(new URL("https://www.google.com/s2/favicons?domain=" + domainName))
                .build();
    }

    private static boolean isDefaultFavicon(HttpResponseException ex) {
        return ex.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND;
    }
}
