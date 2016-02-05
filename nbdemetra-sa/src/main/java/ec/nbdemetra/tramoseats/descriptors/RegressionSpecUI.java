/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.tstoolkit.modelling.arima.tramo.RegressionSpec;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import ec.tstoolkit.timeseries.regression.InterventionVariable;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
import ec.tstoolkit.timeseries.regression.Ramp;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class RegressionSpecUI extends BaseTramoSpecUI {

    private RegressionSpec inner() {
        return core.getRegression();
    }

    public RegressionSpecUI(TramoSpecification spec, boolean ro) {
        super(spec, ro);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = calendarDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = prespecDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = interventionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = rampsDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = userdefinedDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    @Messages("regressionSpecUI.getDisplayName=Regression")
    public String getDisplayName() {
        return Bundle.regressionSpecUI_getDisplayName();
    }

    public OutlierDefinition[] getPreSpecifiedOutliers() {
        return inner().getOutliers();
    }

    public void setPreSpecifiedOutliers(OutlierDefinition[] value) {
        inner().setOutliers(value);
    }

    public InterventionVariable[] getInterventionVariables() {
        return inner().getInterventionVariables();
    }

    public void setInterventionVariables(InterventionVariable[] value) {
        inner().setInterventionVariables(value);
    }

    public Ramp[] getRamps() {
        return inner().getRamps();
    }

    public void setRamps(Ramp[] value) {
        inner().setRamps(value);
    }

    public TsVariableDescriptor[] getUserDefinedVariables() {
        return inner().getUserDefinedVariables();
    }

    public void setUserDefinedVariables(TsVariableDescriptor[] value) {
        inner().setUserDefinedVariables(value);
    }
    private static final int CALENDAR_ID = 1, PRESPEC_ID = 1, INTERV_ID = 2, RAMPS_ID = 3, USERDEF_ID = 4;

    @Messages({
        "regressionSpecUI.prespecDesc.name=Pre-specified outliers",
        "regressionSpecUI.prespecDesc.desc=Pre-specified outliers"
    })
    private EnhancedPropertyDescriptor prespecDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("PreSpecifiedOutliers", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PRESPEC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regressionSpecUI_prespecDesc_name());
            desc.setShortDescription(Bundle.regressionSpecUI_prespecDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regressionSpecUI.interventionDesc.name=Intervention variables",
        "regressionSpecUI.interventionDesc.desc=Intervention variables"
    })
    private EnhancedPropertyDescriptor interventionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("InterventionVariables", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, INTERV_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regressionSpecUI_interventionDesc_name());
            desc.setShortDescription(Bundle.regressionSpecUI_interventionDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regressionSpecUI.rampsDesc.name=Ramps",
        "regressionSpecUI.rampsDesc.desc=Ramps"
    })
    private EnhancedPropertyDescriptor rampsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ramps", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, RAMPS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regressionSpecUI_rampsDesc_name());
            desc.setShortDescription(Bundle.regressionSpecUI_rampsDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regressionSpecUI.userdefinedDesc.name=User-defined variables",
        "regressionSpecUI.userdefinedDesc.desc=User-defined variables"
    })
    private EnhancedPropertyDescriptor userdefinedDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("UserDefinedVariables", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, USERDEF_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regressionSpecUI_userdefinedDesc_name());
            desc.setShortDescription(Bundle.regressionSpecUI_userdefinedDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regressionSpecUI.calendarDesc.name=Calendar",
        "regressionSpecUI.calendarDesc.desc=Calendar effects"
    })
    private EnhancedPropertyDescriptor calendarDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("calendar", this.getClass(), "getCalendar", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CALENDAR_ID);
            desc.setDisplayName(Bundle.regressionSpecUI_calendarDesc_name());
            desc.setShortDescription(Bundle.regressionSpecUI_calendarDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public CalendarSpecUI getCalendar() {
        return new CalendarSpecUI(core, ro_);
    }
}
