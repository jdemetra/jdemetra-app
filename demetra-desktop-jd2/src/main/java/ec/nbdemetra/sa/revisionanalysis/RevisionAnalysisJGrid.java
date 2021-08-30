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
package ec.nbdemetra.sa.revisionanalysis;

import demetra.desktop.design.SwingComponent;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.algorithm.IProcResults;
import ec.util.grid.swing.AbstractGridModel;
import ec.util.grid.swing.GridModel;
import ec.util.grid.swing.JGrid;
import ec.util.grid.swing.ext.TableGridCommand;
import java.awt.BorderLayout;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Mats Maggi
 */
@SwingComponent
public final class RevisionAnalysisJGrid extends JComponent {

    private JGrid grid;
    private final CompositeResults results;
    private final DecimalFormat df4 = new DecimalFormat();

    public RevisionAnalysisJGrid(IProcResults rslts) {
        setLayout(new BorderLayout());
        df4.setMaximumFractionDigits(4);
        df4.setMinimumFractionDigits(4);
        results = (CompositeResults) rslts;
        if (results != null) {
            grid = createGrid();
            add(grid, BorderLayout.CENTER);
        }
    }

    private JGrid createGrid() {
        final JGrid result = new JGrid();
        result.setDefaultRenderer(Object.class, new TableCellRenderer() {

            final TableCellRenderer delegate = result.getDefaultRenderer(Object.class);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component result = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (result instanceof JLabel) {
                    ((JLabel) result).setHorizontalAlignment(JLabel.RIGHT);
                }
                return result;
            }
        });

        result.setModel(createTableModel(Arrays.asList(MAIN_TITLE), Arrays.asList(MAIN)));

        JMenu menu = new JMenu();
        menu.add(TableGridCommand.copyAll(true, true).toAction(result)).setText("Copy All");
        result.setComponentPopupMenu(menu.getPopupMenu());

        return result;
    }

    private static final String[] MAIN = {"summary.sastdev", "summary.samin", "summary.samax", "summary.dsastdev", "summary.dsamin", "summary.dsamax", "summary.sstdev", "summary.smin", "summary.smax", "summary.cstdev", "summary.cmin", "summary.cmax"};
    private static final String[] MAIN_TITLE = {"SA (stdev)", "SA (min)", "SA (max)", "SA changes (stdev)", "SA changes (min)", "SA changes (max)", "S (stdev)", "S (min)", "S (max)", "C (stdev)", "C (min)", "C (max)"};

    private GridModel createTableModel(List<String> titles, List<String> items) {
        final List<double[]> dataList = new ArrayList<>();
        final List<String> titleList = new ArrayList<>();

        for (int i = 0; i < items.size(); ++i) {
            double[] data = results.getData(items.get(i), double[].class);
            if (data != null) {
                dataList.add(data);
                titleList.add(titles.get(i));
            }
        }

        final int nbRows = dataList.isEmpty() ? 0 : dataList.get(0).length;

        GridModel result = new AbstractGridModel() {

            @Override
            public int getRowCount() {
                return nbRows;
            }

            @Override
            public int getColumnCount() {
                return dataList.size();
            }

            @Override
            public String getRowName(int rowIndex) {
                return String.valueOf(rowIndex + 1);
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return df4.format(dataList.get(columnIndex)[rowIndex]);
            }

            @Override
            public String getColumnName(int column) {
                return titleList.get(column);
            }
        };

        return result;
    }

}
