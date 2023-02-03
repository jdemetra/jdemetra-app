/*
 * Copyright 2022 National Bank of Belgium
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
package demetra.desktop.sa.descriptors.regular;

import demetra.modelling.regular.CalendarSpec;
import demetra.modelling.regular.EasterSpec;
import demetra.modelling.regular.EstimateSpec;
import demetra.modelling.regular.ModellingSpec;
import demetra.modelling.regular.OutlierSpec;
import demetra.modelling.regular.RegressionSpec;
import demetra.modelling.regular.SeriesSpec;
import demetra.modelling.regular.TradingDaysSpec;
import demetra.modelling.regular.TransformSpec;

/**
 *
 * @author palatej
 */
public interface RegularSpecUI {

    boolean isRo();

    default boolean hasFixedCoefficients() {
        return preprocessing().getRegression().hasFixedCoefficients();
    }

    default boolean isAdjust() {
        return preprocessing().getRegression().hasFixedCoefficients();
    }

    default boolean isPreprocessing() {
        return preprocessing().isEnabled();
    }

    ModellingSpec preprocessing();

    default TradingDaysSpec td() {
        return preprocessing().getRegression().getCalendar().getTradingDays();
    }

    default TransformSpec transform() {
        return preprocessing().getTransform();
    }

    default OutlierSpec outlier() {
        return preprocessing().getOutliers();
    }

    void update(ModellingSpec spec);

    default void update(EstimateSpec spec) {
        update(preprocessing().toBuilder().estimate(spec).build());
    }

    default void update(OutlierSpec spec) {
        update(preprocessing().toBuilder().outliers(spec).build());
    }

    default void update(RegressionSpec spec) {
        update(preprocessing().toBuilder().regression(spec).build());

    }

    default void update(TransformSpec spec) {
        update(preprocessing().toBuilder().transform(spec).build());
    }

    default void update(SeriesSpec spec) {
        update(preprocessing().toBuilder().series(spec).build());
    }

    default void update(EasterSpec spec) {
        ModellingSpec preprocessing = preprocessing();
        CalendarSpec calendar = preprocessing.getRegression().getCalendar();
        calendar = calendar.toBuilder().easter(spec).build();
        update(preprocessing.toBuilder()
                .regression(preprocessing.getRegression().toBuilder()
                        .calendar(calendar)
                        .build()
                ).build());
    }

    default void update(TradingDaysSpec spec) {
        ModellingSpec preprocessing = preprocessing();
        CalendarSpec calendar = preprocessing.getRegression().getCalendar();
        calendar = calendar.toBuilder().tradingDays(spec).build();
        update(preprocessing.toBuilder()
                .regression(preprocessing.getRegression().toBuilder()
                        .calendar(calendar)
                        .build()
                ).build());
    }
}
