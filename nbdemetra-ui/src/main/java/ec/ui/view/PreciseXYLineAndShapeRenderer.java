/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view;

import java.awt.Shape;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Kristof Bayens
 */
public class PreciseXYLineAndShapeRenderer extends XYLineAndShapeRenderer {

    public PreciseXYLineAndShapeRenderer(boolean lines, boolean shapes) {
        super(lines, shapes);
    }

    @Override
    protected void addEntity(EntityCollection entities, Shape area, XYDataset dataset, int series, int item, double entityX, double entityY) {
        if (area.getBounds().width < 2 || area.getBounds().height < 2)
            super.addEntity(entities, null, dataset, series, item, entityX, entityY);
        else
            super.addEntity(entities, area, dataset, series, item, entityX, entityY);
    }
}
