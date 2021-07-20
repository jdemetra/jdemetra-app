/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.arima.tramo.OutlierSpec;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
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
public class OutlierSpecUI extends BaseTramoSpecUI {

    OutlierSpecUI(TramoSpecification spec, boolean ro) {
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
        desc = emlDesc();
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
    @Messages("outlierSpecUI.getDisplayName=Outliers")
    public String getDisplayName() {
        return Bundle.outlierSpecUI_getDisplayName();
    }

    public boolean isOutliersDetectionEnabled() {
        OutlierSpec spec = inner();
        return spec.isUsed();
    }

    public void setOutliersDetectionEnabled(boolean value) {
        OutlierSpec spec = inner();
        if (!value) {
            spec.clearTypes();
        } else {
            //} else if (spec.getTypes() != null) {
            spec.add(OutlierType.AO);
            spec.add(OutlierType.LS);
            spec.add(OutlierType.TC);
        }
    }

    public TsPeriodSelectorUI getSpan() {
        return new TsPeriodSelectorUI(inner().getSpan(), ro_);
    }

    public void setSpan(TsPeriodSelectorUI value) {
    }

    public boolean isAO() {
        return inner().contains(OutlierType.AO);
    }

    public void setAO(boolean ao) {
        OutlierSpec spec = inner();
        if (ao) {
            spec.add(OutlierType.AO);
        } else {
            spec.remove(OutlierType.AO);
        }
    }

    public boolean isLS() {
        return inner().contains(OutlierType.LS);
    }

    public void setLS(boolean ls) {
        OutlierSpec spec = inner();
        if (ls) {
            spec.add(OutlierType.LS);
        } else {
            spec.remove(OutlierType.LS);
        }
    }

    public boolean isTC() {
        return inner().contains(OutlierType.TC);
    }

    public void setTC(boolean tc) {
        OutlierSpec spec = inner();
        if (tc) {
            spec.add(OutlierType.TC);
        } else {
            spec.remove(OutlierType.TC);
        }
    }

    public boolean isSO() {
        return inner().contains(OutlierType.SO);
    }

    public void setSO(boolean so) {
        OutlierSpec spec = inner();
        if (so) {
            spec.add(OutlierType.SO);
        } else {
            spec.remove(OutlierType.SO);
        }
    }

    public double getVa() {
        double va = inner().getCriticalValue();
        return va == 0 ? 3.5 : va;
    }

    public void setVa(double value) {
        inner().setCriticalValue(value);
    }

    public boolean isAutoVa() {
        return inner().getCriticalValue() == 0;
    }

    public void setAutoVa(boolean value) {
        inner().setCriticalValue(value ? 0 : 3.5);
    }

    public double getTCRate() {
        return inner().getDeltaTC();
    }

    public void setTCRate(double value) {
        inner().setDeltaTC(value);
    }

    public boolean isEML() {
        return inner().isEML();
    }

    public void setEML(boolean value) {
        inner().setEML(value);
    }
    private static final int ENABLED_ID = 0, SPAN_ID = 1,
            AO_ID = 2, LS_ID = 3, TC_ID = 4, SO_ID = 5,
            DEFAULTVA_ID = 3, VA_ID = 4,
            TCRATE_ID = 5, EML_ID = 6;

    @Messages({
        "outliersSpecUI.enableDesc.name=Is enabled",
        "outliersSpecUI.enableDesc.desc=[iatip] Is automatic outliers detection enabled"
    })
    private EnhancedPropertyDescriptor enabledDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("OutliersDetectionEnabled", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ENABLED_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_enableDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_enableDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outliersSpecUI.spanDesc.name=Detection span",
        "outliersSpecUI.spanDesc.desc=[int1, int2] Time span used for the automatic outliers detection. Encompasses the int1 and int2 parameters."
    })
    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Span", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_spanDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_spanDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outliersSpecUI.aoDesc.name=Additive",
        "outliersSpecUI.aoDesc.desc=[aio-partim] Additive outlier"
    })
    private EnhancedPropertyDescriptor aoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AO_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_aoDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_aoDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outliersSpecUI.lsDesc.name=Level shift",
        "outliersSpecUI.lsDesc.desc=[aio-partim] Level shift"
    })
    private EnhancedPropertyDescriptor lsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LS", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_lsDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_lsDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outliersSpecUI.tcDesc.name=Transitory",
        "outliersSpecUI.tcDesc.desc=[aio-partim] Transitory change"
    })
    private EnhancedPropertyDescriptor tcDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TC", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_tcDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_tcDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outliersSpecUI.soDesc.name=Seasonal",
        "outliersSpecUI.soDesc.desc=[aio-partim] Seasonal outlier"
    })
    private EnhancedPropertyDescriptor soDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SO_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_soDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_soDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outliersSpecUI.vaDesc.name=Critical value",
        "outliersSpecUI.vaDesc.desc=[va] The critical value used in the outliers detection procedure"
    })
    private EnhancedPropertyDescriptor vaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Va", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, VA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_vaDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_vaDesc_desc());
            edesc.setReadOnly(ro_ || isAutoVa());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outliersSpecUI.autoDesc.name=Use default critical value",
        "outliersSpecUI.autoDesc.desc=[va] The critical value is automatically determined. It depends on the number of observations considered in the outliers detection procedure"
    })
    private EnhancedPropertyDescriptor autoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("autoVa", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DEFAULTVA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_autoDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_autoDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outliersSpecUI.tcrateDesc.name=TC rate",
        "outliersSpecUI.tcrateDesc.desc=[deltatc] Rate of decay for the temporary change outlier regressor"
    })
    private EnhancedPropertyDescriptor tcrateDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TCRate", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TCRATE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_tcrateDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_tcrateDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "outliersSpecUI.emlDesc.name=EML estimation",
        "outliersSpecUI.emlDesc.desc=[imvx] True if exact likelihood estimation method is used, false if the fast Hannan-Rissanen method is used"
    })
    private EnhancedPropertyDescriptor emlDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("EML", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, EML_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_emlDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_emlDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
