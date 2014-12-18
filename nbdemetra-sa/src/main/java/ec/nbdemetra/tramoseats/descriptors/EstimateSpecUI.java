/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.tstoolkit.modelling.arima.tramo.EstimateSpec;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.ui.descriptors.TsPeriodSelectorUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pcuser
 */
public class EstimateSpecUI extends BaseTramoSpecUI {

    @Override
    public String toString() {
        return "";
    }

    private EstimateSpec inner() {
        return core.getEstimate();
    }

    EstimateSpecUI(TramoSpecification spec, boolean ro) {
        super(spec, ro);
    }

    public TsPeriodSelectorUI getSpan() {
        EstimateSpec spec = core.getEstimate();
        if (spec == null) {
            spec = new EstimateSpec();
            core.setEstimate(spec);
        }
        return new TsPeriodSelectorUI(spec.getSpan(), ro_);
    }

    public void setSpan(TsPeriodSelectorUI span) {
        inner().setSpan(span.getCore());
    }

    public boolean isEml() {
        return inner().isEML();
    }

    public void setEml(boolean value) {
        inner().setEML(value);
    }

    public double getTol() {
        return inner().getTol();
    }

    public void setTol(double value) {
        inner().setTol(value);
    }

    public double getUbp() {
        return inner().getUbp();
    }

    public void setUbp(double u) {
        inner().setUbp(u);
    }

    private EnhancedPropertyDescriptor urlimitDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ubp", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, UBP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(URLIMIT_NAME);
            desc.setShortDescription(URLIMIT_NAME);
            edesc.setReadOnly(ro_);
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
        desc = tolDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = emlDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = urlimitDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private static final int SPAN_ID = 0, TOL_ID = 1, EML_ID = 2, XL_ID = 3, UBP_ID = 4;
    private static final String TOL_NAME = "Tolerance", EML_NAME = "Exact ML", SPAN_NAME = "Model span";
    private static final String TOL_DESC = "Precision of the estimation procedure",
            EML_DESC = "Use exact maximum likelihood in optimization procedure",
            SPAN_DESC = "Span used for the estimation of the pre-processing model";

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

    private EnhancedPropertyDescriptor emlDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Eml", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, EML_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(EML_NAME);
            desc.setShortDescription(EML_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "Estimate";
    }
    private static final String URLIMIT_NAME = "Unit root limit", URLIMIT_DESC = "[urfinal] Unit root limit for final model. Should be > 1.";
}
