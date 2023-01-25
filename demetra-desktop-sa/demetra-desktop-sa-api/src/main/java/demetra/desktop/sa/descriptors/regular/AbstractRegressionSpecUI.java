/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.descriptors.regular;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.desktop.sa.properties.l2fprod.SaInterventionVariableDescriptor;
import demetra.desktop.sa.properties.l2fprod.SaTsVariableDescriptor;
import demetra.desktop.ui.properties.l2fprod.OutlierDescriptor;
import demetra.desktop.ui.properties.l2fprod.RampDescriptor;
import demetra.desktop.ui.properties.l2fprod.UserInterfaceContext;
import demetra.modelling.TransformationType;
import demetra.modelling.regular.RegressionSpec;
import demetra.sa.ComponentType;
import demetra.sa.SaVariable;
import demetra.timeseries.TsDomain;
import demetra.timeseries.regression.AdditiveOutlier;
import demetra.timeseries.regression.IOutlier;
import demetra.timeseries.regression.InterventionVariable;
import demetra.timeseries.regression.LevelShift;
import demetra.timeseries.regression.PeriodicOutlier;
import demetra.timeseries.regression.Ramp;
import demetra.timeseries.regression.TransitoryChange;
import demetra.timeseries.regression.TsContextVariable;
import demetra.timeseries.regression.Variable;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractRegressionSpecUI implements IPropertyDescriptors {

    private static IOutlier toOutlier(OutlierDescriptor od, int period, double tc) {
        return switch (od.getType()) {
            case AO ->
                new AdditiveOutlier(od.getPosition().atStartOfDay());
            case LS ->
                new LevelShift(od.getPosition().atStartOfDay(), false);
            case TC ->
                new TransitoryChange(od.getPosition().atStartOfDay(), tc);
            case SO ->
                new PeriodicOutlier(od.getPosition().atStartOfDay(), period, false);
            default ->
                null;
        };
    }

    protected abstract RegressionSpec spec();
    
    protected abstract RegularSpecUI root();
    
    
    
    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = meanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = muDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = calendarDesc();
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

//        desc = fixedCoefficientsDesc();
//        if (desc != null) {
//            descs.add(desc);
//        }
        return descs;
    }

    @Override
    @Messages("regular.regressionSpecUI.getDisplayName=Regression")
    public String getDisplayName() {
        return Bundle.regular_regressionSpecUI_getDisplayName();
    }

    public OutlierDescriptor[] getPreSpecifiedOutliers() {
        return spec().getOutliers()
                .stream()
                .map(var -> {
                    IOutlier o = var.getCore();
                    return new OutlierDescriptor(o.getPosition().toLocalDate(), OutlierDescriptor.OutlierType.valueOf(o.getCode()), var.getCoefficient(0), var.getName());
                })
                .sorted((o1, o2) -> o1.getPosition().compareTo(o2.getPosition()))
                .toArray(n -> new OutlierDescriptor[n]);
    }

    public void setPreSpecifiedOutliers(OutlierDescriptor[] value) {

        double tc = root().outlier().getDeltaTC();
        TsDomain domain = UserInterfaceContext.INSTANCE.getDomain();
        int period = domain == null ? 0 : domain.getAnnualFrequency();
        List<Variable<IOutlier>> list = Arrays.stream(value).map(v -> {
            IOutlier o = toOutlier(v, period, tc);
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

    public Parameter[] parameter(Parameter p) {
        if (root().transform().getFunction() == TransformationType.Auto) {
            p = Parameter.undefined();
        }
        return new Parameter[]{p};
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

    public RampDescriptor[] getRamps() {
        return spec().getRamps()
                .stream()
                .map(var -> new RampDescriptor(var))
                .toArray(RampDescriptor[]::new);
    }

    public void setRamps(RampDescriptor[] value) {
        List<Variable<Ramp>> list = Arrays.stream(value).map(v -> Variable.<Ramp>builder()
                .name(v.getName())
                .core(v.getCore())
                .coefficients(parameter(v.getCoefficient()))
                .attribute(SaVariable.REGEFFECT, ComponentType.Trend.name())
                .build())
                .collect(Collectors.toList());
        root().update(spec().toBuilder().clearRamps().ramps(list).build());
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

    public boolean isMean() {
        return spec().getMean() != null;
    }

    public void setMean(boolean m) {
        if (m) {
            root().update(spec().toBuilder().mean(Parameter.undefined()).build());
        } else {
            root().update(spec().toBuilder().mean(null).build());
        }
    }
    
    public Parameter getMu(){
        return spec().getMean();
    }

    public void setMu(Parameter mu){
        root().update(spec().toBuilder().mean(mu).build());
    }

    private static final int MEAN_ID = 1, CALENDAR_ID = 2, PRESPEC_ID = 3, INTERV_ID = 4, RAMPS_ID = 5, USERDEF_ID = 6, FCOEFF_ID = 7, MU_ID = 8;

    @Messages({
        "regular.regressionSpecUI.meanDesc.desc=[imean] Mean correction"
    })
    private EnhancedPropertyDescriptor meanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mean", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MEAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.regular_regressionSpecUI_meanDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.regressionSpecUI.muDesc.desc=Mean coefficient"
    })
    private EnhancedPropertyDescriptor muDesc() {
        if ( ! isMean()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("mu", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MU_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.regular_regressionSpecUI_muDesc_desc());
            edesc.setReadOnly(root().isRo() || root().transform().getFunction() == TransformationType.Auto);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.regressionSpecUI.prespecDesc.name=Pre-specified outliers",
        "regular.regressionSpecUI.prespecDesc.desc=Pre-specified outliers"
    })
    private EnhancedPropertyDescriptor prespecDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("PreSpecifiedOutliers", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PRESPEC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_regressionSpecUI_prespecDesc_name());
            desc.setShortDescription(Bundle.regular_regressionSpecUI_prespecDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.regressionSpecUI.interventionDesc.name=Intervention variables",
        "regular.regressionSpecUI.interventionDesc.desc=Intervention variables"
    })
    private EnhancedPropertyDescriptor interventionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("InterventionVariables", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, INTERV_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_regressionSpecUI_interventionDesc_name());
            desc.setShortDescription(Bundle.regular_regressionSpecUI_interventionDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.regressionSpecUI.rampsDesc.name=Ramps",
        "regular.regressionSpecUI.rampsDesc.desc=Ramps"
    })
    private EnhancedPropertyDescriptor rampsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ramps", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, RAMPS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_regressionSpecUI_rampsDesc_name());
            desc.setShortDescription(Bundle.regular_regressionSpecUI_rampsDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.regressionSpecUI.userdefinedDesc.name=User-defined variables",
        "regular.regressionSpecUI.userdefinedDesc.desc=User-defined variables"
    })
    private EnhancedPropertyDescriptor userdefinedDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("UserDefinedVariables", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, USERDEF_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.regular_regressionSpecUI_userdefinedDesc_name());
            desc.setShortDescription(Bundle.regular_regressionSpecUI_userdefinedDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

//    @Messages({
//        "regular.regressionSpecUI.fixedCoefficientsDesc.name=Fixed regression coefficients",
//        "regular.regressionSpecUI.fixedCoefficientsDesc.desc="
//    })
//    private EnhancedPropertyDescriptor fixedCoefficientsDesc() {
//        try {
//            PropertyDescriptor desc = new PropertyDescriptor("FixedCoefficients", this.getClass());
//            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FCOEFF_ID);
//            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
//            desc.setDisplayName(Bundle.regular_regressionSpecUI_fixedCoefficientsDesc_name());
//            desc.setShortDescription(Bundle.regular_regressionSpecUI_fixedCoefficientsDesc_desc());
//            // Disabled when the transformation is on "auto"
//            edesc.setReadOnly(root().isRo() || core().getTransform().getFunction() == TransformationType.Auto);
//            return edesc;
//        } catch (IntrospectionException ex) {
//            return null;
//        }
//    }
//
    @Messages({
        "regular.regressionSpecUI.calendarDesc.name=Calendar",
        "regular.regressionSpecUI.calendarDesc.desc=Calendar effects"
    })
    private EnhancedPropertyDescriptor calendarDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("calendar", this.getClass(), "getCalendar", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CALENDAR_ID);
            desc.setDisplayName(Bundle.regular_regressionSpecUI_calendarDesc_name());
            desc.setShortDescription(Bundle.regular_regressionSpecUI_calendarDesc_desc());
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public abstract IPropertyDescriptors getCalendar();
}
