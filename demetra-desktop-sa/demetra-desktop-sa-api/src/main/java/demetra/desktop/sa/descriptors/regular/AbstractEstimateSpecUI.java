/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.descriptors.regular;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.DateSelectorUI;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.modelling.regular.EstimateSpec;
import demetra.timeseries.TimeSelector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractEstimateSpecUI implements IPropertyDescriptors {

    @Override
    public String toString() {
        return "";
    }
    
    protected abstract EstimateSpec spec();
    
    protected abstract RegularSpecUI root();
    
    public DateSelectorUI getSpan() {
        return new DateSelectorUI(spec().getSpan(), root().isRo(), selector->updateSpan(selector));
    }

     public void updateSpan(TimeSelector span){
        root().update(spec().toBuilder().span(span).build());
    }

    public double getTol() {
        return spec().getPrecision();
    }

    public void setTol(double value) {
        root().update(spec().toBuilder().precision(value).build());
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
    @Messages({
        "regular.estimateSpecUI.tolDesc.name=Tolerance",
        "regular.estimateSpecUI.tolDesc.desc=[tol] Precision used in the optimization procedure."
    })
    private EnhancedPropertyDescriptor tolDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Tol", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TOL_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_estimateSpecUI_tolDesc_name());
            desc.setShortDescription(Bundle.regular_estimateSpecUI_tolDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.estimateSpecUI.spanDesc.name=Model span",
        "regular.estimateSpecUI.spanDesc.desc=Span used for the estimation of the pre-processing model"
    })
    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.regular_estimateSpecUI_spanDesc_desc());
            desc.setDisplayName(Bundle.regular_estimateSpecUI_spanDesc_name());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    private static final int SPAN_ID = 0, TOL_ID = 1;

    @Override
    @Messages("regular.estimateSpecUI.getDisplayName=ESTIMATE")
    public String getDisplayName() {
        return Bundle.regular_estimateSpecUI_getDisplayName();
    }
}

