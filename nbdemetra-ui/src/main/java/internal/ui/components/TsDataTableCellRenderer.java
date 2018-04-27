/*
 * Copyright 2018 National Bank of Belgium
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
package internal.ui.components;

import demetra.ui.components.HasObsFormat;
import ec.nbdemetra.ui.DemetraUI;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.Formatters;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.chart.TsSparklineCellRenderer;
import ec.util.various.swing.StandardSwingColor;
import java.awt.Component;
import java.util.Objects;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Philippe Charles
 */
public final class TsDataTableCellRenderer implements TableCellRenderer {

    private final HasObsFormat target;
    private final DemetraUI demetraUI;
    private final TsSparklineCellRenderer dataRenderer;
    private final DefaultTableCellRenderer labelRenderer;

    private DataFormat currentFormat;
    private Formatters.Formatter<Number> currentFormatter;

    public TsDataTableCellRenderer(HasObsFormat target, DemetraUI demetraUI) {
        this.target = target;
        this.demetraUI = demetraUI;
        this.dataRenderer = new TsSparklineCellRenderer();
        this.labelRenderer = new DefaultTableCellRenderer();
        StandardSwingColor.TEXT_FIELD_INACTIVE_FOREGROUND.lookup().ifPresent(labelRenderer::setForeground);
        labelRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        this.currentFormat = null;
        this.currentFormatter = null;
    }

    private DataFormat lookupObsFormat() {
        DataFormat result = target.getDataFormat();
        return result != null ? result : demetraUI.getDataFormat();
    }

    private String formatValue(Number o) {
        DataFormat x = lookupObsFormat();
        if (!Objects.equals(x, currentFormat)) {
            currentFormat = x;
            currentFormatter = x.numberFormatter();
        }
        return currentFormatter.formatAsString(o);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof TsData) {
            TsData data = (TsData) value;
            switch (data.getObsCount()) {
                case 0:
                    return renderUsingLabel(table, "No obs", isSelected, hasFocus, row, column);
                case 1:
                    return renderUsingLabel(table, "Single: " + formatValue(data.get(0)), isSelected, hasFocus, row, column);
                default:
                    return renderUsingSparkline(table, value, isSelected, hasFocus, row, column);
            }
        }
        return renderUsingLabel(table, value, isSelected, hasFocus, row, column);
    }

    private Component renderUsingSparkline(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return dataRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    private Component renderUsingLabel(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        labelRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        labelRenderer.setToolTipText(labelRenderer.getText());
        return labelRenderer;
    }
}
