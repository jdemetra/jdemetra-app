/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer.impl;

import demetra.ui.OldTsUtil;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataCollector;
import ec.tstoolkit.timeseries.simplets.TsDataTable;
import ec.tstoolkit.timeseries.simplets.TsDataTableInfo;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
class TsCollectionAnalyser {

    String[] titles;
    Date[] dates;
    Matrix data;

    public demetra.timeseries.TsCollection create() {
        if (titles == null || dates == null || data == null) {
            return null;
        }
        List<demetra.timeseries.Ts> coll = new ArrayList<>();
        for (int i = 1; i < titles.length; ++i) {
            TsDataCollector cur = new TsDataCollector();
            for (int j = 0; j < dates.length; ++j) {
                double val = data.get(j, i);
                if (!Double.isNaN(val)) {
                    cur.addObservation(dates[j], val);
                }
            }
            TsData data = cur.make(TsFrequency.Undefined, TsAggregationType.None);
            coll.add(OldTsUtil.toTs(titles[i], data));
        }
        return demetra.timeseries.TsCollection.of(coll);
    }

    void set(TsCollection col, boolean begin) {
        dates = null;
        data = null;
        titles = new String[col.getCount()];
        TsDataTable table = new TsDataTable();

        for (int i = 0; i < titles.length; ++i) {
            Ts cur = col.get(i);
            table.insert(-1, cur.getTsData());
            titles[i] = cur.getName();
        }

        TsDomain domain = table.getDomain();
        if (domain == null) {
            dates = new Date[0];
            data = new Matrix(0, 0);
            return;
        }
        dates = new Date[domain.getLength()];
        for (int i = 0; i < dates.length; ++i) {
            if (begin) {
                dates[i] = domain.get(i).firstday().getTime();
            } else {
                dates[i] = domain.get(i).lastday().getTime();
            }
        }
        data = new Matrix(dates.length, titles.length);
        data.set(Double.NaN);
        for (int i = 0; i < dates.length; ++i) {
            for (int j = 0; j < titles.length; ++j) {
                if (table.getDataInfo(i, j) == TsDataTableInfo.Valid) {
                    data.set(i, j, table.getData(i, j));
                }
            }
        }
    }
}
