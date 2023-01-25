/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.descriptors.regular;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.DateSelectorUI;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.modelling.regular.OutlierSpec;
import demetra.timeseries.TimeSelector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Kristof Bayens
 */
public abstract class AbstractOutlierSpecUI implements IPropertyDescriptors {

    @Override
    public String toString() {
        return "";
    }

    protected abstract OutlierSpec spec();
    
    protected abstract RegularSpecUI root();
    

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
        desc = tcDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = soDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = tcrateDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    @Messages("regular.outlierSpecUI.getDisplayName=Outliers")
    public String getDisplayName() {
        return Bundle.regular_outlierSpecUI_getDisplayName();
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

    public boolean isTC() {
        return spec().isTc();
    }

    public void setTC(boolean tc) {
        OutlierSpec spec = spec();
        if (tc == spec.isTc())
            return;
        root().update(spec().toBuilder().tc(tc).build());
    }

    public boolean isSO() {
        return spec().isSo();
    }

    public void setSO(boolean so) {
        OutlierSpec spec = spec();
        if (so == spec.isSo())
            return;
        root().update(spec().toBuilder().so(so).build());
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

    public double getTCRate() {
        return spec().getDeltaTC();
    }

    public void setTCRate(double value) {
        OutlierSpec spec = spec();
        if (spec.getDeltaTC() == value)
            return;
        root().update(spec().toBuilder().deltaTC(value).build());
   }

    private static final int ENABLED_ID = 0, SPAN_ID = 1,
            AO_ID = 2, LS_ID = 3, TC_ID = 4, SO_ID = 5,
            DEFAULTVA_ID = 3, VA_ID = 4,
            TCRATE_ID = 5;

    @Messages({
        "regular.outlierSpecUI.enableDesc.name=Is enabled",
        "regular.outlierSpecUI.enableDesc.desc=[iatip] Is automatic outliers detection enabled"
    })
    private EnhancedPropertyDescriptor enabledDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("OutliersDetectionEnabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_outlierSpecUI_enableDesc_name());
            desc.setShortDescription(Bundle.regular_outlierSpecUI_enableDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.outlierSpecUI.spanDesc.name=Detection span",
        "regular.outlierSpecUI.spanDesc.desc=[int1, int2] Time span used for the automatic outliers detection. Encompasses the int1 and int2 parameters."
    })
    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_outlierSpecUI_spanDesc_name());
            desc.setShortDescription(Bundle.regular_outlierSpecUI_spanDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.outlierSpecUI.aoDesc.name=Additive",
        "regular.outlierSpecUI.aoDesc.desc=[aio-partim] Additive outlier"
    })
    private EnhancedPropertyDescriptor aoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AO_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_outlierSpecUI_aoDesc_name());
            desc.setShortDescription(Bundle.regular_outlierSpecUI_aoDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.outlierSpecUI.lsDesc.name=Level shift",
        "regular.outlierSpecUI.lsDesc.desc=[aio-partim] Level shift"
    })
    private EnhancedPropertyDescriptor lsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LS", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_outlierSpecUI_lsDesc_name());
            desc.setShortDescription(Bundle.regular_outlierSpecUI_lsDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.outlierSpecUI.tcDesc.name=Transitory",
        "regular.outlierSpecUI.tcDesc.desc=[aio-partim] Transitory change"
    })
    private EnhancedPropertyDescriptor tcDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TC", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_outlierSpecUI_tcDesc_name());
            desc.setShortDescription(Bundle.regular_outlierSpecUI_tcDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.outlierSpecUI.soDesc.name=Seasonal",
        "regular.outlierSpecUI.soDesc.desc=[aio-partim] Seasonal outlier"
    })
    private EnhancedPropertyDescriptor soDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SO_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_outlierSpecUI_soDesc_name());
            desc.setShortDescription(Bundle.regular_outlierSpecUI_soDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.outlierSpecUI.vaDesc.name=Critical value",
        "regular.outlierSpecUI.vaDesc.desc=[va] The critical value used in the outliers detection procedure"
    })
    private EnhancedPropertyDescriptor vaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Va", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, VA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_outlierSpecUI_vaDesc_name());
            desc.setShortDescription(Bundle.regular_outlierSpecUI_vaDesc_desc());
            edesc.setReadOnly(root().isRo() || isAutoVa());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.outlierSpecUI.autoDesc.name=Use default critical value",
        "regular.outlierSpecUI.autoDesc.desc=[va] The critical value is automatically determined. It depends on the number of observations considered in the outliers detection procedure"
    })
    private EnhancedPropertyDescriptor autoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("autoVa", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DEFAULTVA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_outlierSpecUI_autoDesc_name());
            desc.setShortDescription(Bundle.regular_outlierSpecUI_autoDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.outlierSpecUI.tcrateDesc.name=TC rate",
        "regular.outlierSpecUI.tcrateDesc.desc=[deltatc] Rate of decay for the temporary change outlier regressor"
    })
    private EnhancedPropertyDescriptor tcrateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TCRate", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TCRATE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_outlierSpecUI_tcrateDesc_name());
            desc.setShortDescription(Bundle.regular_outlierSpecUI_tcrateDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

}
