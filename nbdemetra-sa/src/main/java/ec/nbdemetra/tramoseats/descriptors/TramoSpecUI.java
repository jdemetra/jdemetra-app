/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class TramoSpecUI extends BaseTramoSpecUI implements IObjectDescriptor<TramoSpecification> {

    public TramoSpecUI(TramoSpecification spec, boolean ro) {
        super(spec, ro);
    }

    public BasicSpecUI getBasic() {
        return new BasicSpecUI(core, ro_);
    }

    public RegressionSpecUI getRegression() {
        return new RegressionSpecUI(core, ro_);
    }

    public TransformSpecUI getTransform() {
        return new TransformSpecUI(core, ro_);
    }

    public OutlierSpecUI getOutlier() {
        return new OutlierSpecUI(core, ro_);
    }

    public ArimaSpecUI getArima() {
        return new ArimaSpecUI(core, ro_);
    }

    public EstimateSpecUI getEstimate() {
        return new EstimateSpecUI(core, ro_);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
//        EnhancedPropertyDescriptor desc = basicDesc();
//        if (desc != null) {
//            descs.add(desc);
//        }
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
    ///////////////////////////////////////////////////////////////////////////
    private static final int BASIC_ID = 1, TRANSFORM_ID = 2, REGRESSION_ID = 3, OUTLIER_ID = 4, ARIMA_ID = 5, ESTIMATE_ID = 7;

    @Messages({"tramoSpecUI.regressionDesc.name=REGRESSION",
        "tramoSpecUI.regressionDesc.desc="
    })
    private EnhancedPropertyDescriptor regressionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("regression", this.getClass(), "getRegression", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REGRESSION_ID);
            desc.setDisplayName(Bundle.tramoSpecUI_regressionDesc_name());
            desc.setShortDescription(Bundle.tramoSpecUI_regressionDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSpecUI.transformDesc.name=TRANSFORMATION",
        "tramoSpecUI.transformDesc.desc="
    })
    private EnhancedPropertyDescriptor transformDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("transform", this.getClass(), "getTransform", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TRANSFORM_ID);
            desc.setDisplayName(Bundle.tramoSpecUI_transformDesc_name());
            desc.setShortDescription(Bundle.tramoSpecUI_transformDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSpecUI.basicDesc.name=SERIES",
        "tramoSpecUI.basicDesc.desc="
    })
    private EnhancedPropertyDescriptor basicDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("basic", this.getClass(), "getBasic", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BASIC_ID);
            desc.setDisplayName(Bundle.tramoSpecUI_basicDesc_name());
            desc.setShortDescription(Bundle.tramoSpecUI_basicDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSpecUI.outlierDesc.name=OUTLIERS",
        "tramoSpecUI.outlierDesc.desc="
    })
    private EnhancedPropertyDescriptor outlierDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("outlier", this.getClass(), "getOutlier", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OUTLIER_ID);
            desc.setDisplayName(Bundle.tramoSpecUI_outlierDesc_name());
            desc.setShortDescription(Bundle.tramoSpecUI_outlierDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSpecUI.arimaDesc.name=ARIMA",
        "tramoSpecUI.arimaDesc.desc="
    })
    private EnhancedPropertyDescriptor arimaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("arima", this.getClass(), "getArima", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ARIMA_ID);
            desc.setDisplayName(Bundle.tramoSpecUI_arimaDesc_name());
            desc.setShortDescription(Bundle.tramoSpecUI_arimaDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSpecUI.estimateDesc.name=ESTIMATE",
        "tramoSpecUI.estimateDesc.desc="
    })
    private EnhancedPropertyDescriptor estimateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("estimate", this.getClass(), "getEstimate", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ESTIMATE_ID);
            desc.setDisplayName(Bundle.tramoSpecUI_estimateDesc_name());
            desc.setShortDescription(Bundle.tramoSpecUI_estimateDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages("tramoSpecUI.getDisplayName=Tramo")
    @Override
    public String getDisplayName() {
        return Bundle.tramoSpecUI_getDisplayName();
    }
}
