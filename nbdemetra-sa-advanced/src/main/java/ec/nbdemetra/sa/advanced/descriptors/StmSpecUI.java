/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced.descriptors;

import ec.satoolkit.special.PreprocessingSpecification;
import ec.satoolkit.special.StmSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.modelling.arima.Method;
import ec.ui.descriptors.benchmarking.SaBenchmarkingSpecUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class StmSpecUI implements IObjectDescriptor<StmSpecification> {

    final StmSpecification core;

    public StmSpecUI(StmSpecification spec) {
        core = spec;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = ppDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = bsmDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = benchDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Structural model";
    }

    public PreprocessingSpecUI getPreprocessing() {
        if (core.getPreprocessingSpec() == null) {
            core.setPreprocessingSpec(new PreprocessingSpecification());
            core.getPreprocessingSpec().method = Method.None;
        }
        return new PreprocessingSpecUI(core.getPreprocessingSpec());
    }

    public BsmSpecUI getBsm() {
        return new BsmSpecUI(core.getDecompositionSpec());
    }
    
     public SaBenchmarkingSpecUI getBenchmarking() {
        return new SaBenchmarkingSpecUI(core.getBenchmarkingSpec(), false);
    }

    private EnhancedPropertyDescriptor benchDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("benchmarking", this.getClass(), "getBenchmarking", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BENCH_ID);
            desc.setDisplayName("BENCHMARKING");
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }
    
   private static final int PP_ID = 1, BSM_ID = 2, BENCH_ID=3;

    private EnhancedPropertyDescriptor ppDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("preprocessing", this.getClass(), "getPreprocessing", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(PP_NAME);
            desc.setShortDescription(PP_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor bsmDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("bsmSpec", this.getClass(), "getBsm", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BSM_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(BSM_NAME);
            desc.setShortDescription(BSM_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    private static final String PP_NAME = "Pre-processing",
            BSM_NAME = "Basic structural model";
    private static final String PP_DESC = "Pre-processing",
            BSM_DESC = "Basic structural model";

    public StmSpecification getCore() {
        return core;
    }
}
