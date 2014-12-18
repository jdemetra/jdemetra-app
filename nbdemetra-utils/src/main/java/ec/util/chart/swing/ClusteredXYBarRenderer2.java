/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.chart.swing;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

/**
 * Extension of ClusteredXYBarRenderer that fixes ItemVisibility &
 * ItemLabelDrawing bugs.
 *
 * @author Philippe Charles
 */
class ClusteredXYBarRenderer2 extends ClusteredXYBarRenderer {

    private static final int SHADOW_PASS = 0;
    private static final int BAR_PASS = 1;
    private static final int LABEL_PASS = 2;
    //
    protected final boolean centerBarAtStartValue;

    public ClusteredXYBarRenderer2() {
        this(0.0, false);
    }

    public ClusteredXYBarRenderer2(double margin, boolean centerBarAtStartValue) {
        super(margin, centerBarAtStartValue);
        this.centerBarAtStartValue = centerBarAtStartValue;
    }

    @Override
    public int getPassCount() {
        return 3;
    }

    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        // do nothing if item is not visible
        if (!getItemVisible(series, item)) {
            return;
        }
        // do nothing during shadow pass if shadow is not visible
        if (pass == SHADOW_PASS && !getShadowsVisible()) {
            return;
        }
        // do nothing during label pass if label is not visible
        if (pass == LABEL_PASS && !isItemLabelVisible(series, item)) {
            return;
        }

        IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;

        double y0;
        double y1;
        if (getUseYInterval()) {
            y0 = intervalDataset.getStartYValue(series, item);
            y1 = intervalDataset.getEndYValue(series, item);
        } else {
            y0 = getBase();
            y1 = intervalDataset.getYValue(series, item);
        }
        if (Double.isNaN(y0) || Double.isNaN(y1)) {
            return;
        }

        double yy0 = rangeAxis.valueToJava2D(y0, dataArea,
                plot.getRangeAxisEdge());
        double yy1 = rangeAxis.valueToJava2D(y1, dataArea,
                plot.getRangeAxisEdge());

        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        double x0 = intervalDataset.getStartXValue(series, item);
        double xx0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);

        double x1 = intervalDataset.getEndXValue(series, item);
        double xx1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);

        double intervalW = xx1 - xx0;  // this may be negative
        double baseX = xx0;
        if (this.centerBarAtStartValue) {
            baseX = baseX - intervalW / 2.0;
        }
        double m = getMargin();
        if (m > 0.0) {
            double cut = intervalW * getMargin();
            intervalW = intervalW - cut;
            baseX = baseX + (cut / 2);
        }

        double intervalH = Math.abs(yy0 - yy1);  // we don't need the sign

        PlotOrientation orientation = plot.getOrientation();

        int numSeries = dataset.getSeriesCount();
        double seriesBarWidth = intervalW / numSeries;  // may be negative

        Rectangle2D bar;
        if (orientation == PlotOrientation.HORIZONTAL) {
            double barY0 = baseX + (seriesBarWidth * series);
            double barY1 = barY0 + seriesBarWidth;
            double rx = Math.min(yy0, yy1);
            double rw = intervalH;
            double ry = Math.min(barY0, barY1);
            double rh = Math.abs(barY1 - barY0);
            bar = new Rectangle2D.Double(rx, ry, rw, rh);
        } else {
            double barX0 = baseX + (seriesBarWidth * series);
            double barX1 = barX0 + seriesBarWidth;
            double rx = Math.min(barX0, barX1);
            double rw = Math.abs(barX1 - barX0);
            double ry = Math.min(yy0, yy1);
            double rh = intervalH;
            bar = new Rectangle2D.Double(rx, ry, rw, rh);
        }
        boolean positive = (y1 > 0.0);
        boolean inverted = rangeAxis.isInverted();
        RectangleEdge barBase;
        if (orientation == PlotOrientation.HORIZONTAL) {
            if (positive && inverted || !positive && !inverted) {
                barBase = RectangleEdge.RIGHT;
            } else {
                barBase = RectangleEdge.LEFT;
            }
        } else {
            if (positive && !inverted || !positive && inverted) {
                barBase = RectangleEdge.BOTTOM;
            } else {
                barBase = RectangleEdge.TOP;
            }
        }

        switch (pass) {
            case SHADOW_PASS:
                getBarPainter().paintBarShadow(g2, this, series, item, bar, barBase, !getUseYInterval());
                break;
            case BAR_PASS:
                getBarPainter().paintBar(g2, this, series, item, bar, barBase);
                // add an entity for the item...
                if (info != null) {
                    EntityCollection entities = info.getOwner().getEntityCollection();
                    if (entities != null) {
                        addEntity(entities, bar, dataset, series, item, bar.getCenterX(), bar.getCenterY());
                    }
                }
                break;
            case LABEL_PASS:
                XYItemLabelGenerator generator = getItemLabelGenerator(series, item);
                drawItemLabel(g2, dataset, series, item, plot, generator, bar, y1 < 0.0);
                break;
        }
    }
}
