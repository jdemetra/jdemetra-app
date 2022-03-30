/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.highfreq.ExtendedAirlineDecompositionSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class FractionalAirlineDecompositionSpecUI implements IObjectDescriptor<ExtendedAirlineDecompositionSpec> {
    
    private final FractionalAirlineDecompositionSpecRoot root;
    
    @Override
    public ExtendedAirlineDecompositionSpec getCore(){
        return root.getCore();
    }

    public FractionalAirlineDecompositionSpecUI(ExtendedAirlineDecompositionSpec spec, boolean ro) {
        root=new FractionalAirlineDecompositionSpecRoot(spec, ro);
    }

    public TransformSpecUI getTransform() {
        return new TransformSpecUI(root.getPreprocessing());
    }

    public RegressionSpecUI getRegression() {
        return new RegressionSpecUI(root.getPreprocessing());
    }

    public StochasticSpecUI getStochastic() {
        return new StochasticSpecUI(root.getPreprocessing());
    }

    public OutlierSpecUI getOutlier() {
        return new OutlierSpecUI(root.getPreprocessing());
    }

    public EstimateSpecUI getEstimate() {
        return new EstimateSpecUI(root.getPreprocessing());
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
        desc = decompositionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int TRANSFORM_ID = 2, REGRESSION_ID = 3, STOCHASTIC_ID = 4, OUTLIER_ID = 5, ESTIMATE_ID = 7, DECOMPOSITION_ID=8;

    @Messages({"fractionalAirlineDecompositionSpecUI.regressionDesc.name=REGRESSION",
        "fractionalAirlineDecompositionSpecUI.regressionDesc.desc="
    })
    private EnhancedPropertyDescriptor regressionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("regression", this.getClass(), "getRegression", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REGRESSION_ID);
            desc.setDisplayName(Bundle.fractionalAirlineDecompositionSpecUI_regressionDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineDecompositionSpecUI_regressionDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"fractionalAirlineDecompositionSpecUI.transformDesc.name=SERIES",
        "fractionalAirlineDecompositionSpecUI.transformDesc.desc="
    })
    private EnhancedPropertyDescriptor transformDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("transform", this.getClass(), "getTransform", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TRANSFORM_ID);
            desc.setDisplayName(Bundle.fractionalAirlineDecompositionSpecUI_transformDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineDecompositionSpecUI_transformDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"fractionalAirlineDecompositionSpecUI.outlierDesc.name=OUTLIERS",
        "fractionalAirlineDecompositionSpecUI.outlierDesc.desc="
    })
    private EnhancedPropertyDescriptor outlierDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("outlier", this.getClass(), "getOutlier", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OUTLIER_ID);
            desc.setDisplayName(Bundle.fractionalAirlineDecompositionSpecUI_outlierDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineDecompositionSpecUI_outlierDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"fractionalAirlineDecompositionSpecUI.stochasticDesc.name=MODEL",
        "fractionalAirlineDecompositionSpecUI.stochasticDesc.desc="
    })
    private EnhancedPropertyDescriptor stochasticDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("stochastic", this.getClass(), "getStochastic", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, STOCHASTIC_ID);
            desc.setDisplayName(Bundle.fractionalAirlineDecompositionSpecUI_stochasticDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineDecompositionSpecUI_stochasticDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"fractionalAirlineDecompositionSpecUI.estimateDesc.name=ESTIMATE",
        "fractionalAirlineDecompositionSpecUI.estimateDesc.desc="
    })
    private EnhancedPropertyDescriptor estimateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("estimate", this.getClass(), "getEstimate", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ESTIMATE_ID);
            desc.setDisplayName(Bundle.fractionalAirlineDecompositionSpecUI_estimateDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineDecompositionSpecUI_estimateDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages("fractionalAirlineDecompositionSpecUI.getDisplayName=Fractional airline decomposition")
    @Override
    public String getDisplayName() {
        return Bundle.fractionalAirlineDecompositionSpecUI_getDisplayName();
    }

    public DecompositionSpecUI getDecomposition() {
        return new DecompositionSpecUI(root);
    }

    @Messages({"fractionalAirlineDecompositionSpecUI.decompositionDesc.name=DECOMPOSITION",
        "fractionalAirlineDecompositionSpecUI.decompositionDesc.desc=Includes the settings relevant to the decomposition step"
    })
    private EnhancedPropertyDescriptor decompositionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("seats", this.getClass(), "getDecomposition", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DECOMPOSITION_ID);
            desc.setDisplayName(Bundle.fractionalAirlineDecompositionSpecUI_decompositionDesc_name());
            desc.setShortDescription(Bundle.fractionalAirlineDecompositionSpecUI_decompositionDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

}
