/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.descriptors;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.ui.properties.l2fprod.OutlierDefinition;
import demetra.desktop.ui.properties.l2fprod.UserInterfaceContext;
import demetra.modelling.TransformationType;
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
import demetra.tramo.RegressionSpec;
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
public class RegressionSpecUI extends BaseTramoSpecUI {

    private static IOutlier toOutlier(OutlierDefinition od, int period, double tc) {
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

    private RegressionSpec inner() {
        return core().getRegression();
    }

    public RegressionSpecUI(TramoSpecRoot root) {
        super(root);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = meanDesc();
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
    @Messages("regressionSpecUI.getDisplayName=Regression")
    public String getDisplayName() {
        return Bundle.regressionSpecUI_getDisplayName();
    }

    public OutlierDefinition[] getPreSpecifiedOutliers() {
        return inner().getOutliers()
                .stream()
                .map(var -> {
                    IOutlier o = var.getCore();
                    return new OutlierDefinition(o.getPosition().toLocalDate(), OutlierDefinition.OutlierType.valueOf(o.getCode()), var.getCoefficient(0));
                })
                .sorted((o1, o2)->o1.getPosition().compareTo(o2.getPosition()))
                .toArray(n -> new OutlierDefinition[n]);
    }

    public void setPreSpecifiedOutliers(OutlierDefinition[] value) {

        double tc = core().getOutliers().getDeltaTC();
        TsDomain domain = UserInterfaceContext.INSTANCE.getDomain();
        int period = domain == null ? 0 : domain.getAnnualFrequency();
        List<Variable<IOutlier>> list = Arrays.stream(value).map(v -> {
            Parameter parameter = v.getParameter();
            IOutlier o = toOutlier(v, period, tc);
            return Variable.<IOutlier>builder()
                    .name(o.description(null))
                    .core(o)
                    .coefficients(new Parameter[]{parameter.isFixed() ? parameter : Parameter.undefined()})
                    .build();
        }).collect(Collectors.toList());
        update(inner().toBuilder().clearOutliers().outliers(list).build());
    }

    public InterventionVariable[] getInterventionVariables() {
        return inner().getInterventionVariables()
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
        update(inner().toBuilder().clearInterventionVariables().interventionVariables(list).build());
    }

    public Ramp[] getRamps() {
        return inner().getRamps()
                .stream()
                .map(var -> var.getCore())
                .toArray(n -> new Ramp[n]);
    }

    public void setRamps(Ramp[] value) {
        List<Variable<Ramp>> list = Arrays.stream(value).map(v -> Variable.<Ramp>builder()
                .name(v.description(null))
                .core(v)
                .build())
                .collect(Collectors.toList());
        update(inner().toBuilder().clearRamps().ramps(list).build());
    }

    public TsContextVariable[] getUserDefinedVariables() {
        return inner().getUserDefinedVariables()
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
        update(inner().toBuilder().clearUserDefinedVariables().userDefinedVariables(list).build());
    }

    public boolean isMean() {
        return inner().getMean() != null;
    }

    public void setMean(boolean m) {
        if (m) {
            update(inner().toBuilder().mean(Parameter.undefined()).build());
        } else {
            update(inner().toBuilder().mean(null).build());
        }
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
    private static final int MEAN_ID = 1, CALENDAR_ID = 2, PRESPEC_ID = 3, INTERV_ID = 4, RAMPS_ID = 5, USERDEF_ID = 6, FCOEFF_ID = 7;

    @Messages({
        "regressionSpecUI.meanDesc.desc=[imean] Mean correction"
    })
    private EnhancedPropertyDescriptor meanDesc() {
        if (core().isUsingAutoModel()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mean", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MEAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setShortDescription(Bundle.regressionSpecUI_meanDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
            // Disabled when the transformation is on "auto"
            edesc.setReadOnly(isRo() || core().getTransform().getFunction() == TransformationType.Auto);
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
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public CalendarSpecUI getCalendar() {
        return new CalendarSpecUI(root);
    }
}
