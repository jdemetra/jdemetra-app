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

import ec.util.grid.CellIndex;
import ec.util.chart.ObsIndex;
import ec.util.chart.TimeSeriesChart;
import ec.util.chart.swing.JTimeSeriesChart;
import static ec.util.grid.swing.AGrid.COLUMN_SELECTION_ALLOWED_PROPERTY;
import static ec.util.grid.swing.AGrid.DRAG_ENABLED_PROPERTY;
import static ec.util.grid.swing.AGrid.MODEL_PROPERTY;
import static ec.util.grid.swing.AGrid.ROW_SELECTION_ALLOWED_PROPERTY;
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.JCommand;
import ec.util.various.swing.LineBorder2;
import ec.util.various.swing.ModernUI;
import ec.util.various.swing.StandardSwingColor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.IntervalXYDataset;

/**
 *
 * @author Philippe Charles
 */
public final class JGridDemo extends JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(JGridDemo.class)
                .title("Grid Demo")
                .size(750, 300)
                .launch();
    }

    private final JGrid grid;
    private final JTimeSeriesChart chart;

    public JGridDemo() {
        this.grid = new JGrid();
        this.chart = new JTimeSeriesChart();

        SampleData sampleData = new SampleData();

        grid.setModel(sampleData.asModel());
        grid.setPreferredSize(new Dimension(350, 10));
        grid.setRowSelectionAllowed(true);
        grid.setColumnSelectionAllowed(true);
        grid.setRowRenderer(new RowRenderer());
        grid.setColumnRenderer(new ColumnRenderer());
        grid.setCornerRenderer(new CornerRenderer());
        grid.setDefaultRenderer(Object.class, new CellRenderer(grid));
        grid.setOddBackground(null);
        grid.setComponentPopupMenu(createGridMenu().getPopupMenu());

        chart.setPreferredSize(new Dimension(350, 10));
        chart.setElementVisible(TimeSeriesChart.Element.TITLE, false);
        chart.setElementVisible(TimeSeriesChart.Element.LEGEND, false);
        chart.setElementVisible(TimeSeriesChart.Element.AXIS, false);
        chart.setElementVisible(TimeSeriesChart.Element.CROSSHAIR, true);
        chart.setCrosshairOrientation(TimeSeriesChart.CrosshairOrientation.BOTH);
        chart.setCrosshairTrigger(TimeSeriesChart.DisplayTrigger.SELECTION);
        chart.setDataset(sampleData.asDataset());

        enableSync();

        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, ModernUI.withEmptyBorders(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, grid, chart)));
    }

    private JMenu createGridMenu() {
        JMenu result = new JMenu();

        result.add(apply(grid, MODEL_PROPERTY, GridModels.empty())).setText("Clear");
        result.add(apply(grid, MODEL_PROPERTY, grid.getModel())).setText("Fill");

        result.addSeparator();
        result.add(new JCheckBoxMenuItem(toggle(grid, DRAG_ENABLED_PROPERTY))).setText("Enable drag");
        result.add(new JCheckBoxMenuItem(toggle(grid, ROW_SELECTION_ALLOWED_PROPERTY))).setText("Row selection");
        result.add(new JCheckBoxMenuItem(toggle(grid, COLUMN_SELECTION_ALLOWED_PROPERTY))).setText("Column selection");

        result.addSeparator();
        JMenu menu = new JMenu("Zoom");
        for (int o : new int[]{200, 100, 75, 50, 25}) {
            Font font = getFont();
            font = font.deriveFont(font.getSize2D() * (o / 100f));
            menu.add(new JCheckBoxMenuItem(apply(grid, "font", font))).setText(o + "%");
        }
        result.add(menu);

        return result;
    }

    private void enableSync() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            boolean updating = false;

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (updating) {
                    return;
                }
                updating = true;
                switch (evt.getPropertyName()) {
                    case JGrid.FOCUSED_CELL_PROPERTY:
                        chart.setFocusedObs(toObsIndex(grid.getFocusedCell()));
                        break;
                    case JGrid.SELECTED_CELL_PROPERTY:
                        chart.setSelectedObs(toObsIndex(grid.getSelectedCell()));
                        break;
                    case JTimeSeriesChart.FOCUSED_OBS_PROPERTY:
                        grid.setFocusedCell(toCellIndex(chart.getFocusedObs()));
                        break;
                    case JTimeSeriesChart.SELECTED_OBS_PROPERTY:
                        grid.setSelectedCell(toCellIndex(chart.getSelectedObs()));
                        break;
                }
                updating = false;
            }

            private ObsIndex toObsIndex(CellIndex index) {
                return ObsIndex.valueOf(index.getColumn(), index.getRow());
            }

            private CellIndex toCellIndex(ObsIndex index) {
                return CellIndex.valueOf(index.getObs(), index.getSeries());
            }
        };

        grid.addPropertyChangeListener(listener);
        chart.addPropertyChangeListener(listener);
    }

    //<editor-fold defaultstate="collapsed" desc="Models">
    private static final class SampleData {

        private final long startTimeMillis = new Date().getTime();
        private final double[][] values = getValues(3, 12 * 3, new Random(), startTimeMillis);

        private static double[][] getValues(int series, int obs, Random rng, long startTimeMillis) {
            double[][] result = new double[series][obs];
            for (int i = 0; i < series; i++) {
                for (int j = 0; j < obs; j++) {
                    result[i][j] = Math.abs((100 * (Math.cos(startTimeMillis * i))) + (100 * (Math.sin(startTimeMillis) - Math.cos(rng.nextDouble()) + Math.tan(rng.nextDouble())))) - 50;
                }
            }
            return result;
        }

        public GridModel asModel() {
            return new AbstractGridModel() {
                final Calendar cal = Calendar.getInstance();
                final DateFormat format = new SimpleDateFormat("yyyy-MM");

                @Override
                public String getRowName(int rowIndex) {
                    cal.setTimeInMillis(startTimeMillis);
                    cal.add(Calendar.MONTH, rowIndex);
                    return format.format(cal.getTimeInMillis());
                }

                @Override
                public String getColumnName(int column) {
                    return "Series " + column;
                }

                @Override
                public int getRowCount() {
                    return values[0].length;
                }

                @Override
                public int getColumnCount() {
                    return values.length;
                }

                @Override
                public Object getValueAt(int rowIndex, int columnIndex) {
                    return values[columnIndex][rowIndex];
                }
            };
        }

        public IntervalXYDataset asDataset() {
            TimeSeriesCollection result = new TimeSeriesCollection();
            result.setXPosition(TimePeriodAnchor.MIDDLE);
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < values.length; i++) {
                TimeSeries ts = new TimeSeries(i);
                cal.setTimeInMillis(startTimeMillis);
                for (int j = 0; j < values[i].length; j++) {
                    cal.add(Calendar.MONTH, 1);
                    ts.add(new TimeSeriesDataItem(new Month(cal.getTime()), values[i][j]));
                }
                result.addSeries(ts);
            }
            return result;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Renderers">
    private abstract static class HeaderRenderer extends DefaultTableCellRenderer {

        private final Color background;
        private final Border padding;

        public HeaderRenderer() {
            this.background = StandardSwingColor.CONTROL.or(Color.LIGHT_GRAY);
            this.padding = new LineBorder2(background.brighter(), 0, 0, 1, 1);
        }

        abstract protected boolean isSelected(JTable table, int row, int column);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            boolean x = isSelected(table, row, column);

            JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, x, hasFocus, row, column);

            result.setBorder(padding);
            result.setBackground(x ? table.getSelectionBackground().darker() : background);
            result.setHorizontalAlignment(JLabel.CENTER);
            result.setPreferredSize(new Dimension(10, table.getRowHeight() + 1));
            return result;
        }

    }

    private static final class RowRenderer extends HeaderRenderer {

        @Override
        protected boolean isSelected(JTable table, int row, int column) {
            return table.getRowSelectionAllowed() && table.isRowSelected(row);
        }
    }

    private static final class ColumnRenderer extends HeaderRenderer {

        @Override
        protected boolean isSelected(JTable table, int row, int column) {
            return table.getColumnSelectionAllowed() && table.isColumnSelected(column);
        }
    }

    private static final class CornerRenderer extends HeaderRenderer {

        @Override
        protected boolean isSelected(JTable table, int row, int column) {
            return false;
        }
    }

    private static final class CellRenderer extends DefaultTableCellRenderer {

        private final NumberFormat format;
        private final JGrid grid;

        public CellRenderer(JGrid grid) {
            this.format = new DecimalFormat("#.00");
            this.grid = grid;
            setHorizontalAlignment(JLabel.TRAILING);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String formattedValue = format.format(value);
            super.getTableCellRendererComponent(table, formattedValue, isSelected, hasFocus, row, column);
            setToolTipText(formattedValue);
            if (grid.getFocusedCell().equals(row, column)) {
                setBorder(new LineBorder(table.getSelectionBackground().darker()));
            }
            return this;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Commands">
    private static Action apply(JGrid grid, String propertyName, Object value) {
        return new ApplyProperty(JGrid.class, propertyName, value).toAction(grid);
    }

    private static Action toggle(JGrid grid, String propertyName) {
        return new ToggleProperty(JGrid.class, propertyName).toAction(grid);
    }

    private static PropertyDescriptor lookupProperty(Class<?> clazz, String propertyName) throws IllegalArgumentException {
        try {
            for (PropertyDescriptor o : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
                if (o.getName().equals(propertyName)) {
                    return o;
                }
            }
        } catch (IntrospectionException ex) {
            throw new RuntimeException(ex);
        }
        throw new IllegalArgumentException(propertyName);
    }

    private static final class ApplyProperty<X extends Component> extends JCommand<X> {

        private final PropertyDescriptor property;
        private final Object value;

        public ApplyProperty(Class<X> clazz, String propertyName, Object value) {
            this.property = lookupProperty(clazz, propertyName);
            this.value = value;
        }

        @Override
        public void execute(X component) throws Exception {
            property.getWriteMethod().invoke(component, value);
        }

        @Override
        public boolean isSelected(X component) {
            try {
                return Objects.equals(property.getReadMethod().invoke(component), value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public JCommand.ActionAdapter toAction(X component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, property.getName());
        }
    }

    private static final class ToggleProperty<X extends Component> extends JCommand<X> {

        private final PropertyDescriptor property;

        public ToggleProperty(Class<X> clazz, String propertyName) {
            this.property = lookupProperty(clazz, propertyName);
            if (!property.getPropertyType().equals(boolean.class)) {
                throw new IllegalArgumentException("Invalid property type: " + property.getPropertyType());
            }
        }

        @Override
        public void execute(X component) throws Exception {
            Boolean value = (Boolean) property.getReadMethod().invoke(component);
            property.getWriteMethod().invoke(component, !value);
        }

        @Override
        public boolean isSelected(X component) {
            try {
                return (Boolean) property.getReadMethod().invoke(component);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(property.getName(), ex);
            }
        }

        @Override
        public JCommand.ActionAdapter toAction(X component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, property.getName());
        }
    }
    //</editor-fold>
}
