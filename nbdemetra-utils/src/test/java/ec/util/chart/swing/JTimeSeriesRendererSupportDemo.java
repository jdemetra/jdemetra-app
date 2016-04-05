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
package ec.util.chart.swing;

import ec.util.chart.TimeSeriesChart.RendererType;
import ec.util.chart.impl.TangoColorScheme;
import ec.util.grid.swing.XTable;
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import ec.util.various.swing.ModernUI;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Stroke;
import java.util.Calendar;
import java.util.Random;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Philippe Charles
 */
public final class JTimeSeriesRendererSupportDemo extends JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(JTimeSeriesRendererSupportDemo.class)
                .title("Support Demo")
                .icons(() -> {
                    Color c = new Color(TangoColorScheme.DARK_SKY_BLUE);
                    return FontAwesome.FA_TACHOMETER.getImages(c, 16f, 32f, 64f);
                })
                .launch();
    }

    private final SwingColorSchemeSupport colorSchemeSupport;
    private final CustomRendererSupport support;
    private final JFreeChart chart;

    public JTimeSeriesRendererSupportDemo() {
        this.colorSchemeSupport = SwingColorSchemeSupport.from(new TangoColorScheme());
        this.support = new CustomRendererSupport(3, 24, colorSchemeSupport);
        this.chart = createTsChart();

        RANDOM_DATA.executeSafely(chart);

        setLayout(new BorderLayout());
        add(createToolBar(), BorderLayout.NORTH);
        add(ModernUI.withEmptyBorders(new JSplitPane(JSplitPane.VERTICAL_SPLIT, createChartPanel(), createMissionControl())), BorderLayout.CENTER);
    }

    private static JFreeChart createTsChart() {
        XYPlot plot = new XYPlot();

        plot.setAxisOffset(RectangleInsets.ZERO_INSETS);

        DateAxis domainAxis = new DateAxis();
        domainAxis.setTickLabelsVisible(false);
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);
        plot.setDomainAxis(domainAxis);

        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setTickLabelsVisible(false);
        rangeAxis.setLowerMargin(0.02);
        rangeAxis.setUpperMargin(0.02);
        plot.setRangeAxis(rangeAxis);

        JFreeChart result = new JFreeChart("", null, plot, true);
        result.setPadding(new RectangleInsets(5, 5, 5, 5));
        result.getLegend().setFrame(BlockBorder.NONE);
        result.getLegend().setBackgroundPaint(null);

        return result;
    }

    private Component createToolBar() {
        JToolBar result = new JToolBar();
        result.setFloatable(false);

        result.add(RANDOM_DATA.toAction(chart)).setIcon(FontAwesome.FA_RANDOM.getIcon(getForeground(), 16f));

        JComboBox<RendererType> types = new JComboBox<>(support.getSupportedRendererTypes().toArray(new RendererType[0]));
        types.setMaximumSize(new Dimension(150, 100));
        types.addItemListener(evt -> {
            RendererType type = (RendererType) evt.getItem();
            chart.getXYPlot().setRenderer(support.createRenderer(type));
            chart.getXYPlot().setBackgroundPaint(support.getPlotColor());
            chart.setBackgroundPaint(colorSchemeSupport.getBackColor());
        });
        types.setSelectedIndex(1);
        result.add(types);

        return result;
    }

    private Component createChartPanel() {
        final ChartPanel result = Charts.avoidScaling(new ChartPanel(chart));
        result.setMinimumSize(new Dimension(400, 300));
        return result;
    }

    private Component createMissionControl() {
        ListSelectionModel seriesSelectionModel = new DefaultListSelectionModel();
        JSplitPane result = ModernUI.withEmptyBorders(new JSplitPane(JSplitPane.VERTICAL_SPLIT, createSeriesTable(seriesSelectionModel), createObsTable(seriesSelectionModel)));
        result.getTopComponent().setMinimumSize(new Dimension(100, 100));
        result.setDividerLocation(.5);
        return result;
    }

    private Component createSeriesTable(ListSelectionModel seriesSelectionModel) {
        XTable result = new XTable();
        result.setSelectionModel(seriesSelectionModel);
        result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        result.setModel(new SeriesModel(support.seriesInfos));
        ColorCellRenderer r0 = new ColorCellRenderer(FontAwesome.FA_CIRCLE_O);
        result.setDefaultRenderer(Color.class, r0);
        result.setDefaultEditor(Color.class, new ColorCellEditor(colorSchemeSupport, r0));
        ColorCellRenderer r3 = new ColorCellRenderer(FontAwesome.FA_FONT);
        result.getColumnModel().getColumn(3).setCellRenderer(r3);
        result.getColumnModel().getColumn(3).setCellEditor(new ColorCellEditor(colorSchemeSupport, r3));
        result.setDefaultRenderer(Font.class, FontCellRenderer.INSTANCE);
        result.setDefaultEditor(Font.class, new FontCellEditor());
        result.setDefaultRenderer(Stroke.class, StrokeCellRenderer.INSTANCE);
        result.setDefaultEditor(Stroke.class, new StrokeCellEditor());
        result.getModel().addTableModelListener(evt -> chart.fireChartChanged());
        return ModernUI.withEmptyBorders(new JScrollPane(result));
    }

    private Component createObsTable(ListSelectionModel seriesSelectionModel) {
        final XTable result = new XTable();
        ColorCellRenderer renderer = new ColorCellRenderer(FontAwesome.FA_CIRCLE_O);
        result.setDefaultRenderer(Color.class, renderer);
        result.setDefaultEditor(Color.class, new ColorCellEditor(colorSchemeSupport, renderer));
        result.setDefaultRenderer(Font.class, FontCellRenderer.INSTANCE);
        result.setDefaultEditor(Font.class, new FontCellEditor());
        result.setDefaultRenderer(Stroke.class, StrokeCellRenderer.INSTANCE);
        result.setDefaultEditor(Stroke.class, new StrokeCellEditor());
        seriesSelectionModel.addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                int index = evt.getFirstIndex();
                result.setModel(index != -1 ? new ObsModel(support.seriesInfos[index].obsInfos) : null);
                result.getModel().addTableModelListener(e -> chart.fireChartChanged());
            }
        });
        return ModernUI.withEmptyBorders(new JScrollPane(result));
    }

    private static final class SeriesInfo {

        Color color;
        Color labelColor;
        Font labelFont;
        Stroke stroke;
        boolean labelVisible;
        String label;
        ObsInfo[] obsInfos;
    }

    private static final class ObsInfo {

        Color color;
        Font labelFont;
        Stroke stroke;
        String label;
        boolean highlighted;
        boolean labelVisible;
    }

    private static final class CustomRendererSupport extends JTimeSeriesRendererSupport {

        Color plotColor;
        final SeriesInfo[] seriesInfos;

        public CustomRendererSupport(int nbrSeries, int nbrObs, SwingColorSchemeSupport colorSchemeSupport) {
            this.plotColor = colorSchemeSupport.getPlotColor();
            this.seriesInfos = new SeriesInfo[nbrSeries];
            for (int i = 0; i < seriesInfos.length; i++) {
                SeriesInfo seriesInfo = new SeriesInfo();
                seriesInfo.color = colorSchemeSupport.getLineColor(i);
                seriesInfo.labelColor = seriesInfo.color;
                seriesInfo.labelFont = AbstractRenderer.DEFAULT_VALUE_LABEL_FONT;
                seriesInfo.stroke = AbstractRenderer.DEFAULT_STROKE;
                seriesInfo.labelVisible = true;
                seriesInfo.label = "Series " + i;
                ObsInfo[] obsInfos = new ObsInfo[nbrObs];
                for (int j = 0; j < obsInfos.length; j++) {
                    ObsInfo obsInfo = new ObsInfo();
                    obsInfo.color = seriesInfo.color;
                    obsInfo.labelFont = seriesInfo.labelFont;
                    obsInfo.stroke = seriesInfo.stroke;
                    obsInfo.label = "Obs " + j;
                    obsInfo.highlighted = false;
                    obsInfo.labelVisible = false;
                    obsInfos[j] = obsInfo;
                }
                seriesInfo.obsInfos = obsInfos;
                seriesInfos[i] = seriesInfo;
            }
        }

        @Override
        public Color getPlotColor() {
            return plotColor;
        }

        @Override
        public Color getSeriesColor(int series) {
            return seriesInfos[series].color;
        }

        @Override
        public Color getSeriesLabelColor(int series) {
            return seriesInfos[series].labelColor;
        }

        @Override
        public Font getSeriesLabelFont(int series) {
            return seriesInfos[series].labelFont;
        }

        @Override
        public Stroke getSeriesStroke(int series) {
            return seriesInfos[series].stroke;
        }

        @Override
        public boolean isSeriesLabelVisible(int series) {
            return seriesInfos[series].labelVisible;
        }

        @Override
        public Color getObsColor(int series, int item) {
            return seriesInfos[series].obsInfos[item].color;
        }

        @Override
        public Font getObsLabelFont(int series, int item) {
            return seriesInfos[series].obsInfos[item].labelFont;
        }

        @Override
        public Stroke getObsStroke(int series, int item) {
            return seriesInfos[series].obsInfos[item].stroke;
        }

        @Override
        public String getObsLabel(int series, int item) {
            return seriesInfos[series].obsInfos[item].label;
        }

        @Override
        public String getSeriesLabel(int series) {
            return seriesInfos[series].label;
        }

        @Override
        public boolean isObsHighlighted(int series, int item) {
            return seriesInfos[series].obsInfos[item].highlighted;
        }

        @Override
        public boolean isObsLabelVisible(int series, int item) {
            return seriesInfos[series].obsInfos[item].labelVisible;
        }
    }

    private static abstract class ArrayTableModel<T> extends AbstractTableModel {

        private final String[] columnNames;
        private final Class<?>[] columnTypes;
        private final T[] data;

        public ArrayTableModel(T[] data, String[] columnNames, Class<?>[] columnTypes) {
            this.data = data;
            this.columnNames = columnNames;
            this.columnTypes = columnTypes;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        abstract protected Object getValue(T row, int columnIndex);

        abstract protected void setValue(Object value, T row, int columnIndex);

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return getValue(data[rowIndex], columnIndex);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            setValue(aValue, data[rowIndex], columnIndex);
            fireTableDataChanged();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
    }

    private static final class SeriesModel extends ArrayTableModel<SeriesInfo> {

        static final String[] NAMES = {"color", "stroke", "label", "labelColor", "labelFont", "labelVisible"};
        static final Class<?>[] TYPES = {Color.class, Stroke.class, String.class, Color.class, Font.class, Boolean.class};

        public SeriesModel(SeriesInfo[] seriesInfos) {
            super(seriesInfos, NAMES, TYPES);
        }

        @Override
        protected Object getValue(SeriesInfo row, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return row.color;
                case 1:
                    return row.stroke;
                case 2:
                    return row.label;
                case 3:
                    return row.labelColor;
                case 4:
                    return row.labelFont;
                case 5:
                    return row.labelVisible;
            }
            return null;
        }

        @Override
        protected void setValue(Object value, SeriesInfo row, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    row.color = (Color) value;
                    for (ObsInfo tmp : row.obsInfos) {
                        tmp.color = row.color;
                    }
                    break;
                case 1:
                    row.stroke = (Stroke) value;
                    for (ObsInfo tmp : row.obsInfos) {
                        tmp.stroke = row.stroke;
                    }
                    break;
                case 2:
                    row.label = (String) value;
                    break;
                case 3:
                    row.labelColor = (Color) value;
                    break;
                case 4:
                    row.labelFont = (Font) value;
                    break;
                case 5:
                    row.labelVisible = (Boolean) value;
                    break;
            }
        }
    }

    private static final class ObsModel extends ArrayTableModel<ObsInfo> {

        static final String[] NAMES = {"color", "stroke", "label", "labelFont", "labelVisible", "highlighted"};
        static final Class<?>[] TYPES = {Color.class, Stroke.class, String.class, Font.class, Boolean.class, Boolean.class};

        public ObsModel(ObsInfo[] data) {
            super(data, NAMES, TYPES);
        }

        @Override
        protected Object getValue(ObsInfo row, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return row.color;
                case 1:
                    return row.stroke;
                case 2:
                    return row.label;
                case 3:
                    return row.labelFont;
                case 4:
                    return row.labelVisible;
                case 5:
                    return row.highlighted;
            }
            return null;
        }

        @Override
        protected void setValue(Object value, ObsInfo row, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    row.color = (Color) value;
                    break;
                case 1:
                    row.stroke = (Stroke) value;
                    break;
                case 2:
                    row.label = (String) value;
                    break;
                case 3:
                    row.labelFont = (Font) value;
                    break;
                case 4:
                    row.labelVisible = (Boolean) value;
                    break;
                case 5:
                    row.highlighted = (Boolean) value;
                    break;
            }
        }
    }

    private static final JCommand<JFreeChart> RANDOM_DATA = new JCommand<JFreeChart>() {
        final Random random = new Random();
        final Calendar cal = Calendar.getInstance();

        @Override
        public void execute(JFreeChart chart) {
            cal.set(Calendar.YEAR, 2012);
            cal.set(Calendar.MONTH, 02);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long start = cal.getTimeInMillis();
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            dataset.setXPosition(TimePeriodAnchor.MIDDLE);
            double[][] values = getValues(3, 24, random, start);
            for (int i = 0; i < values.length; i++) {
                TimeSeries ts = new TimeSeries(i);
                cal.setTimeInMillis(start);
                for (int j = 0; j < values[i].length; j++) {
                    cal.add(Calendar.MONTH, 1);
                    ts.add(new TimeSeriesDataItem(new Month(cal.getTime()), values[i][j]));
                }
                dataset.addSeries(ts);
            }

            chart.getXYPlot().setDataset(new FilteredXYDataset(dataset, new int[]{0, 1, 2}));
        }

        double[][] getValues(int series, int obs, Random rng, long startTimeMillis) {
            double[][] result = new double[series][obs];
            for (int i = 0; i < series; i++) {
                for (int j = 0; j < obs; j++) {
                    result[i][j] = Math.abs((100 * (Math.cos(startTimeMillis * i))) + (100 * (Math.sin(startTimeMillis) - Math.cos(rng.nextDouble()) + Math.tan(rng.nextDouble())))) - 50;
                }
            }
            return result;
        }
    };

    //<editor-fold defaultstate="collapsed" desc="Details">
    private static class ColorCellRenderer implements TableCellRenderer, ListCellRenderer {

        private final DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
        private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
        private final FontAwesome fontAwesome;

        public ColorCellRenderer(FontAwesome fontAwesome) {
            this.fontAwesome = fontAwesome;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return apply(result, (Color) value, isSelected);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel result = (JLabel) listCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            return apply(result, (Color) value, isSelected);
        }

        private JLabel apply(JLabel label, Color color, boolean selected) {
            label.setText("[" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + "]");
            label.setIcon(fontAwesome.getIcon(selected ? label.getForeground() : color, 12f));
            return label;
        }
    }

    private static final class ColorCellEditor extends DefaultCellEditor {

        public ColorCellEditor(SwingColorSchemeSupport colorSchemeSupport, ColorCellRenderer renderer) {
            super(new JComboBox());
            JComboBox cb = (JComboBox) getComponent();
            colorSchemeSupport.getColorScheme().getLineColors().stream()
                    .map(SwingColorSchemeSupport::rgbToColor)
                    .forEach(o -> ((DefaultComboBoxModel) cb.getModel()).addElement(o));
            cb.setRenderer(renderer);
        }
    }

    private static class FontCellRenderer implements TableCellRenderer, ListCellRenderer {

        public static final FontCellRenderer INSTANCE = new FontCellRenderer();

        private final DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
        private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return apply(result, (Font) value);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel result = (JLabel) listCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            return apply(result, (Font) value);
        }

        private JLabel apply(JLabel label, Font font) {
            label.setFont(font);
            label.setText(font.getName() + " " + font.getSize());
            return label;
        }
    }

    private static class FontCellEditor extends DefaultCellEditor {

        public FontCellEditor() {
            super(new JComboBox());
            JComboBox cb = (JComboBox) getComponent();
            ((DefaultComboBoxModel) cb.getModel()).addElement(AbstractRenderer.DEFAULT_VALUE_LABEL_FONT);
            ((DefaultComboBoxModel) cb.getModel()).addElement(JFreeChart.DEFAULT_TITLE_FONT);
            cb.setRenderer(FontCellRenderer.INSTANCE);
        }
    }

    private static class StrokeCellRenderer implements TableCellRenderer, ListCellRenderer {

        public static final StrokeCellRenderer INSTANCE = new StrokeCellRenderer();

        private final DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
        private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel result = (JLabel) tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return apply(result, (Stroke) value);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel result = (JLabel) listCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            return apply(result, (Stroke) value);
        }

        private JLabel apply(JLabel label, Stroke stroke) {
            if (stroke instanceof BasicStroke) {
                label.setText("Basic stroke " + ((BasicStroke) stroke).getLineWidth());
            }
            return label;
        }
    }

    private static class StrokeCellEditor extends DefaultCellEditor {

        public StrokeCellEditor() {
            super(new JComboBox());
            JComboBox cb = (JComboBox) getComponent();
            ((DefaultComboBoxModel) cb.getModel()).addElement(new BasicStroke(1f));
            ((DefaultComboBoxModel) cb.getModel()).addElement(new BasicStroke(2f));
            ((DefaultComboBoxModel) cb.getModel()).addElement(new BasicStroke(3f));
            cb.setRenderer(StrokeCellRenderer.INSTANCE);
        }
    }
    //</editor-fold>

}
