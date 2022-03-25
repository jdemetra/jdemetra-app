/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.DateSelectorUI;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.highfreq.EstimateSpec;
import demetra.timeseries.TimeSelector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class EstimateSpecUI extends BaseFractionalAirlineSpecUI {

    @Override
    public String toString() {
        return "";
    }

    private EstimateSpec inner() {
        return core().getEstimate();
    }

    public EstimateSpecUI(FractionalAirlineSpecRoot root) {
        super(root);
    }

    public DateSelectorUI getSpan() {
        return new DateSelectorUI(inner().getSpan(), isRo(), selector -> updateSpan(selector));
    }

    public void updateSpan(TimeSelector span) {
        update(inner().toBuilder().span(span).build());
    }

    public boolean isApproximateHessian() {
        return inner().isApproximateHessian();
    }

    public void setApproximateHessian(boolean value) {
        update(inner().toBuilder().approximateHessian(value).build());
    }

    public double getTol() {
        return inner().getPrecision();
    }

    public void setTol(double value) {
        update(inner().toBuilder().precision(value).build());
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
        desc = hessianDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    private static final int SPAN_ID = 0, TOL_ID = 1, HESSIAN_ID = 2;

    @NbBundle.Messages({
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

    @NbBundle.Messages({
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

    private EnhancedPropertyDescriptor hessianDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ApproximateHessian", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HESSIAN_ID);
            desc.setDisplayName("approximate hessian");
            desc.setShortDescription("Use approximate hessian to comput stderr of the parameters");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    @NbBundle.Messages("estimateSpecUI.getDisplayName=Estimate")
    public String getDisplayName() {
        return Bundle.estimateSpecUI_getDisplayName();
    }
}
