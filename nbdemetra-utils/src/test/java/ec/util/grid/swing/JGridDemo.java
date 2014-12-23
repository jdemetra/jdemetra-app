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

import ec.util.chart.ObsIndex;
import ec.util.chart.ObsPredicate;
import ec.util.chart.TimeSeriesChart;
import ec.util.chart.swing.JTimeSeriesChart;
import static ec.util.grid.swing.AGrid.COLUMN_SELECTION_ALLOWED_PROPERTY;
import static ec.util.grid.swing.AGrid.DRAG_ENABLED_PROPERTY;
import static ec.util.grid.swing.AGrid.MODEL_PROPERTY;
import static ec.util.grid.swing.AGrid.ROW_SELECTION_ALLOWED_PROPERTY;
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import ec.util.various.swing.LineBorder2;
import ec.util.various.swing.ModernUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
                .icons(new Callable<List<? extends Image>>() {
                    @Override
                    public List<? extends Image> call() throws Exception {
                        return FontAwesome.FA_TH.getImages(Color.BLACK, 16f, 32f, 64f);
                    }
                })
                .logLevel(Level.FINE)
                .size(750, 300)
                .launch();
    }

    private Point focusedCell = new Point(-1, -1);
    private final JGrid grid;
    private final JTimeSeriesChart chart;
    private final long startTimeMillis;
    private final double[][] values;

    public JGridDemo() {
        this.startTimeMillis = new Date().getTime();
        this.values = getValues(3, 12 * 3, new Random(), startTimeMillis);

        this.grid = new JGrid();
        this.chart = new JTimeSeriesChart();

        grid.setModel(asModel(values, startTimeMillis));
        grid.setPreferredSize(new Dimension(350, 10));
        grid.setRowSelectionAllowed(true);
        grid.setColumnSelectionAllowed(true);
        grid.setRowRenderer(new RowRenderer());
        grid.setColumnRenderer(new ColumnRenderer());
        grid.setCornerRenderer(new CornerRenderer());
        grid.setOddBackground(null);
        grid.addMouseListener(new MouseAdapter() {
            private final JPopupMenu popup = createMenu().getPopupMenu();

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        grid.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final NumberFormat format = new DecimalFormat("#.##");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                ObsIndex focusedObs = ObsIndex.valueOf(table.columnAtPoint(focusedCell), table.rowAtPoint(focusedCell));
                chart.setFocusedObs(focusedObs);
                boolean xxx = focusedObs.equals(column, row);
                setHorizontalAlignment(JLabel.TRAILING);
                super.getTableCellRendererComponent(table, format.format(value), isSelected, hasFocus, row, column);
                if (xxx) {
                    setBorder(new LineBorder(table.getSelectionBackground().darker()));
                }
                return this;
            }
        });
        grid.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                focusedCell = e.getPoint();
                grid.repaint();
            }
        });

        chart.setPreferredSize(new Dimension(350, 10));
        chart.setElementVisible(TimeSeriesChart.Element.TITLE, false);
        chart.setElementVisible(TimeSeriesChart.Element.LEGEND, false);
        chart.setElementVisible(TimeSeriesChart.Element.AXIS, false);
        chart.setElementVisible(TimeSeriesChart.Element.CROSSHAIR, true);
        chart.setCrosshairOrientation(TimeSeriesChart.CrosshairOrientation.VERTICAL);
        chart.setCrosshairTrigger(TimeSeriesChart.DisplayTrigger.SELECTION);
        chart.setDataset(asDataset(values, startTimeMillis));
        chart.setEnabled(false);

        grid.getRowSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                chart.setObsHighlighter(new ObsPredicate() {
                    @Override
                    public boolean apply(int series, int obs) {
                        chart.setSelectedObs(ObsIndex.valueOf(series, obs));
                        return grid.getColumnSelectionModel().isSelectedIndex(series) && grid.getRowSelectionModel().isSelectedIndex(obs);
                    }
                });
            }
        });
        grid.setColumnSelectionModel(chart.getSeriesSelectionModel());

        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, ModernUI.withEmptyBorders(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, grid, chart)));
    }

    private JMenu createMenu() {
        JMenu result = new JMenu();

        result.add(apply(grid, MODEL_PROPERTY, GridModels.empty())).setText("Clear");
        result.add(apply(grid, MODEL_PROPERTY, asModel(values, startTimeMillis))).setText("Fill");

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

    private static double[][] getValues(int series, int obs, Random rng, long startTimeMillis) {
        double[][] result = new double[series][obs];
        for (int i = 0; i < series; i++) {
            for (int j = 0; j < obs; j++) {
                result[i][j] = Math.abs((100 * (Math.cos(startTimeMillis * i))) + (100 * (Math.sin(startTimeMillis) - Math.cos(rng.nextDouble()) + Math.tan(rng.nextDouble())))) - 50;
            }
        }
        return result;
    }

    private static GridModel asModel(final double[][] values, final long startTimeMillis) {
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

    private static IntervalXYDataset asDataset(double[][] values, long startTimeMillis) {
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

    private abstract static class HeaderRenderer extends DefaultTableCellRenderer {

        private final Color background;
        private final Border padding;

        public HeaderRenderer() {
            Color controlColor = UIManager.getColor("control");
            this.background = controlColor != null ? controlColor : Color.LIGHT_GRAY;
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

    private static class RowRenderer extends HeaderRenderer {

        @Override
        protected boolean isSelected(JTable table, int row, int column) {
            return table.getRowSelectionAllowed() && table.isRowSelected(row);
        }
    }

    private static class ColumnRenderer extends HeaderRenderer {

        @Override
        protected boolean isSelected(JTable table, int row, int column) {
            return table.getColumnSelectionAllowed() && table.isColumnSelected(column);
        }
    }

    private static class CornerRenderer extends HeaderRenderer {

        @Override
        protected boolean isSelected(JTable table, int row, int column) {
            return false;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Commands">
    private static <X extends Component> Action apply(X component, String propertyName, Object value) {
        return new ApplyProperty(component.getClass(), propertyName, value).toAction(component);
    }

    private static <X extends Component> Action toggle(X component, String propertyName) {
        return new ToggleProperty(component.getClass(), propertyName).toAction(component);
    }

    private static <X extends Component> JCommand<X> applyProperty(Class<X> clazz, String propertyName, Object value) {
        return new ApplyProperty(clazz, propertyName, value);
    }

    private static <X extends Component> JCommand<X> toggleProperty(Class<X> clazz, String propertyName) {
        return new ToggleProperty(clazz, propertyName);
    }

    private static PropertyDescriptor lookupProperty(Class<?> clazz, String propertyName) {
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

    private static final class ApplyProperty<C extends Component> extends JCommand<C> {

        private final PropertyDescriptor property;
        private final Object value;

        public ApplyProperty(Class<C> clazz, String propertyName, Object value) {
            this.property = lookupProperty(clazz, propertyName);
            this.value = value;
        }

        @Override
        public void execute(C component) {
            try {
                property.getWriteMethod().invoke(component, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean isSelected(C component) {
            try {
                return Objects.equals(property.getReadMethod().invoke(component), value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public ActionAdapter toAction(C component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, property.getName());
        }
    }

    private static final class ToggleProperty<C extends Component> extends JCommand<C> {

        private final PropertyDescriptor property;

        public ToggleProperty(Class<C> clazz, String propertyName) {
            this.property = lookupProperty(clazz, propertyName);
            if (!property.getPropertyType().equals(boolean.class)) {
                throw new IllegalArgumentException("Invalid property type: " + property.getPropertyType());
            }
        }

        @Override
        public void execute(C component) {
            try {
                Boolean value = (Boolean) property.getReadMethod().invoke(component);
                property.getWriteMethod().invoke(component, !value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean isSelected(C component) {
            try {
                return (Boolean) property.getReadMethod().invoke(component);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public JCommand.ActionAdapter toAction(C component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, property.getName());
        }
    }
    //</editor-fold>
}
