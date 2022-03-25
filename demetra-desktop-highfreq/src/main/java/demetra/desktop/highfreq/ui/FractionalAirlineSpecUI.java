/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.highfreq.ExtendedAirlineModellingSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class FractionalAirlineSpecUI implements IObjectDescriptor<ExtendedAirlineModellingSpec> {

    private final FractionalAirlineSpecRoot root;

    @Override
    public ExtendedAirlineModellingSpec getCore(){
        return root.getCore();
    }

    public FractionalAirlineSpecUI(ExtendedAirlineModellingSpec spec, boolean ro) {
        root=new FractionalAirlineSpecRoot(spec, ro);
    }

    public FractionalAirlineSpecUI(FractionalAirlineSpecRoot root) {
        this.root=root;
    }

    public TransformSpecUI getTransform() {
        return new TransformSpecUI(root);
    }

    public RegressionSpecUI getRegression() {
        return new RegressionSpecUI(root);
    }

    public StochasticSpecUI getStochastic() {
        return new StochasticSpecUI(root);
    }

    public OutlierSpecUI getOutlier() {
        return new OutlierSpecUI(root);
    }

    public EstimateSpecUI getEstimate() {
        return new EstimateSpecUI(root);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = transformDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = estimateDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = regressionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = stochasticDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = outlierDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int TRANSFORM_ID = 2, REGRESSION_ID = 3, STOCHASTIC_ID = 4, OUTLIER_ID = 5, ESTIMATE_ID = 7;

    @Messages({"fractionalAirlineSpecUI.regressionDesc.name=REGRESSION",
        "fractionalAirlineSpecUI.regressionDesc.desc="
    })
    private EnhancedPropertyDescriptor regressionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("regression", this.getClass(), "getRegression", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REGRESSION_ID);
            desc.setDisplayName(Bundle.fractionalAirlineSpecUI_regressionDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineSpecUI_regressionDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"fractionalAirlineSpecUI.transformDesc.name=SERIES",
        "fractionalAirlineSpecUI.transformDesc.desc="
    })
    private EnhancedPropertyDescriptor transformDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("transform", this.getClass(), "getTransform", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TRANSFORM_ID);
            desc.setDisplayName(Bundle.fractionalAirlineSpecUI_transformDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineSpecUI_transformDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"fractionalAirlineSpecUI.outlierDesc.name=OUTLIERS",
        "fractionalAirlineSpecUI.outlierDesc.desc="
    })
    private EnhancedPropertyDescriptor outlierDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("outlier", this.getClass(), "getOutlier", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OUTLIER_ID);
            desc.setDisplayName(Bundle.fractionalAirlineSpecUI_outlierDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineSpecUI_outlierDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"fractionalAirlineSpecUI.stochasticDesc.name=MODEL",
        "fractionalAirlineSpecUI.stochasticDesc.desc="
    })
    private EnhancedPropertyDescriptor stochasticDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("stochastic", this.getClass(), "getStochastic", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, STOCHASTIC_ID);
            desc.setDisplayName(Bundle.fractionalAirlineSpecUI_stochasticDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineSpecUI_stochasticDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"fractionalAirlineSpecUI.estimateDesc.name=ESTIMATE",
        "fractionalAirlineSpecUI.estimateDesc.desc="
    })
    private EnhancedPropertyDescriptor estimateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("estimate", this.getClass(), "getEstimate", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ESTIMATE_ID);
            desc.setDisplayName(Bundle.fractionalAirlineSpecUI_estimateDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineSpecUI_estimateDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages("fractionalAirlineSpecUI.getDisplayName=Fractional airline")
    @Override
    public String getDisplayName() {
        return Bundle.fractionalAirlineSpecUI_getDisplayName();
    }
 
}
