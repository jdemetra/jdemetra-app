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
package ec.util.grid.swing;

import ec.util.various.swing.JCommand;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author Philippe Charles
 */
abstract class XTableCommand extends JCommand<XTable> {

    @Override
    public ActionAdapter toAction(XTable table) {
        return super.toAction(table)
                .withWeakPropertyChangeListener(table)
                .withWeakListSelectionListener(table.getSelectionModel());
    }

    public static XTableCommand applyModel(final TableModel model) {
        return new XTableCommand() {
            @Override
            public void execute(XTable table) {
                table.setModel(model);
            }

            @Override
            public boolean isSelected(XTable table) {
                return table.getModel().equals(model);
            }
        };
    }

    public static XTableCommand applyDefaultRenderer(final Class<?> columnClass, final TableCellRenderer renderer) {
        return new XTableCommand() {
            @Override
            public void execute(XTable table) {
                table.setDefaultRenderer(columnClass, renderer);
                table.repaint();
            }

            @Override
            public boolean isEnabled(XTable component) {
                return component.getModel().getRowCount() > 0;
            }

            @Override
            public boolean isSelected(XTable table) {
                return table.getDefaultRenderer(columnClass).equals(renderer);
            }
        };
    }

    public static XTableCommand applyNoDataRenderer(final XTable.NoDataRenderer renderer) {
        return new XTableCommand() {
            @Override
            public void execute(XTable table) {
                table.setNoDataRenderer(renderer);
                table.repaint();
            }

            @Override
            public boolean isSelected(XTable component) {
                return component.getNoDataRenderer().equals(renderer);
            }
        };
    }

    public static XTableCommand applyNoDataMessage(String message) {
        return applyNoDataRenderer(new XTable.DefaultNoDataRenderer(message));
    }

    public static XTableCommand applyColumnWidthAsPercentages(final double... percentages) {
        return new XTableCommand() {
            @Override
            public void execute(XTable component) {
                XTable.setWidthAsPercentages(component, percentages);
            }
        };
    }
}
