/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.satoolkit.x13.X13Specification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.ui.descriptors.benchmarking.SaBenchmarkingSpecUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kristof Bayens
 */
public class X13SpecUI implements IObjectDescriptor<X13Specification> {

    final X13Specification core;
    private TsDomain domain_;
    final boolean ro_;

    public X13SpecUI(X13Specification spec, TsDomain domain, boolean ro) {
        core = spec;
        domain_ = domain;
        ro_ = ro;
    }

    @Override
    public X13Specification getCore() {
        return core;
    }

    public BasicSpecUI getBasic() {
        return new BasicSpecUI(core.getRegArimaSpecification(), ro_);
    }

    public RegressionSpecUI getRegression() {
        return new RegressionSpecUI(core.getRegArimaSpecification(), ro_);
    }

    public TransformSpecUI getTransform() {
        return new TransformSpecUI(core.getRegArimaSpecification(), ro_);
    }

    public ArimaSpecUI getArima() {
        return new ArimaSpecUI(core.getRegArimaSpecification(), ro_);
    }

    public OutlierSpecUI getOutliers() {
        return new OutlierSpecUI(core.getRegArimaSpecification(), ro_);
    }

    public EstimateSpecUI getEstimate() {
        return new EstimateSpecUI(core.getRegArimaSpecification(), ro_);
    }

    public X11SpecUI getX11() {
        return new X11SpecUI(core.getX11Specification(), (domain_ != null ? domain_.getFrequency() : TsFrequency.Undefined), 
                core.getRegArimaSpecification().getBasic().isPreprocessing(), ro_);
    }

    public SaBenchmarkingSpecUI getBenchmarking() {
        return new SaBenchmarkingSpecUI(core.getBenchmarkingSpecification(), ro_);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = basicDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = estimateDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = transformDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = regressionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = arimaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = outlierDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = x11Desc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = benchDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private static final int BASIC_ID = 1,
            TRANSFORM_ID = 2,
            REGRESSION_ID = 3,
            AUTOMODEL_ID = 4,
            ARIMA_ID = 5,
            OUTLIER_ID = 6,
            ESTIMATE_ID = 7,
            X11_ID = 8,
            BENCH_ID = 9;

    private EnhancedPropertyDescriptor regressionDesc() {
        try {
            if (!core.getRegArimaSpecification().getBasic().isPreprocessing()) {
                return null;
            }
            PropertyDescriptor desc = new PropertyDescriptor("regression", this.getClass(), "getRegression", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REGRESSION_ID);
            desc.setDisplayName("REGRESSION");
            edesc.setReadOnly(true);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor transformDesc() {
        try {
            if (!core.getRegArimaSpecification().getBasic().isPreprocessing()) {
                return null;
            }
            PropertyDescriptor desc = new PropertyDescriptor("transform", this.getClass(), "getTransform", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TRANSFORM_ID);
            desc.setDisplayName("TRANSFORMATION");
            //edesc.setReadOnly(true);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor basicDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("basic", this.getClass(), "getBasic", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BASIC_ID);
            desc.setDisplayName("SERIES");
            //edesc.setReadOnly(true);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor arimaDesc() {
        try {
            if (!core.getRegArimaSpecification().getBasic().isPreprocessing()) {
                return null;
            }
            PropertyDescriptor desc = new PropertyDescriptor("arima", this.getClass(), "getArima", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ARIMA_ID);
            desc.setDisplayName("ARIMA");
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor outlierDesc() {
        try {
            if (!core.getRegArimaSpecification().getBasic().isPreprocessing()) {
                return null;
            }
            PropertyDescriptor desc = new PropertyDescriptor("basic", this.getClass(), "getOutliers", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OUTLIER_ID);
            desc.setDisplayName("OUTLIERS");
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor estimateDesc() {
        try {
            if (!core.getRegArimaSpecification().getBasic().isPreprocessing()) {
                return null;
            }
            PropertyDescriptor desc = new PropertyDescriptor("basic", this.getClass(), "getEstimate", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ESTIMATE_ID);
            desc.setDisplayName("ESTIMATE");
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor x11Desc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("basic", this.getClass(), "getX11", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, X11_ID);
            desc.setDisplayName("X11");
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
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

    @Override
    public String getDisplayName() {
        return "X13";
    }
}
