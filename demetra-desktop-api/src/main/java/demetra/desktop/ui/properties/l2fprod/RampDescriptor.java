/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import demetra.desktop.descriptors.*;
import demetra.timeseries.regression.Ramp;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class RampDescriptor implements IObjectDescriptor<Ramp> {
    
    private LocalDate start, end;
    
    @Override
    public String toString(){
        return getCore().toString();
    }
    
    public RampDescriptor(){
        start=LocalDate.now();
        end=LocalDate.now();
    }

    public RampDescriptor(Ramp ramp){
        start=ramp.getStart().toLocalDate();
        end=ramp.getEnd().toLocalDate();
    }

    @Override
    public Ramp getCore() {
        return new Ramp(start.atStartOfDay(), end.atStartOfDay());
    }
    
    public LocalDate getStart(){
        return start;
    }

    public void setStart(LocalDate day){
        start=day;
    }

    public LocalDate getEnd(){
        return end;
    }

    public void setEnd(LocalDate day){
        end=day;
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
