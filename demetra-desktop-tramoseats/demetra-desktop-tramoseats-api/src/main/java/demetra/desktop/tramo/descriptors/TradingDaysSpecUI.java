/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tramo.descriptors;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.ui.properties.l2fprod.Holidays;
import demetra.desktop.ui.properties.l2fprod.UserVariables;
import demetra.desktop.modelling.util.TradingDaysSpecType;
import demetra.desktop.ui.properties.l2fprod.NamedParameters;
import demetra.modelling.TransformationType;
import demetra.timeseries.calendars.CalendarManager;
import demetra.timeseries.calendars.LengthOfPeriodType;
import demetra.timeseries.calendars.TradingDaysType;
import demetra.tramo.RegressionTestType;
import demetra.tramo.TradingDaysSpec;
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
public class TradingDaysSpecUI extends BaseTramoSpecUI {

    @Override
    public String toString() {
        return inner().isUsed() ? "in use" : "";
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = autoDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = pftdDesc();
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
        desc = autoadjustDesc();
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
        TradingDaysSpec spec = core().getRegression().getCalendar().getTradingDays();

        return spec;
    }

    TradingDaysSpecUI(TramoSpecRoot root) {
        super(root);
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
        if (value == getOption())
            return;
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
                update(automatic ? TradingDaysSpec.automatic(lp, spec.getAutomaticMethod(), spec.getProbabilityForFTest(), auto)
                        : TradingDaysSpec.td(TradingDaysType.TD7, lp,
                                RegressionTestType.Separate_T,
                                auto));
                break;
            case Holidays:
                update(automatic ? TradingDaysSpec.automaticHolidays(CalendarManager.DEF, lp, spec.getAutomaticMethod(), spec.getProbabilityForFTest(), auto)
                        : TradingDaysSpec.holidays(CalendarManager.DEF,
                                TradingDaysType.TD7, lp,
                                RegressionTestType.Separate_T,
                                auto));
                break;
            case Stock:
                update(TradingDaysSpec.stockTradingDays(31, RegressionTestType.Separate_T));
                break;
            case UserDefined:
                update(TradingDaysSpec.userDefined(new String[]{}, RegressionTestType.Separate_T));
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public RegressionTestType getRegressionTestType() {
        return inner().getRegressionTestType();
    }

    public void setRegressionTestType(RegressionTestType value) {
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getRegressionTestType())) {
            return;
        }
        switch (getOption()) {
            case Default:
                update(TradingDaysSpec.td(td.getTradingDaysType(), td.getLengthOfPeriodType(), value, td.isAutoAdjust()));
                break;
            case Holidays:
                update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(), td.getLengthOfPeriodType(), value, td.isAutoAdjust()));
                break;
            case Stock:
                update(TradingDaysSpec.stockTradingDays(td.getStockTradingDays(), value));
                break;
            case UserDefined:
                update(TradingDaysSpec.userDefined(td.getUserVariables(), value));
                break;
        }
    }

    public boolean hasFixedCoefficients() {
        return inner().hasFixedCoefficients();
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
                update(TradingDaysSpec.td(value, td.getLengthOfPeriodType(), td.getRegressionTestType(), td.isAutoAdjust()));
                break;
            case Holidays:
                update(TradingDaysSpec.holidays(td.getHolidays(), value, td.getLengthOfPeriodType(), td.getRegressionTestType(), td.isAutoAdjust()));
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
                    update(TradingDaysSpec.automatic(value, td.getAutomaticMethod(), td.getProbabilityForFTest(), td.isAutoAdjust()));
                } else {
                    update(TradingDaysSpec.td(td.getTradingDaysType(), value, td.getRegressionTestType(), td.isAutoAdjust()));
                }
                break;
            case Holidays:
                if (td.isAutomatic()) {
                    update(TradingDaysSpec.automaticHolidays(td.getHolidays(), value, td.getAutomaticMethod(), td.getProbabilityForFTest(), td.isAutoAdjust()));
                } else {
                    update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(), value, td.getRegressionTestType(), td.isAutoAdjust()));
                }
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
                    update(TradingDaysSpec.automatic(td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getProbabilityForFTest(), value));
                } else {
                    update(TradingDaysSpec.td(td.getTradingDaysType(), td.getLengthOfPeriodType(),
                            td.getRegressionTestType(), value));
                }
                break;
            case Holidays:
                if (td.isAutomatic()) {
                    update(TradingDaysSpec.automaticHolidays(td.getHolidays(), td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getProbabilityForFTest(), value));
                } else {
                    update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(),
                            td.getLengthOfPeriodType(), td.getRegressionTestType(),
                            value));
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
        update(automatic ? TradingDaysSpec.automaticHolidays(holidays.getName(), td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getProbabilityForFTest(), td.isAutoAdjust())
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
                    update(TradingDaysSpec.td(TradingDaysType.TD2, td.getLengthOfPeriodType(), RegressionTestType.Joint_F, td.isAutoAdjust()));
                    break;
                case Holidays:
                    update(TradingDaysSpec.holidays(td.getHolidays(), TradingDaysType.TD2, td.getLengthOfPeriodType(), RegressionTestType.Joint_F, td.isAutoAdjust()));
                    break;
            }
        } else {
            double pr = td.getProbabilityForFTest();
            switch (getOption()) {
                case Default:
                    update(TradingDaysSpec.automatic(td.getLengthOfPeriodType(), value,
                            pr != 0 ? pr : TradingDaysSpec.DEF_PFTD, td.isAutoAdjust()));
                    break;
                case Holidays:
                    update(TradingDaysSpec.automaticHolidays(td.getHolidays(), td.getLengthOfPeriodType(), value,
                            pr != 0 ? pr : TradingDaysSpec.DEF_PFTD, td.isAutoAdjust()));
                    break;
            }
        }
    }

    public double getPftd() {
        return inner().getProbabilityForFTest();
    }

    public void setPftd(double value) {
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value == td.getProbabilityForFTest()) {
            return;
        }
        switch (getOption()) {
            case Default:
                update(TradingDaysSpec.automatic(td.getLengthOfPeriodType(), td.getAutomaticMethod(), value, td.isAutoAdjust()));
                break;
            case Holidays:
                update(TradingDaysSpec.automaticHolidays(td.getHolidays(), td.getLengthOfPeriodType(), td.getAutomaticMethod(), value, td.isAutoAdjust()));
                break;
        }
    }

/////////////////////////////////////////////////////////
    private static final int AUTO_ID = 0, PFTD_ID = 10, OPTION_ID = 20, STOCK_ID = 30, HOLIDAYS_ID = 40, USER_ID = 50, TEST_ID = 1, COEFF_ID = 100;

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
    private EnhancedPropertyDescriptor autoDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("automatic", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTO_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_automaticDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_automaticDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tradingDaysSpecUI.holidaysDesc.name=holidays",
        "tradingDaysSpecUI.holidaysDesc.desc=holidays"
    })
    private EnhancedPropertyDescriptor holidaysDesc() {
        if (inner().getHolidays() == null) {
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

    @Messages({"tradingDaysSpecUI.pftdDesc.name=Pftd",
        "tradingDaysSpecUI.pftdDesc.desc=P-Value applied in the test specified by the automatic parameter to assess the significance of the pre-tested calendar effect and to decide if calendar effects are included into the TRAMO model."
    })
    private EnhancedPropertyDescriptor pftdDesc() {
        if (!inner().isAutomatic()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("pftd", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PFTD_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_pftdDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_pftdDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tradingDaysSpecUI.userDesc.name=User Variable",
        "tradingDaysSpecUI.userDesc.desc="
    })
    private EnhancedPropertyDescriptor userDesc() {
        if (inner().getUserVariables() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("userVariables", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, USER_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_userDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_userDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tradingDaysSpecUI.testDesc.name=RegressionTestType",
        "tradingDaysSpecUI.testDesc.desc="
    })
    private EnhancedPropertyDescriptor testDesc() {
        if (getOption() == TradingDaysSpecType.None) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("RegressionTestType", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_testDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_testDesc_desc());
            edesc.setReadOnly(isRo() || inner().isAutomatic() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tradingDaysSpecUI.tdDesc.name=tradingDays",
        "tradingDaysSpecUI.tdDesc.desc="
    })
    private EnhancedPropertyDescriptor tdDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("tradingDays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_tdDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_tdDesc_desc());
            edesc.setReadOnly(isRo() || inner().isAutomatic() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"tradingDaysSpecUI.stdDesc.name=w",
        "tradingDaysSpecUI.stdDesc.desc=Position of the day in the month. 31 for last day."
    })
    private EnhancedPropertyDescriptor stdDesc() {
        if (getOption() != TradingDaysSpecType.Stock) {
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

    @Messages({"tradingDaysSpecUI.leapyearDesc.name=Leap year",
        "tradingDaysSpecUI.leapyearDesc.desc=Enables/disables for a leap-year correction."
    })
    private EnhancedPropertyDescriptor lpDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        boolean adjust = core().getTransform().getAdjust() != LengthOfPeriodType.None;
        if (adjust) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("leapYear", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_leapyearDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_leapyearDesc_desc());
            edesc.setReadOnly(isRo()|| core().getTransform().getAdjust() != LengthOfPeriodType.None
                    || isAutoAdjust() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "tradingDaysSpecUI.autoadjustDesc.name=autoAdjust",
        "tradingDaysSpecUI.autoadjustDesc.desc="
    })
    private EnhancedPropertyDescriptor autoadjustDesc() {
        if (!inner().isDefaultTradingDays() && !inner().isHolidays()) {
            return null;
        }
        boolean auto = core().getTransform().getFunction() == TransformationType.Auto;
        if (!auto) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("autoAdjust", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTO_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_autoadjustDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_autoadjustDesc_desc());
            edesc.setReadOnly(isRo() || hasFixedCoefficients() || inner().getLengthOfPeriodType() == LengthOfPeriodType.None);
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
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, COEFF_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_coeffDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_coeffDesc_desc());
            edesc.setReadOnly(isRo() || !isTransformationDefined());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

}
