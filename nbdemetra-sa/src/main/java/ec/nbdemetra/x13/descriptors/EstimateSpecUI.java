/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.tstoolkit.modelling.arima.x13.EstimateSpec;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.ui.descriptors.TsPeriodSelectorUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pcuser & BAYENSK
 */
public class EstimateSpecUI extends BaseRegArimaSpecUI {

    EstimateSpecUI(RegArimaSpecification spec, boolean ro) {
        super(spec, ro);
    }

    private EstimateSpec inner() {
        return core.getEstimate();
    }

    public TsPeriodSelectorUI getSpan() {
        return new TsPeriodSelectorUI(inner().getSpan(), ro_);
    }

    public void setSpan(TsPeriodSelectorUI span) {
        inner().setSpan(span.getCore());
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = spanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = tolDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Estimate";
    }

    public double getTol() {
        EstimateSpec spec = inner();
        return spec.getTol();
    }

    public void setTol(double value) {
        inner().setTol(value);
    }

    private EnhancedPropertyDescriptor tolDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Tol", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TOL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(TOL_NAME);
            desc.setShortDescription(TOL_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(SPAN_DESC);
            desc.setDisplayName(SPAN_NAME);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    private static final int SPAN_ID = 0, TOL_ID = 1;
    private static final String TOL_NAME = "Tolerance", EML_NAME = "Exact ML", SPAN_NAME = "Model span";
    private static final String TOL_DESC = "[tol] Precision used in the optimization procedure.",
            SPAN_DESC = "Span used for the estimation of the pre-processing model";
}
