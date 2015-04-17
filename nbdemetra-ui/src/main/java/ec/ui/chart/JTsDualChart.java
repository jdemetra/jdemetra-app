/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.chart;

import ec.tstoolkit.utilities.IntList;
import ec.util.chart.SeriesFunction;

/**
 *
 * @author Kristof Bayens
 */
public class JTsDualChart extends JTsChart {

    protected final IntList subTss_;

    public JTsDualChart() {
        subTss_ = new IntList();
        chartPanel.setComponentPopupMenu(buildChartMenu().getPopupMenu());

        chartPanel.setPlotWeights(new int[]{2, 1});
        chartPanel.setPlotDispatcher(new SeriesFunction<Integer>() {
            @Override
            public Integer apply(int series) {
                return isSubTs(series) ? 1 : 0;
            }
        });
    }

    public boolean isSubTs(int idx) {
        return subTss_.contains(idx);
    }

    public boolean hasSubTs() {
        return subTss_.size() > 0;
    }

    public void setTsLevel(int idx, boolean isSubTs) {
        setTsLevel(idx, isSubTs, true);
    }

    public void setTsLevel(int idx, boolean isSubTs, boolean redraw) {
        if (idx < 0) {
            return;
        }
        if (isSubTs && !subTss_.contains(idx)) {
            subTss_.add(idx);
        } else if (!isSubTs && subTss_.contains(idx)) {
            subTss_.remove(idx);
        }
        if (redraw) {
            onCollectionChange();
        }
    }

    public void clearSubChart() {
        subTss_.clear();
        onCollectionChange();
    }

    public void reset() {
        subTss_.clear();
        collection.clear();
    }
}
