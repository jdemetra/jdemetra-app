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
import demetra.modelling.highfreq.OutlierSpec;
import demetra.timeseries.TimeSelector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author PALATEJ
 */
public abstract class AbstractOutlierSpecUI implements IPropertyDescriptors  {

    @Override
    public String toString() {
        return "";
    }

    protected abstract HighFreqSpecUI root();
    
    protected abstract OutlierSpec spec();

     @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = enabledDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = autoDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = vaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = spanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = aoDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lsDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = woDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    @Messages("highfreq.outlierSpecUI.getDisplayName=Outliers")
    public String getDisplayName() {
        return Bundle.highfreq_outlierSpecUI_getDisplayName();
    }

    public boolean isOutliersDetectionEnabled() {
        OutlierSpec spec = spec();
        return spec.isUsed();
    }

    public void setOutliersDetectionEnabled(boolean value) {
        if (!value) {
            root().update(OutlierSpec.DEFAULT_DISABLED);
        } else {
            root().update(OutlierSpec.DEFAULT_ENABLED);
        }
    }

    public DateSelectorUI getSpan() {
        return new DateSelectorUI(spec().getSpan(), root().isRo(), span->updateSpan(span));
    }

    public void updateSpan(TimeSelector span){
         root().update(spec().toBuilder().span(span).build());
    }

    public boolean isAO() {
        return spec().isAo();
    }

    public void setAO(boolean ao) {
        OutlierSpec spec = spec();
        if (ao == spec.isAo())
            return;
        root().update(spec().toBuilder().ao(ao).build());
    }

    public boolean isLS() {
        return spec().isLs();
    }

    public void setLS(boolean ls) {
        OutlierSpec spec = spec();
        if (ls == spec.isLs())
            return;
        root().update(spec().toBuilder().ls(ls).build());
    }

    public boolean isWO() {
        return spec().isWo();
    }

    public void setWO(boolean wo) {
        OutlierSpec spec = spec();
        if (wo == spec.isWo())
            return;
        root().update(spec().toBuilder().wo(wo).build());
    }

    public double getVa() {
        double va = spec().getCriticalValue();
        return va == 0 ? 3.5 : va;
    }

    public void setVa(double value) {
        OutlierSpec spec = spec();
        if (spec.getCriticalValue() == value)
            return;
        root().update(spec().toBuilder().criticalValue(value).build());
    }

    public boolean isAutoVa() {
        return spec().getCriticalValue() == 0;
    }

    public void setAutoVa(boolean value) {
        setVa(value ? 0 : 3.5);
    }

    private static final int ENABLED_ID = 0, SPAN_ID = 1,
            AO_ID = 2, LS_ID = 3, WO_ID = 5,
            DEFAULTVA_ID = 3, VA_ID = 4,
            TCRATE_ID = 5;

    @Messages({
        "highfreq.outlierSpecUI.enableDesc.name=Is enabled",
        "highfreq.outlierSpecUI.enableDesc.desc=[iatip] Is automatic outliers detection enabled"
    })
    private EnhancedPropertyDescriptor enabledDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("OutliersDetectionEnabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.highfreq_outlierSpecUI_enableDesc_name());
            desc.setShortDescription(Bundle.highfreq_outlierSpecUI_enableDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "highfreq.outlierSpecUI.spanDesc.name=Detection span",
        "highfreq.outlierSpecUI.spanDesc.desc=[int1, int2] Time span used for the automatic outliers detection. Encompasses the int1 and int2 parameters."
    })
    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.highfreq_outlierSpecUI_spanDesc_name());
            desc.setShortDescription(Bundle.highfreq_outlierSpecUI_spanDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "highfreq.outlierSpecUI.aoDesc.name=Additive",
        "highfreq.outlierSpecUI.aoDesc.desc=[aio-partim] Additive outlier"
    })
    private EnhancedPropertyDescriptor aoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AO_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.highfreq_outlierSpecUI_aoDesc_name());
            desc.setShortDescription(Bundle.highfreq_outlierSpecUI_aoDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "highfreq.outlierSpecUI.lsDesc.name=Level shift",
        "highfreq.outlierSpecUI.lsDesc.desc=[aio-partim] Level shift"
    })
    private EnhancedPropertyDescriptor lsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LS", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.highfreq_outlierSpecUI_lsDesc_name());
            desc.setShortDescription(Bundle.highfreq_outlierSpecUI_lsDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "highfreq.outlierSpecUI.woDesc.name=Switch",
        "highfreq.outlierSpecUI.woDesc.desc=Switch outlier"
    })
    private EnhancedPropertyDescriptor woDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("WO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, WO_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.highfreq_outlierSpecUI_woDesc_name());
            desc.setShortDescription(Bundle.highfreq_outlierSpecUI_woDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "highfreq.outlierSpecUI.vaDesc.name=Critical value",
        "highfreq.outlierSpecUI.vaDesc.desc=[va] The critical value used in the outliers detection procedure"
    })
    private EnhancedPropertyDescriptor vaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Va", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, VA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.highfreq_outlierSpecUI_vaDesc_name());
            desc.setShortDescription(Bundle.highfreq_outlierSpecUI_vaDesc_desc());
            edesc.setReadOnly(root().isRo() || isAutoVa());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "highfreq.outlierSpecUI.autoDesc.name=Use default critical value",
        "highfreq.outlierSpecUI.autoDesc.desc=[va] The critical value is automatically determined. It depends on the number of observations considered in the outliers detection procedure"
    })
    private EnhancedPropertyDescriptor autoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("autoVa", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DEFAULTVA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.highfreq_outlierSpecUI_autoDesc_name());
            desc.setShortDescription(Bundle.highfreq_outlierSpecUI_autoDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

}
