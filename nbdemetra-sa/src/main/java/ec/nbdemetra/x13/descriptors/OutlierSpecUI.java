/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.arima.x13.OutlierSpec;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.regression.OutlierType;
import ec.ui.descriptors.TsPeriodSelectorUI;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

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
        desc = lsrunDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Outliers";
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

    private EnhancedPropertyDescriptor enabledDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("enabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(ENABLED_NAME);
            desc.setShortDescription(ENABLED_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Span", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(SPAN_NAME);
            desc.setShortDescription(SPAN_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor aoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AO_ID);
            desc.setDisplayName(AO_NAME);
            desc.setShortDescription(AO_DESC);
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor lsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LS", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LS_ID);
            desc.setDisplayName(LS_NAME);
            desc.setShortDescription(LS_DESC);
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor tcDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TC", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TC_ID);
            desc.setDisplayName(TC_NAME);
            desc.setShortDescription(TC_DESC);
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor soDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SO_ID);
            desc.setDisplayName(SO_NAME);
            desc.setShortDescription(SO_DESC);
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor defaultvaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("DefaultVa", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DEFAULTVA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(DEFAULTVA_NAME);
            desc.setShortDescription(DEFAULTVA_DESC);
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor vaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Va", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, VA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(VA_NAME);
            desc.setShortDescription(VA_DESC);
            edesc.setReadOnly(ro_ || (!isEnabled() && isDefaultVa()));
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor tcrateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TCRate", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TCRATE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(TCRATE_NAME);
            desc.setShortDescription(TCRATE_DESC);
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor methodDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Method", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, METHOD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(METHOD_NAME);
            desc.setShortDescription(METHOD_DESC);
            edesc.setReadOnly(ro_ || !isEnabled());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor lsrunDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LSRun", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LSRUN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(LSRUN_NAME);
            desc.setShortDescription(LSRUN_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    private static final String ENABLED_NAME = "Is enabled",
            SPAN_NAME = "Detection span",
            AO_NAME = "Additive",
            LS_NAME = "Level shift",
            TC_NAME = "Transitory",
            SO_NAME = "Seasonal",
            DEFAULTVA_NAME = "Use default critical value",
            VA_NAME = "Critical value",
            TCRATE_NAME = "TC rate",
            METHOD_NAME = "Method",
            LSRUN_NAME = "LS Run";
    private static final String ENABLED_DESC = "Is automatic outliers detection enabled",
            SPAN_DESC = "[int1, int2] Time span used for the automatic outliers detection. Encompasses the int1 and int2 parameters.",
            AO_DESC = "[ao] Additive outlier",
            LS_DESC = "[ls] Level shift",
            TC_DESC = "[tc] Transitory change",
            SO_DESC = "[so] Seasonal outlier",
            DEFAULTVA_DESC = "[critical] The critical value is automatically determined. It depends on the number of observations considered in the outliers detection procedure.",
            VA_DESC = "[critical] The critical value used in the outliers detection procedure.",
            TCRATE_DESC = "[tcrate] Rate of decay for the temporary change outlier regressor.",
            METHOD_DESC = "[method] Determines how the program successively adds detected outliers to the model.",
            LSRUN_DESC = "[lsrun] Compute t-statistics to test null hypotheses that each run of n lsrun successive level shifts cancels to form a temporary level shift.";
}
