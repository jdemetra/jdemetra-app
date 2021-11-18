/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.properties.l2fprod;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class InterventionVariableDescriptor implements IObjectDescriptor<InterventionVariable> {
    
    private final InterventionVariable core_;
    
    @Override
    public String toString(){
        return core_.toString();
    }

    public InterventionVariableDescriptor(){
        core_=new InterventionVariable();
    }

    
    public InterventionVariableDescriptor(InterventionVariable var){
        core_= var;
    }

    @Override
    public InterventionVariable getCore() {
        return core_;
    }
    
    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = seqDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = deltaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = deltaSDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = d1dSDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    
    private static final int SEQ_ID = 1,
            DELTA_ID = 2, DELTAS_ID=3, D1DS_ID=4;

    private EnhancedPropertyDescriptor seqDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("sequences", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SEQ_ID);
            desc.setDisplayName("Sequences");
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor deltaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("delta", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DELTA_ID);
            desc.setDisplayName("Delta");
            edesc.setReadOnly(core_.getD1DS() );
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    private EnhancedPropertyDescriptor deltaSDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("deltaS", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DELTAS_ID);
            desc.setDisplayName("Seasonal delta");
            edesc.setReadOnly(core_.getD1DS() );
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor d1dSDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("D1DS", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, D1DS_ID);
            desc.setDisplayName("D1 DS");
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    public Sequence[] getSequences(){
        return core_.getSequences();
    }
    
    public void setSequences(Sequence[] val) {
        core_.setSequences(val);
    }

    public double getDelta(){
        return core_.getDelta();
    }
    
    public void setDelta(double val) {
        core_.setDelta(val);
    }

    public double getDeltaS(){
        return core_.getDeltaS();
    }
    
    public void setDeltaS(double val) {
        core_.setDeltaS(val);
    }

    public boolean isD1DS(){
        return core_.getD1DS();
    }
    
    public void setD1DS(boolean val) {
        core_.setD1DS(val);
    }

    @Override
    public String getDisplayName() {
        return "Intervention variable";
    }
    
}
