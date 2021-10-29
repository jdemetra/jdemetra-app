/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package demetra.desktop.anomalydetection.comparer;

import demetra.desktop.anomalydetection.OutlierEstimation;
import java.util.Comparator;

/**
 * Comparator for OutlierEstimation
 * Allow a sort of the Outlier estimations by TsPeriod 
 * @author Mats Maggi
 */
public class OutlierEstimationComparator implements Comparator<OutlierEstimation> {

    @Override
    public int compare(OutlierEstimation o1, OutlierEstimation o2) {
        return Integer.compare(o1.getPosition(), o2.getPosition());
    }
}
