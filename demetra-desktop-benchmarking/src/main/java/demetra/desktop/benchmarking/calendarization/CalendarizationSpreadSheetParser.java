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
package demetra.desktop.benchmarking.calendarization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Parser retrieving information about Calendarization from a selected range of
 * spreadsheet's cells
 *
 * @author Mats Maggi
 */
public class CalendarizationSpreadSheetParser {

    public List<Calendarization.PeriodObs> parse(Table<?> data) throws IOException {

        List<Calendarization.PeriodObs> observations = new ArrayList<>();

        if (!isValid(data)) {
            throw new IllegalArgumentException("Given book is not valid !");
        }

        Day start, end;
        double value;

        try {
            for (int i = 0; i < data.getRowsCount(); i++) {
                if (data.get(i, 0) != null || data.get(i, 1) != null || data.get(i, 2) != null) {
                    start = new Day((Date) data.get(i, 0));
                    end = new Day((Date) data.get(i, 1));
                    value = ((Number) data.get(i, 2)).doubleValue();

                    observations.add(new Calendarization.PeriodObs(start, end, value));
                }
            }
        } catch (ClassCastException | NullPointerException e) {
            throw new IllegalArgumentException("Some of the given data (or their format) are not valid !");
        }

        return observations;
    }

    private boolean isValid(Table<?> t) throws IOException {
        return t.getColumnsCount() == 3 && t.getRowsCount() != 0;
    }
}
