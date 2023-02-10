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

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.desktop.sa.properties.l2fprod.SaInterventionVariableDescriptor;
import demetra.desktop.sa.properties.l2fprod.SaTsVariableDescriptor;
import demetra.desktop.ui.properties.l2fprod.HighFreqOutlierDescriptor;
import demetra.modelling.TransformationType;
import demetra.modelling.highfreq.RegressionSpec;
import demetra.sa.SaVariable;
import demetra.timeseries.regression.AdditiveOutlier;
import demetra.timeseries.regression.IOutlier;
import demetra.timeseries.regression.InterventionVariable;
import demetra.timeseries.regression.LevelShift;
import demetra.timeseries.regression.SwitchOutlier;
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

    private static IOutlier toOutlier(HighFreqOutlierDescriptor od) {
        return switch (od.getType()) {
            case AO -> new AdditiveOutlier(od.getPosition().atStartOfDay());
            case LS -> new LevelShift(od.getPosition().atStartOfDay(), false);
            case WO -> new SwitchOutlier(od.getPosition().atStartOfDay());
            default -> null;
        };
    }

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
        desc = prespecDesc();
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
    @NbBundle.Messages("highfreq.regressionSpecUI.getDisplayName=Regression")
    public String getDisplayName() {
        return Bundle.highfreq_regressionSpecUI_getDisplayName();
    }

        public Parameter[] parameter(Parameter p) {
        if (root().transform().getFunction() == TransformationType.Auto) {
            p = Parameter.undefined();
        }
        return new Parameter[]{p};
    }


    public HighFreqOutlierDescriptor[] getPreSpecifiedOutliers() {
        return spec().getOutliers()
                .stream()
                .map(var -> {
                    IOutlier o = var.getCore();
                    return new HighFreqOutlierDescriptor(o.getPosition().toLocalDate(), HighFreqOutlierDescriptor.OutlierType.valueOf(o.getCode()), var.getCoefficient(0), var.getName());
                })
                .sorted((o1, o2) -> o1.getPosition().compareTo(o2.getPosition()))
                .toArray(n -> new HighFreqOutlierDescriptor[n]);
    }

    public void setPreSpecifiedOutliers(HighFreqOutlierDescriptor[] value) {

        List<Variable<IOutlier>> list = Arrays.stream(value).map(v -> {
            IOutlier o = toOutlier(v);
            return Variable.<IOutlier>builder()
                    .name(v.getName())
                    .core(o)
                    .coefficients(parameter(v.getCoefficient()))
                    .build();
        }).collect(Collectors.toList());
        root().update(spec().toBuilder().clearOutliers().outliers(list).build());
    }

    public SaInterventionVariableDescriptor[] getInterventionVariables() {
        return spec().getInterventionVariables()
                .stream()
                .map(var -> new SaInterventionVariableDescriptor(var))
                .toArray(SaInterventionVariableDescriptor[]::new);
    }

    public void setInterventionVariables(SaInterventionVariableDescriptor[] value) {
        List<Variable<InterventionVariable>> list = Arrays.stream(value).map(v -> Variable.<InterventionVariable>builder()
                .name(v.getName())
                .core(v.getCore())
                .coefficients(parameter(v.getCoefficient()))
                .attribute(SaVariable.REGEFFECT, v.getRegressionEffect().name())
                .build())
                .collect(Collectors.toList());
        root().update(spec().toBuilder().clearInterventionVariables().interventionVariables(list).build());
    }

     public SaTsVariableDescriptor[] getUserDefinedVariables() {
        return spec().getUserDefinedVariables()
                .stream()
                .map(var -> new SaTsVariableDescriptor(var))
                .toArray(SaTsVariableDescriptor[]::new);
    }

    public void setUserDefinedVariables(SaTsVariableDescriptor[] value) {
        List<Variable<TsContextVariable>> list = Arrays.stream(value).map(v -> Variable.<TsContextVariable>builder()
                .name(v.getName())
                .core(v.getCore())
                .coefficients(parameter(v.getCoefficient()))
                .attribute(SaVariable.REGEFFECT, v.getRegressionEffect().name())
                .build())
                .collect(Collectors.toList());
        root().update(spec().toBuilder().clearUserDefinedVariables().userDefinedVariables(list).build());
    }


    private static final int CALENDAR_ID = 1, EASTER_ID = 2, PRESPEC_ID = 3, INTERV_ID = 4, RAMPS_ID = 5, USERDEF_ID = 6;

    @NbBundle.Messages({
        "highfreq.regressionSpecUI.interventionDesc.name=Intervention variables",
        "highfreq.regressionSpecUI.interventionDesc.desc=Intervention variables"
    })
    private EnhancedPropertyDescriptor interventionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("InterventionVariables", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, INTERV_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.highfreq_regressionSpecUI_interventionDesc_name());
            desc.setShortDescription(Bundle.highfreq_regressionSpecUI_interventionDesc_desc());
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

    @NbBundle.Messages({
        "highfreq.regressionSpecUI.prespecDesc.name=Pre-specified outliers",
        "highfreq.regressionSpecUI.prespecDesc.desc=Pre-specified outliers"
    })
    private EnhancedPropertyDescriptor prespecDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("PreSpecifiedOutliers", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PRESPEC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.highfreq_regressionSpecUI_prespecDesc_name());
            desc.setShortDescription(Bundle.highfreq_regressionSpecUI_prespecDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public abstract IPropertyDescriptors getCalendar();

    public abstract IPropertyDescriptors getEaster();
}
