/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.datatransfer.ts;

import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDataTable;
import demetra.timeseries.TsDomain;
import demetra.timeseries.util.ObsCharacteristics;
import demetra.timeseries.util.ObsGathering;
import demetra.timeseries.util.TsDataBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import jdplus.math.matrices.FastMatrix;

/**
 *
 * @author Jean Palate
 */
class TsCollectionAnalyser {

    String[] titles;
    LocalDate[] dates;
    FastMatrix data;

    public List<Ts> create() {
        if (titles == null || dates == null || data == null) {
            return Collections.emptyList();
        }
        List<Ts> coll = new ArrayList<>();
        for (int i = 1; i < titles.length; ++i) {
            TsDataBuilder<LocalDate> cur = TsDataBuilder.byDate(ObsGathering.DEFAULT, ObsCharacteristics.ORDERED);
            for (int j = 0; j < dates.length; ++j) {
                double val = data.get(j, i);
                if (!Double.isNaN(val)) {
                    cur.add(dates[j], val);
                }
            }
            TsData data = cur.build();
            coll.add(Ts.of(titles[i], data));
        }
        return coll;
    }

    void set(TsCollection col, boolean begin) {
        dates = null;
        data = null;
        titles = new String[col.size()];

        TsDataTable table = TsDataTable.of(col, s -> s.getData());

        for (int i = 0; i < titles.length; ++i) {
            Ts cur = col.get(i);
            titles[i] = cur.getName();
        }

        TsDomain domain = table.getDomain();
        if (domain.isEmpty()) {
            dates = new LocalDate[0];
            data = FastMatrix.EMPTY;
            return;
        }
        dates = new LocalDate[domain.length()];
        for (int i = 0; i < dates.length; ++i) {
            if (begin) {
                dates[i] = domain.get(i).start().toLocalDate();
            } else {
                dates[i] = domain.get(i).end().toLocalDate().minusDays(1);
            }
        }
        data = FastMatrix.make(dates.length, titles.length);
        data.set(Double.NaN);
        TsDataTable.Cursor cursor = table.cursor(begin ? TsDataTable.DistributionType.FIRST : TsDataTable.DistributionType.LAST);
        for (int i = 0; i < dates.length; ++i) {
            for (int j = 0; j < titles.length; ++j) {
                cursor.moveTo(i, j);
                TsDataTable.ValueStatus status = cursor.getStatus();
                if (status == TsDataTable.ValueStatus.PRESENT) {
                    data.set(i, j, cursor.getValue());
                }
            }
        }
    }
}
