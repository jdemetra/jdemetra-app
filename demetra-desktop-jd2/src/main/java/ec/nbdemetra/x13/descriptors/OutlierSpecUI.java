/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.arima.x13.OutlierSpec;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.ui.descriptors.TsPeriodSelectorUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Kristof Bayens
 */
public class OutlierSpecUI extends BaseRegArimaSpecUI {

    OutlierSpecUI(RegArimaSpecification spec, boolean ro) {
        super(spec, ro);
    }

    @Override
    public String toString() {
        return "";
    }

    private OutlierSpec inner() {
        return core.getOutliers();
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = enabledDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = spanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = defaultvaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = vaDesc();
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
        desc = methodDesc();
        if (desc != null) {
            descs.add(desc);
        }
        // Not implemented !
//        desc = lsrunDesc();
//        if (desc != null) {
//            descs.add(desc);
//        }
        return descs;
    }

    @Messages("outlierSpecUI.getDisplayName=Outliers")
    @Override
    public String getDisplayName() {
        return Bundle.outlierSpecUI_getDisplayName();
    }

    public boolean isEnabled() {
        return inner().getTypesCount() > 0;
    }

    public void setEnabled(boolean value) {
        OutlierSpec spec = inner();
        if (!value) {
            spec.clearTypes();
        } else if (spec.getTypesCount() == 0) {
            spec.add(OutlierType.AO);
            spec.add(OutlierType.LS);
            spec.add(OutlierType.TC);
        }
    }

    public TsPeriodSelectorUI getSpan() {
        return new TsPeriodSelectorUI(inner().getSpan(), ro_);
    }

    public void setSpan(TsPeriodSelectorUI value) {
        inner().setSpan(value.getCore());
    }

    public boolean getAO() {
        return inner().search(OutlierType.AO) != null;
    }

    public void setAO(boolean value) {
        if (value) {
            inner().add(OutlierType.AO);
        } else {
            inner().remove(OutlierType.AO);
        }
    }

    public boolean getLS() {
        return inner().search(OutlierType.LS) != null;
    }

    public void setLS(boolean value) {
        if (value) {
            inner().add(OutlierType.LS);
        } else {
            inner().remove(OutlierType.LS);
        }
    }

    public boolean getTC() {
        return inner().search(OutlierType.TC) != null;
    }

    public void setTC(boolean value) {
        if (value) {
            inner().add(OutlierType.TC);
        } else {
            inner().remove(OutlierType.TC);
        }
    }

    public boolean getSO() {
        return inner().search(OutlierType.SO) != null;
    }

    public void setSO(boolean value) {
        if (value) {
            inner().add(OutlierType.SO);
        } else {
            inner().remove(OutlierType.SO);
        }
    }

    public boolean isDefaultVa() {
        return inner().getDefaultCriticalValue() == 0;

    }

    public void setDefaultVa(boolean value) {
        if (value) {
            inner().setDefaultCriticalValue(0);
        } else {
            inner().setDefaultCriticalValue(4);
        }
    }

    public double getVa() {
        return inner().getDefaultCriticalValue() == 0 ? 4 : inner().getDefaultCriticalValue();

    }

    public void setVa(double value) {
        inner().setDefaultCriticalValue(value);
    }

    public double getTCRate() {
        return inner().getMonthlyTCRate();
    }

    public void setTCRate(double value) {
        inner().setMonthlyTCRate(value);
    }

    public OutlierSpec.Method getMethod() {
        return inner().getMethod();
    }

    public void setMethod(OutlierSpec.Method value) {
        inner().setMethod(value);
    }

    public int getLSRun() {
        return inner().getLSRun();
    }

    public void setLSRun(int value) {
        inner().setLSRun(value);
    }
    private static final int ENABLED_ID = 0, SPAN_ID = 1, AO_ID = 2, LS_ID = 3, TC_ID = 4, SO_ID = 5,
            DEFAULTVA_ID = 6, VA_ID = 7, TCRATE_ID = 8, METHOD_ID = 9, LSRUN_ID = 10;

    @Messages({
        "outlierSpecUI.enabledDesc.name=Is enabled",
        "outlierSpecUI.enabledDesc.desc=Is automatic outliers detection enabled"
    })
    private EnhancedPropertyDescriptor enabledDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("enabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outlierSpecUI_enabledDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_enabledDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outlierSpecUI.spanDesc.name=Detection span",
        "outlierSpecUI.spanDesc.desc=[int1, int2] Time span used for the automatic outliers detection. Encompasses the int1 and int2 parameters."
    })
    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Span", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outlierSpecUI_spanDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_spanDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outlierSpecUI.aoDesc.name=Additive",
        "outlierSpecUI.aoDesc.desc=[ao] Additive outlier"
    })
    private EnhancedPropertyDescriptor aoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AO_ID);
            desc.setDisplayName(Bundle.outlierSpecUI_aoDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_aoDesc_desc());
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outlierSpecUI.lsDesc.name=Level shift",
        "outlierSpecUI.lsDesc.desc=[ls] Level shift"
    })
    private EnhancedPropertyDescriptor lsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LS", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LS_ID);
            desc.setDisplayName(Bundle.outlierSpecUI_lsDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_lsDesc_desc());
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outlierSpecUI.tcDesc.name=Transitory",
        "outlierSpecUI.tcDesc.desc=[tc] Transitory change"
    })
    private EnhancedPropertyDescriptor tcDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TC", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TC_ID);
            desc.setDisplayName(Bundle.outlierSpecUI_tcDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_tcDesc_desc());
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outlierSpecUI.soDesc.name=Seasonal",
        "outlierSpecUI.soDesc.desc=[so] Seasonal outlier"
    })
    private EnhancedPropertyDescriptor soDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SO_ID);
            desc.setDisplayName(Bundle.outlierSpecUI_soDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_soDesc_desc());
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outlierSpecUI.defaultvaDesc.name=Use default critical value",
        "outlierSpecUI.defaultvaDesc.desc=[critical] The critical value is automatically determined. It depends on the number of observations considered in the outliers detection procedure."
    })
    private EnhancedPropertyDescriptor defaultvaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("DefaultVa", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DEFAULTVA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outlierSpecUI_defaultvaDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_defaultvaDesc_desc());
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outlierSpecUI.vaDesc.name=Critical value",
        "outlierSpecUI.vaDesc.desc=[critical] The critical value used in the outliers detection procedure."
    })
    private EnhancedPropertyDescriptor vaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Va", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, VA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outlierSpecUI_vaDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_vaDesc_desc());
            edesc.setReadOnly(ro_ || (!isEnabled() && isDefaultVa()));
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outlierSpecUI.tcrateDesc.name=TC rate",
        "outlierSpecUI.tcrateDesc.desc=[tcrate] Rate of decay for the temporary change outlier regressor."
    })
    private EnhancedPropertyDescriptor tcrateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TCRate", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TCRATE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outlierSpecUI_tcrateDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_tcrateDesc_desc());
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outlierSpecUI.methodDesc.name=Method",
        "outlierSpecUI.methodDesc.desc=[method] Determines how the program successively adds detected outliers to the model."
    })
    private EnhancedPropertyDescriptor methodDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Method", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, METHOD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outlierSpecUI_methodDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_methodDesc_desc());
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outlierSpecUI.lsrunDesc.name=LS Run",
        "outlierSpecUI.lsrunDesc.desc=[lsrun] Compute t-statistics to test null hypotheses that each run of n lsrun successive level shifts cancels to form a temporary level shift."
    })
    private EnhancedPropertyDescriptor lsrunDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LSRun", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LSRUN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outlierSpecUI_lsrunDesc_name());
            desc.setShortDescription(Bundle.outlierSpecUI_lsrunDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
