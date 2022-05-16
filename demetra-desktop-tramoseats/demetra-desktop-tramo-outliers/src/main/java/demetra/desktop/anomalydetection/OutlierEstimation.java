/*
 * Copyright 2020 National Bank of Belgium
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
package demetra.desktop.anomalydetection;

import demetra.timeseries.TsDomain;
import demetra.timeseries.TsPeriod;
import demetra.timeseries.regression.IOutlier;
import java.time.LocalDateTime;
import jdplus.modelling.regression.RegressionDesc;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author palatej
 */
@lombok.Value
public class OutlierEstimation {
    
    private static final OutlierEstimation[] NO_OUTLIER=new OutlierEstimation[0];

    public static OutlierEstimation of(RegressionDesc desc, TsDomain estimationDomain) {
        IOutlier outlier = (IOutlier) desc.getCore();
        LocalDateTime pos = outlier.getPosition();
        int idx = estimationDomain.indexOf(pos);
        return new OutlierEstimation(desc.getCoef(), desc.getStderr(), desc.getPvalue(), outlier.getCode(), estimationDomain.get(idx), idx);
    }

    public static OutlierEstimation[] of(RegSarimaModel model) {
        TsDomain estimationDomain = model.getEstimation().getDomain();
        return model.getDetails().getRegressionItems().stream()
                .filter(desc->desc.getCore() instanceof IOutlier)
                .map(desc->of(desc, estimationDomain))
                .toArray(n->new OutlierEstimation[n]);
    }
    
    private double value, stderr, pvalue;
    private String code;
    private TsPeriod period;
    private int position;

    public double getTstat() {
        return value / stderr;
    }

}
