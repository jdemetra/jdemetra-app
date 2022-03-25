/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.ui.properties.l2fprod.OutlierDefinition;
import demetra.highfreq.RegressionSpec;
import demetra.timeseries.regression.AdditiveOutlier;
import demetra.timeseries.regression.IOutlier;
import demetra.timeseries.regression.InterventionVariable;
import demetra.timeseries.regression.LevelShift;
import demetra.timeseries.regression.PeriodicOutlier;
import demetra.timeseries.regression.TransitoryChange;
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
public class RegressionSpecUI extends BaseFractionalAirlineSpecUI {

    private static IOutlier toOutlier(OutlierDefinition od, int period, double tc) {
        switch (od.getType()) {
            case AO:
                return new AdditiveOutlier(od.getPosition().atStartOfDay());
            case LS:
                return new LevelShift(od.getPosition().atStartOfDay(), false);
            case TC:
                return new TransitoryChange(od.getPosition().atStartOfDay(), tc);
            case SO:
                return new PeriodicOutlier(od.getPosition().atStartOfDay(), period, false);
            default:
                return null;
        }
    }

    private RegressionSpec inner() {
        return core().getRegression();
    }

    public RegressionSpecUI(FractionalAirlineSpecRoot root) {
        super(root);
    }

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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public CalendarSpecUI getCalendar() {
        return new CalendarSpecUI(root);
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
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public EasterSpecUI getEaster() {
        return new EasterSpecUI(root);
    }
}
