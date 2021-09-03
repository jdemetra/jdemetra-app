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
package demetra.desktop.util;

import demetra.desktop.TsManager;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import demetra.timeseries.TsMoniker;
import demetra.tsprovider.TsMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public final class FrozenTsHelper {

    private FrozenTsHelper() {
        // static class
    }

    public static boolean isFrozen(@NonNull Ts ts) {
        return ts.getMeta().keySet().stream().anyMatch(FrozenTsHelper::isFreezeKey);
    }

    @Nullable
    public static LocalDateTime getTimestamp(@NonNull Ts ts) {
        return TsMeta.TIMESTAMP.load(ts.getMeta());
    }

    public static @Nullable TsMoniker getOriginalMoniker(@NonNull TsMoniker moniker) {
        if (moniker.isProvided()) {
            return moniker;
        }
        demetra.timeseries.Ts ts = TsManager.getDefault().makeTs(moniker, TsInformationType.MetaData);
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
        return TsMoniker.of(source, id);
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
