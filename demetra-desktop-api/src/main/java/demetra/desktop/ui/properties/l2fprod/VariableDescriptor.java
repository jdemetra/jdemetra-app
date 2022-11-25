/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import demetra.data.Parameter;
import demetra.desktop.descriptors.*;
import demetra.timeseries.TsDomain;
import demetra.timeseries.TsPeriod;
import demetra.timeseries.TsUnit;
import demetra.timeseries.regression.ITsVariable;
import demetra.timeseries.regression.Variable;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openide.util.NbBundle;

/**
 *
 * @author Jean Palate
 * @param <T>
 */
public abstract class VariableDescriptor<T extends ITsVariable> implements IObjectDescriptor<T> {

    private String name;
    private Parameter coefficient;

    @Override
    public String toString() {
        return getName();
    }

    protected VariableDescriptor() {
        name = null;
        coefficient = Parameter.undefined();
    }
    
    public abstract String name();

    protected VariableDescriptor(Variable<T> var) {
        this.coefficient = var.getCoefficient(0);
        this.name=null;
    }

    protected VariableDescriptor(VariableDescriptor<T> desc) {
        this.name = desc.name;
        this.coefficient = desc.coefficient;
    }

   public String getName() {
        return name == null ? name() : name;
    }

    public void setName(String name) {
        if (!name.isBlank() && !name.equals(name())) {
            this.name = name;
        }else
            this.name=null;
    }

    public Parameter getCoefficient() {
        return coefficient;
    }

    public double getParameter() {
        return coefficient.isDefined() ? coefficient.getValue() : null;
    }

    public void setParameter(double p) {
        coefficient = p == 0 ? Parameter.undefined() : Parameter.fixed(p);
    }

    public boolean isFixedParameter() {
        return coefficient.isFixed();
    }

    public void setFixedParameter(boolean f) {
        if (f && !coefficient.isFixed()) {
            coefficient = Parameter.fixed(coefficient.getValue());
        } else if (coefficient.isFixed()) {
            if (coefficient.getValue() == 0) {
                coefficient = Parameter.undefined();
            } else {
                coefficient = Parameter.initial(coefficient.getValue());
            }
        }
    }

    private static final int NAME_ID = 1, FIXEDPARAMETER_ID = 2, PARAMETER_ID = 3;

    @NbBundle.Messages("variableDescriptor.nameDesc.display=Name")
    protected EnhancedPropertyDescriptor nameDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("name", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NAME_ID);
            desc.setDisplayName(Bundle.variableDescriptor_nameDesc_display());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages("variableDescriptor.fixedParamDesc.display=Fixed coeff.")
    protected EnhancedPropertyDescriptor fixedParameterDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("fixedParameter", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FIXEDPARAMETER_ID);
            desc.setDisplayName(Bundle.variableDescriptor_fixedParamDesc_display());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages("variableDescriptor.paramDesc.display=Coefficient")
    protected EnhancedPropertyDescriptor parameterDesc() {
        if (!coefficient.isFixed()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("parameter", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PARAMETER_ID);
            desc.setDisplayName(Bundle.variableDescriptor_paramDesc_display());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public static String toShortString(LocalDateTime start, LocalDateTime end, TsDomain domain) {
        int period = domain == null ? 0 : domain.getAnnualFrequency();
        StringBuilder builder = new StringBuilder();
        if (period <= 0) {
            builder.append(start.toLocalDate().format(DateTimeFormatter.ISO_DATE))
                    .append(" - ").append(end.toLocalDate().format(DateTimeFormatter.ISO_DATE));
        } else {
            TsUnit unit = TsUnit.ofAnnualFrequency(period);
            TsPeriod pstart = TsPeriod.of(unit, start);
            TsPeriod pend = TsPeriod.of(unit, end);
            builder.append(pstart.display())
                    .append(" - ").append(pend.display());

        }
        return builder.toString();
    }

}
