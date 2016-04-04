/*
 * Copyright 2013 National Bank of Belgium
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
package ec.util.chart;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 *
 * @author Philippe Charles
 */
@Immutable
public final class ObsIndex {

    @Nonnull
    public static final ObsIndex NULL = new ObsIndex(-1, -1);

    @Nonnull
    public static ObsIndex valueOf(int series, int obs) {
        return NULL.equals(series, obs) ? NULL : new ObsIndex(series, obs);
    }

    private final int series;
    private final int obs;

    private ObsIndex(int series, int obs) {
        this.series = series;
        this.obs = obs;
    }

    public int getSeries() {
        return series;
    }

    public int getObs() {
        return obs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(series, obs);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof ObsIndex && equals((ObsIndex) obj));
    }

    private boolean equals(ObsIndex that) {
        return equals(that.series, that.obs);
    }

    public boolean equals(int series, int obs) {
        return this.series == series && this.obs == obs;
    }

    @Override
    public String toString() {
        return series + "x" + obs;
    }
}
