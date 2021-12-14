/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.DateSelectorUI;
import demetra.regarima.OutlierSpec;
import demetra.regarima.SingleOutlierSpec;
import demetra.timeseries.TimeSelector;
import demetra.timeseries.regression.AdditiveOutlier;
import demetra.timeseries.regression.LevelShift;
import demetra.timeseries.regression.PeriodicOutlier;
import demetra.timeseries.regression.TransitoryChange;
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

    public OutlierSpecUI(RegArimaSpecRoot root) {
        super(root);
    }

    @Override
    public String toString() {
        return "";
    }

    private OutlierSpec inner() {
        return core().getOutliers();
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
        return descs;
    }

    @Override
    @Messages("outlierSpecUI.getDisplayName=Outliers")
    public String getDisplayName() {
        return Bundle.outlierSpecUI_getDisplayName();
    }

    public boolean isEnabled() {
        return inner().isUsed();
    }

    public void setEnabled(boolean value) {
        if (!value) {
            update(OutlierSpec.DEFAULT_DISABLED);
        } else {
            update(OutlierSpec.DEFAULT_ENABLED);
        }
    }

    public DateSelectorUI getSpan() {
        return new DateSelectorUI(inner().getSpan(), isRo(), span -> updateSpan(span));
    }

    public void updateSpan(TimeSelector span) {
        update(inner().toBuilder().span(span).build());
    }

    public boolean isAO() {
        return inner().search(AdditiveOutlier.CODE) != null;
    }

    private void update(String o, boolean enabled) {
        if (enabled == (inner().search(o) != null))
            return;
        if (enabled) {
            update(inner().toBuilder().type(new SingleOutlierSpec(o, 0)).build());
        } else {
            OutlierSpec.Builder builder = inner().toBuilder();
            for (SingleOutlierSpec spec : inner().getTypes()) {
                if (!spec.getType().equals(o)) {
                    builder.type(spec);
                }
            }
            update(builder.build());
        }
    }

    public void setAO(boolean ao) {
        update(AdditiveOutlier.CODE, ao);
    }

    public boolean isLS() {
        return inner().search(LevelShift.CODE) != null;
   }

    public void setLS(boolean ls) {
         update(LevelShift.CODE, ls);
     }

    public boolean isTC() {
        return inner().search(TransitoryChange.CODE) != null;
    }

    public void setTC(boolean tc) {
        update(TransitoryChange.CODE, tc);
    }

    public boolean isSO() {
        return inner().search(PeriodicOutlier.CODE) != null;
    }

    public void setSO(boolean so) {
        update(PeriodicOutlier.CODE, so);
    }

    public boolean isDefaultVa() {
        return inner().getDefaultCriticalValue() == 0;

    }

    public void setDefaultVa(boolean value) {
        if (value) {
            update(inner().toBuilder().defaultCriticalValue(0).build());
        } else {
            update(inner().toBuilder().defaultCriticalValue(4).build());
        }
    }

    public double getVa() {
        return inner().getDefaultCriticalValue() == 0 ? 4 : inner().getDefaultCriticalValue();

    }

    public void setVa(double value) {
            update(inner().toBuilder().defaultCriticalValue(value).build());
    }

    public double getTCRate() {
        return inner().getMonthlyTCRate();
    }

    public void setTCRate(double value) {
             update(inner().toBuilder().monthlyTCRate(value).build());
   }

    public OutlierSpec.Method getMethod() {
        return inner().getMethod();
    }

    public void setMethod(OutlierSpec.Method value) {
             update(inner().toBuilder().method(value).build());
    }

    public int getLSRun() {
        return inner().getLsRun();
    }

    public void setLSRun(int value) {
             update(inner().toBuilder().lsRun(value).build());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo() || !isEnabled());
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
            edesc.setReadOnly(isRo() || !isEnabled());
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
            edesc.setReadOnly(isRo() || !isEnabled());
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
            edesc.setReadOnly(isRo() || !isEnabled());
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
            edesc.setReadOnly(isRo() || !isEnabled());
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
            edesc.setReadOnly(isRo() || (!isEnabled() && isDefaultVa()));
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
            edesc.setReadOnly(isRo() || !isEnabled());
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
            edesc.setReadOnly(isRo() || !isEnabled());
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
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
