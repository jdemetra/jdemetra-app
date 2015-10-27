/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.sa.advanced.descriptors;

import ec.tstoolkit.arima.special.MaSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IPropertyDescriptors;
import ec.tstoolkit.utilities.Arrays2;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class MaSpecUI implements IPropertyDescriptors {

    final MaSpecification core;

    public MaSpecUI(MaSpecification spec) {
        core = spec;
    }

    public int[] getNoisyPeriods() {
        return core.noisyPeriods;
    }

    public void setNoisyPeriods(int[] values){
        if (Arrays2.isNullOrEmpty(values))
            core.noisyPeriods=null;
        else
            core.noisyPeriods=values;
    }

    public boolean isAllPeriods() {
        return core.allPeriods;
    }

    public void setAllPeriods(boolean all){
        core.allPeriods=all;
    }

    private EnhancedPropertyDescriptor npDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("noisyPeriods", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NP_ID);
            desc.setDisplayName(NP_NAME);
            desc.setShortDescription(NP_DESC);
            edesc.setReadOnly(core.allPeriods);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor aDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("allPeriods", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, A_ID);
            desc.setDisplayName(A_NAME);
            desc.setShortDescription(A_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = npDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = aDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    public static final int NP_ID = 0, A_ID = 1;
    public static final String NP_NAME = "Noisy periods",
            A_NAME = "All periods";
    public static final String NP_DESC = "Noisy periods",
            A_DESC = "Consider all periods as noisy";

    public String getDisplayName() {
        return "Airline + Noise";
    }
}
