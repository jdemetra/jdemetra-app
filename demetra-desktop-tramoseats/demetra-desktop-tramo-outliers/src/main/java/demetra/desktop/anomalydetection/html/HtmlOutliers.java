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

import demetra.desktop.anomalydetection.comparer.OutlierEstimationComparator;
import demetra.desktop.core.l2fprod.OutlierColorChooser;
import demetra.html.AbstractHtmlElement;
import static demetra.html.Bootstrap4.TEXT_CENTER;
import demetra.html.HtmlElement;
import demetra.html.HtmlStream;
import demetra.html.HtmlTable;
import demetra.html.HtmlTableCell;
import demetra.html.HtmlTableHeader;
import demetra.html.HtmlTag;
import demetra.timeseries.regression.IOutlier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdplus.regsarima.regular.RegSarimaModel.RegressionDesc;

/**
 * Html content displaying a table with all outliers information
 *
 * @author Mats Maggi
 */
public class HtmlOutliers extends AbstractHtmlElement implements HtmlElement {

    private final RegressionDesc[] outliers_;
    Map<String, OutlierPojo> map = new HashMap<>();

    public HtmlOutliers(RegressionDesc[] estimations) {
        outliers_ = estimations;
    }

    @Override
    public void write(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER3, "Outliers").newLine();
        stream.write("Number of outliers : " + outliers_.length).newLine().newLine();

        stream.open(new HtmlTable().withWidth(350));

        // Headers
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader("").withWidth(40));
        stream.write(new HtmlTableHeader("Period"));
        stream.write(new HtmlTableHeader("Value"));
        stream.write(new HtmlTableHeader("StdErr"));
        stream.write(new HtmlTableHeader("TStat"));
        stream.close(HtmlTag.TABLEROW);

        // Data
        Arrays.sort(outliers_, new OutlierEstimationComparator());

        for (RegressionDesc e : outliers_) {
            IOutlier outlier=(IOutlier) e.getCore();
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(outlier.getCode()).withWidth(40).withClass(OutlierColorChooser.getCodeClass(outlier.getCode())));
            stream.write(new HtmlTableCell(outlier.getPosition().toString()).withWidth(50));
            stream.write(new HtmlTableCell(df4.format(e.getCoef())).withWidth(80));
            stream.write(new HtmlTableCell(df4.format(e.getStderr())).withWidth(80));
            stream.write(new HtmlTableCell(df4.format(e.getTStat())).withWidth(80));
            stream.close(HtmlTag.TABLEROW);
        }

        stream.close(HtmlTag.TABLE);

        stream.newLine();
        stream.write(HtmlTag.HEADER3, "Summary").newLine();
        stream.open(new HtmlTable().withWidth(200));

        // Headers
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader("").withWidth(40));
        stream.write(new HtmlTableHeader("Number").withClass(TEXT_CENTER));
        stream.write(new HtmlTableHeader("Avg Value"));
        stream.close(HtmlTag.TABLEROW);

        // Data
        processOutliers();
        List<String> l = new ArrayList<>(map.keySet());
        for (String s : l) {
            stream.open(HtmlTag.TABLEROW);
            OutlierPojo o = map.get(s);

            stream.write(new HtmlTableCell(s).withWidth(40).withClass(OutlierColorChooser.getCodeClass(s)));
            stream.write(new HtmlTableCell(String.valueOf(o.getNumberOfValues())).withWidth(80).withClass(TEXT_CENTER));
            stream.write(new HtmlTableCell(df4.format(o.getAverageValue())).withWidth(80));
            stream.close(HtmlTag.TABLEROW);
        }
    }

    private static class OutlierPojo {

        private double totalValue;
        private int numberOfValues;

        public OutlierPojo() {
            totalValue = 0.0;
            numberOfValues = 0;
        }

        public void add(RegressionDesc e) {
            totalValue += e.getCoef();
            numberOfValues++;
        }

        public int getNumberOfValues() {
            return numberOfValues;
        }

        public double getAverageValue() {
            if (numberOfValues > 0) {
                return totalValue / (double) numberOfValues;
            } else {
                return 0.0;
            }
        }
    }

    private void processOutliers() {
        map.clear();
        map.put("AO", new OutlierPojo());
        map.put("LS", new OutlierPojo());
        map.put("TC", new OutlierPojo());
        map.put("SO", new OutlierPojo());

        for (RegressionDesc e : outliers_) {
            IOutlier outlier=(IOutlier) e.getCore();
           map.get(outlier.getCode()).add(e);
        }
    }
}
