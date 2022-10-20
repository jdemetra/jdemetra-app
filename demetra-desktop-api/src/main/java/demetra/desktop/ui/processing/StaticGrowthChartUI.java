/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.timeseries.Ts;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
public class StaticGrowthChartUI implements ItemUI<List<Ts>> {


    public StaticGrowthChartUI(){
    }


    @Override
    public JComponent getView(List<Ts> ts) {

        return TsViewToolkit.getGrowthChart(ts);
    }

 }
