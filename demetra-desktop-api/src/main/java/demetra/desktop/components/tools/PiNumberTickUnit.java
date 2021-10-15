/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.components.tools;

import org.jfree.chart.axis.NumberTickUnit;

 /**
 *
 * @author Jeremy Demortier
 */
public class PiNumberTickUnit extends NumberTickUnit {

    private static final double margin = 0.01;

    public PiNumberTickUnit(double size) {
        super(size);
    }

    @Override
    public String valueToString(double value) {
        if (value <= (Math.PI + margin) && value >= (Math.PI - margin))
            return "PI";
        for (int i=2; i<=12; i++) {
            if (value <= ((Math.PI / i) + margin) && value >= ((Math.PI / i) - margin))
                return "PI/" + i;
        }
        for (int i=3; i<=12; i++) {
            if (value <= (((2 * Math.PI) / i) + margin) && value >= (((2 * Math.PI) / i) - margin))
                return "2PI/" + i;
        }
        for (int i=2; i<=12; i++) {
            if (value <= (((3 * Math.PI) / i) + margin) && value >= (((3 * Math.PI) / i) - margin))
                return "3PI/" + i;
        }
        return super.valueToString(value);
    }
}
