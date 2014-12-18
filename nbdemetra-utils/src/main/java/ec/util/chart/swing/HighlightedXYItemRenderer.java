/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.chart.swing;

import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.event.RendererChangeEvent;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public interface HighlightedXYItemRenderer extends HighlightedXYItemRenderer2 {

    /**
     * Returns the paint used to draw the background of item labels for a
     * series.
     *
     * @param series the series index (zero based).
     *
     * @return The background paint (possibly <code>null</code>).
     *
     * @see #setSeriesItemLabelBackgroundPaint(int, Paint)
     */
    Paint getSeriesItemLabelBackgroundPaint(int series);

    Paint getSeriesItemLabelOutlinePaint(int series);

    Stroke getSeriesItemLabelOutlineStroke(int series);

    /**
     * Sets the item label background paint for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series the series (zero based index).
     * @param paint the paint (<code>null</code> permitted).
     *
     * @see #getItemLabelBackgroundPaint(int,int)
     */
    void setSeriesItemLabelBackgroundPaint(int series, Paint paint);

    void setSeriesItemLabelOutlinePaint(int series, Paint paint);

    void setSeriesItemLabelOutlineStroke(int series, Stroke paint);
}
