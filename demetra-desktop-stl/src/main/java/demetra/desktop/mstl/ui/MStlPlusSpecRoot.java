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
package demetra.desktop.mstl.ui;

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
import demetra.stl.MStlPlusSpec;
import demetra.stl.MStlSpec;

/**
 *
 * @author Jean Palate
 */
@lombok.Getter
@lombok.AllArgsConstructor
public class MStlPlusSpecRoot implements HighFreqSpecUI {

    MStlPlusSpec core;
    boolean ro;

    public boolean isPreprocessingEnabled() {
        return core.getPreprocessing().isEnabled();
    }

    public void setPreprocessingEnabled(boolean enabled) {
        update(getPreprocessing().toBuilder().enabled(enabled).build());
    }

    public ExtendedAirlineModellingSpec getPreprocessing() {
        return core.getPreprocessing();
    }
    
    public MStlSpec stl(){
        return core.getStl();
    }
    
    public void update(MStlSpec stl){
        core=core.toBuilder().stl(stl).build();
    }

    public void update(ExtendedAirlineModellingSpec spec) {
        core = core.toBuilder().preprocessing(spec).build();
    }

    @Override
    public boolean hasFixedCoefficients() {
        return core.getPreprocessing().getRegression().hasFixedCoefficients();
    }

    public void update(ExtendedAirlineSpec spec) {
        update(getPreprocessing().toBuilder().stochastic(spec).build());
    }

    @Override
    public void update(EstimateSpec spec) {
        update(getPreprocessing().toBuilder().estimate(spec).build());
    }

    @Override
    public void update(OutlierSpec spec) {
        update(getPreprocessing().toBuilder().outlier(spec).build());
    }

    @Override
    public void update(RegressionSpec spec) {
        update(getPreprocessing().toBuilder().regression(spec).build());
    }

    @Override
    public void update(TransformSpec spec) {
        update(getPreprocessing().toBuilder().transform(spec).build());
    }

    @Override
    public void update(SeriesSpec spec) {
        update(getPreprocessing().toBuilder().series(spec).build());
    }

    @Override
    public void update(EasterSpec spec) {
        update(getPreprocessing().getRegression()
                .toBuilder()
                .easter(spec)
                .build());
    }

    @Override
    public void update(HolidaysSpec spec) {
        update(getPreprocessing().getRegression()
                .toBuilder()
                .holidays(spec)
                .build());
    }

    @Override
    public TransformSpec transform() {
        return getPreprocessing().getTransform();
    }

    @Override
    public OutlierSpec outlier() {
        return getPreprocessing().getOutlier();
    }

}
