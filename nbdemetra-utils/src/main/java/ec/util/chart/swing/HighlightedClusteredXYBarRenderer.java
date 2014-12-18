/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.chart.swing;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.util.PaintList;
import org.jfree.util.StrokeList;

/**
 *
 * @author Philippe Charles
 */
public class HighlightedClusteredXYBarRenderer extends ClusteredXYBarRenderer2 implements HighlightedXYItemRenderer {

    final Point highlight;
    final PaintList itemLabelBackgroundPaintList;
    final PaintList itemLabelOutlinePaintList;
    final StrokeList itemLabelOutlineStrokeList;

    public HighlightedClusteredXYBarRenderer() {
        super();
        this.highlight = new Point(-1, -1);
        this.itemLabelBackgroundPaintList = new PaintList();
        this.itemLabelOutlinePaintList = new PaintList();
        this.itemLabelOutlineStrokeList = new StrokeList();
    }

    @Override
    public void setHighlightedItem(int series, int item) {
        if (highlight.x != series || highlight.y != item) {
            highlight.setLocation(series, item);
            notifyListeners(new RendererChangeEvent(this));
        }
    }

    protected boolean isHighlightedItem(int series, int item) {
        return highlight.x == series && highlight.y == item;
    }

    @Override
    public boolean isItemLabelVisible(int series, int item) {
        return isHighlightedItem(series, item);
    }

    //<editor-fold defaultstate="collapsed" desc="SeriesItemLabel Getters/Setters">
    @Override
    public Paint getSeriesItemLabelBackgroundPaint(int series) {
        return itemLabelBackgroundPaintList.getPaint(series);
    }

    @Override
    public void setSeriesItemLabelBackgroundPaint(int series, Paint paint) {
        itemLabelBackgroundPaintList.setPaint(series, paint);
        notifyListeners(new RendererChangeEvent(this));
    }

    @Override
    public Paint getSeriesItemLabelOutlinePaint(int series) {
        return itemLabelOutlinePaintList.getPaint(series);
    }

    @Override
    public void setSeriesItemLabelOutlinePaint(int series, Paint paint) {
        itemLabelOutlinePaintList.setPaint(series, paint);
        notifyListeners(new RendererChangeEvent(this));
    }

    @Override
    public Stroke getSeriesItemLabelOutlineStroke(int series) {
        return itemLabelOutlineStrokeList.getStroke(series);
    }

    @Override
    public void setSeriesItemLabelOutlineStroke(int series, Stroke paint) {
        itemLabelOutlineStrokeList.setStroke(series, paint);
        notifyListeners(new RendererChangeEvent(this));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ItemLabel Getters/Setters">
    /**
     * @deprecated use {@link #getItemLabelFillPaint(int, int) } instead
     */
    @Deprecated
    public Paint getItemLabelBackgroundPaint(int series, int item) {
        return getSeriesItemLabelBackgroundPaint(series);
    }

    public Paint getItemLabelFillPaint(int series, int item) {
        return getItemLabelBackgroundPaint(series, item);
    }

    public Paint getItemLabelOutlinePaint(int series, int item) {
        return getSeriesItemLabelOutlinePaint(series);
    }

    public Stroke getItemLabelOutlineStroke(int series, int item) {
        return getSeriesItemLabelOutlineStroke(series);
    }
    //</editor-fold>

    @Override
    protected void drawItemLabel(Graphics2D g2, XYDataset dataset, int series, int item, XYPlot plot, XYItemLabelGenerator generator, Rectangle2D bar, boolean negative) {
        if (generator == null) {
            return;  // nothing to do
        }
        String label = generator.generateLabel(dataset, series, item);
        if (label == null || label.isEmpty()) {
            return;  // nothing to do
        }
        drawItemLabel(g2, bar.getCenterX(), bar.getCenterY(), negative, plot.getOrientation(), series, item, label);
    }

    protected void drawItemLabel(Graphics2D g2, double x, double y, boolean negative, PlotOrientation orientation, int series, int item, String label) {
        label = " " + label + " ";

//        ItemLabelPosition position = !negative ? getPositiveItemLabelPosition(series, item) : getNegativeItemLabelPosition(series, item);
        ItemLabelPosition position = Charts.computeItemLabelPosition(g2.getClipBounds(), x, y);
        Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);

        // font is used to compute hotspot!
        g2.setFont(getItemLabelFont(series, item));

        Shape hotspot = TextUtilities.calculateRotatedStringBounds(label, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(),
                position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());

        // paint background
        Paint fillPaint = getItemLabelFillPaint(series, item);
        if (fillPaint != null) {
            g2.setPaint(fillPaint);
            g2.fill(hotspot);
        }

        // paint foreground
        Paint paint = getItemLabelPaint(series, item);
        if (paint != null) {
            g2.setPaint(paint);
            TextUtilities.drawRotatedString(label, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(),
                    position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }

        // paint border
        Paint outlinePaint = getItemLabelOutlinePaint(series, item);
        Stroke outlineStroke = getItemLabelOutlineStroke(series, item);
        if (outlinePaint != null && outlineStroke != null) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(hotspot);
        }
    }
}
