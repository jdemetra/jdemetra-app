/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.modelling.TransformationType;
import demetra.regarima.TradingDaysSpec;
import demetra.regarima.TransformSpec;
import demetra.timeseries.calendars.LengthOfPeriodType;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class TransformSpecUI extends BaseRegArimaSpecUI {

//    final Validator validator;
    @Override
    public String toString() {
        return "";
    }

    private TransformSpec inner() {
        return core().getTransform();
    }

    public TransformSpecUI(RegArimaSpecRoot root) {
        super(root);
//        validator=null;
    }

//    TransformSpecUI(RegArimaSpecRoot root, Validator validator) {
//        super(root);
//        this.validator=validator;
//    }
    @Override
    public boolean isRo() {
        return super.isRo()
                || core().getRegression().hasFixedCoefficients();
    }

    public TransformationType getFunction() {
        return inner().getFunction();

    }

    public void setFunction(TransformationType value) {
        TradingDaysSpec td = core().getRegression().getTradingDays();
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

    public boolean isOutliers() {
        return inner().isOutliersCorrection();
    }

    public void setOutliers(boolean outliers) {
        update(inner().toBuilder().outliersCorrection(outliers).build());
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = fnDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = aicDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = outliersDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = adjustDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Messages("transformSpecUI.getDisplayName=Transformation")
    @Override
    public String getDisplayName() {
        return Bundle.transformSpecUI_getDisplayName();
    }

    public double getAic() {
        return inner().getAicDiff();
    }

    public void setAic(double value) {
        update(inner().toBuilder().aicDiff(value).build());
    }

    public LengthOfPeriodType getAdjust() {
        return inner().getAdjust();
    }

    public void setAdjust(LengthOfPeriodType value) {
        update(inner().toBuilder().adjust(value).build());
        TradingDaysSpec td = core().getRegression().getTradingDays();
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
    private static final int FN_ID = 1, AIC_ID = 2, ADJUST_ID = 3, OUTLIERS_ID = 4;

    @Messages({
        "transformSpecUI.fnDesc.name=function",
        "transformSpecUI.fnDesc.desc=[lam] None=no transformation of data; Log=takes logs of data; Auto:the program tests for the log-level specification."
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

    @Messages({
        "transformSpecUI.aicDesc.name=AIC difference",
        "transformSpecUI.aicDesc.desc=[aicdiff] Defines the difference in AICC needed to accept no transformation when the automatic transformation selection option is invoked."
    })
    private EnhancedPropertyDescriptor aicDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("aic", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AIC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || getFunction() != TransformationType.Auto);
            desc.setShortDescription(Bundle.transformSpecUI_aicDesc_desc());
            desc.setDisplayName(Bundle.transformSpecUI_aicDesc_name());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"transformSpecUI.outliersDesc.name=Outliers corr.",
        "transformSpecUI.outliersDesc.desc=Pre-correction for large outliers (AO and LS only)"
    })
    private EnhancedPropertyDescriptor outliersDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("outliers", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OUTLIERS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || getFunction() != TransformationType.Auto);
            desc.setDisplayName(Bundle.transformSpecUI_outliersDesc_name());
            desc.setShortDescription(Bundle.transformSpecUI_outliersDesc_desc());
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
