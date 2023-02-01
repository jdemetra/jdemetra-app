/*
 * Copyright 2023 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.sa.descriptors.highfreq;

import demetra.desktop.descriptors.DateSelectorUI;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.modelling.highfreq.EstimateSpec;
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
public abstract class AbstractEstimateSpecUI implements IPropertyDescriptors {

    @Override
    public String toString() {
        return "";
    }

    protected abstract HighFreqSpecUI root();
    
    protected abstract EstimateSpec spec();


    public DateSelectorUI getSpan() {
        return new DateSelectorUI(spec().getSpan(), root().isRo(), selector -> updateSpan(selector));
    }

    public void updateSpan(TimeSelector span) {
        root().update(spec().toBuilder().span(span).build());
    }

    public boolean isApproximateHessian() {
        return spec().isApproximateHessian();
    }

    public void setApproximateHessian(boolean value) {
        root().update(spec().toBuilder().approximateHessian(value).build());
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
            edesc.setReadOnly(root().isRo());
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
            edesc.setReadOnly(root().isRo());
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
            edesc.setReadOnly(root().isRo());
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
