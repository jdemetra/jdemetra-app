/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.tss.datatransfer.impl;

import demetra.ui.TsManager;
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
import java.util.Date;

/**
 *
 * @author Jean Palate
 */
class TsCollectionAnalyser {

    String[] titles;
    Date[] dates;
    Matrix data;

    public TsCollection create() {
        if (titles == null || dates == null || data == null) {
            return null;
        }
        TsCollection coll = TsManager.getDefault().newTsCollection();
        for (int i = 1; i < titles.length; ++i) {
            TsDataCollector cur = new TsDataCollector();
            for (int j = 0; j < dates.length; ++j) {
                double val = data.get(j, i);
                if (!Double.isNaN(val)) {
                    cur.addObservation(dates[j], val);
                }
            }
            TsData sdata = cur.make(TsFrequency.Undefined, TsAggregationType.None);
            Ts s = TsManager.getDefault().newTsWithName(titles[i]);
            s.set(sdata);
            coll.quietAdd(s);
        }
        return coll;
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
