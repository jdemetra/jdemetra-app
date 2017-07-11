/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package ec.util.grid.swing.ext;

import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.IFormatter;
import ec.util.chart.ColorScheme;
import ec.util.chart.swing.SwingColorSchemeSupport;
import ec.util.spreadsheet.Cell;
import ec.util.spreadsheet.helpers.CellRefHelper;
import java.awt.Color;
import java.awt.Component;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Philippe Charles
 */
public final class SheetCellRenderer implements TableCellRenderer {

    private final IFormatter<Number> numberFormat;
    private final IFormatter<Date> dateFormat;
    private final SwingColorSchemeSupport colorSchemeSupport;
    private final boolean invertColors;
    private final DefaultTableCellRenderer2 delegate;

    public SheetCellRenderer(@Nonnull DataFormat dataFormat, @Nullable SwingColorSchemeSupport colorSchemeSupport, boolean invertColors) {
        this.numberFormat = dataFormat.numberFormatter();
        this.dateFormat = dataFormat.dateFormatter();
        this.colorSchemeSupport = colorSchemeSupport;
        this.invertColors = invertColors;
        this.delegate = new DefaultTableCellRenderer2() {
            @Override
            protected void configure(JToolTip toolTip) {
                if (SheetCellRenderer.this.colorSchemeSupport != null) {
                    toolTip.setForeground(getBackground());
                    toolTip.setBackground(getForeground());
                }
            }
        };
        delegate.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel result = (JLabel) delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ColorScheme.KnownColor cellColor = ColorScheme.KnownColor.GRAY;
        Cell cell = (Cell) value;
        if (cell == null) {
            result.setText("");
            result.setToolTipText("<html>" + CellRefHelper.getCellRef(row, column) + ": Null");
            cellColor = ColorScheme.KnownColor.GRAY;
        } else if (cell.isDate()) {
            result.setText(dateFormat.formatAsString(cell.getDate()));
            result.setToolTipText("<html>" + CellRefHelper.getCellRef(row, column) + ": Date<br>" + result.getText());
            result.setHorizontalAlignment(JLabel.TRAILING);
            cellColor = ColorScheme.KnownColor.RED;
        } else if (cell.isNumber()) {
            result.setText(numberFormat.formatAsString(cell.getNumber()));
            result.setToolTipText("<html>" + CellRefHelper.getCellRef(row, column) + ": Number<br>" + result.getText());
            result.setHorizontalAlignment(JLabel.TRAILING);
            cellColor = ColorScheme.KnownColor.GREEN;
        } else if (cell.isString()) {
            result.setText(cell.getString());
            result.setToolTipText("<html>" + CellRefHelper.getCellRef(row, column) + ": String<br>" + result.getText());
            result.setHorizontalAlignment(JLabel.LEADING);
            cellColor = ColorScheme.KnownColor.BLUE;
        }
        if (colorSchemeSupport != null) {
            result.setBackground(colorSchemeSupport.getPlotColor());
            result.setForeground(colorSchemeSupport.getLineColor(cellColor));
        }
        if ((isSelected && colorSchemeSupport != null) ^ (invertColors)) {
            Color saved = result.getForeground();
            result.setForeground(result.getBackground());
            result.setBackground(saved);
        }
        return result;
    }

    private static abstract class DefaultTableCellRenderer2 extends DefaultTableCellRenderer {

        private final JToolTip toolTip = super.createToolTip();

        @Override
        public JToolTip createToolTip() {
            configure(toolTip);
            return toolTip;
        }

        abstract protected void configure(JToolTip toolTip);
    }
}
