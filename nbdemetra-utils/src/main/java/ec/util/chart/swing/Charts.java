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
package ec.util.chart.swing;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.AbstractList;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartTransferable;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.SeriesDataset;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 * Utility class for charts.
 *
 * @author Jeremy Demortier
 * @author Philippe Charles
 */
public final class Charts {

    private Charts() {
        // static class
    }

    /**
     * Gets the distance between a point and a segment. Replaces default method
     * because of what seems like a bug in jdk1.6.0_21
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param px
     * @param py
     * @return
     * @see http://forums.sun.com/thread.jspa?threadID=5267876
     */
    public static double ptSegDist(double x1, double y1, double x2, double y2, double px, double py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double dotprod = px * x2 + py * y2;
        if (dotprod <= 0.0) {
            return Math.sqrt(px * px + py * py);
        } else {
            px = x2 - px;
            py = y2 - py;
            dotprod = px * x2 + py * y2;
            if (dotprod <= 0.0) {
                return Math.sqrt(px * px + py * py);
            } else {
                return Math.abs(px * y2 - py * x2) / Math.sqrt(x2 * x2 + y2 * y2);
            }
        }
    }

    /**
     * Finds the index of the nearest dataitem of a series to the left of the
     * point we clicked on the chart by using dichotomy.
     *
     * @param chartX Position of the click on the domain axis
     * @param begin Lower bound of current interval
     * @param end Upper bound of current interval
     * @param series Index of series in dataset
     * @param dataset Data used by the chart
     * @return Index of dataitem in the series
     */
    public static int getNearestLeftPoint(double chartX, int begin, int end, int series, @Nonnull XYDataset dataset) {
        // Index of center point
        int mid = begin + (end - begin) / 2;

        // Click is totally on the left side of chart panel
        if (mid == 0) {
            return 0;
        }

        if (mid < dataset.getItemCount(series) - 1) {
            // Get positions on the domain axis
            double left = dataset.getXValue(series, mid);
            double right = dataset.getXValue(series, mid + 1);

            if (left <= chartX && right >= chartX) // We've found our target
            {
                return mid;
            } else if (left <= chartX && right <= chartX) // Our target is on the
            // right side from mid
            {
                return getNearestLeftPoint(chartX, mid + 1, end, series, dataset);
            } else // Our target is on the left side from mid
            {
                return getNearestLeftPoint(chartX, begin, mid, series, dataset);
            }
        } else // Click is totally on the right side of chart panel
        {
            return dataset.getItemCount(series) - 1;
        }
    }
    private static final int TOL = 3;
    private static final int NO_SERIES_FOUND_INDEX = -1;

    @Nonnull
    public static LegendItemEntity createFakeLegendItemEntity(XYDataset dataset, Comparable<?> seriesKey) {
        LegendItemEntity result = new LegendItemEntity(new Area());
        result.setDataset(dataset);
        result.setSeriesKey(seriesKey);
        return result;
    }

    @Nullable
    public static LegendItemEntity getSeriesForPoint(@Nonnull Point pt, @Nonnull ChartPanel cp) {

        final double chartX;
        final double chartY;
        final Rectangle2D plotArea;
        final XYPlot plot;
        {
            // Let's find the X and Y values of the clicked point
            Point2D p = cp.translateScreenToJava2D(pt);
            chartX = p.getX();
            chartY = p.getY();
            // Let's find plotArea and plot
            XYPlot tmpPlot = cp.getChart().getXYPlot();
            PlotRenderingInfo plotInfo = cp.getChartRenderingInfo().getPlotInfo();
            if (tmpPlot instanceof CombinedDomainXYPlot) {
                int subplotIndex = plotInfo.getSubplotIndex(p);
                if (subplotIndex == -1) {
                    return null;
                }
                plotArea = plotInfo.getSubplotInfo(subplotIndex).getDataArea();
                plot = ((CombinedDomainXYPlot) tmpPlot).findSubplot(plotInfo, p);
            } else {
                plotArea = plotInfo.getDataArea();
                plot = tmpPlot;
            }
        }

        // Let's avoid unnecessary computation
        final ValueAxis domainAxis = plot.getDomainAxis();
        final ValueAxis rangeAxis = plot.getRangeAxis();
        final RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();
        final RectangleEdge rangeAxisEdge = plot.getRangeAxisEdge();
        final double x = domainAxis.java2DToValue(chartX, plotArea, domainAxisEdge);

        final double sensitivity = TOL;
        double distanceClickSeries = TOL + 1;

        Entry<XYDataset, Comparable> result = null;

        // For each series in each datasets
        for (XYDataset dataset : asDatasetList(plot)) {
            for (int series = 0; series < dataset.getSeriesCount(); series++) {
                // Index of the closest data item of the current series just left to the click
                int lp = getNearestLeftPoint(x, 0, dataset.getItemCount(series) - 1, series, dataset);

                try {
                    // X and Y values of data items to the left and to the right
                    double leftX = dataset.getXValue(series, lp);
                    double leftY = dataset.getYValue(series, lp);
                    double rightX = dataset.getXValue(series, lp + 1);
                    double rightY = dataset.getYValue(series, lp + 1);

                    double lx = domainAxis.valueToJava2D(leftX, plotArea, domainAxisEdge);
                    double ly = rangeAxis.valueToJava2D(leftY, plotArea, rangeAxisEdge);
                    double rx = domainAxis.valueToJava2D(rightX, plotArea, domainAxisEdge);
                    double ry = rangeAxis.valueToJava2D(rightY, plotArea, rangeAxisEdge);

                    // Distance to left point
                    double distL = Point2D.distance(lx, ly, chartX, chartY);
                    // Distance to right point
                    double distR = Point2D.distance(rx, ry, chartX, chartY);
                    // Average of both distances
                    double distLRavg = (distL + distR) / 2d;
                    // Distance to the segment between L and R
                    //double distSeg = Line2D.ptSegDist(leftX, leftY, rightX, rightY, chartX, chartY);
                    double distSeg = ptSegDist(lx, ly, rx, ry, chartX, chartY);

                    // With a line renderer, this is probably a bit of overkill as
                    // distSeg would be enough, but it becomes more reliable to check all these
                    // if using splines
                    double tmp = Math.min(Math.min(distSeg, Math.min(distL, distR)), distLRavg);

                    // Are we closer than the previous series?
                    if (tmp < sensitivity && tmp < distanceClickSeries) {
                        distanceClickSeries = tmp;
                        result = new SimpleEntry<>(dataset, dataset.getSeriesKey(series));
                    }
                } catch (Exception ex) {
                    /*
                     * An exception might happen when some series have less data
                     * than others, catching the the exception here will simply rule
                     * them out from the detection on this click
                     */
                }
            }
        }

        return result != null ? createFakeLegendItemEntity(result.getKey(), result.getValue()) : null;
    }

    /**
     * Finds the series selected by a click on a ChartPanel (from JFreeChart)
     *
     * @param pt Point where the mouse click happened
     * @param cp ChartPanel being clicked on
     * @return Index of the series in the chart; -1 if no series found
     * @deprecated use {@link #getSeriesForPoint(java.awt.Point, org.jfree.chart.ChartPanel)
     * } instead
     */
    @Deprecated
    public static int getSelectedSeries(@Nonnull Point pt, @Nonnull ChartPanel cp) {
        LegendItemEntity result = getSeriesForPoint(pt, cp);
        return result != null ? ((SeriesDataset) result.getDataset()).indexOf(result.getSeriesKey()) : NO_SERIES_FOUND_INDEX;
    }

    @Nonnull
    public static ChartPanel avoidScaling(@Nonnull ChartPanel chartPanel) {
        chartPanel.setMinimumDrawWidth(1);
        chartPanel.setMinimumDrawHeight(1);
        chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
        chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
        return chartPanel;
    }

    /**
     * A sparkline is a type of information graphic characterized by its small
     * size and high data density. Sparklines present trends and variations
     * associated with some measurement, such as average temperature or stock
     * market activity, in a simple and condensed way. Several sparklines are
     * often used together as elements of a small multiple.<br>
     *
     * {@link http://en.wikipedia.org/wiki/Sparkline}
     *
     * @param dataset
     * @return
     * @author Philippe Charles
     */
    @Nonnull
    public static JFreeChart createSparkLineChart(@Nonnull XYDataset dataset) {
        JFreeChart result = ChartFactory.createTimeSeriesChart(null, null, null, dataset, false, false, false);
        result.setBorderVisible(false);
        result.setBackgroundPaint(null);
        result.setAntiAlias(true);
        XYPlot plot = result.getXYPlot();
        plot.getRangeAxis().setVisible(false);
        plot.getDomainAxis().setVisible(false);
        plot.setDomainGridlinesVisible(false);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setRangeCrosshairVisible(false);
        plot.setOutlineVisible(false);
        plot.setInsets(RectangleInsets.ZERO_INSETS);
        plot.setAxisOffset(RectangleInsets.ZERO_INSETS);
        plot.setBackgroundPaint(null);
        ((XYLineAndShapeRenderer) plot.getRenderer()).setAutoPopulateSeriesPaint(false);
        return result;
    }

    /**
     * need focus for inputmap
     *
     * @param p
     * @return
     */
    @Nonnull
    public static ChartPanel enableFocusOnClick(@Nonnull ChartPanel p) {
        p.addMouseListener(FOCUS_ON_CLICK);
        return p;
    }

    public static boolean isPopup(@Nonnull MouseEvent e) {
        return !SwingUtilities.isLeftMouseButton(e);
    }

    public static boolean isDoubleClick(@Nonnull MouseEvent e) {
        return e.getClickCount() > 1;
    }

    /**
     * Compute an ItemLabelPosition by dividing the drawing bounds into 4 areas.
     *
     * @param bounds
     * @param x
     * @param y
     * @return
     */
    @Nonnull
    public static ItemLabelPosition computeItemLabelPosition(@Nonnull Rectangle bounds, double x, double y) {
        boolean left = x < bounds.x + bounds.width / 2;
        boolean top = y < bounds.y + bounds.height / 2;
        return left ? (top ? TOP_LEFT : BOTTOM_LEFT) : (top ? TOP_RIGHT : BOTTOM_RIGHT);
    }

    public static boolean isNullOrEmpty(SeriesDataset dataset) {
        return dataset == null || dataset.getSeriesCount() == 0;
    }

    @Nonnull
    public static IntervalXYDataset emptyXYDataset() {
        return EmptyDataset.INSTANCE;
    }

    @Nonnull
    public static List<XYDataset> asDatasetList(@Nonnull final XYPlot plot) {
        return new AbstractList<XYDataset>() {

            @Override
            public XYDataset get(int index) {
                return plot.getDataset(index);
            }

            @Override
            public int size() {
                return plot.getDatasetCount();
            }
        };
    }

    public static void drawItemLabelAsTooltip(Graphics2D g2, double x, double y, double anchorOffset, String label, Font font, Paint paint, Paint fillPaint, Paint outlinePaint, Stroke outlineStroke) {
        JTimeSeriesRendererSupport.drawToolTip(g2, x, y, anchorOffset, label, font, paint, fillPaint, outlinePaint, outlineStroke);
    }

    public static void copyChart(@Nonnull ChartPanel chartPanel) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Insets insets = chartPanel.getInsets();
        int w = chartPanel.getWidth() - insets.left - insets.right;
        int h = chartPanel.getHeight() - insets.top - insets.bottom;
        Transferable selection = new ChartTransferable2(chartPanel.getChart(), w, h,
                chartPanel.getMinimumDrawWidth(), chartPanel.getMinimumDrawHeight(),
                chartPanel.getMaximumDrawWidth(), chartPanel.getMaximumDrawHeight(), true);
        clipboard.setContents(selection, null);
    }

    public static void saveChart(@Nonnull ChartPanel chartPanel) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter defaultFilter = new FileNameExtensionFilter("PNG (.png)", "png");
        fileChooser.addChoosableFileFilter(defaultFilter);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPG (.jpg) (.jpeg)", "jpg", "jpeg"));
        if (Charts.canWriteChartAsSVG()) {
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("SVG (.svg)", "svg"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Compressed SVG (.svgz)", "svgz"));
        }
        fileChooser.setFileFilter(defaultFilter);
        File currentDir = chartPanel.getDefaultDirectoryForSaveAs();
        if (currentDir != null) {
            fileChooser.setCurrentDirectory(currentDir);
        }
        if (fileChooser.showSaveDialog(chartPanel) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (OutputStream stream = Files.newOutputStream(file.toPath())) {
                writeChart(getMediaType(file), stream, chartPanel.getChart(), chartPanel.getWidth(), chartPanel.getHeight());
            }
            chartPanel.setDefaultDirectoryForSaveAs(fileChooser.getCurrentDirectory());
        }
    }

    public static void writeChart(@Nonnull String mediaType, @Nonnull OutputStream stream, @Nonnull JFreeChart chart, @Nonnegative int width, @Nonnegative int height) throws IOException {
        switch (mediaType) {
            case SVG_MEDIA_TYPE:
                Charts.writeChartAsSVG(stream, chart, width, height);
                break;
            case SVG_COMP_MEDIA_TYPE:
                try (GZIPOutputStream gzip = new GZIPOutputStream(stream)) {
                    Charts.writeChartAsSVG(gzip, chart, width, height);
                }
                break;
            case JPEG_MEDIA_TYPE:
                ChartUtilities.writeChartAsJPEG(stream, chart, width, height);
                break;
            case PNG_MEDIA_TYPE:
                ChartUtilities.writeChartAsPNG(stream, chart, width, height);
                break;
            default:
                throw new IOException("Media type '" + mediaType + "' not supported");
        }
    }

    public static void writeChartAsSVG(@Nonnull OutputStream stream, @Nonnull JFreeChart chart, @Nonnegative int width, @Nonnegative int height) throws IOException {
        String svg = generateSVG(chart, width, height);
        try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            writer.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
            writer.write(svg + "\n");
            writer.flush();
        }
    }

    public static boolean canWriteChartAsSVG() {
        try {
            Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Internal Implementation">
    private static final ItemLabelPosition TOP_LEFT = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE4, TextAnchor.TOP_LEFT);
    private static final ItemLabelPosition TOP_RIGHT = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE8, TextAnchor.TOP_RIGHT);
    private static final ItemLabelPosition BOTTOM_LEFT = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE2, TextAnchor.BOTTOM_LEFT);
    private static final ItemLabelPosition BOTTOM_RIGHT = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE10, TextAnchor.BOTTOM_RIGHT);

    private static final MouseListener FOCUS_ON_CLICK = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof ChartPanel) {
                ((ChartPanel) e.getSource()).requestFocusInWindow();
            }
        }
    };

    private static final class EmptyDataset extends AbstractIntervalXYDataset {

        static final EmptyDataset INSTANCE = new EmptyDataset();

        @Override
        public int getSeriesCount() {
            return 0;
        }

        @Override
        public Comparable getSeriesKey(int series) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getItemCount(int series) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Number getX(int series, int item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Number getY(int series, int item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Number getStartX(int series, int item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Number getEndX(int series, int item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Number getStartY(int series, int item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Number getEndY(int series, int item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static String generateSVG(JFreeChart chart, int width, int height) throws IOException {
        try {
            Class<?> svgGraphics2d = Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
            Graphics2D g2 = (Graphics2D) svgGraphics2d.getConstructor(int.class, int.class).newInstance(width, height);
            // we suppress shadow generation, because SVG is a vector format and
            // the shadow effect is applied via bitmap effects...
            g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);
            chart.draw(g2, new Rectangle2D.Double(0, 0, width, height));
            return (String) g2.getClass().getMethod("getSVGElement").invoke(g2);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IOException("Cannot generate SVG", ex);
        }
    }

    private static final String PNG_MEDIA_TYPE = "image/png";
    private static final String JPEG_MEDIA_TYPE = "image/jpeg";
    private static final String SVG_MEDIA_TYPE = "image/svg+xml";
    private static final String SVG_COMP_MEDIA_TYPE = "image/svg+xml-compressed";

    @Nonnull
    private static String getMediaType(@Nonnull File file) {
        String ext = file.getPath().toLowerCase(Locale.ROOT);
        if (ext.endsWith(".png")) {
            return PNG_MEDIA_TYPE;
        }
        if (ext.endsWith(".jpeg") || ext.endsWith(".jpg")) {
            return JPEG_MEDIA_TYPE;
        }
        if (ext.endsWith(".svg")) {
            return SVG_MEDIA_TYPE;
        }
        if (ext.endsWith(".svgz")) {
            return SVG_COMP_MEDIA_TYPE;
        }
        return PNG_MEDIA_TYPE;
    }

    private static final class ChartTransferable2 extends ChartTransferable {

        private static final DataFlavor SVG_DATA_FLAVOR = new DataFlavor(SVG_MEDIA_TYPE + "; charset=unicode; class=java.lang.String", "Scalable Vector Graphics");

        private final JFreeChart chart;
        private final int width;
        private final int height;

        public ChartTransferable2(JFreeChart chart, int width, int height, int minDrawW, int minDrawH, int maxDrawW, int maxDrawH, boolean cloneData) {
            super(chart, width, height, minDrawW, minDrawH, maxDrawW, maxDrawH, cloneData);
            this.chart = chart;
            this.width = width;
            this.height = height;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] parent = super.getTransferDataFlavors();
            DataFlavor[] result = new DataFlavor[parent.length + 1];
            System.arraycopy(parent, 0, result, 0, parent.length);
            result[parent.length] = SVG_DATA_FLAVOR;
            return result;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return super.isDataFlavorSupported(flavor) || SVG_DATA_FLAVOR.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (SVG_DATA_FLAVOR.equals(flavor)) {
                try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                    writeChartAsSVG(stream, chart, width, height);
                    return new String(stream.toByteArray(), StandardCharsets.UTF_8);
                }
            }
            return super.getTransferData(flavor);
        }
    }
    //</editor-fold>
}
