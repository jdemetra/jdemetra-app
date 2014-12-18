/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.chart.swing;

import java.util.List;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

/**
 *
 * @author Philippe Charles
 */
public class HighlightChartMouseListener implements ChartMouseListener {

    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
        // FIXME: drag problem if you put mousePressed code here
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
        if (event.getChart().getPlot() instanceof XYPlot) {
            handle(event.getChart().getXYPlot(), event.getEntity());
        }
    }

    private void handle(XYPlot plot, ChartEntity entity) {
        if (entity instanceof XYItemEntity) {
            highlight(plot, (XYItemEntity) entity);
        } else {
            reset(plot);
        }
    }

    private void highlight(XYPlot plot, XYItemEntity entity) {
        if (plot instanceof CombinedDomainXYPlot) {
            for (XYPlot o : (List<XYPlot>) ((CombinedDomainXYPlot) plot).getSubplots()) {
                highlight(o, entity);
            }
        } else {
            XYItemRenderer r = plot.getRendererForDataset(entity.getDataset());
            if (r instanceof HighlightedXYItemRenderer2) {
                ((HighlightedXYItemRenderer2) r).setHighlightedItem(entity.getSeriesIndex(), entity.getItem());
            }
        }
    }

    private void reset(XYPlot plot) {
        if (plot instanceof CombinedDomainXYPlot) {
            for (XYPlot o : (List<XYPlot>) ((CombinedDomainXYPlot) plot).getSubplots()) {
                reset(o);
            }
        } else {
            for (int i = 0; i < plot.getRendererCount(); i++) {
                XYItemRenderer r = plot.getRenderer(i);
                if (r instanceof HighlightedXYItemRenderer2) {
                    ((HighlightedXYItemRenderer2) r).setHighlightedItem(-1, -1);
                }
            }
        }
    }
}
