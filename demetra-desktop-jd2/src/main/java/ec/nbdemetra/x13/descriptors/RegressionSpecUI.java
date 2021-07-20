/*
 * Copyright 2017 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.nbdemetra.x13.descriptors;

import ec.nbdemetra.ui.properties.l2fprod.Coefficients;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.TsVariableDescriptor;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.modelling.arima.x13.RegressionSpec;
import ec.tstoolkit.timeseries.regression.InterventionVariable;
import ec.tstoolkit.timeseries.regression.OutlierDefinition;
import ec.tstoolkit.timeseries.regression.Ramp;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Kristof Bayens
 */
public class RegressionSpecUI extends BaseRegArimaSpecUI {

    private RegressionSpec inner() {
        return core.getRegression();
    }

    public RegressionSpecUI(RegArimaSpecification spec, boolean ro) {
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

        desc = fixedCoefficientsDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Messages("regressionSpecUI.getDisplayName=Regression")
    @Override
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

    public CalendarSpecUI getCalendar() {
        return new CalendarSpecUI(core, ro_);
    }

    public Coefficients getFixedCoefficients() {
        Coefficients c = new Coefficients(inner().getAllFixedCoefficients());
        c.setAllNames(inner().getRegressionVariableNames(TsFrequency.Undefined));
        return c;
    }

    public void setFixedCoefficients(Coefficients coeffs) {
        inner().setAllFixedCoefficients(coeffs.getFixedCoefficients());
    }
    
    private static final int CALENDAR_ID = 1, PRESPEC_ID = 1, INTERV_ID = 2, RAMPS_ID = 3, USERDEF_ID = 4, FCOEFF_ID = 5;

    @Messages({
        "regressionSpecUI.prespecDesc.name=Pre-specified outliers",
        "regressionSpecUI.prespecDesc.desc="
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
        "regressionSpecUI.interventionDesc.desc="
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
        "regressionSpecUI.rampsDesc.desc="
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
        "regressionSpecUI.userdefinedDesc.desc="
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
        "regressionSpecUI.fixedCoefficientsDesc.name=Fixed regression coefficients",
        "regressionSpecUI.fixedCoefficientsDesc.desc="
    })
    private EnhancedPropertyDescriptor fixedCoefficientsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("FixedCoefficients", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FCOEFF_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regressionSpecUI_fixedCoefficientsDesc_name());
            desc.setShortDescription(Bundle.regressionSpecUI_fixedCoefficientsDesc_desc());
            edesc.setReadOnly(ro_ || core.getTransform().getFunction() == DefaultTransformationType.Auto);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regressionSpecUI.calendarDesc.name=Calendar",
        "regressionSpecUI.calendarDesc.desc="
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
}
