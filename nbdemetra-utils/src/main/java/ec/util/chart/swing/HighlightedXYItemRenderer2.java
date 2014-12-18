/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.chart.swing;

import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 *
 * @author Philippe Charles
 */
public interface HighlightedXYItemRenderer2 extends XYItemRenderer {

    void setHighlightedItem(int row, int column);
}
