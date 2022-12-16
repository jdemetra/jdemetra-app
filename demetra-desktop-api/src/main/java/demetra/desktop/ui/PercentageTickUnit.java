/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demetra.desktop.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import org.jfree.chart.axis.NumberTickUnit;

/**
 *
 * @author Kristof Bayens
 */
public class PercentageTickUnit extends NumberTickUnit {

    private final DecimalFormat formatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ROOT);
    
    public PercentageTickUnit(double size) {
        super(size);
        formatter.applyPattern("#0.00");
    }

    @Override
    public String valueToString(double value) {
        return formatter.format(value * 100) + "%";
    }
}
