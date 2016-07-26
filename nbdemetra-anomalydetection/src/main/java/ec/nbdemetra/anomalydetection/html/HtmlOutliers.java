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
package ec.nbdemetra.anomalydetection.html;

import ec.nbdemetra.anomalydetection.comparer.OutlierEstimationComparator;
import ec.nbdemetra.ui.properties.l2fprod.ColorChooser;
import ec.tss.html.AbstractHtmlElement;
import ec.tss.html.HtmlStream;
import ec.tss.html.HtmlStyle;
import static ec.tss.html.HtmlStyle.Black;
import static ec.tss.html.HtmlStyle.CustomDark;
import static ec.tss.html.HtmlStyle.CustomLight;
import ec.tss.html.HtmlTable;
import ec.tss.html.HtmlTableCell;
import ec.tss.html.HtmlTableHeader;
import ec.tss.html.HtmlTag;
import ec.tss.html.IHtmlElement;
import ec.tstoolkit.timeseries.regression.OutlierEstimation;
import ec.tstoolkit.timeseries.regression.OutlierType;
import static ec.tstoolkit.timeseries.regression.OutlierType.AO;
import static ec.tstoolkit.timeseries.regression.OutlierType.LS;
import static ec.tstoolkit.timeseries.regression.OutlierType.TC;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Html content displaying a table with all outliers information
 *
 * @author Mats Maggi
 */
public class HtmlOutliers extends AbstractHtmlElement implements IHtmlElement {

    private OutlierEstimation[] outliers_;
    Map<String, OutlierPojo> map = new HashMap<>();

    public HtmlOutliers(OutlierEstimation[] estimations) {
        outliers_ = estimations;
    }

    @Override
    public void write(HtmlStream stream) throws IOException {
        stream.write(HtmlTag.HEADER3, h3, "Outliers").newLine();
        stream.write("Number of outliers : " + outliers_.length).newLine().newLine();

        stream.open(new HtmlTable(0, 350));

        // Headers
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader("", 40));
        stream.write(new HtmlTableHeader("Period", HtmlStyle.Bold));
        stream.write(new HtmlTableHeader("Value", HtmlStyle.Bold));
        stream.write(new HtmlTableHeader("StdErr", HtmlStyle.Bold));
        stream.write(new HtmlTableHeader("TStat", HtmlStyle.Bold));
        stream.close(HtmlTag.TABLEROW);

        // Data
        Arrays.sort(outliers_, new OutlierEstimationComparator());

        for (OutlierEstimation e : outliers_) {
            stream.open(HtmlTag.TABLEROW);
            stream.write(new HtmlTableCell(e.getCode(), 40, HtmlStyle.Center, getForeground(e.getCode())), ColorChooser.getBgHexColor(e.getCode()));
            stream.write(new HtmlTableCell(e.getPosition().toString(), 50));
            stream.write(new HtmlTableCell(df4.format(e.getValue()), 80));
            stream.write(new HtmlTableCell(df4.format(e.getStdev()), 80));
            stream.write(new HtmlTableCell(df4.format(e.getTStat()), 80));
            stream.close(HtmlTag.TABLEROW);
        }

        stream.close(HtmlTag.TABLE);

        stream.newLine();
        stream.write(HtmlTag.HEADER3, h3, "Summary").newLine();
        stream.open(new HtmlTable(0, 200));

        // Headers
        stream.open(HtmlTag.TABLEROW);
        stream.write(new HtmlTableHeader("", 40));
        stream.write(new HtmlTableHeader("Number", HtmlStyle.Bold, HtmlStyle.Center));
        stream.write(new HtmlTableHeader("Avg Value", HtmlStyle.Bold));
        stream.close(HtmlTag.TABLEROW);

        // Data
        processOutliers();
        List<String> l = new ArrayList<>(map.keySet());
        for (int i = 0; i < l.size(); i++) {
            stream.open(HtmlTag.TABLEROW);
            OutlierPojo o = map.get(l.get(i));

            stream.write(new HtmlTableCell(l.get(i).toString(), 40, HtmlStyle.Center, getForeground(l.get(i))), ColorChooser.getBgHexColor(l.get(i)));
            stream.write(new HtmlTableCell(String.valueOf(o.getNumberOfValues()), 80, HtmlStyle.Center));
            stream.write(new HtmlTableCell(df4.format(o.getAverageValue()), 80));
            stream.close(HtmlTag.TABLEROW);
        }
    }

    public static HtmlStyle getForeground(String t) {
        switch (t) {
            case "AO":
                return CustomDark;
            case "LS":
            case "TC":
                return CustomLight;
            default:
                return Black;
        }
    }

    private static class OutlierPojo {

        private double totalValue;
        private int numberOfValues;

        public OutlierPojo() {
            totalValue = 0.0;
            numberOfValues = 0;
        }

        public void add(OutlierEstimation e) {
            totalValue += e.getValue();
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

        for (OutlierEstimation e : outliers_) {
            map.get(e.getCode()).add(e);
        }
    }
}
