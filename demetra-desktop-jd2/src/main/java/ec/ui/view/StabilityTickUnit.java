/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view;

import java.util.List;
import org.jfree.chart.axis.NumberTickUnit;

/**
 *
 * @author Kristof Bayens
 */
public class StabilityTickUnit extends NumberTickUnit {
    private List<String> names;
    
    public StabilityTickUnit(List<String> names) {
        super(1);
        this.names = names;
    }

    @Override
    public String valueToString(double value) {
        return names.get((int)value);
    }
}
