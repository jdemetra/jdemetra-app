/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import demetra.desktop.descriptors.*;
import demetra.timeseries.regression.Ramp;
import demetra.timeseries.regression.Variable;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class RampDescriptor extends VariableDescriptor<Ramp> {

    @Override
    public String name() {
        return "rp: " + VariableDescriptor.toShortString(core.getStart(), core.getEnd(), UserInterfaceContext.INSTANCE.getDomain());
    }

    private Ramp core;

    public RampDescriptor() {
        core = new Ramp(LocalDateTime.now(), LocalDateTime.now());
    }

    public RampDescriptor duplicate() {
        return new RampDescriptor(this);
    }

    public RampDescriptor(Variable<Ramp> ramp) {
        super(ramp);
        core = ramp.getCore();
        setName(ramp.getName());
    }

    public RampDescriptor(RampDescriptor desc) {
        super(desc);
        this.core = desc.core;
    }

    @Override
    public Ramp getCore() {
        return core;
    }

    public LocalDate getStart() {
        return core.getStart().toLocalDate();
    }

    public void setStart(LocalDate day) {
        core = new Ramp(day.atStartOfDay(), core.getEnd());
    }

    public LocalDate getEnd() {
        return core.getEnd().toLocalDate();
    }

    public void setEnd(LocalDate day) {
        core = new Ramp(core.getStart(), day.atStartOfDay());
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc;
        desc = nameDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = startDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = endDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = fixedParameterDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = parameterDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private static final int START_ID = 10,
            END_ID = 11;

    private EnhancedPropertyDescriptor startDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("start", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, START_ID);
            desc.setDisplayName("Start");
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor endDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("end", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, END_ID);
            desc.setDisplayName("End");
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "Ramp";
    }

}
