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
import org.openide.util.NbBundle.Messages;

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

    @Messages({
        "estimateSpecUI.urlimitDesc.name=Unit root limit",
        "estimateSpecUI.urlimitDesc.desc=[urfinal] Unit root limit for final model. Should be > 1."
    })
    private EnhancedPropertyDescriptor urlimitDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ubp", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, UBP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.estimateSpecUI_urlimitDesc_name());
            desc.setShortDescription(Bundle.estimateSpecUI_urlimitDesc_desc());
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

    @Messages({
        "estimateSpecUI.spanDesc.name=Model span",
        "estimateSpecUI.spanDesc.desc=Span used for the estimation of the pre-processing model"
    })
    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.estimateSpecUI_spanDesc_desc());
            desc.setDisplayName(Bundle.estimateSpecUI_spanDesc_name());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "estimateSpecUI.tolDesc.name=Tolerance",
        "estimateSpecUI.tolDesc.desc=Precision of the estimation procedure"
    })
    private EnhancedPropertyDescriptor tolDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Tol", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TOL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.estimateSpecUI_tolDesc_name());
            desc.setShortDescription(Bundle.estimateSpecUI_tolDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "estimateSpecUI.emlDesc.name=Exact ML",
        "estimateSpecUI.emlDesc.desc=Use exact maximum likelihood in optimization procedure"
    })
    private EnhancedPropertyDescriptor emlDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Eml", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, EML_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.estimateSpecUI_emlDesc_name());
            desc.setShortDescription(Bundle.estimateSpecUI_emlDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    @Messages("estimateSpecUI.getDisplayName=Estimate")
    public String getDisplayName() {
        return Bundle.estimateSpecUI_getDisplayName();
    }
}
