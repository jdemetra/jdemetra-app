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
package demetra.desktop.anomalydetection.html;

import demetra.desktop.anomalydetection.AnomalyItem;
import demetra.html.AbstractHtmlElement;
import demetra.html.HtmlElement;
import demetra.html.HtmlStream;
import demetra.html.HtmlTag;
import demetra.timeseries.regression.IOutlier;
import java.io.IOException;
import java.text.DecimalFormat;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.regsarima.regular.RegSarimaModel.RegressionDesc;

/**
 * Html report of a check last processed Ts
 *
 * @author Mats Maggi
 */
public class HtmlCheckLast extends AbstractHtmlElement implements HtmlElement {

    private final AnomalyItem anomalyItem;
    private final RegSarimaModel model;
    private final DecimalFormat df = new DecimalFormat("0.000");

    public HtmlCheckLast(AnomalyItem a, RegSarimaModel m) {
        anomalyItem = a;
        model = m;
    }

    @Override
    public void write(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER1, anomalyItem.getTs().getName()).newLine();       

        HtmlRegArima reg = new HtmlRegArima(model, false);
        reg.writeDetails(stream, false);
        RegressionDesc[] all = model.getDetails().getRegressionItems().stream()
                .filter(item->item.getCore() instanceof IOutlier)
                .toArray(n->new RegressionDesc[n]);
        stream.write(new HtmlOutliers(all));
    }
}
