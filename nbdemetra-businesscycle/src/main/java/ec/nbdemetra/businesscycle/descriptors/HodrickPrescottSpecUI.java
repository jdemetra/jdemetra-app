/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.businesscycle.descriptors;

import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.satoolkit.SaSpecification;
import ec.tss.businesscycle.documents.HodrickPrescottSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class HodrickPrescottSpecUI implements IObjectDescriptor<HodrickPrescottSpecification> {
    
    static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(SaSpecification.Method.class);
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(SaSpecification.Spec.class);
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(HodrickPrescottSpecification.Target.class);
    }

    final HodrickPrescottSpecification core;

    public HodrickPrescottSpecUI(HodrickPrescottSpecification spec) {
        core = spec;
    }
    
    @Override
    public HodrickPrescottSpecification getCore(){
        return core;
    }
    
    public HodrickPrescottSpecification.Target getTarget(){
        return core.getTarget();
    }
    
    public void setTarget(HodrickPrescottSpecification.Target target){
        core.setTarget(target);
        if (target != HodrickPrescottSpecification.Target.Original && core.getSaSpecification().getMethod() 
                == SaSpecification.Method.None)
            core.getSaSpecification().setMethod(SaSpecification.Method.TramoSeats);
    }

    public SaSpecification.Method getMethod(){
        return core.getSaSpecification().getMethod();       
    }
    
    public void setMethod(SaSpecification.Method method){
        core.getSaSpecification().setMethod(method);
    }

    public SaSpecification.Spec getSpec(){
        return core.getSaSpecification().getSpecification();       
    }
    
    public void setSpec(SaSpecification.Spec spec){
        core.getSaSpecification().setSpecification(spec);
    }
    
    public double getCycleLength(){
        return core.getCycleLength();
    }

    public void setCycleLength(double l){
        core.setCycleLength(l);
    }
    
    public double getLambda(){
        return core.getLambda();
    }

    public void setLambda(double l){
        core.setLambda(l);
    }
    
    private EnhancedPropertyDescriptor tDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("target", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TARGET_ID);
            desc.setDisplayName(TARGET_NAME);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor mDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("method", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, METHOD_ID);
            desc.setDisplayName(METHOD_NAME);
            edesc.setReadOnly(core.getTarget() == HodrickPrescottSpecification.Target.Original);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor sDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("spec", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPEC_ID);
            desc.setDisplayName(SPEC_NAME);
            edesc.setReadOnly(core.getTarget() == HodrickPrescottSpecification.Target.Original);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor cDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("cycleLength", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LENGTH_ID);
            desc.setDisplayName(LENGTH_NAME);
            edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    private EnhancedPropertyDescriptor lDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("lambda", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LAMBDA_ID);
            desc.setDisplayName(LAMBDA_NAME);
            edesc.setReadOnly(core.getCycleLength()!=0);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
 
    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = tDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = mDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = sDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = cDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    public static final int TARGET_ID=0, METHOD_ID = 1, SPEC_ID = 2, LENGTH_ID=3, LAMBDA_ID=4;
    public static final String TARGET_NAME="Target", METHOD_NAME = "SA method",
            SPEC_NAME = "SA specifications", LENGTH_NAME="Cycle length", LAMBDA_NAME="Lambda";

    @Override
    public String getDisplayName() {
        return "Hodrick-Prescott";
    }
}
