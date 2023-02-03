/*
 * Copyright 2023 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.sa.descriptors.highfreq.HighFreqSpecUI;
import demetra.highfreq.ExtendedAirlineModellingSpec;
import demetra.highfreq.ExtendedAirlineSpec;
import demetra.modelling.highfreq.EasterSpec;
import demetra.modelling.highfreq.EstimateSpec;
import demetra.modelling.highfreq.HolidaysSpec;
import demetra.modelling.highfreq.OutlierSpec;
import demetra.modelling.highfreq.RegressionSpec;
import demetra.modelling.highfreq.SeriesSpec;
import demetra.modelling.highfreq.TransformSpec;

/**
 *
 * @author Jean Palate
 */
@lombok.Getter
@lombok.AllArgsConstructor
public class FractionalAirlineSpecRoot implements HighFreqSpecUI {

    ExtendedAirlineModellingSpec core;
    boolean ro;

    @Override
    public boolean hasFixedCoefficients() {
        return core.getRegression().hasFixedCoefficients();
    }

    public void update(ExtendedAirlineSpec spec) {
        core = core.toBuilder().stochastic(spec).build();
    }

    @Override
    public void update(EstimateSpec spec) {
        core = core.toBuilder().estimate(spec).build();
    }

    @Override
    public void update(OutlierSpec spec) {
        core = core.toBuilder().outlier(spec).build();
    }

    @Override
    public void update(RegressionSpec spec) {
        core = core.toBuilder().regression(spec).build();
    }

    @Override
    public void update(TransformSpec spec) {
        core = core.toBuilder().transform(spec).build();
    }

    @Override
    public void update(SeriesSpec spec) {
        core = core.toBuilder().series(spec).build();
    }

    @Override
    public void update(EasterSpec spec) {
        update(core.getRegression()
                .toBuilder()
                .easter(spec)
                .build());
    }

    @Override
    public void update(HolidaysSpec spec) {
        update(core.getRegression()
                .toBuilder()
                .holidays(spec)
                .build());
    }

    @Override
    public TransformSpec transform() {
        return core.getTransform();
    }

    @Override
    public OutlierSpec outlier() {
        return core.getOutlier();
    }

}
