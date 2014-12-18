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
import static ec.util.chart.TimeSeriesChart.RendererType.AREA;
import static ec.util.chart.TimeSeriesChart.RendererType.COLUMN;
import static ec.util.chart.TimeSeriesChart.RendererType.LINE;
import static ec.util.chart.TimeSeriesChart.RendererType.MARKER;
import static ec.util.chart.TimeSeriesChart.RendererType.SPLINE;
import static ec.util.chart.TimeSeriesChart.RendererType.STACKED_AREA;
import static ec.util.chart.TimeSeriesChart.RendererType.STACKED_COLUMN;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Size2D;

/**
 *
 * @author Philippe Charles
 */
abstract class JTimeSeriesRendererSupport implements XYItemLabelGenerator, XYSeriesLabelGenerator {

    abstract public Color getPlotColor();

    abstract public Color getSeriesColor(int series);

    abstract public Stroke getSeriesStroke(int series);

    abstract public Color getSeriesLabelColor(int series);

    abstract public String getSeriesLabel(int series);

    abstract public Font getSeriesLabelFont(int series);

    abstract public boolean isSeriesLabelVisible(int series);

    abstract public Color getObsColor(int series, int item);

    abstract public Stroke getObsStroke(int series, int item);

    abstract public boolean isObsHighlighted(int series, int item);

    abstract public String getObsLabel(int series, int item);

    abstract public Font getObsLabelFont(int series, int item);

    abstract public boolean isObsLabelVisible(int series, int item);

    @Override
    final public String generateLabel(XYDataset dataset, int series, int item) {
        return getObsLabel(series, item);
    }

    @Override
    final public String generateLabel(XYDataset dataset, int series) {
        return getSeriesLabel(series);
    }

    final public void drawItemLabel(Graphics2D g2, XYDataset dataset, int series, int item, double x, double y) {
        String label = generateLabel(dataset, series, item);
        Font font = getObsLabelFont(series, item);
        Color fillColor = getObsColor(series, item);
        Paint paint = getForegroundColor(SwingColorSchemeSupport.getLuminance(fillColor));
        Paint outlinePaint = getPlotColor();
        Stroke outlineStroke = AbstractRenderer.DEFAULT_STROKE;
        drawToolTip(g2, x, y, 3d, label, font, paint, fillColor, outlinePaint, outlineStroke);
    }

    @Nonnull
    public EnumSet<RendererType> getSupportedRendererTypes() {
        return EnumSet.of(LINE, COLUMN, SPLINE, STACKED_COLUMN, MARKER, AREA, STACKED_AREA);
    }

    @Nonnull
    public XYItemRenderer createRenderer(@Nonnull RendererType type) {
        switch (type) {
            case LINE:
                return new LineRenderer(this);
            case SPLINE:
                return new SplineRenderer(this);
            case COLUMN:
                return new BarRenderer(this);
            case MARKER:
                return new MarkerRenderer(this);
            case STACKED_COLUMN:
                return new StackedBarRenderer(this);
            case AREA:
                return new AreaRenderer(this);
            case STACKED_AREA:
                return new StackedAreaRenderer(this);
        }
        throw new RuntimeException("Not implemented");
    }

    //<editor-fold defaultstate="collapsed" desc="Renderers implementation">
    private static final Shape ITEM_SHAPE = new Ellipse2D.Double(-3, -3, 6, 6);

    private static class LineRenderer extends XYLineAndShapeRenderer {

        protected final JTimeSeriesRendererSupport support;

        public LineRenderer(JTimeSeriesRendererSupport support) {
            this.support = support;
            setBaseItemLabelsVisible(true);
            setAutoPopulateSeriesShape(false);
            setAutoPopulateSeriesFillPaint(false);
            setAutoPopulateSeriesOutlineStroke(false);
            setLegendLine(ITEM_SHAPE);
            setBaseShape(ITEM_SHAPE);
            setUseFillPaint(true);
            setLegendItemLabelGenerator(support);
            setBaseItemLabelGenerator(support);
        }

        @Override
        public boolean isItemLabelVisible(int series, int item) {
            return support.isObsLabelVisible(series, item);
        }

        @Override
        public boolean getItemShapeVisible(int series, int item) {
            return support.isObsHighlighted(series, item);
        }

        @Override
        public boolean isSeriesVisibleInLegend(int series) {
            return support.isSeriesLabelVisible(series);
        }

        @Override
        public Paint getSeriesPaint(int series) {
            return support.getSeriesColor(series);
        }

        @Override
        public Paint getItemPaint(int series, int item) {
            return support.getObsColor(series, item);
        }

        @Override
        public Paint getItemFillPaint(int series, int item) {
            return support.getPlotColor();
        }

        @Override
        public Paint getLegendTextPaint(int series) {
            return support.getSeriesLabelColor(series);
        }

        @Override
        public Stroke getSeriesStroke(int series) {
            return support.getSeriesStroke(series);
        }

        @Override
        public Stroke getItemStroke(int series, int item) {
            return support.getObsStroke(series, item);
        }

        @Override
        public Stroke getItemOutlineStroke(int series, int item) {
            return support.getSeriesStroke(series);
        }

        @Override
        public Font getLegendTextFont(int series) {
            return support.getSeriesLabelFont(series);
        }

        @Override
        protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y, boolean negative) {
            support.drawItemLabel(g2, dataset, series, item, x, y);
        }
    };

    private static class SplineRenderer extends XYSplineRenderer {

        private final JTimeSeriesRendererSupport support;

        public SplineRenderer(JTimeSeriesRendererSupport support) {
            this.support = support;
            setAutoPopulateSeriesShape(false);
            setLegendLine(ITEM_SHAPE);
            setBaseShape(ITEM_SHAPE);
            setUseFillPaint(true);
            setLegendItemLabelGenerator(support);
            setBaseItemLabelGenerator(support);
        }

        @Override
        public boolean isSeriesVisibleInLegend(int series) {
            return support.isSeriesLabelVisible(series);
        }

        @Override
        public boolean getItemShapeVisible(int series, int item) {
            return support.isObsHighlighted(series, item);
        }

        @Override
        public boolean isItemLabelVisible(int series, int item) {
            return support.isObsLabelVisible(series, item);
        }

        @Override
        public Paint getSeriesPaint(int series) {
            return support.getSeriesColor(series);
        }

        @Override
        public Paint getItemPaint(int series, int item) {
            // always use last item, see: XYSplineRenderer#drawPrimaryLineAsPath()
            return support.getSeriesColor(series);
        }

        @Override
        public Paint getItemFillPaint(int series, int item) {
            return support.getPlotColor();
        }

        @Override
        public Paint getLegendTextPaint(int series) {
            return support.getSeriesLabelColor(series);
        }

        @Override
        public Stroke getSeriesStroke(int series) {
            return support.getSeriesStroke(series);
        }

        @Override
        public Stroke getItemStroke(int series, int item) {
            // always use last item, see: XYSplineRenderer#drawPrimaryLineAsPath()
            return support.getSeriesStroke(series);
        }

        @Override
        public Stroke getItemOutlineStroke(int series, int item) {
            return support.getSeriesStroke(series);
        }

        @Override
        public Font getLegendTextFont(int series) {
            return support.getSeriesLabelFont(series);
        }

        @Override
        protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y, boolean negative) {
            support.drawItemLabel(g2, dataset, series, item, x, y);
        }
    }

    private static class BarRenderer extends ClusteredXYBarRenderer2 {

        private final JTimeSeriesRendererSupport support;

        public BarRenderer(JTimeSeriesRendererSupport support) {
            this.support = support;
            setAutoPopulateSeriesOutlineStroke(false);
            setMargin(.1);
            setLegendBar(ITEM_SHAPE);
            setShadowVisible(false);
            setDrawBarOutline(true);
            setBaseItemLabelsVisible(true);
            setBarPainter(new StandardXYBarPainter()); // avoid gradient
            setDrawBarOutline(false);
            setLegendItemLabelGenerator(support);
            setBaseItemLabelGenerator(support);
        }

        @Override
        public boolean isItemLabelVisible(int series, int item) {
            return support.isObsLabelVisible(series, item);
        }

        @Override
        public boolean isSeriesVisibleInLegend(int series) {
            return support.isSeriesLabelVisible(series);
        }

        @Override
        public Paint getSeriesPaint(int series) {
            return support.getSeriesColor(series);
        }

        @Override
        public Paint getItemPaint(int series, int item) {
            Color result = support.getObsColor(series, item);
            return support.isObsHighlighted(series, item) ? result.brighter() : result;
        }

        @Override
        public Paint getLegendTextPaint(int series) {
            return support.getSeriesLabelColor(series);
        }

        @Override
        public Font getLegendTextFont(int series) {
            return support.getSeriesLabelFont(series);
        }

        @Override
        protected void drawItemLabel(Graphics2D g2, XYDataset dataset, int series, int item, XYPlot plot, XYItemLabelGenerator generator, Rectangle2D bar, boolean negative) {
            support.drawItemLabel(g2, dataset, series, item, bar.getCenterX(), bar.getCenterY());
        }
    }

    private static class MarkerRenderer extends LineRenderer {

        public MarkerRenderer(JTimeSeriesRendererSupport support) {
            super(support);
        }

        @Override
        public Boolean getSeriesLinesVisible(int series) {
            return Boolean.FALSE;
        }

        @Override
        public boolean getItemShapeVisible(int series, int item) {
            return true;
        }

        @Override
        public Stroke getItemOutlineStroke(int series, int item) {
            return support.getObsStroke(series, item);
        }

        @Override
        public boolean getItemShapeFilled(int series, int item) {
            return support.isObsHighlighted(series, item);
        }

        @Override
        public Paint getItemFillPaint(int series, int item) {
            return support.getObsColor(series, item);
        }
    }

    private static class StackedBarRenderer extends StackedXYBarRenderer {

        private final JTimeSeriesRendererSupport support;

        public StackedBarRenderer(JTimeSeriesRendererSupport support) {
            this.support = support;
            setAutoPopulateSeriesOutlineStroke(false);
            setMargin(.1);
            setLegendBar(ITEM_SHAPE);
            setShadowVisible(false);
            setDrawBarOutline(true);
            setBaseItemLabelsVisible(true);
            setBarPainter(new StandardXYBarPainter()); // avoid gradient
            setDrawBarOutline(false);
            setLegendItemLabelGenerator(support);
            setBaseItemLabelGenerator(support);
        }

        @Override
        public Range findRangeBounds(XYDataset dataset) {
            // Fix NumberAxis#setAutoRangeIncludesZero()
            return dataset.getSeriesCount() != 0 ? super.findRangeBounds(dataset) : null;
        }

        @Override
        public boolean isItemLabelVisible(int series, int item) {
            return support.isObsLabelVisible(series, item);
        }

        @Override
        public boolean isSeriesVisibleInLegend(int series) {
            return support.isSeriesLabelVisible(series);
        }

        @Override
        public Paint getSeriesPaint(int series) {
            return support.getSeriesColor(series);
        }

        @Override
        public Paint getItemPaint(int series, int item) {
            Color result = support.getObsColor(series, item);
            return support.isObsHighlighted(series, item) ? result.brighter() : result;
        }

        @Override
        public Paint getLegendTextPaint(int series) {
            return support.getSeriesLabelColor(series);
        }

        @Override
        public Font getLegendTextFont(int series) {
            return support.getSeriesLabelFont(series);
        }

        @Override
        protected void drawItemLabel(Graphics2D g2, XYDataset dataset, int series, int item, XYPlot plot, XYItemLabelGenerator generator, Rectangle2D bar, boolean negative) {
            support.drawItemLabel(g2, dataset, series, item, bar.getCenterX(), bar.getCenterY());
        }
    }

    private static final class AreaRenderer extends XYAreaRenderer2 {

        private final JTimeSeriesRendererSupport support;

        public AreaRenderer(JTimeSeriesRendererSupport support) {
            this.support = support;
            setAutoPopulateSeriesShape(false);
            setAutoPopulateSeriesFillPaint(false);
            setAutoPopulateSeriesOutlineStroke(false);
            setBaseItemLabelsVisible(true);
            setLegendItemLabelGenerator(support);
            setBaseItemLabelGenerator(support);
            setLegendArea(ITEM_SHAPE);
        }

        @Override
        public boolean isItemLabelVisible(int row, int column) {
            return support.isObsLabelVisible(row, column);
        }

        @Override
        public boolean isSeriesVisibleInLegend(int series) {
            return support.isSeriesLabelVisible(series);
        }

        @Override
        public Paint getSeriesPaint(int series) {
            return support.getSeriesColor(series);
        }

        @Override
        public Paint getItemPaint(int series, int item) {
            Color result = support.getObsColor(series, item);
            return support.isObsHighlighted(series, item) ? result.brighter() : result;
        }

        @Override
        public Paint getLegendTextPaint(int series) {
            return support.getSeriesLabelColor(series);
        }

        @Override
        public Font getLegendTextFont(int series) {
            return support.getSeriesLabelFont(series);
        }

        // FIXME: method never called
        @Override
        protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y, boolean negative) {
            support.drawItemLabel(g2, dataset, series, item, x, y);
        }
    }

    private static final class StackedAreaRenderer extends StackedXYAreaRenderer2 {

        private final JTimeSeriesRendererSupport support;

        public StackedAreaRenderer(JTimeSeriesRendererSupport support) {
            this.support = support;
            setAutoPopulateSeriesShape(false);
            setAutoPopulateSeriesFillPaint(false);
            setAutoPopulateSeriesOutlineStroke(false);
            setBaseItemLabelsVisible(true);
            setLegendItemLabelGenerator(support);
            setBaseItemLabelGenerator(support);
            setLegendArea(ITEM_SHAPE);
        }

        @Override
        public boolean isItemLabelVisible(int row, int column) {
            return support.isObsLabelVisible(row, column);
        }

        @Override
        public boolean isSeriesVisibleInLegend(int series) {
            return support.isSeriesLabelVisible(series);
        }

        @Override
        public Paint getSeriesPaint(int series) {
            return support.getSeriesColor(series);
        }

        @Override
        public Paint getItemPaint(int series, int item) {
            Color result = support.getObsColor(series, item);
            return support.isObsHighlighted(series, item) ? result.brighter() : result;
        }

        @Override
        public Paint getLegendTextPaint(int series) {
            return support.getSeriesLabelColor(series);
        }

        @Override
        public Font getLegendTextFont(int series) {
            return support.getSeriesLabelFont(series);
        }

        // FIXME: method never called
        @Override
        protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y, boolean negative) {
            support.drawItemLabel(g2, dataset, series, item, x, y);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Custom tooltip">
    // package-visible to be used by Charts.
    static void drawToolTip(Graphics2D g2, double x, double y, double anchorOffset, String label, Font font, Paint paint, Paint fillPaint, Paint outlinePaint, Stroke outlineStroke) {
        LabelBlock block = new LabelBlock(label/*.replace("\n", "")*/, font, paint);
        block.setMargin(3, 3, 3, 3);

        Rectangle2D hotspot = createHotspot(g2, x, y, anchorOffset + 10, block.arrange(g2));
        Shape shape = createShape(x, y, hotspot);

        if (fillPaint != null) {
            g2.setPaint(fillPaint);
            g2.fill(shape);
        }

        if (outlinePaint != null && outlineStroke != null) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(shape);
        }

        block.draw(g2, hotspot);
    }
    
    private static Color getForegroundColor(double luminance) {
        return (luminance > 127) ? Color.BLACK : Color.WHITE;
    }

    private static Shape createShape(double x, double y, Rectangle2D hotspot) {
        Area result = new Area(new RoundRectangle2D.Double(hotspot.getX(), hotspot.getY(), hotspot.getWidth(), hotspot.getHeight(), 8, 8));

        boolean right = hotspot.getMinX() > x;

        Polygon po = new Polygon();
        po.addPoint(0, 0);
        po.addPoint(0, 10);
        po.addPoint(10, 0);
        AffineTransform af = new AffineTransform();
        if (right) {
            af.translate(hotspot.getX() - 7, hotspot.getY() + hotspot.getHeight() / 2);
            af.rotate(-Math.PI / 4);
        } else {
            af.translate(hotspot.getMaxX() + 7, hotspot.getY() + hotspot.getHeight() / 2);
            af.rotate(Math.PI * 3 / 4);
        }

        Shape shape = af.createTransformedShape(po);
        result.add(new Area(shape));
        return result;
    }

    private static Rectangle2D createHotspot(Graphics2D g2, double x, double y, double xOffset, Size2D blockSize) {
        Rectangle bounds = g2.getClipBounds();
        bounds = new Rectangle(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
        return createHotspot(bounds, x, y, xOffset, blockSize);
    }

    private static Rectangle2D createHotspot(Rectangle bounds, double x, double y, double xOffset, Size2D blockSize) {
        double xx = (x + xOffset + blockSize.width < bounds.getMaxX()) ? (x + xOffset) : (x - xOffset - blockSize.width);
        double halfHeight = blockSize.height / 2;
        double yy = (y - halfHeight < bounds.getMinY()) ? (bounds.getMinY()) : (y + halfHeight > bounds.getMaxY()) ? (bounds.getMaxY() - blockSize.height) : (y - halfHeight);
        return new Rectangle2D.Double(xx, yy, blockSize.width, blockSize.height);
    }
    //</editor-fold>
}
