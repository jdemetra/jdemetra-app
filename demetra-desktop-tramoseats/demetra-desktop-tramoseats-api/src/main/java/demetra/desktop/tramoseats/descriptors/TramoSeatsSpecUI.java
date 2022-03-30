/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramoseats.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.sa.descriptors.SaBenchmarkingSpecUI;
import demetra.desktop.tramo.descriptors.ArimaSpecUI;
import demetra.desktop.tramo.descriptors.BasicSpecUI;
import demetra.desktop.tramo.descriptors.EstimateSpecUI;
import demetra.desktop.tramo.descriptors.OutlierSpecUI;
import demetra.desktop.tramo.descriptors.RegressionSpecUI;
import demetra.desktop.tramo.descriptors.TransformSpecUI;
import demetra.tramoseats.TramoSeatsSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class TramoSeatsSpecUI implements IObjectDescriptor<TramoSeatsSpec> {
    
    private final TramoSeatsSpecRoot root;
    
    @Override
    public TramoSeatsSpec getCore(){
        return root.getCore();
    }

    public TramoSeatsSpecUI(TramoSeatsSpec spec, boolean ro) {
        root=new TramoSeatsSpecRoot(spec, ro);
    }

    public BasicSpecUI getBasic() {
        return new BasicSpecUI(root.getTramo());
    }

    public RegressionSpecUI getRegression() {
        return new RegressionSpecUI(root.getTramo());
    }

    public TransformSpecUI getTransform() {
        return new TransformSpecUI(root.getTramo());
    }

    public ArimaSpecUI getArima() {
        return new ArimaSpecUI(root.getTramo());
    }

    public OutlierSpecUI getOutlier() {
        return new OutlierSpecUI(root.getTramo());
    }

    public EstimateSpecUI getEstimate() {
        return new EstimateSpecUI(root.getTramo());
    }
    public DecompositionSpecUI getSeats() {
        return new DecompositionSpecUI(root);
    }

    public SaBenchmarkingSpecUI getBenchmarking() {
        return new SaBenchmarkingSpecUI(root.getBenchmarking(), root.isRo(),
                bspec->root.update(bspec));
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
        desc = outlierDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = arimaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = seatsDesc();
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
            ARIMA_ID = 5,
            OUTLIER_ID = 6,
            ESTIMATE_ID = 7,
            SEATS_ID = 8,
            BENCH_ID = 9;

    @Messages({"tramoSeatsSpecUI.regressionDesc.name=REGRESSION",
        "tramoSeatsSpecUI.regressionDesc.desc="
    })
    private EnhancedPropertyDescriptor regressionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("regression", this.getClass(), "getRegression", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, REGRESSION_ID);
            desc.setDisplayName(Bundle.tramoSeatsSpecUI_regressionDesc_name());
            desc.setShortDescription(Bundle.tramoSeatsSpecUI_regressionDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSeatsSpecUI.transformDesc.name=TRANSFORMATION",
        "tramoSeatsSpecUI.transformDesc.desc="
    })
    private EnhancedPropertyDescriptor transformDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("transform", this.getClass(), "getTransform", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TRANSFORM_ID);
            desc.setDisplayName(Bundle.tramoSeatsSpecUI_transformDesc_name());
            desc.setShortDescription(Bundle.tramoSeatsSpecUI_transformDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSeatsSpecUI.basicDesc.name=SERIES",
        "tramoSeatsSpecUI.basicDesc.desc="
    })
    private EnhancedPropertyDescriptor basicDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("basic", this.getClass(), "getBasic", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BASIC_ID);
            desc.setDisplayName(Bundle.tramoSeatsSpecUI_basicDesc_name());
            desc.setShortDescription(Bundle.tramoSeatsSpecUI_basicDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSeatsSpecUI.outlierDesc.name=OUTLIERS",
        "tramoSeatsSpecUI.outlierDesc.desc="
    })
    private EnhancedPropertyDescriptor outlierDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("outlier", this.getClass(), "getOutlier", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OUTLIER_ID);
            desc.setDisplayName(Bundle.tramoSeatsSpecUI_outlierDesc_name());
            desc.setShortDescription(Bundle.tramoSeatsSpecUI_outlierDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSeatsSpecUI.arimaDesc.name=ARIMA",
        "tramoSeatsSpecUI.arimaDesc.desc="
    })
    private EnhancedPropertyDescriptor arimaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("arima", this.getClass(), "getArima", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ARIMA_ID);
            desc.setDisplayName(Bundle.tramoSeatsSpecUI_arimaDesc_name());
            desc.setShortDescription(Bundle.tramoSeatsSpecUI_arimaDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSeatsSpecUI.estimateDesc.name=ESTIMATE",
        "tramoSeatsSpecUI.estimateDesc.desc="
    })
    private EnhancedPropertyDescriptor estimateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("estimate", this.getClass(), "getEstimate", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ESTIMATE_ID);
            desc.setDisplayName(Bundle.tramoSeatsSpecUI_estimateDesc_name());
            desc.setShortDescription(Bundle.tramoSeatsSpecUI_estimateDesc_desc());
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSeatsSpecUI.seatsDesc.name=SEATS",
        "tramoSeatsSpecUI.seatsDesc.desc=Includes the settings relevant to the decomposition step, performed by the SEATS algorithm."
    })
    private EnhancedPropertyDescriptor seatsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("seats", this.getClass(), "getSeats", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SEATS_ID);
            desc.setDisplayName(Bundle.tramoSeatsSpecUI_seatsDesc_name());
            desc.setShortDescription(Bundle.tramoSeatsSpecUI_seatsDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tramoSeatsSpecUI.benchDesc.name=BENCHMARKING",
        "tramoSeatsSpecUI.benchDesc.desc="
    })
    private EnhancedPropertyDescriptor benchDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("benchmarking", this.getClass(), "getBenchmarking", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BENCH_ID);
            desc.setDisplayName(Bundle.tramoSeatsSpecUI_benchDesc_name());
            desc.setShortDescription(Bundle.tramoSeatsSpecUI_benchDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages("tramoSeatsSpecUI.getDisplayName=TramoSeats")
    @Override
    public String getDisplayName() {
        return Bundle.tramoSeatsSpecUI_getDisplayName();
    }

}
