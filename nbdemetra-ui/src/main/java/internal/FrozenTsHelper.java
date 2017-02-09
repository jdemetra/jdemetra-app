/*
 * Copyright 2017 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package internal;

import ec.tss.Ts;
import ec.tstoolkit.MetaData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author Philippe Charles
 */
public final class FrozenTsHelper {

    @Nullable
    public static String getSource(@Nonnull Ts ts) {
        String source = ts.getMoniker().getSource();
        if (source != null) {
            return source;
        }
        MetaData metaData = ts.getMetaData();
        if (metaData != null) {
            return getSource(metaData);
        }
        return null;
    }

    @Nullable
    private static String getSource(@Nonnull MetaData md) {
        String result = md.get(MetaData.SOURCE);
        return result != null ? result : md.get(Ts.SOURCE_OLD);
    }

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ROOT);

    @Nullable
    public static LocalDateTime getTimestamp(@Nonnull Ts ts) {
        MetaData md = ts.getMetaData();
        return md != null ? getTimestamp(md) : null;
    }

    @Nullable
    private static LocalDateTime getTimestamp(@Nonnull MetaData md) {
        String dateAsString = md.get(MetaData.DATE);
        return dateAsString != null ? LocalDateTime.parse(dateAsString, TIMESTAMP_FORMATTER) : null;
    }
}
