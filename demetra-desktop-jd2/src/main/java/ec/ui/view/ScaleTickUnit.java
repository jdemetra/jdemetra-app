/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.ui.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jfree.chart.axis.NumberTickUnit;

/**
 *
 * @author Kristof Bayens
 */
public class ScaleTickUnit extends NumberTickUnit {
    public enum ScalingType {
        None,
        Multiply,
        Divide
    }

    private ScalingType type_;
    private double factor_;
    private NumberFormat formatter_;

    public ScaleTickUnit(double value, ScalingType type, double factor) {
        this(value, type, factor, new DecimalFormat("0.00"));
    }
    public ScaleTickUnit(double value, ScalingType type, double factor, NumberFormat format) {
        super(value);
        type_ = type;
        factor_ = factor;
        formatter_ = format;
    }

    @Override
    public String valueToString(double value) {
        if (type_ == ScalingType.Multiply) value *= factor_;
        if (type_ == ScalingType.Divide) value /= factor_;
        return formatter_.format(value);
    }
}
