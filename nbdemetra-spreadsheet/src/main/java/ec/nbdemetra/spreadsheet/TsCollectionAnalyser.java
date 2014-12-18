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
package ec.nbdemetra.spreadsheet;

import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tstoolkit.maths.matrices.Matrix;
import ec.tstoolkit.timeseries.simplets.TsDataTable;
import ec.tstoolkit.timeseries.simplets.TsDataTableInfo;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import java.util.Date;

/**
 *
 * @author pcuser
 */
class TsCollectionAnalyser {

    String[] titles;
    Date[] dates;
    Matrix data;

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
