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
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.LineBorder2;
import ec.util.various.swing.ModernUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
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

    public JGridDemo() {
        long startTimeMillis = new Date().getTime();
        double[][] values = getValues(3, 12 * 3, new Random(), startTimeMillis);

        final JGrid grid = new JGrid();
        final JTimeSeriesChart chart = new JTimeSeriesChart();

        grid.setModel(asModel(values, startTimeMillis));
        grid.setPreferredSize(new Dimension(350, 10));
        grid.setRowSelectionAllowed(true);
        grid.setColumnSelectionAllowed(true);
        grid.setRowRenderer(new RowRenderer());
        grid.setColumnRenderer(new ColumnRenderer());
        grid.setCornerRenderer(new CornerRenderer());

        grid.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final NumberFormat format = new DecimalFormat("#.##");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                ObsIndex focusedObs = ObsIndex.valueOf(table.columnAtPoint(focusedCell), table.rowAtPoint(focusedCell));
                chart.setFocusedObs(focusedObs);
                boolean xxx = focusedObs.equals(column, row);
                setHorizontalAlignment(JLabel.TRAILING);
                super.getTableCellRendererComponent(table, format.format(value), isSelected, hasFocus, row, column);
                setForeground(xxx ? Color.RED : null);
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
        grid.getColumnModel().setSelectionModel(chart.getSeriesSelectionModel());

        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, ModernUI.withEmptyBorders(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, grid, chart)));
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

            boolean x = isSelected || isSelected(table, row, column);

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
            return table.isRowSelected(row);
        }
    }

    private static class ColumnRenderer extends HeaderRenderer {

        @Override
        protected boolean isSelected(JTable table, int row, int column) {
            return table.isColumnSelected(column);
        }
    }

    private static class CornerRenderer extends HeaderRenderer {

        @Override
        protected boolean isSelected(JTable table, int row, int column) {
            return false;
//            return table.isCellSelected(row, column);
        }
    }
}
