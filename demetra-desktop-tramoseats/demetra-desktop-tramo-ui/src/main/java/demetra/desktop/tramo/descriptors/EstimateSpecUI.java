/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.DateSelectorUI;
import demetra.timeseries.TimeSelector;
import demetra.tramo.EstimateSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class EstimateSpecUI extends BaseTramoSpecUI {

    @Override
    public String toString() {
        return "";
    }
    
    private EstimateSpec inner(){
        return core().getEstimate();
    }
    
    EstimateSpecUI(TramoSpecRoot root) {
        super(root);
     }

    public DateSelectorUI getSpan() {
        return new DateSelectorUI(inner().getSpan(), isRo(), selector->updateSpan(selector));
    }

     public void updateSpan(TimeSelector span){
        update(inner().toBuilder().span(span).build());
    }

    public boolean isEml() {
        return inner().isMaximumLikelihood();
    }

    public void setEml(boolean value) {
        update(inner().toBuilder().maximumLikelihood(value).build());
    }

    public double getTol() {
        return inner().getTol();
    }

    public void setTol(double value) {
        update(inner().toBuilder().tol(value).build());
    }

    public double getUbp() {
        return inner().getUbp();
    }

    public void setUbp(double u) {
        update(inner().toBuilder().ubp(u).build());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
