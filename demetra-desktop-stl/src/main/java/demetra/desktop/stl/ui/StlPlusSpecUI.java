/*
 * Copyright 2023 National Bank of Belgium
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
package demetra.desktop.stl.ui;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.stl.StlPlusSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class StlPlusSpecUI implements IObjectDescriptor<StlPlusSpec> {
    
    private final StlPlusSpecRoot root;
    
    @Override
    public StlPlusSpec getCore(){
        return root.getCore();
    }

    public StlPlusSpecUI(StlPlusSpec spec, boolean ro) {
        root=new StlPlusSpecRoot(spec, ro);
    }

   public SeriesSpecUI getSeries() {
        return new SeriesSpecUI(root);
    }
   
   public boolean isPreprocessing(){
       return root.getPreprocessing().isEnabled();
   }
   
    public TransformSpecUI getTransform() {
        return new TransformSpecUI(root);
    }

    public RegressionSpecUI getRegression() {
        return new RegressionSpecUI(root);
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
        EnhancedPropertyDescriptor desc = seriesDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = transformDesc();
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
//        desc = stochasticDesc();
//        if (desc != null) {
//            descs.add(desc);
//        }
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
    private static final int SERIES_ID=1, TRANSFORM_ID = 2, REGRESSION_ID = 3, STOCHASTIC_ID = 4, OUTLIER_ID = 5, ESTIMATE_ID = 7, DECOMPOSITION_ID=8;

    @Messages({"stlPlusSpecUI.regressionDesc.name=REGRESSION",
        "stlPlusSpecUI.regressionDesc.desc="
    })
    private EnhancedPropertyDescriptor regressionDesc() {
        if (! root.isPreprocessing())
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("regression", this.getClass(), "getRegression", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REGRESSION_ID);
            desc.setDisplayName(Bundle.stlPlusSpecUI_regressionDesc_name());
            desc.setShortDescription(Bundle.stlPlusSpecUI_regressionDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"stlPlusSpecUI.seriesDesc.name=SERIES",
        "stlPlusSpecUI.seriesDesc.desc="
    })
    private EnhancedPropertyDescriptor seriesDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("transform", this.getClass(), "getSeries", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SERIES_ID);
            desc.setDisplayName(Bundle.stlPlusSpecUI_seriesDesc_name());
            desc.setShortDescription(Bundle.stlPlusSpecUI_seriesDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"stlPlusSpecUI.transformDesc.name=TRANSFORM",
        "stlPlusSpecUI.transformDesc.desc="
    })
    private EnhancedPropertyDescriptor transformDesc() {
        if (! root.isPreprocessing())
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("transform", this.getClass(), "getTransform", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TRANSFORM_ID);
            desc.setDisplayName(Bundle.stlPlusSpecUI_transformDesc_name());
            desc.setShortDescription(Bundle.stlPlusSpecUI_transformDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"stlPlusSpecUI.outlierDesc.name=OUTLIERS",
        "stlPlusSpecUI.outlierDesc.desc="
    })
    private EnhancedPropertyDescriptor outlierDesc() {
        if (! root.isPreprocessing())
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("outlier", this.getClass(), "getOutlier", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OUTLIER_ID);
            desc.setDisplayName(Bundle.stlPlusSpecUI_outlierDesc_name());
            desc.setShortDescription(Bundle.stlPlusSpecUI_outlierDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

//    @Messages({"stlPlusSpecUI.stochasticDesc.name=MODEL",
//        "stlPlusSpecUI.stochasticDesc.desc="
//    })
//    private EnhancedPropertyDescriptor stochasticDesc() {
//        try {
//            PropertyDescriptor desc = new PropertyDescriptor("stochastic", this.getClass(), "getStochastic", null);
//            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, STOCHASTIC_ID);
//            desc.setDisplayName(Bundle.stlPlusSpecUI_stochasticDesc_name());
//            desc.setShortDescription(Bundle.stlPlusSpecUI_stochasticDesc_desc());
//            //edesc.setReadOnly(true);
//            return edesc;
//        } catch (IntrospectionException ex) {
//            return null;
//        }
//    }
//
    @Messages({"stlPlusSpecUI.estimateDesc.name=ESTIMATE",
        "stlPlusSpecUI.estimateDesc.desc="
    })
    private EnhancedPropertyDescriptor estimateDesc() {
        if (! root.isPreprocessing())
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("estimate", this.getClass(), "getEstimate", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ESTIMATE_ID);
            desc.setDisplayName(Bundle.stlPlusSpecUI_estimateDesc_name());
            desc.setShortDescription(Bundle.stlPlusSpecUI_estimateDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages("stlPlusSpecUI.getDisplayName=Fractional airline decomposition")
    @Override
    public String getDisplayName() {
        return Bundle.stlPlusSpecUI_getDisplayName();
    }

    public StlSpecUI getDecomposition() {
        return new StlSpecUI(root);
    }

    @Messages({"stlPlusSpecUI.decompositionDesc.name=DECOMPOSITION",
        "stlPlusSpecUI.decompositionDesc.desc=Includes the settings relevant to the decomposition step"
    })
    private EnhancedPropertyDescriptor decompositionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("seats", this.getClass(), "getDecomposition", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DECOMPOSITION_ID);
            desc.setDisplayName(Bundle.stlPlusSpecUI_decompositionDesc_name());
            desc.setShortDescription(Bundle.stlPlusSpecUI_decompositionDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

}
