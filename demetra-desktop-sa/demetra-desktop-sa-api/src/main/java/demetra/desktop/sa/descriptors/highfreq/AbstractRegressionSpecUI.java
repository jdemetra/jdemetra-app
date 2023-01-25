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

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.modelling.highfreq.RegressionSpec;
import demetra.timeseries.regression.InterventionVariable;
import demetra.timeseries.regression.TsContextVariable;
import demetra.timeseries.regression.Variable;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public abstract class AbstractRegressionSpecUI implements IPropertyDescriptors {

//    private static IOutlier toOutlier(OutlierDescriptor od, int period, double tc) {
//        switch (od.getType()) {
//            case AO:
//                return new AdditiveOutlier(od.getPosition().atStartOfDay());
//            case LS:
//                return new LevelShift(od.getPosition().atStartOfDay(), false);
//            case TC:
//                return new TransitoryChange(od.getPosition().atStartOfDay(), tc);
//            case SO:
//                return new PeriodicOutlier(od.getPosition().atStartOfDay(), period, false);
//            default:
//                return null;
//        }
//    }
    protected abstract HighFreqSpecUI root();

    protected abstract RegressionSpec spec();

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = calendarDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = interventionDesc();
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
    @NbBundle.Messages("regressionSpecUI.getDisplayName=Regression")
    public String getDisplayName() {
        return Bundle.regressionSpecUI_getDisplayName();
    }

    public InterventionVariable[] getInterventionVariables() {
        return spec().getInterventionVariables()
                .stream()
                .map(var -> var.getCore())
                .toArray(n -> new InterventionVariable[n]);
    }

    public void setInterventionVariables(InterventionVariable[] value) {
        List<Variable<InterventionVariable>> list = Arrays.stream(value).map(v -> Variable.<InterventionVariable>builder()
                .name(v.description(null))
                .core(v)
                .build())
                .collect(Collectors.toList());
        root().update(spec().toBuilder().clearInterventionVariables().interventionVariables(list).build());
    }

    public TsContextVariable[] getUserDefinedVariables() {
        return spec().getUserDefinedVariables()
                .stream()
                .map(var -> var.getCore())
                .toArray(n -> new TsContextVariable[n]);
    }

    public void setUserDefinedVariables(TsContextVariable[] value) {
        List<Variable<TsContextVariable>> list = Arrays.stream(value).map(v -> Variable.<TsContextVariable>builder()
                .name(v.description(null))
                .core(v)
                .build())
                .collect(Collectors.toList());
        root().update(spec().toBuilder().clearUserDefinedVariables().userDefinedVariables(list).build());
    }

//    public Coefficients getFixedCoefficients() {
//        Coefficients c = new Coefficients(inner().getAllFixedCoefficients());
//        c.setAllNames(inner().getRegressionVariableNames(TsFrequency.Undefined));
//        return c;
//    }
//
//    public void setFixedCoefficients(Coefficients coeffs) {
//        inner().setAllFixedCoefficients(coeffs.getFixedCoefficients());
//    }
    private static final int CALENDAR_ID = 1, EASTER_ID = 2, PRESPEC_ID = 3, INTERV_ID = 4, RAMPS_ID = 5, USERDEF_ID = 6;

    @NbBundle.Messages({
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
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
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
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "regressionSpecUI.calendarDesc.name=Holidays",
        "regressionSpecUI.calendarDesc.desc=Holidays"
    })
    private EnhancedPropertyDescriptor calendarDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("calendar", this.getClass(), "getCalendar", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CALENDAR_ID);
            desc.setDisplayName(Bundle.regressionSpecUI_calendarDesc_name());
            desc.setShortDescription(Bundle.regressionSpecUI_calendarDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "regressionSpecUI.easterDesc.name=Easter",
        "regressionSpecUI.easterDesc.desc=Easter"
    })
    private EnhancedPropertyDescriptor easterDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("calendar", this.getClass(), "getEaster", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, EASTER_ID);
            desc.setDisplayName(Bundle.regressionSpecUI_easterDesc_name());
            desc.setShortDescription(Bundle.regressionSpecUI_easterDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public abstract IPropertyDescriptors getCalendar();

    public abstract IPropertyDescriptors getEaster();
}
