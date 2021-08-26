/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view;

import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import org.jfree.chart.axis.NumberTickUnit;

/**
 *
 * @author Kristof Bayens
 */
public class TsFrequencyTickUnit extends NumberTickUnit {
    private final TsFrequency freq_;

    public TsFrequencyTickUnit(TsFrequency freq) {
        super(1);
        freq_ = freq;
    }

    @Override
    public String valueToString(double value) {
        return TsPeriod.formatShortPeriod(freq_, (int)value);
    }
}
