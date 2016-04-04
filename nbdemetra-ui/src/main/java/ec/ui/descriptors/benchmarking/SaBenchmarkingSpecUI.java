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
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
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

    @Messages({
        "benchmarkingSpecUI.enable.name=Is enabled",
        "benchmarkingSpecUI.enable.desc=When marked, it enables the user to perform benchmarking."
    })
    private EnhancedPropertyDescriptor eDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("enabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            desc.setDisplayName(Bundle.benchmarkingSpecUI_enable_name());
            desc.setShortDescription(Bundle.benchmarkingSpecUI_enable_desc());
            edesc.setReadOnly(ro_);
          return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "benchmarkingSpecUI.target.name=Target",
        "benchmarkingSpecUI.target.desc=Specifies the target variable for the benchmarking procedure, which can be the raw series (Original); or the series adjusted for calendar effects (Calendar Adjusted)."
    })
    private EnhancedPropertyDescriptor tDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("target", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TARGET_ID);
            desc.setDisplayName(Bundle.benchmarkingSpecUI_target_name());
            desc.setShortDescription(Bundle.benchmarkingSpecUI_target_desc());
            edesc.setReadOnly(ro_ || ! core.isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "benchmarkingSpecUI.rho.name=Rho",
        "benchmarkingSpecUI.rho.desc=The value of the AR(1) parameter (set between 0 and 1) in the function used for benchmarking."
    })
    private EnhancedPropertyDescriptor rDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("rho", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, RHO_ID);
            desc.setDisplayName(Bundle.benchmarkingSpecUI_rho_name());
            desc.setShortDescription(Bundle.benchmarkingSpecUI_rho_desc());
            edesc.setReadOnly(ro_ || ! core.isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "benchmarkingSpecUI.lambda.name=Lambda",
        "benchmarkingSpecUI.lambda.desc=A parameter in the function used for benchmarking that relates to the weights in the regression equation; it is typically equal to 0, 1/2 or 1."
    })
    private EnhancedPropertyDescriptor lDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("lambda", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LAMBDA_ID);
            desc.setDisplayName(Bundle.benchmarkingSpecUI_lambda_name());
            desc.setShortDescription(Bundle.benchmarkingSpecUI_lambda_desc());
            edesc.setReadOnly(ro_ || ! core.isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "benchmarkingSpecUI.forecast.name=Use forecasts",
        "benchmarkingSpecUI.forecast.desc=When marked, the forecasts of the seasonally adjusted series and of the target variable (Target) are used in the benchmarking computation so the benchmarking constrain is applied also to the forecasting period."
    })
    private EnhancedPropertyDescriptor forecastDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("UseForecast", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FORECAST_ID);
            desc.setDisplayName(Bundle.benchmarkingSpecUI_forecast_name());
            desc.setShortDescription(Bundle.benchmarkingSpecUI_forecast_desc());
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
}