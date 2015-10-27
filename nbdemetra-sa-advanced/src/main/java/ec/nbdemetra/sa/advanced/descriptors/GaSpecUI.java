/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.sa.advanced.descriptors;

import ec.tstoolkit.arima.special.GaSpecification;
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
public class GaSpecUI implements IObjectDescriptor<GaSpecification> {

    final GaSpecification core;

    public GaSpecUI(GaSpecification spec) {
        core = spec;
    }
    
    public boolean isFourParameters(){
        return core.isFreeZeroFrequencyParameter();
    }
    
    public void setFourParameters(boolean four){
        core.setFreeZeroFrequencyParameter(four);
    }
    
    public int getMinFrequencyGroup(){
        return core.getMinFrequencyGroup();
    }

    public void setMinFrequencyGroup(int value)
    {
        core.setMinFrequencyGroup(value);
    }

    public int getMaxFrequencyGroup(){
        return core.getMaxFrequencyGroup();
    }

    public void setMaxFrequencyGroup(int value)
    {
        core.setMaxFrequencyGroup(value);
    }

    public double getUrBound(){
        return core.getURBound();
    }

    public void setUrBound(double value){
        core.setURBound(value);
    }


    private EnhancedPropertyDescriptor fDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("fourParameters", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, F_ID);
            desc.setDisplayName(F_NAME);
            desc.setShortDescription(F_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor minDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("minFrequencyGroup", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, m_ID);
            desc.setDisplayName(m_NAME);
            desc.setShortDescription(m_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor maxDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("maxFrequencyGroup", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, M_ID);
            desc.setDisplayName(M_NAME);
            desc.setShortDescription(M_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor urDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("UrBound", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, UR_ID);
            desc.setDisplayName(UR_NAME);
            desc.setShortDescription(UR_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = fDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = minDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = maxDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = urDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    public static final int F_ID = 0, m_ID = 1, M_ID =2, UR_ID=3;
    public static final String F_NAME = "Four parameters",
            m_NAME = "Min group",
            M_NAME = "Max group",
            UR_NAME = "UR limit";
    public static final String F_DESC = "Four parameters",
            m_DESC = "Min frequency parameter group",
            M_DESC = "Max frequency parameter group",
            UR_DESC = "Unit root limit";

    public String getDisplayName() {
        return "Airline + Noise";
    }

    public GaSpecification getCore() {
        return core;
    }
}
