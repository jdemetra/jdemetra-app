/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.chart;

import ec.tss.Ts;
import ec.tstoolkit.design.Status;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * TODO: merge this with TsXYDatasets?
 *
 * @author Philippe Charles
 */
@Status(level = Status.Level.Initial)
public class DataFeatureModel {

    private final Ts.DataFeature[] features = Ts.DataFeature.values();
    private boolean[][][] internalData = {};

    public void setData(Ts[] tss) {
        internalData = new boolean[features.length][tss.length][];
        for (int f = 0; f < features.length; f++) {
            Ts.DataFeature feature = features[f];
            for (int series = 0; series < tss.length; series++) {
                Ts ts = tss[series];
                TsData data = ts.getTsData();
                if (data != null && ts.isFeature(feature)) {
                    internalData[f][series] = new boolean[data.getLength()];
                    TsDomain mainDomain = data.getDomain();
                    TsDomain subDomain = data.select(ts.getSelector(feature)).getDomain();
                    if (!subDomain.isEmpty()) {
                        int first = mainDomain.search(subDomain.getStart());
                        int end = first + subDomain.getLength();
                        //                        System.out.println("Feature=" + i + ", Ts=" + j + " [" + min + ", " + max + "]");
                        Arrays.fill(internalData[f][series], first, end, true);
                    }
                }
            }
        }
    }

    public boolean hasFeature(Ts.DataFeature feature, int series, int obs) {
        if (internalData.length <= feature.ordinal()) {
            return false;
        }
        boolean[][] tmp = internalData[feature.ordinal()];
        if (tmp == null || tmp.length <= series) {
            return false;
        }
        boolean[] cbool = tmp[series];
        if (cbool == null || cbool.length <= obs) {
            return false;
        }
        return cbool[obs];
    }

    public EnumSet<Ts.DataFeature> getFeatures(int series, int obs) {
        List<Ts.DataFeature> result = new ArrayList(features.length);
        for (int f = 0; f < features.length; f++) {
            if (internalData[f][series] != null && internalData[f][series][obs]) {
                result.add(features[f]);
            }
        }
        return EnumSet.copyOf(result);
    }
}
