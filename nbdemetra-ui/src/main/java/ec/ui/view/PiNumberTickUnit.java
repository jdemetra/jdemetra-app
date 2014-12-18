/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view;

import org.jfree.chart.axis.NumberTickUnit;

 /**
 *
 * @author Jeremy Demortier & BAYENSK
 */
public class PiNumberTickUnit extends NumberTickUnit {

    private static final double margin_ = 0.01;

    public PiNumberTickUnit(double size) {
        super(size);
    }

    @Override
    public String valueToString(double value) {
        if (value <= (Math.PI + margin_) && value >= (Math.PI - margin_))
            return "PI";
        for (int i=2; i<=12; i++) {
            if (value <= ((Math.PI / i) + margin_) && value >= ((Math.PI / i) - margin_))
                return "PI/" + i;
        }
        for (int i=3; i<=12; i++) {
            if (value <= (((2 * Math.PI) / i) + margin_) && value >= (((2 * Math.PI) / i) - margin_))
                return "2PI/" + i;
        }
        for (int i=2; i<=12; i++) {
            if (value <= (((3 * Math.PI) / i) + margin_) && value >= (((3 * Math.PI) / i) - margin_))
                return "3PI/" + i;
        }
        return super.valueToString(value);
    }
}
