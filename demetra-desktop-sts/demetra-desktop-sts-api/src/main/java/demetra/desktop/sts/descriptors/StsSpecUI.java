/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.sts.descriptors;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class StsSpecUI implements IObjectDescriptor<StsSpecification> {

    final StsSpecification core;

    public StsSpecUI(StsSpecification spec) {
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

    public StsSpecification getCore() {
        return core;
    }
}
