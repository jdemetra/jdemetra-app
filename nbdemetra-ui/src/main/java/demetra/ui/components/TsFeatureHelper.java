/*
 * Copyright 2018 National Bank of Belgium
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
package demetra.ui.components;

import demetra.timeseries.TsData;
import demetra.tsprovider.Ts;
import demetra.tsprovider.TsMeta;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import lombok.AccessLevel;

/**
 *
 * @author Philippe Charles
 */
@lombok.AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TsFeatureHelper {

    public enum Feature {
        Backcasts,
        Actual,
        Forecasts,
        Public,
        Confidential
    }

    public static final TsFeatureHelper EMPTY = of(Collections.emptyList());

    @Nonnull
    public static TsFeatureHelper of(@Nonnull List<Ts> list) {
        int[] begIndexes = new int[list.size()];
        int[] endIndexes = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Ts ts = list.get(i);
            begIndexes[i] = getBegIndex(ts.getData(), TsMeta.BEG.load(ts.getMeta()));
            endIndexes[i] = getEndIndex(ts.getData(), TsMeta.END.load(ts.getMeta()));
        }
        return new TsFeatureHelper(begIndexes, endIndexes);
    }

    private final int[] begIndexes;
    private final int[] endIndexes;

    public boolean hasFeature(@Nonnull Feature feature, @Nonnegative int series, @Nonnegative int obs) {
        switch (feature) {
            case Backcasts:
                return obs < begIndexes[series];
            case Actual:
                return begIndexes[series] <= obs && obs <= endIndexes[series];
            case Forecasts:
                return endIndexes[series] < obs;
            case Public:
                return false;
            case Confidential:
                return false;
            default:
                return false;
        }
    }

    private static int getBegIndex(TsData data, LocalDateTime beg) {
        return beg == null ? Integer.MIN_VALUE : data.getStart().until(data.getStart().withDate(beg));
    }

    private static int getEndIndex(TsData data, LocalDateTime end) {
        return end == null ? Integer.MAX_VALUE : data.getStart().until(data.getStart().withDate(end));
    }
}
