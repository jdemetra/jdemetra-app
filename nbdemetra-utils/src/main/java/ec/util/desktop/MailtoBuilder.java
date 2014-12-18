/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.util.desktop;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.EnumMap;
import java.util.Map.Entry;
import javax.annotation.Nonnull;

/**
 * A utility class that eases the creation of a mailto URI.
 *
 * @see
 * http://shadow2531.com/opera/testcases/mailto/modern_mailto_uri_scheme.html
 * @see http://www.ietf.org/rfc/rfc2368.txt
 *
 * @author Philippe Charles
 */
public class MailtoBuilder {

    protected enum HName {

        TO, CC, BCC, SUBJECT, BODY
    };
    //
    protected final EnumMap<HName, String> map;

    public MailtoBuilder() {
        this.map = new EnumMap<>(HName.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Options">
    @Nonnull
    public MailtoBuilder to(@Nonnull String... emails) {
        map.put(HName.TO, join(emails));
        return this;
    }

    @Nonnull
    public MailtoBuilder cc(@Nonnull String... emails) {
        map.put(HName.CC, join(emails));
        return this;
    }

    @Nonnull
    public MailtoBuilder bcc(@Nonnull String... emails) {
        map.put(HName.BCC, join(emails));
        return this;
    }

    @Nonnull
    public MailtoBuilder subject(@Nonnull String subject) {
        map.put(HName.SUBJECT, subject);
        return this;
    }

    @Nonnull
    public MailtoBuilder body(@Nonnull String body) {
        map.put(HName.BODY, body);
        return this;
    }

    @Nonnull
    public MailtoBuilder clear() {
        map.clear();
        return this;
    }
    //</editor-fold>

    @Nonnull
    public URI build() {
        StringBuilder sb = new StringBuilder("mailto:?");
        boolean first = true;
        for (Entry<HName, String> o : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(o.getKey().toString().toLowerCase()).append("=").append(encodex(o.getValue()));
        }
        return URI.create(sb.toString());
    }

    @Nonnull
    static String join(@Nonnull String... list) {
        if (list.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder(list[0]);
        for (int i = 1; i < list.length; i++) {
            result.append(", ").append(list[i]);
        }
        return result.toString();
    }

    static String encodex(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%0A", "%0D%0A");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
