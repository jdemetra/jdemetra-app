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
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import com.toedter.components.JSpinField;
import ec.util.grid.swing.AbstractGridModel;
import ec.util.grid.swing.GridModel;
import ec.util.grid.swing.JGrid;
import static demetra.desktop.util.SwingProperties.SPIN_FIELD_VALUE_PROPERTY;
import ec.nbdemetra.ui.demo.DemoComponentFactory;
import ec.tstoolkit.utilities.Id;
import ec.util.grid.swing.XTable;
import java.awt.Component;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

@DirectImpl
@ServiceProvider
public final class GridFactory implements DemoComponentFactory {

    @Override
    public Map<Id, Callable<Component>> getComponents() {
        return ImmutableMap.of(OtherFactory.ID.extend("JGrid"), GridFactory::create);
    }

    private static Component create() {
        JPanel result = new JPanel(new BorderLayout());
        final DynamicModel model = new DynamicModel();

        final JSpinField rowCount = new JSpinField(0, 1000);
        rowCount.setValue(model.getRowCount());
        rowCount.addPropertyChangeListener(SPIN_FIELD_VALUE_PROPERTY, evt -> model.setRowCount(rowCount.getValue()));
        final JSpinField colCount = new JSpinField(0, 1000);
        colCount.setValue(model.getColumnCount());
        colCount.addPropertyChangeListener(SPIN_FIELD_VALUE_PROPERTY, evt -> model.setColCount(colCount.getValue()));

        JPanel north = new JPanel(new FlowLayout());
        north.add(new JLabel("RowCount:"));
        north.add(rowCount);
        north.add(new JLabel("ColCount:"));
        north.add(colCount);
        north.setBorder((Border) UIManager.get("Nb.Editor.Toolbar.border"));
        result.add(north, BorderLayout.NORTH);

        JGrid grid = new JGrid();

        grid.setModel(model);
        grid.setNoDataRenderer(new XTable.DefaultNoDataRenderer("Drag anything here"));
        grid.setRowSelectionAllowed(true);
        grid.setColumnSelectionAllowed(true);
        grid.setTransferHandler(new TransferHandler() {
            Random r = new Random();

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return true;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                model.setColCount(r.nextInt(10));
                model.setRowCount(r.nextInt(10));
                return true;
            }
        });

        result.add(grid, BorderLayout.CENTER);
        return result;
    }

    private static final class DynamicModel extends AbstractGridModel implements GridModel {

        private static final long serialVersionUID = 1L;
        int rowCount = 0;
        int colCount = 0;

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return rowCount;
        }

        public void setColCount(int colCount) {
            this.colCount = colCount;
            fireTableStructureChanged();
        }

        @Override
        public int getColumnCount() {
            return colCount;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return "(" + rowIndex + "x" + columnIndex + ")";
        }
    }
}
