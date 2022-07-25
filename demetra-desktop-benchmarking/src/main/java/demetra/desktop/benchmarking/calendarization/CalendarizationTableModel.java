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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Table model used to display the PeriodObs used for Calendarization
 *
 * @author Mats Maggi
 */
public class CalendarizationTableModel extends ListTableModel<PeriodObs> {

    private List<PeriodObs> data = new ArrayList<>();
    private final DecimalFormat doubleFormat = new DecimalFormat("###0.#######");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public CalendarizationTableModel(List<PeriodObs> obs) {
        data = obs;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    protected List<String> getColumnNames() {
        return Arrays.asList("From", "To", "Value", "Avg");
    }

    @Override
    protected List<PeriodObs> getValues() {
        return data;
    }

    @Override
    public Object getValueAt(int row, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return dateFormat.format(data.get(row).start.getTime());
            case 1:
                return dateFormat.format(data.get(row).end.getTime());
            case 2:
                return doubleFormat.format(data.get(row).value);
            case 3:
                int nbDays = data.get(row).end.difference(data.get(row).start) + 1;
                return doubleFormat.format(data.get(row).value / nbDays);
            default:
                throw new IllegalArgumentException("Wrong given column for table model (" + columnIndex + ")");
        }
    }

    @Override
    protected Object getValueAt(PeriodObs row, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return dateFormat.format(row.start);
            case 1:
                return dateFormat.format(row.end);
            case 2:
                return doubleFormat.format(row.value);
            case 3:
                int nbDays = row.end.difference(row.start) + 1;
                return doubleFormat.format(row.value / nbDays);
            default:
                throw new IllegalArgumentException("Wrong given column for table model (" + columnIndex + ")");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col != 2 || value == null) {
            return;
        }

        try {
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
            Number num = numberFormat.parse((String) value);
            double d = num.doubleValue();

            data.get(row).value = d;

            fireTableRowsUpdated(row, row);
        } catch (ParseException ex) {
            // Do nothing ?
        }
    }

}
