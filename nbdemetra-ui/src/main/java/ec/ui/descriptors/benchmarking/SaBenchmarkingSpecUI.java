/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.descriptors.benchmarking;

import ec.satoolkit.benchmarking.SaBenchmarkingSpec;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pcuser
 */
public class SaBenchmarkingSpecUI implements IPropertyDescriptors {

    private final SaBenchmarkingSpec core;
    private final boolean ro_; 

    public SaBenchmarkingSpecUI(SaBenchmarkingSpec spec, boolean ro) {
        core = spec;
        ro_=ro;
    }
    
    public boolean isEnabled()
    {
        return core.isEnabled(); 
    }
    
    public void setEnabled(boolean value){
        core.setEnabled(value);
    }
    
    public SaBenchmarkingSpec.Target getTarget(){
        return core.getTarget();
    }
    
    public void setTarget(SaBenchmarkingSpec.Target target){
        core.setTarget(target);
    }
    
    public double getRho(){
        return core.getRho();
    }
    
    public void setRho(double value){
        core.setRho(value);
    }

    public double getLambda(){
        return core.getLambda();
    }
    
    public void setLambda(double value){
        core.setLambda(value);
    }
    
    public boolean isUseForecast(){
        return core.isUsingForecast();
    }

    public void setUseForecast(boolean bf){
        core.useForecast(bf);
    }

    private EnhancedPropertyDescriptor eDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("enabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            desc.setDisplayName(ENABLED_NAME);
            desc.setShortDescription(ENABLED_DESC);
            edesc.setReadOnly(ro_);
          return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor tDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("target", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TARGET_ID);
            desc.setDisplayName(TARGET_NAME);
            desc.setShortDescription(TARGET_DESC);
            edesc.setReadOnly(ro_ || ! core.isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor rDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("rho", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, RHO_ID);
            desc.setDisplayName(RHO_NAME);
            desc.setShortDescription(RHO_DESC);
            edesc.setReadOnly(ro_ || ! core.isEnabled());
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
            desc.setShortDescription(LAMBDA_DESC);
            edesc.setReadOnly(ro_ || ! core.isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor forecastDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("UseForecast", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FORECAST_ID);
            desc.setDisplayName(FORECAST_NAME);
            desc.setShortDescription(FORECAST_DESC);
            edesc.setReadOnly(ro_ || ! core.isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = eDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = tDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = forecastDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = rDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    
    @Override
    public String getDisplayName() {
        return "Benchmarking";
    }
    private static final int ENABLED_ID = 0, TARGET_ID = 10, FORECAST_ID=20, RHO_ID = 30, LAMBDA_ID = 40;
    private static final String ENABLED_NAME = "Is enabled",
            TARGET_NAME = "Target",
            FORECAST_NAME = "Use forecasts",
            RHO_NAME = "Rho",
            LAMBDA_NAME = "Lambda";
    private static final String ENABLED_DESC = "Is enabled",
            TARGET_DESC = "Target",
            FORECAST_DESC = "Integrate the forecasts in the benchmarking",
            RHO_DESC = "Rho",
            LAMBDA_DESC = "Lambda";
}