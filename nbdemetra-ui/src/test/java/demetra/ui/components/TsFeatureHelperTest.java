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
import demetra.timeseries.TsPeriod;
import demetra.tsprovider.Ts;
import static demetra.ui.components.TsFeatureHelper.Feature.*;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class TsFeatureHelperTest {

    @Test
    public void test() {

        Ts.Builder ts = Ts.builder().data(TsData.ofInternal(TsPeriod.monthly(2010, 1), new double[]{1, 2, 3}));

        assertThat(TsFeatureHelper.of(Arrays.asList(ts.build()))).satisfies(o -> {
            assertThat(o.hasFeature(Backcasts, 0, 0)).isFalse();
            assertThat(o.hasFeature(Actual, 0, 0)).isTrue();
            assertThat(o.hasFeature(Forecasts, 0, 0)).isFalse();
            assertThat(o.hasFeature(Backcasts, 0, 1)).isFalse();
            assertThat(o.hasFeature(Actual, 0, 1)).isTrue();
            assertThat(o.hasFeature(Forecasts, 0, 1)).isFalse();
            assertThat(o.hasFeature(Backcasts, 0, 2)).isFalse();
            assertThat(o.hasFeature(Actual, 0, 2)).isTrue();
            assertThat(o.hasFeature(Forecasts, 0, 2)).isFalse();
        });

        assertThat(TsFeatureHelper.of(Arrays.asList(ts.meta("@beg", "2010-02-01").build()))).satisfies(o -> {
            assertThat(o.hasFeature(Backcasts, 0, 0)).isTrue();
            assertThat(o.hasFeature(Actual, 0, 0)).isFalse();
            assertThat(o.hasFeature(Forecasts, 0, 0)).isFalse();
            assertThat(o.hasFeature(Backcasts, 0, 1)).isFalse();
            assertThat(o.hasFeature(Actual, 0, 1)).isTrue();
            assertThat(o.hasFeature(Forecasts, 0, 1)).isFalse();
            assertThat(o.hasFeature(Backcasts, 0, 2)).isFalse();
            assertThat(o.hasFeature(Actual, 0, 2)).isTrue();
            assertThat(o.hasFeature(Forecasts, 0, 2)).isFalse();
        });

        assertThat(TsFeatureHelper.of(Arrays.asList(ts.meta("@end", "2010-02-31").build()))).satisfies(o -> {
            assertThat(o.hasFeature(Backcasts, 0, 0)).isTrue();
            assertThat(o.hasFeature(Actual, 0, 0)).isFalse();
            assertThat(o.hasFeature(Forecasts, 0, 0)).isFalse();
            assertThat(o.hasFeature(Backcasts, 0, 1)).isFalse();
            assertThat(o.hasFeature(Actual, 0, 1)).isTrue();
            assertThat(o.hasFeature(Forecasts, 0, 1)).isFalse();
            assertThat(o.hasFeature(Backcasts, 0, 2)).isFalse();
            assertThat(o.hasFeature(Actual, 0, 2)).isFalse();
            assertThat(o.hasFeature(Forecasts, 0, 2)).isTrue();
        });
    }
}
