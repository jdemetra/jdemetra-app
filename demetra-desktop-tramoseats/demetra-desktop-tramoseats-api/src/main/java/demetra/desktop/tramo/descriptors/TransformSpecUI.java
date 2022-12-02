/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.DateSelectorUI;
import demetra.modelling.TransformationType;
import demetra.timeseries.TimeSelector;
import demetra.timeseries.calendars.LengthOfPeriodType;
import demetra.tramo.TradingDaysSpec;
import demetra.tramo.TransformSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class TransformSpecUI extends BaseTramoSpecUI {

    @Override
    public String toString() {
        return "";
    }

    private TransformSpec inner() {
        return core().getTransform();
    }

    public TransformSpecUI(TramoSpecRoot root) {
        super(root);
    }

    @Override
    public boolean isRo() {
        return super.isRo()
                || core().getRegression().hasFixedCoefficients();
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = fnDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = fctDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = adjustDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Transformation";
    }

    public TransformationType getFunction() {
        return inner().getFunction();

    }

    public void setFunction(TransformationType value) {
        TradingDaysSpec td = core().getRegression().getCalendar().getTradingDays();
        boolean adjust = td.isAutoAdjust();
        LengthOfPeriodType lpt = inner().getAdjust(), lpreg = td.getLengthOfPeriodType();
        switch (value) {
            case Auto -> {
                if (lpt != LengthOfPeriodType.None) {
                    lpreg = lpt;
                }
                lpt = LengthOfPeriodType.None;
                adjust = true;
            }
            case Log ->
                adjust = false;
            case None -> {
                adjust = false;
                if (lpt != LengthOfPeriodType.None) {
                    lpreg = lpt;
                }
                lpt = LengthOfPeriodType.None;
            }
        }
        update(inner().toBuilder()
                .function(value)
                .adjust(lpt)
                .build());
        // Fn == None -> No autoadjust + Lp set if auto-adjust
        if (td.isDefaultTradingDays() || td.isHolidays()) {
            if (td.isAutomatic()) {
                if (td.isDefaultTradingDays()) {
                    update(TradingDaysSpec.automatic(lpreg, td.getAutomaticMethod(), td.getProbabilityForFTest(), adjust));         
                } else {
                    update(TradingDaysSpec.automaticHolidays(td.getHolidays(), lpreg, td.getAutomaticMethod(), td.getProbabilityForFTest(), adjust));
                }

            } else {
                // we need to update the trading days options
                if (td.isDefaultTradingDays()) {
                    update(TradingDaysSpec.td(td.getTradingDaysType(), lpreg,
                            td.getRegressionTestType(), adjust));
                } else {
                    update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(), lpreg,
                            td.getRegressionTestType(), adjust));
                }
            }
        }

    }

    public DateSelectorUI getSpan() {
        return new DateSelectorUI(inner().getSpan(), isRo(), span -> updateSpan(span));
    }

    public void updateSpan(TimeSelector span) {
        update(inner().toBuilder().span(span).build());
    }

    public double getFct() {
        return inner().getFct();
    }

    public void setFct(double value) {
        update(inner().toBuilder().fct(value).build());
    }

    public LengthOfPeriodType getAdjust() {
        return inner().getAdjust();
    }

    public void setAdjust(LengthOfPeriodType value) {
        update(inner().toBuilder().adjust(value).build());
        TradingDaysSpec td = core().getRegression().getCalendar().getTradingDays();
        if ((td.isDefaultTradingDays() || td.isHolidays())
                && value != LengthOfPeriodType.None && td.getLengthOfPeriodType() != LengthOfPeriodType.None) {
            // we need to update the trading days options
            if (td.isDefaultTradingDays()) {
                update(TradingDaysSpec.td(td.getTradingDaysType(), LengthOfPeriodType.None,
                        td.getRegressionTestType(), false));
            } else {
                update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(), LengthOfPeriodType.None,
                        td.getRegressionTestType(), false));
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    private static final int SPAN_ID = 0, FN_ID = 1, FCT_ID = 2, ADJUST_ID = 3;

    @Messages({"transformSpecUI.fnDesc.name=Function",
        "transformSpecUI.fnDesc.desc=[lam]. None=no transformation of data; Log=takes logs of data; Auto:the program tests for the log-level specification."
    })
    private EnhancedPropertyDescriptor fnDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("function", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.transformSpecUI_fnDesc_name());
            desc.setShortDescription(Bundle.transformSpecUI_fnDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"transformSpecUI.fctDesc.name=Fct",
        "transformSpecUI.fctDesc.desc=[fct] Controls the bias in the log/level pretest: Fct > 1 favors levels, Fct < 1 favors logs."
    })
    private EnhancedPropertyDescriptor fctDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("fct", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FCT_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || getFunction() != TransformationType.Auto);
            desc.setDisplayName(Bundle.transformSpecUI_fctDesc_name());
            desc.setShortDescription(Bundle.transformSpecUI_fctDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "transformSpecUI.adjustDesc.name=Adjust",
        "transformSpecUI.adjustDesc.desc=[adjust] Preadjustment of the series for length of period or leap year effects. The series is divided by the specified effect. Not available with the \"auto\" mode"
    })
    private EnhancedPropertyDescriptor adjustDesc() {
        if (inner().getFunction() != TransformationType.Log) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("adjust", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ADJUST_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || getFunction() != TransformationType.Log);
            desc.setShortDescription(Bundle.transformSpecUI_adjustDesc_desc());
            desc.setDisplayName(Bundle.transformSpecUI_adjustDesc_name());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
