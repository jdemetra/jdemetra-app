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

import demetra.bridge.TsConverter;
import demetra.timeseries.TsInformationType;
import demetra.tsprovider.TsMeta;
import demetra.ui.TsManager;
import ec.tss.Ts;
import ec.tss.TsMoniker;
import ec.tstoolkit.MetaData;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    public static boolean isFrozen(demetra.timeseries.@NonNull Ts ts) {
        return TsConverter.fromTs(ts).isFrozen();
    }

    @Nullable
    public static LocalDateTime getTimestamp(demetra.timeseries.@NonNull Ts ts) {
        return getTimestamp(TsConverter.fromTs(ts));
    }

    @Nullable
    public static TsMoniker getOriginalMoniker(demetra.timeseries.@NonNull TsMoniker moniker) {
        return getOriginalMoniker(TsConverter.fromTsMoniker(moniker));
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
    private static String getSource(@NonNull Map<String, String> md) {
        String result = TsMeta.SOURCE.load(md);
        return result != null ? result : TsMeta.SOURCE_OLD.load(md);
    }

    @Nullable
    private static String getId(@NonNull Map<String, String> md) {
        String result = TsMeta.ID.load(md);
        return result != null ? result : TsMeta.ID_OLD.load(md);
    }

    @Nullable
    public static LocalDateTime getTimestamp(@NonNull Ts ts) {
        MetaData md = ts.getMetaData();
        return md != null ? TsMeta.TIMESTAMP.load(md) : null;
    }

    @Nullable
    public static TsMoniker getOriginalMoniker(@NonNull TsMoniker moniker) {
        if (!moniker.isAnonymous()) {
            return moniker;
        }
        demetra.timeseries.Ts ts = TsManager.getDefault().getNextTsManager().getTs(TsConverter.toTsMoniker(moniker), TsInformationType.MetaData);
        if (ts == null) {
            return null;
        }
        String source = getSource(ts.getMeta());
        if (source == null) {
            return null;
        }
        String id = getId(ts.getMeta());
        if (id == null) {
            return null;
        }
        return new TsMoniker(source, id);
    }

    private static final Set<String> FREEZE_KEYS
            = Stream.of(
                    TsMeta.SOURCE_OLD,
                    TsMeta.ID_OLD,
                    TsMeta.SOURCE,
                    TsMeta.ID,
                    TsMeta.TIMESTAMP
            ).map(TsMeta::getKey).collect(Collectors.toSet());

    public static boolean isFreezeKey(String key) {
        return FREEZE_KEYS.contains(key);
    }
}
