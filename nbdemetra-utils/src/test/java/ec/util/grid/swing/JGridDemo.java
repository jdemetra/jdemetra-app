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
import ec.util.various.swing.ModernUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
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
        grid.setRowRenderer(new HeaderRenderer(true));
        grid.setColumnRenderer(new HeaderRenderer(false));

        grid.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            final NumberFormat format = new DecimalFormat("#.##");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                ObsIndex focusedObs = ObsIndex.valueOf(table.columnAtPoint(focusedCell), table.rowAtPoint(focusedCell));
                chart.setFocusedObs(focusedObs);
                setHorizontalAlignment(JLabel.TRAILING);
                super.getTableCellRendererComponent(table, format.format(value), isSelected, hasFocus, row, column);
                setForeground(focusedObs.equals(column, row) ? Color.RED : null);
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

    private static class HeaderRenderer extends DefaultTableCellRenderer {

        private final Color background;
        private final Color selectedBackground;
        private final Border border;
        private final Border selectedBorder;

        private static Color getControlColor() {
            Color result = UIManager.getColor("control");
            return result != null ? result : Color.LIGHT_GRAY;
        }

        public HeaderRenderer(boolean stuff) {
            this.background = getControlColor();
            this.selectedBackground = background.darker();
            this.border = new CustomBorder(2, 2, 2, 2, new Color(0f, 0, 0, 0));
//            this.border = new CustomBorder(0, 0, 0, 2, Color.BLACK);
            this.selectedBorder = new CustomBorder(0, 0, stuff ? 0 : 2, stuff ? 2 : 0, new JTable().getSelectionBackground());
            setHorizontalAlignment(JLabel.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            boolean x = isSelected || (table.isColumnSelected(column));

            result.setForeground(x ? table.getSelectionForeground() : table.getForeground());
            result.setBackground(x ? selectedBackground : background);
            result.setBorder(x ? selectedBorder : border);
            return result;
        }
    }

    private static final class CustomBorder implements Border {

        private final int left, right, top, bottom;
        private final Color color;

        public CustomBorder(int top, int left, int bottom, int right, Color color) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (g instanceof Graphics2D) {
                Graphics2D g2d = (Graphics2D) g;

                Color oldColor = g2d.getColor();
                g2d.setColor(color);

                Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
                path.append(new Rectangle2D.Float(x, y, width, height), false);
                path.append(new Rectangle2D.Float(x + left, y + top, width - left - right, height - top - bottom), false);
                g2d.fill(path);

                g2d.setColor(oldColor);
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(top, left, bottom, right);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }
}
