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
package ec.nbdemetra.anomalydetection.report;

import ec.nbdemetra.anomalydetection.AnomalyItem;
import ec.nbdemetra.anomalydetection.report.AnomalyPojo.Status;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Mats Maggi
 */
public class CheckLastReportAction {

    public static void process(List<AnomalyItem> items, Map parameters) {
        AnomalyPojoComparator comparator = null;
        ICheckLastReportFactory factory = null;

        NotifyDescriptor d = new NotifyDescriptor(
                new CheckLastSortPanel(),
                "Select sorting of time series",
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE,
                null,
                null);

        Object retValue = DialogDisplayer.getDefault().notify(d);

        if (retValue == NotifyDescriptor.CANCEL_OPTION
                || retValue == NotifyDescriptor.CLOSED_OPTION) {
            return;
        } else {
            comparator = ((CheckLastSortPanel) d.getMessage()).getComparator();
            factory = ((CheckLastSortPanel) d.getMessage()).getFactory();
        }

        List<AnomalyPojo> valid = new ArrayList<>();
        List<AnomalyPojo> invalid = new ArrayList<>();
        List<AnomalyPojo> empty = new ArrayList<>();
        for (AnomalyItem item : items) {
            for (int j = 0; j < item.getBackCount(); j++) {
                if (item.getTsData() != null) {
                    if (item.isNotProcessable()) {
                        AnomalyPojo p = new AnomalyPojo(item.getTs().getName(),
                                null,
                                Double.NaN,
                                Double.NaN,
                                Status.Invalid);
                        invalid.add(p);
                    } else if (item.isInvalid()) {
                        AnomalyPojo p = new AnomalyPojo(item.getTs().getName(),
                                null,
                                Double.NaN,
                                Double.NaN,
                                Status.Empty);
                        empty.add(p);
                    } else {

                        if (parameters.containsKey("_ORANGE_CELLS")
                                && Math.abs(item.getRelativeError(j)) >= ((Double) parameters.get("_ORANGE_CELLS"))) {
                            AnomalyPojo p = new AnomalyPojo(item.getTs().getName(),
                                    item.getTs().getTsData().getLastPeriod().minus(j),
                                    item.getRelativeError(j),
                                    item.getAbsoluteError(j),
                                    Status.Valid);
                            valid.add(p);
                        }
                    }

                }
            }
        }

        // Data passed by parameter and not datasource
        parameters.put("_VALID", valid);
        parameters.put("_INVALID", invalid);
        parameters.put("_EMPTY", empty);
        parameters.put("_NB_ANOMALY", valid.size());

        if (comparator != null) {
            valid.sort(comparator);
            parameters.put("_SORTING", comparator.toString());
        } else {
            parameters.put("_SORTING", "Original order");
        }

        if (factory != null) {
            factory.createReport(parameters);
        }
    }
}
