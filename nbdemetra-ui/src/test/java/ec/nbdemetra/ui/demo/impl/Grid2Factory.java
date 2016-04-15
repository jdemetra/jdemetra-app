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
package ec.nbdemetra.ui.demo.impl;

import com.google.common.collect.ImmutableMap;
import ec.nbdemetra.ui.demo.DemoComponentFactory;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.Formatters.Formatter;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.util.grid.swing.AbstractGridModel;
import ec.util.grid.swing.GridModel;
import ec.util.grid.swing.JGrid;
import java.awt.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = DemoComponentFactory.class)
public final class Grid2Factory extends DemoComponentFactory {

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return builder().put(new LinearId("(2) Other", "JGrid*"), Grid2Factory::excelGrid).build();
    }

    private static Component excelGrid() {
        JGrid result = new JGrid();
        result.setModel(new ExcelModel());
        result.setRowSelectionAllowed(true);
        result.setColumnSelectionAllowed(true);
        result.setDragEnabled(true);
        result.setDefaultRenderer(Object.class, new ExcelCellRenderer(result));
        return result;
    }

    private static final class ExcelModel extends AbstractGridModel implements GridModel {

        int rowCount = 300;
        int colCount = 300;
        Map<String, Object> data = new HashMap<>();

        ExcelModel() {
            data = ImmutableMap.<String, Object>builder()
                    .put(key(2, 2), "hello")
                    .put(key(2, 4), 123.5)
                    .put(key(5, 4), true)
                    .put(key(4, 3), new Date())
                    .build();
        }

        static String key(int rowIndex, int columnIndex) {
            return rowIndex + "x" + columnIndex;
        }

        @Override
        public int getRowCount() {
            return rowCount;
        }

        @Override
        public String getRowName(int rowIndex) {
            return Integer.toString(rowIndex + 1);
        }

        @Override
        public int getColumnCount() {
            return colCount;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.get(key(rowIndex, columnIndex));
        }
    }

    private static final class ExcelCellRenderer implements TableCellRenderer {

        final TableCellRenderer delegate;
        final Formatter<Number> numberFormatter = DataFormat.DEFAULT.numberFormatter();
        final Formatter<Date> dateFormatter = DataFormat.DEFAULT.dateFormatter();

        public ExcelCellRenderer(JGrid grid) {
            this.delegate = grid.getDefaultRenderer(Object.class);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) {
                result.setText(numberFormatter.formatAsString((Number) value));
                result.setHorizontalAlignment(JLabel.TRAILING);
            } else if (value instanceof Date) {
                result.setText(dateFormatter.formatAsString((Date) value));
                result.setHorizontalAlignment(JLabel.LEADING);
            } else if (value instanceof CharSequence) {
                result.setHorizontalAlignment(JLabel.LEADING);
            } else if (value instanceof Boolean) {
                result.setText(((Boolean) value) ? "TRUE" : "FALSE");
                result.setHorizontalAlignment(JLabel.CENTER);
            }
            return result;
        }
    }
}
