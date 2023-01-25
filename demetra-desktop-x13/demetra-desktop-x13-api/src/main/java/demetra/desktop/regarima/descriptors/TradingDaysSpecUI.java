/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.descriptors;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.ui.properties.l2fprod.Holidays;
import demetra.desktop.ui.properties.l2fprod.UserVariables;
import demetra.desktop.sa.descriptors.regular.TradingDaysSpecType;
import demetra.desktop.ui.properties.l2fprod.NamedParameters;
import demetra.modelling.TransformationType;
import demetra.regarima.RegressionTestSpec;
import demetra.regarima.TradingDaysSpec;
import demetra.timeseries.calendars.CalendarManager;
import demetra.timeseries.calendars.LengthOfPeriodType;
import demetra.timeseries.calendars.TradingDaysType;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class TradingDaysSpecUI extends BaseRegArimaSpecUI {

    @Override
    public String toString() {
        return inner().isUsed() ? "in use" : "";
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = mautoDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = pval1Desc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = pval2Desc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = optionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = holidaysDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = userDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = tdDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lpDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = autoDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = stdDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = testDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = coeffDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Messages("tradingDaysSpecUI.getDisplayName=Trading days")
    @Override
    public String getDisplayName() {
        return Bundle.tradingDaysSpecUI_getDisplayName();
    }

    private TradingDaysSpec inner() {
        TradingDaysSpec spec = core().getRegression().getTradingDays();

        return spec;
    }

    TradingDaysSpecUI(RegArimaSpecRoot root) {
        super(root);
    }

    @Override
    public boolean isRo() {
        return super.isRo();
    }

    public TradingDaysSpecType getOption() {
        TradingDaysSpec spec = inner();
        if (spec.isUsed()) {
            if (spec.isStockTradingDays()) {
                return TradingDaysSpecType.Stock;
            } else if (spec.isHolidays()) {
                return TradingDaysSpecType.Holidays;
            } else if (spec.isUserDefined()) {
                return TradingDaysSpecType.UserDefined;
            } else if (spec.isUsed()) {
                return TradingDaysSpecType.Default;
            }
        }
        return TradingDaysSpecType.None;
    }

    public void setOption(TradingDaysSpecType value) {
        if (value == getOption()) {
            return;
        }
        LengthOfPeriodType adjust = core().getTransform().getAdjust();
        TransformationType function = core().getTransform().getFunction();
        boolean auto = function == TransformationType.Auto;
        TradingDaysSpec spec = inner();
        boolean automatic = spec.isAutomatic();
        LengthOfPeriodType lp = adjust == LengthOfPeriodType.None ? LengthOfPeriodType.LeapYear : LengthOfPeriodType.None;
        switch (value) {
            case None:
                update(TradingDaysSpec.none());
                break;
            case Default:
                update(automatic ? TradingDaysSpec.automatic(lp, spec.getAutomaticMethod(), spec.getAutoPvalue1(), spec.getAutoPvalue2(), auto)
                        : TradingDaysSpec.td(TradingDaysType.TD7, lp,
                                RegressionTestSpec.Remove,
                                auto));
                break;
            case Holidays:
                update(automatic ? TradingDaysSpec.automaticHolidays(CalendarManager.DEF, lp, spec.getAutomaticMethod(), spec.getAutoPvalue1(), spec.getAutoPvalue2(), auto)
                        : TradingDaysSpec.holidays(CalendarManager.DEF,
                                TradingDaysType.TD7, lp,
                                RegressionTestSpec.Remove,
                                auto)
                );
                break;
            case Stock:
                update(TradingDaysSpec.stockTradingDays(31, RegressionTestSpec.Remove));
                break;
            case UserDefined:
                update(TradingDaysSpec.userDefined(new String[]{}, RegressionTestSpec.Remove));
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public NamedParameters getCoefficients() {
        TradingDaysSpec inner = inner();
        NamedParameters np = new NamedParameters();
        if (inner.getLengthOfPeriodType() != LengthOfPeriodType.None) {
            np.add("lp", inner.getLpCoefficient());
        }
        if (inner.isDefaultTradingDays() || inner.isHolidays()) {
            TradingDaysType type = inner.getTradingDaysType();
            if (type != TradingDaysType.NONE) {
                String[] names = type.contrastNames();
                Parameter[] ptd = inner.getTdCoefficients();
                np.addAll(names, ptd);
            }
        } else if (inner.isStockTradingDays()) {
            String[] names = TradingDaysType.TD7.contrastNames();
            Parameter[] ptd = inner.getTdCoefficients();
            np.addAll(names, ptd);
        } else if (inner.isUserDefined()) {
            np.addAll(inner.getUserVariables(), inner.getTdCoefficients());
        }
        return np;
    }

    public void setCoefficients(NamedParameters p) {
        Parameter[] parameters = p.parameters();
        Parameter[] td;
        Parameter lp;
        if (inner().getLengthOfPeriodType() != LengthOfPeriodType.None) {
            lp = parameters[0];
            if (parameters.length > 1) {
                td = Arrays.copyOfRange(parameters, 1, parameters.length);
            } else {
                td = null;
            }
        } else {
            lp = null;
            td = parameters;
        }
        update(inner().withCoefficients(td, lp));
    }

    public boolean hasFixedCoefficients() {
        return inner().hasFixedCoefficients();
    }

    public RegressionTestSpec getRegressionTest() {
        return inner().getRegressionTestType();
    }

    public void setRegressionTest(RegressionTestSpec value) {
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getRegressionTestType())) {
            return;
        }
        switch (getOption()) {
            case Default:
                update(TradingDaysSpec.td(td.getTradingDaysType(), td.getLengthOfPeriodType(),
                        value, td.isAutoAdjust()));
                break;
            case Holidays:
                update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(),
                        td.getLengthOfPeriodType(), value, td.isAutoAdjust()));
                break;
            case Stock:
                update(TradingDaysSpec.stockTradingDays(td.getStockTradingDays(), value));
                break;
            case UserDefined:
                update(TradingDaysSpec.userDefined(td.getUserVariables(), value));
                break;
        }
    }

    public int getW() {
        return inner().getStockTradingDays();
    }

    public void setW(int w) {
        TradingDaysSpec td = inner();
        if (w == td.getStockTradingDays()) {
            return;
        }
        update(TradingDaysSpec.stockTradingDays(w, td.getRegressionTestType()));

    }

    public TradingDaysType getTradingDays() {
        return inner().getTradingDaysType();
    }

    public void setTradingDays(TradingDaysType value) {
        if (value.equals(TradingDaysType.NONE)) {
            setOption(TradingDaysSpecType.None);
            return;
        }
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getTradingDaysType())) {
            return;
        }
        switch (getOption()) {
            case Default:
                update(TradingDaysSpec.td(value, td.getLengthOfPeriodType(),
                        td.getRegressionTestType(), td.isAutoAdjust()));
                break;
            case Holidays:
                update(TradingDaysSpec.holidays(td.getHolidays(), value,
                        td.getLengthOfPeriodType(), td.getRegressionTestType(),
                        td.isAutoAdjust()));
                break;
        }
    }

    public boolean isAutoAdjust() {
        return inner().isAutoAdjust();
    }

    public void setAutoAdjust(boolean value) {
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        switch (getOption()) {
            case Default:
                if (td.isAutomatic()) {
                    update(TradingDaysSpec.automatic(td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getAutoPvalue1(), td.getAutoPvalue2(), value));
                } else {

                    update(TradingDaysSpec.td(td.getTradingDaysType(), td.getLengthOfPeriodType(),
                            td.getRegressionTestType(), value));
                }
                break;
            case Holidays:
                if (td.isAutomatic()) {
                    update(TradingDaysSpec.automaticHolidays(td.getHolidays(), td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getAutoPvalue1(), td.getAutoPvalue2(), value));
                } else {
                    update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(),
                            td.getLengthOfPeriodType(), td.getRegressionTestType(),
                            value));
                }
                break;
        }
    }

    public LengthOfPeriodType getLeapYear() {
        return inner().getLengthOfPeriodType();
    }

    public void setLeapYear(LengthOfPeriodType value) {
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getLengthOfPeriodType())) {
            return;
        }
        switch (getOption()) {
            case Default:
                if (td.isAutomatic()) {
                    update(TradingDaysSpec.automatic(value, td.getAutomaticMethod(), td.getAutoPvalue1(), td.getAutoPvalue2(), td.isAutoAdjust()));
                } else {
                    update(TradingDaysSpec.td(td.getTradingDaysType(), value, td.getRegressionTestType(), td.isAutoAdjust()));
                }
                break;
            case Holidays:
                if (td.isAutomatic()) {
                    update(TradingDaysSpec.automaticHolidays(td.getHolidays(), value, td.getAutomaticMethod(), td.getAutoPvalue1(), td.getAutoPvalue2(), td.isAutoAdjust()));
                } else {
                    update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(), value, td.getRegressionTestType(), td.isAutoAdjust()));
                }
                break;
        }
    }

    public Holidays getHolidays() {
        return new Holidays(inner().getHolidays());
    }

    public void setHolidays(Holidays holidays) {
        TradingDaysSpec td = inner();
        boolean automatic = td.isAutomatic();
        update(automatic ? TradingDaysSpec.automaticHolidays(holidays.getName(), td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getAutoPvalue1(), td.getAutoPvalue2(), td.isAutoAdjust())
                : TradingDaysSpec.holidays(holidays.getName(), td.getTradingDaysType(), td.getLengthOfPeriodType(), td.getRegressionTestType(), td.isAutoAdjust()));
    }

    public UserVariables getUserVariables() {
        return new UserVariables(inner().getUserVariables());
    }

    public void setUserVariables(UserVariables vars) {
        TradingDaysSpec td = inner();
        update(TradingDaysSpec.userDefined(vars.getNames(), td.getRegressionTestType()));
    }

    public TradingDaysSpec.AutoMethod getAutomatic() {
        return inner().getAutomaticMethod();
    }

    public void setAutomatic(TradingDaysSpec.AutoMethod value) {
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getAutomaticMethod())) {
            return;
        }
        if (value.equals(TradingDaysSpec.AutoMethod.UNUSED)) {
            switch (getOption()) {
                case Default:
                    update(TradingDaysSpec.td(TradingDaysType.TD2, td.getLengthOfPeriodType(), RegressionTestSpec.Remove, td.isAutoAdjust()));
                    break;
                case Holidays:
                    update(TradingDaysSpec.holidays(td.getHolidays(), TradingDaysType.TD2, td.getLengthOfPeriodType(), RegressionTestSpec.Remove, td.isAutoAdjust()));
                    break;
            }
        } else {
            double p1 = td.getAutoPvalue1(), p2 = td.getAutoPvalue2();
            switch (getOption()) {
                case Default:
                    update(TradingDaysSpec.automatic(td.getLengthOfPeriodType(), value,
                            p1 != 0 ? p1 : TradingDaysSpec.DEF_AUTO_PVALUE1,
                            p2 != 0 ? p2 : TradingDaysSpec.DEF_AUTO_PVALUE2,
                            td.isAutoAdjust()));
                    break;
                case Holidays:
                    update(TradingDaysSpec.automaticHolidays(td.getHolidays(), td.getLengthOfPeriodType(), value,
                            p1 != 0 ? p1 : TradingDaysSpec.DEF_AUTO_PVALUE1,
                            p2 != 0 ? p2 : TradingDaysSpec.DEF_AUTO_PVALUE2,
                            td.isAutoAdjust()));
                    break;
            }
        }
    }

    public double getPvalue1() {
        return inner().getAutoPvalue1();
    }

    public void setPvalue1(double value) {
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value == td.getAutoPvalue1()) {
            return;
        }
        switch (getOption()) {
            case Default:
                update(TradingDaysSpec.automatic(td.getLengthOfPeriodType(), td.getAutomaticMethod(), value, td.getAutoPvalue2(), td.isAutoAdjust()));
                break;
            case Holidays:
                update(TradingDaysSpec.automaticHolidays(td.getHolidays(), td.getLengthOfPeriodType(), td.getAutomaticMethod(), value, td.getAutoPvalue2(), td.isAutoAdjust()));
                break;
        }
    }

    public double getPvalue2() {
        return inner().getAutoPvalue2();
    }

    public void setPvalue2(double value) {
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value == td.getAutoPvalue2()) {
            return;
        }
        switch (getOption()) {
            case Default:
                update(TradingDaysSpec.automatic(td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getAutoPvalue1(), value, td.isAutoAdjust()));
                break;
            case Holidays:
                update(TradingDaysSpec.automaticHolidays(td.getHolidays(), td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getAutoPvalue1(), value, td.isAutoAdjust()));
                break;
        }
    }

/////////////////////////////////////////////////////////
    private static final int OPTION_ID = 1, STOCK_ID = 2, HOLIDAYS_ID = 3, USER_ID = 4, TD_ID = 5, LP_ID = 6, AUTO_ID = 7, MAUTO_ID = 8, MAUTO_PVAL1_ID = 9, MAUTO_PVAL2_ID = 10, TEST_ID = 12;

    @Messages({"tradingDaysSpecUI.optionDesc.name=option",
        "tradingDaysSpecUI.optionDesc.desc=Specifies the type of a calendar being assigned to the series (Default – default calendar without country-specific holidays; Stock – day-of-week effects for inventories and other stock reported for the w-th day of the month; Holidays – the calendar variables based on user-defined calendar possibly with country specific holidays; UserDefined – calendar variables specified by the user) or excludes calendar variables from the regression model (None)."
    })
    private EnhancedPropertyDescriptor optionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("option", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_optionDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_optionDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tradingDaysSpecUI.automaticDesc.name=automatic",
        "tradingDaysSpecUI.automaticDesc.desc= The calendar effects can be added to the model manually, through the Option, tradingDays and LeapYear parameters (Unused ); or automatically, where the  choice of the number of calendar variables is based on  F Test or Wald test.  In both cases for an automatic choice the model with higher F value is chosen, provided that it is higher than Pftd."
    })
    private EnhancedPropertyDescriptor mautoDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("automatic", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MAUTO_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_automaticDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_automaticDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tradingDaysSpecUI.pval1Desc.name=Pvalue for td",
        "tradingDaysSpecUI.pval1Desc.desc=P-Value applied in the test specified by the automatic parameter to assess the significance of the pre-tested calendar effect and to decide if calendar effects are included into the model."
    })
    private EnhancedPropertyDescriptor pval1Desc() {
        if (!inner().isAutomatic()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("pvalue1", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MAUTO_PVAL1_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_pval1Desc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_pval1Desc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tradingDaysSpecUI.pval2Desc.name=Pvalue for restrictions on td",
        "tradingDaysSpecUI.pval2Desc.desc=P-Value applied to reject restrictions on the coeff. of the td variables"
    })
    private EnhancedPropertyDescriptor pval2Desc() {
        if (!inner().isAutomatic()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("pvalue2", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MAUTO_PVAL2_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_pval2Desc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_pval2Desc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "tradingDaysSpecUI.stdDesc.name=W",
        "tradingDaysSpecUI.stdDesc.desc=Position of the day in the month. 31 for last day."
    })
    private EnhancedPropertyDescriptor stdDesc() {
        if (!inner().isStockTradingDays()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("w", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, STOCK_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_stdDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_stdDesc_desc());
            edesc.setReadOnly(isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor userDesc() {
        if (!inner().isUserDefined()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("userVariables", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, USER_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "tradingDaysSpecUI.testDesc.name=test",
        "tradingDaysSpecUI.testDesc.desc="
    })
    private EnhancedPropertyDescriptor testDesc() {
        if (!inner().isUsed()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("regressionTest", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_testDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_testDesc_desc());
            edesc.setReadOnly(isRo() || hasFixedCoefficients() || inner().isAutomatic());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "tradingDaysSpecUI.autoDesc.name=autoAdjust",
        "tradingDaysSpecUI.autoDesc.desc="
    })
    private EnhancedPropertyDescriptor autoDesc() {
        if (!inner().isDefaultTradingDays() && !inner().isHolidays()) {
            return null;
        }
        boolean auto = core().getTransform().getFunction() != TransformationType.None;
        if (!auto) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("autoAdjust", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTO_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_autoDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_autoDesc_desc());
            edesc.setReadOnly(isRo() || hasFixedCoefficients() || inner().getLengthOfPeriodType() == LengthOfPeriodType.None);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "tradingDaysSpecUI.tdDesc.name=td",
        "tradingDaysSpecUI.tdDesc.desc=Option for trading days"
    })
    private EnhancedPropertyDescriptor tdDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("tradingDays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TD_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_tdDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_tdDesc_desc());
            edesc.setReadOnly(isRo() || inner().isAutomatic() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "tradingDaysSpecUI.coeffDesc.name=Coefficients",
        "tradingDaysSpecUI.coeffDesc.desc=Coefficients"
    })
    private EnhancedPropertyDescriptor coeffDesc() {
        if (!inner().isDefined()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("coefficients", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TD_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_coeffDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_coeffDesc_desc());
            edesc.setReadOnly(isRo() || !isTransformationDefined());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "tradingDaysSpecUI.lpDesc.name=lp",
        "tradingDaysSpecUI.lpDesc.desc=Option for length of period"
    })
    private EnhancedPropertyDescriptor lpDesc() {
        if (!inner().isDefaultTradingDays() && !inner().isHolidays()) {
            return null;
        }
        boolean adjust = core().getTransform().getAdjust() != LengthOfPeriodType.None;
        if (adjust) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LeapYear", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LP_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_lpDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_lpDesc_desc());
            edesc.setReadOnly(isRo() || core().getTransform().getAdjust() != LengthOfPeriodType.None
                        || isAutoAdjust() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "tradingDaysSpecUI.holidaysDesc.name=holidays",
        "tradingDaysSpecUI.holidaysDesc.desc="
    })
    private EnhancedPropertyDescriptor holidaysDesc() {
        if (!inner().isHolidays()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("holidays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HOLIDAYS_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_holidaysDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_holidaysDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
