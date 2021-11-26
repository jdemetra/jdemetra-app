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

import demetra.desktop.anomalydetection.OutlierEstimation;
import demetra.desktop.anomalydetection.comparer.OutlierEstimationComparator;
import demetra.desktop.ui.properties.l2fprod.OutlierColorChooser;
import demetra.html.AbstractHtmlElement;
import demetra.html.Bootstrap4;
import demetra.html.HtmlElement;
import demetra.html.HtmlStream;
import demetra.html.HtmlTable;
import demetra.html.HtmlTableCell;
import demetra.html.HtmlTableHeader;
import demetra.html.HtmlTag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Html content displaying a table with all outliers information
 *
 * @author Mats Maggi
 */
public class HtmlOutliers extends AbstractHtmlElement implements HtmlElement {

    private final OutlierEstimation[] outliers_;
    Map<String, OutlierPojo> map = new HashMap<>();

    public HtmlOutliers(OutlierEstimation[] estimations) {
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

        for (OutlierEstimation e : outliers_) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(e.getCode()).withWidth(40).withClass(OutlierColorChooser.getCodeClass(e.getCode())));
            stream.write(new HtmlTableCell(e.getPeriod().display()).withWidth(50));
            stream.write(new HtmlTableCell(df4.format(e.getValue())).withWidth(80));
            stream.write(new HtmlTableCell(df4.format(e.getStderr())).withWidth(80));
            stream.write(new HtmlTableCell(df4.format(e.getTstat())).withWidth(80));
            stream.close(HtmlTag.TABLEROW);
        }

        stream.close(HtmlTag.TABLE);

        stream.newLine();
        stream.write(HtmlTag.HEADER3, "Summary").newLine();
        stream.open(new HtmlTable().withWidth(200));

        // Headers
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader("").withWidth(40));
        stream.write(new HtmlTableHeader("Number").withClass(Bootstrap4.TEXT_CENTER));
        stream.write(new HtmlTableHeader("Avg Value"));
        stream.close(HtmlTag.TABLEROW);

        // Data
        processOutliers();
        List<String> l = new ArrayList<>(map.keySet());
        for (String s : l) {
            stream.open(HtmlTag.TABLEROW);
            OutlierPojo o = map.get(s);

            stream.write(new HtmlTableCell(s).withWidth(40).withClass(OutlierColorChooser.getCodeClass(s)));
            stream.write(new HtmlTableCell(String.valueOf(o.getNumberOfValues())).withWidth(80).withClass(Bootstrap4.TEXT_CENTER));
            stream.write(new HtmlTableCell(df4.format(o.getMaxT())).withWidth(80));
            stream.close(HtmlTag.TABLEROW);
        }
    }

    private static class OutlierPojo {

        private double maxT;
        private int numberOfValues;

        public OutlierPojo() {
            maxT = 0.0;
            numberOfValues = 0;
        }

        public void add(OutlierEstimation e) {
            if (Math.abs(e.getTstat()) > Math.abs(maxT)) {
                maxT = e.getTstat();
            }
            numberOfValues++;
        }

        public int getNumberOfValues() {
            return numberOfValues;
        }

        public double getMaxT() {
            return maxT;
        }
    }

    private void processOutliers() {
        map.clear();
        map.put("AO", new OutlierPojo());
        map.put("LS", new OutlierPojo());
        map.put("TC", new OutlierPojo());
        map.put("SO", new OutlierPojo());

        for (OutlierEstimation e : outliers_) {
            map.get(e.getCode()).add(e);
        }
    }
}
