/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa.advanced.descriptors.mixedfrequencies;

import ec.tstoolkit.arima.special.mixedfrequencies.EstimateSpec;
import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.ui.descriptors.TsPeriodSelectorUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class EstimateSpecUI extends BaseSpecUI {

    @Override
    public String toString() {
        return "";
    }

    private EstimateSpec inner() {
        return core.getEstimate();
    }

    EstimateSpecUI(MixedFrequenciesSpecification spec) {
        super(spec);
    }

    public TsPeriodSelectorUI getSpan() {
        EstimateSpec spec = core.getEstimate();
        if (spec == null) {
            spec = new EstimateSpec();
            core.setEstimate(spec);
        }
        return new TsPeriodSelectorUI(spec.getSpan(), false);
    }

    public void setSpan(TsPeriodSelectorUI span) {
        inner().setSpan(span.getCore());
    }

    public EstimateSpec.Method getMethod() {
        return inner().getMethod();
    }

    public void setMethod(EstimateSpec.Method m) {
        inner().setMethod(m);
    }

    public double getTol() {
        return inner().getTol();
    }

    public void setTol(double value) {
        inner().setTol(value);
    }

    private EnhancedPropertyDescriptor mDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("method", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, METHOD_ID);
            desc.setDisplayName(METHOD_NAME);
            desc.setShortDescription(METHOD_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = spanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = mDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = tolDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private static final int SPAN_ID = 0, TOL_ID = 1, METHOD_ID = 2;
    private static final String TOL_NAME = "Tolerance", METHOD_NAME = "Estimation method", SPAN_NAME = "Model span";
    private static final String TOL_DESC = "Precision of the estimation procedure",
            METHOD_DESC = "Estimation method",
            SPAN_DESC = "Span used for the estimation of the pre-processing model";

    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(SPAN_DESC);
            desc.setDisplayName(SPAN_NAME);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor tolDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Tol", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TOL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(TOL_NAME);
            desc.setShortDescription(TOL_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "Estimate";
    }

}
