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
import ec.tss.TsFactory;
import ec.tss.TsMoniker;
import ec.tstoolkit.MetaData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Philippe Charles
 */
public final class FrozenTsHelper {

    private FrozenTsHelper() {
        // static class
    }

    @Nullable
    public static String getSource(@NonNull Ts ts) {
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
    private static String getSource(@NonNull MetaData md) {
        String result = md.get(MetaData.SOURCE);
        return result != null ? result : md.get(Ts.SOURCE_OLD);
    }

    @Nullable
    private static String getId(@NonNull MetaData md) {
        String result = md.get(MetaData.ID);
        return result != null ? result : md.get(Ts.ID_OLD);
    }

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ROOT);

    @Nullable
    public static LocalDateTime getTimestamp(@NonNull Ts ts) {
        MetaData md = ts.getMetaData();
        return md != null ? getTimestamp(md) : null;
    }

    @Nullable
    private static LocalDateTime getTimestamp(@NonNull MetaData md) {
        String dateAsString = md.get(MetaData.DATE);
        return dateAsString != null ? LocalDateTime.parse(dateAsString, TIMESTAMP_FORMATTER) : null;
    }

    @Nullable
    public static TsMoniker getOriginalMoniker(@NonNull TsMoniker moniker) {
        if (!moniker.isAnonymous()) {
            return moniker;
        }
        Ts ts = TsFactory.instance.getTs(moniker);
        if (ts == null) {
            return null;
        }
        MetaData md = ts.getMetaData();
        if (md == null) {
            return null;
        }
        String source = getSource(md);
        if (source == null) {
            return null;
        }
        String id = getId(md);
        if (id == null) {
            return null;
        }
        return new TsMoniker(source, id);
    }
}
