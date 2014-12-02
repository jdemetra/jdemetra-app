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

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.annotation.Nonnull;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/*
 * Prevent the specified number of columns from scrolling horizontally in the
 * scroll pane. The table must already exist in the scroll pane.
 *
 * The functionality is accomplished by creating a second JTable (fixed) that
 * will share the TableModel and SelectionModel of the main table. This table
 * will be used as the row header of the scroll pane.
 *
 * The fixed table created can be accessed by using the getFixedTable() method.
 * will be returned from this method. It will allow you to:
 *
 * You can change the model of the main table and the change will be reflected
 * in the fixed model. However, you cannot change the structure of the model.
 *
 * from: http://tips4java.wordpress.com/2008/11/05/fixed-column-table/
 */
final class FixedColumnTable implements PropertyChangeListener, ChangeListener, TableModelListener {

    private final JTable main;
    private final JTable fixed;
    private final JScrollPane scrollPane;

    /*
     * Specify the number of columns to be fixed and the scroll pane containing
     * the table.
     */
    public FixedColumnTable(int fixedColumns, JScrollPane scrollPane) {
        this.scrollPane = scrollPane;

        main = ((JTable) scrollPane.getViewport().getView());
        main.setAutoCreateColumnsFromModel(false);
        main.addPropertyChangeListener(this);

        // Use the existing table to create a new table sharing
        // the DataModel and ListSelectionModel
        fixed = new JTable();
        fixed.setAutoCreateColumnsFromModel(false);
        fixed.setModel(main.getModel());
        fixed.getModel().addTableModelListener(this);
        fixed.setSelectionModel(main.getSelectionModel());
        fixed.setFocusable(false);

        // This makes the fixed columns resizable
        fixed.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        fixed.getTableHeader().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
            }
        });

        // Remove the fixed columns from the main table
        // and add them to the fixed table
        makeColumns(fixedColumns);

        // Add the fixed table to the scroll pane
        fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
        scrollPane.setRowHeaderView(fixed);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixed.getTableHeader());

        // Synchronize scrolling of the row header with the main table
        scrollPane.getRowHeader().addChangeListener(this);

        fixed.setRowHeight(main.getRowHeight());
    }

    private static void clear(TableColumnModel columnModel) {
        while (columnModel.getColumnCount() > 0) {
            columnModel.removeColumn(columnModel.getColumn(0));
        }
    }

    private void makeColumns(int fixedColumns) {
        clear(fixed.getColumnModel());
        clear(main.getColumnModel());
        for (int i = 0; i < fixedColumns; i++) {
            fixed.addColumn(new TableColumn(i));
        }
        for (int i = fixedColumns; i < main.getModel().getColumnCount(); i++) {
            main.addColumn(new TableColumn(i));
        }
    }

    /*
     * Return the table being used in the row header
     */
    @Nonnull
    public JTable getFixedTable() {
        return fixed;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Keep the fixed table in sync with the main table
        switch (evt.getPropertyName()) {
            case "selectionModel":
                fixed.setSelectionModel(main.getSelectionModel());
                break;
            case "model":
                fixed.getModel().removeTableModelListener(this);
                fixed.setModel(main.getModel());
                fixed.getModel().addTableModelListener(this);
                makeColumns(fixed.getColumnCount());
                break;
            case "rowHeight":
                fixed.setRowHeight(main.getRowHeight());
                break;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // Sync the scroll pane scrollbar with the row header
        JViewport viewport = (JViewport) e.getSource();
        FixedColumnTable.this.scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
            makeColumns(fixed.getColumnCount());
        }
    }
}
