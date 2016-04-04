/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.ui.descriptors.benchmarking.SaBenchmarkingSpecUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Kristof Bayens
 */
public class TramoSeatsSpecUI implements IObjectDescriptor<TramoSeatsSpecification> {

    final TramoSeatsSpecification core;
    final boolean ro_;

    public TramoSeatsSpecUI(TramoSeatsSpecification spec, boolean ro) {
        if (spec == null) {
            throw new AssertionError(EMPTY);
        }
       core = spec;
        ro_ = ro;
    }

    @Override
    public TramoSeatsSpecification getCore() {
        return core;
    }

    public BasicSpecUI getBasic() {
        return new BasicSpecUI(core.getTramoSpecification(), ro_);
    }

    public RegressionSpecUI getRegression() {
        return new RegressionSpecUI(core.getTramoSpecification(), ro_);
    }

    public TransformSpecUI getTransform() {
        return new TransformSpecUI(core.getTramoSpecification(), ro_);
    }

    public ArimaSpecUI getArima() {
        return new ArimaSpecUI(core.getTramoSpecification(), ro_);
    }

    public OutlierSpecUI getOutlier() {
        return new OutlierSpecUI(core.getTramoSpecification(), ro_);
    }

    public EstimateSpecUI getEstimate() {
        return new EstimateSpecUI(core.getTramoSpecification(), ro_);
    }

    public SeatsSpecUI getSeats() {
        return new SeatsSpecUI(core.getSeatsSpecification(), ro_);
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
