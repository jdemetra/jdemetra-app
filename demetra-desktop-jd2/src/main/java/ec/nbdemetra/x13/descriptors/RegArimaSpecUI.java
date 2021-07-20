/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.timeseries.simplets.TsDomain;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class RegArimaSpecUI implements IObjectDescriptor<RegArimaSpecification> {

    final RegArimaSpecification core;
    final boolean ro_;
    private TsDomain domain_;

    public RegArimaSpecUI(RegArimaSpecification spec, TsDomain domain, boolean ro) {
        if (spec == null)
            throw new AssertionError(EMPTY);
        core = spec;
        domain_ = domain;
        ro_ = ro;
    }

    @Override
    public RegArimaSpecification getCore() {
        return core;
    }

    public BasicSpecUI getBasic() {
        return new BasicSpecUI(core, ro_);
    }

    public RegressionSpecUI getRegression() {
        return new RegressionSpecUI(core, ro_);
    }

    public TransformSpecUI getTransform() {
        return new TransformSpecUI(core, ro_ || core.getRegression().hasFixedCoefficients());
    }

    public ArimaSpecUI getArima() {
        return new ArimaSpecUI(core, ro_);
    }

    public OutlierSpecUI getOutliers() {
        return new OutlierSpecUI(core, ro_);
    }

    public EstimateSpecUI getEstimate() {
        return new EstimateSpecUI(core, ro_);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
//        EnhancedPropertyDescriptor desc = basicDesc();
//        if (desc != null)
//            descs.add(desc);
        EnhancedPropertyDescriptor desc = estimateDesc();
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
        desc = outlierDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = arimaDesc();
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
            ESTIMATE_ID = 7;

    @Messages({
        "regArimaSpecUI.regressionDesc.name=REGRESSION",
        "regArimaSpecUI.regressionDesc.desc="
    })
    private EnhancedPropertyDescriptor regressionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("regression", this.getClass(), "getRegression", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REGRESSION_ID);
            desc.setDisplayName(Bundle.regArimaSpecUI_regressionDesc_name());
            desc.setShortDescription(Bundle.regArimaSpecUI_regressionDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regArimaSpecUI.transformDesc.name=TRANSFORMATION",
        "regArimaSpecUI.transformDesc.desc="
    })
    private EnhancedPropertyDescriptor transformDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("transform", this.getClass(), "getTransform", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TRANSFORM_ID);
            desc.setDisplayName(Bundle.regArimaSpecUI_transformDesc_name());
            desc.setShortDescription(Bundle.regArimaSpecUI_transformDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regArimaSpecUI.basicDesc.name=SERIES",
        "regArimaSpecUI.basicDesc.desc="
    })
    private EnhancedPropertyDescriptor basicDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("basic", this.getClass(), "getBasic", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BASIC_ID);
            desc.setDisplayName(Bundle.regArimaSpecUI_basicDesc_name());
            desc.setShortDescription(Bundle.regArimaSpecUI_basicDesc_desc());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }
    
    @Messages({
        "regArimaSpecUI.arimaDesc.name=ARIMA",
        "regArimaSpecUI.arimaDesc.desc="
    })
    private EnhancedPropertyDescriptor arimaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("arima", this.getClass(), "getArima", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ARIMA_ID);
            desc.setDisplayName(Bundle.regArimaSpecUI_arimaDesc_name());
            desc.setShortDescription(Bundle.regArimaSpecUI_arimaDesc_desc());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regArimaSpecUI.outlierDesc.name=OUTLIERS",
        "regArimaSpecUI.outlierDesc.desc="
    })
    private EnhancedPropertyDescriptor outlierDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("outlier", this.getClass(), "getOutliers", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OUTLIER_ID);
            desc.setDisplayName(Bundle.regArimaSpecUI_outlierDesc_name());
            desc.setShortDescription(Bundle.regArimaSpecUI_outlierDesc_desc());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regArimaSpecUI.estimateDesc.name=ESTIMATE",
        "regArimaSpecUI.estimateDesc.desc="
    })
    private EnhancedPropertyDescriptor estimateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("estimate", this.getClass(), "getEstimate", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ESTIMATE_ID);
            desc.setDisplayName(Bundle.regArimaSpecUI_estimateDesc_name());
            desc.setShortDescription(Bundle.regArimaSpecUI_estimateDesc_desc());
            return edesc;
        }
        catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages("regArimaSpecUI.getDisplayName=RegArima (X13)")
    @Override
    public String getDisplayName() {
        return Bundle.regArimaSpecUI_getDisplayName();
    }
}
