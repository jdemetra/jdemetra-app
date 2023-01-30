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
package demetra.desktop.stl.ui;

import demetra.desktop.sa.descriptors.regular.RegularSpecUI;
import demetra.modelling.TransformationType;
import demetra.modelling.regular.CalendarSpec;
import demetra.modelling.regular.EasterSpec;
import demetra.modelling.regular.EstimateSpec;
import demetra.modelling.regular.ModellingSpec;
import demetra.modelling.regular.OutlierSpec;
import demetra.modelling.regular.RegressionSpec;
import demetra.modelling.regular.SeriesSpec;
import demetra.modelling.regular.TradingDaysSpec;
import demetra.modelling.regular.TransformSpec;
import demetra.sa.benchmarking.SaBenchmarkingSpec;
import demetra.stl.StlPlusSpec;
import demetra.stl.StlSpec;
import demetra.timeseries.calendars.LengthOfPeriodType;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Jean Palate
 */
@lombok.Getter
public class StlPlusSpecRoot implements RegularSpecUI {

    @NonNull
    ModellingSpec preprocessing;
    StlSpec stl;
    @NonNull
    SaBenchmarkingSpec benchmarking;
    boolean ro;

    public StlPlusSpecRoot(StlPlusSpec spec, boolean ro) {
        this.preprocessing = spec.getPreprocessing();
        this.stl = spec.getStl();
        this.benchmarking = spec.getBenchmarking();
        this.ro = ro;
    }

    public StlPlusSpec getCore() {
        return StlPlusSpec.builder()
                .preprocessing(preprocessing)
                .stl(stl)
                .benchmarking(benchmarking)
                .build();
    }

    @Override
    public boolean hasFixedCoefficients() {
        return preprocessing.getRegression().hasFixedCoefficients();
    }

    public void update(StlSpec spec) {
        stl = spec;
    }

    @Override
    public boolean isAdjust() {
        return preprocessing.getTransform().getAdjust() != LengthOfPeriodType.None;
    }

    @Override
    public TradingDaysSpec td() {
        return preprocessing.getRegression().getCalendar().getTradingDays();
    }

    @Override
    public demetra.modelling.regular.TransformSpec transform() {
        return preprocessing.getTransform();
    }

    @Override
    public demetra.modelling.regular.OutlierSpec outlier() {
        return preprocessing.getOutliers();
    }

    @Override
    public void update(EstimateSpec spec) {
        preprocessing = preprocessing.toBuilder().estimate(spec).build();
    }

    @Override
    public void update(OutlierSpec spec) {
        preprocessing = preprocessing.toBuilder().outliers(spec).build();
    }

    @Override
    public void update(RegressionSpec spec) {
        preprocessing = preprocessing.toBuilder().regression(spec).build();
    }

    @Override
    public void update(TransformSpec spec) {
        preprocessing = preprocessing.toBuilder().transform(spec).build();
    }

    @Override
    public void update(SeriesSpec spec) {
        preprocessing = preprocessing.toBuilder().series(spec).build();
    }

    @Override
    public void update(EasterSpec spec) {
        CalendarSpec calendar = preprocessing.getRegression().getCalendar();
        calendar = calendar.toBuilder().easter(spec).build();

        preprocessing = preprocessing.toBuilder()
                .regression(preprocessing.getRegression().toBuilder()
                        .calendar(calendar)
                        .build()
                ).build();
    }

    @Override
    public void update(TradingDaysSpec spec) {
        CalendarSpec calendar = preprocessing.getRegression().getCalendar();
        calendar = calendar.toBuilder().tradingDays(spec).build();

        preprocessing = preprocessing.toBuilder()
                .regression(preprocessing.getRegression().toBuilder()
                        .calendar(calendar)
                        .build()
                ).build();
    }
}
