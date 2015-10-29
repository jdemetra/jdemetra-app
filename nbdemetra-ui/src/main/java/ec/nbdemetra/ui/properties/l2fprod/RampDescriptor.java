/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties.l2fprod;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.regression.Ramp;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class RampDescriptor implements IObjectDescriptor<Ramp> {
    
    private final Ramp ramp_;
    
    @Override
    public String toString(){
        return ramp_.toString();
    }
    
    public RampDescriptor(){
        ramp_= new Ramp();
    }

    public RampDescriptor(Ramp ramp){
        ramp_= ramp;
    }

    @Override
    public Ramp getCore() {
        return ramp_;
    }
    
    public Day getStart(){
        return ramp_.getStart();
    }

    public void setStart(Day day){
        ramp_.setStart(day);
    }

    public Day getEnd(){
        return ramp_.getEnd();
    }

    public void setEnd(Day day){
        ramp_.setEnd(day);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = startDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = endDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private static final int START_ID = 1,
            END_ID = 2;

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
