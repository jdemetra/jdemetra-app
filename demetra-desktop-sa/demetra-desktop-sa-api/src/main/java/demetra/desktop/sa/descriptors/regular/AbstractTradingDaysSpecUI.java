/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.descriptors.regular;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.desktop.ui.properties.l2fprod.Holidays;
import demetra.desktop.ui.properties.l2fprod.UserVariables;
import demetra.desktop.ui.properties.l2fprod.NamedParameters;
import demetra.modelling.TransformationType;
import demetra.modelling.regular.TradingDaysSpec;
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
public abstract class AbstractTradingDaysSpecUI implements IPropertyDescriptors {

    @Override
    public String toString() {
        return spec().isUsed() ? "in use" : "";
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = autoDesc();
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

    @Messages("regular.tradingDaysSpecUI.getDisplayName=Trading days")
    @Override
    public String getDisplayName() {
        return Bundle.regular_tradingDaysSpecUI_getDisplayName();
    }

    protected abstract TradingDaysSpec spec();
    
    protected abstract RegularSpecUI root();
    
    

    public TradingDaysSpecType getOption() {
        TradingDaysSpec spec = spec();
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
        LengthOfPeriodType adjust = root().transform().getAdjust();
        TransformationType function = root().transform().getFunction();
        boolean auto = function == TransformationType.Auto;
        TradingDaysSpec spec = spec();
        boolean automatic = spec.isAutomatic();
        LengthOfPeriodType lp = adjust == LengthOfPeriodType.None ? LengthOfPeriodType.LeapYear : LengthOfPeriodType.None;
        switch (value) {
            case None:
                root().update(TradingDaysSpec.none());
                break;
            case Default:
                root().update(automatic ? TradingDaysSpec.automatic(lp, spec.getAutomaticMethod(), spec.getProbabilityForFTest(), auto)
                        : TradingDaysSpec.td(TradingDaysType.TD7, lp,
                                true,
                                auto));
                break;
            case Holidays:
                root().update(automatic ? TradingDaysSpec.automaticHolidays(CalendarManager.DEF, lp, spec.getAutomaticMethod(), spec.getProbabilityForFTest(), auto)
                        : TradingDaysSpec.holidays(CalendarManager.DEF,
                                TradingDaysType.TD7, lp,
                                true,
                                auto));
                break;
            case Stock:
                root().update(TradingDaysSpec.stockTradingDays(31, true));
                break;
            case UserDefined:
                root().update(TradingDaysSpec.userDefined(new String[]{}, true));
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public boolean isTest() {
        return spec().isTest();
    }

    public void setTest(boolean value) {
        TradingDaysSpec td = spec();
        // No fixed coefficient (otherwise, read only)
        if (value == td.isTest()) {
            return;
        }
        switch (getOption()) {
            case Default:
                root().update(TradingDaysSpec.td(td.getTradingDaysType(), td.getLengthOfPeriodType(), value, td.isAutoAdjust()));
                break;
            case Holidays:
                root().update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(), td.getLengthOfPeriodType(), value, td.isAutoAdjust()));
                break;
            case Stock:
                root().update(TradingDaysSpec.stockTradingDays(td.getStockTradingDays(), value));
                break;
            case UserDefined:
                root().update(TradingDaysSpec.userDefined(td.getUserVariables(), value));
                break;
        }
    }

    public boolean hasFixedCoefficients() {
        return spec().hasFixedCoefficients();
    }

    public NamedParameters getCoefficients() {
        TradingDaysSpec inner = spec();
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
        if (spec().getLengthOfPeriodType() != LengthOfPeriodType.None) {
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
        root().update(spec().withCoefficients(td, lp));
    }

    public int getW() {
        return spec().getStockTradingDays();
    }

    public void setW(int w) {
        TradingDaysSpec td = spec();
        if (w == td.getStockTradingDays()) {
            return;
        }
        root().update(TradingDaysSpec.stockTradingDays(w, td.isTest()));

    }

    public TradingDaysType getTradingDays() {
        return spec().getTradingDaysType();
    }

    public void setTradingDays(TradingDaysType value) {
        if (value.equals(TradingDaysType.NONE)) {
            setOption(TradingDaysSpecType.None);
            return;
        }

        TradingDaysSpec td = spec();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getTradingDaysType())) {
            return;
        }
        switch (getOption()) {
            case Default:
                root().update(TradingDaysSpec.td(value, td.getLengthOfPeriodType(), td.isTest(), td.isAutoAdjust()));
                break;
            case Holidays:
                root().update(TradingDaysSpec.holidays(td.getHolidays(), value, td.getLengthOfPeriodType(), td.isTest(), td.isAutoAdjust()));
                break;
        }
    }

    public LengthOfPeriodType getLeapYear() {
        return spec().getLengthOfPeriodType();
    }

    public void setLeapYear(LengthOfPeriodType value) {
        TradingDaysSpec td = spec();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getLengthOfPeriodType())) {
            return;
        }
        switch (getOption()) {
            case Default:
                if (td.isAutomatic()) {
                    root().update(TradingDaysSpec.automatic(value, td.getAutomaticMethod(), td.getProbabilityForFTest(), td.isAutoAdjust()));
                } else {
                    root().update(TradingDaysSpec.td(td.getTradingDaysType(), value, td.isTest(), td.isAutoAdjust()));
                }
                break;
            case Holidays:
                if (td.isAutomatic()) {
                    root().update(TradingDaysSpec.automaticHolidays(td.getHolidays(), value, td.getAutomaticMethod(), td.getProbabilityForFTest(), td.isAutoAdjust()));
                } else {
                    root().update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(), value, td.isTest(), td.isAutoAdjust()));
                }
                break;
        }
    }

    public boolean isAutoAdjust() {
        return spec().isAutoAdjust();
    }

    public void setAutoAdjust(boolean value) {
        TradingDaysSpec td = spec();
        // No fixed coefficient (otherwise, read only)
        switch (getOption()) {
            case Default:
                if (td.isAutomatic()) {
                    root().update(TradingDaysSpec.automatic(td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getProbabilityForFTest(), value));
                } else {
                    root().update(TradingDaysSpec.td(td.getTradingDaysType(), td.getLengthOfPeriodType(),
                            td.isTest(), value));
                }
                break;
            case Holidays:
                if (td.isAutomatic()) {
                    root().update(TradingDaysSpec.automaticHolidays(td.getHolidays(), td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getProbabilityForFTest(), value));
                } else {
                    root().update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(),
                            td.getLengthOfPeriodType(), td.isTest(),
                            value));
                }
                break;
        }
    }

    public Holidays getHolidays() {
        return new Holidays(spec().getHolidays());
    }

    public void setHolidays(Holidays holidays) {
        TradingDaysSpec td = spec();
        boolean automatic = td.isAutomatic();
        root().update(automatic ? TradingDaysSpec.automaticHolidays(holidays.getName(), td.getLengthOfPeriodType(), td.getAutomaticMethod(), td.getProbabilityForFTest(), td.isAutoAdjust())
                : TradingDaysSpec.holidays(holidays.getName(), td.getTradingDaysType(), td.getLengthOfPeriodType(), td.isTest(), td.isAutoAdjust()));
    }

    public UserVariables getUserVariables() {
        return new UserVariables(spec().getUserVariables());
    }

    public void setUserVariables(UserVariables vars) {
        TradingDaysSpec td = spec();
        root().update(TradingDaysSpec.userDefined(vars.getNames(), td.isTest()));
    }

    public TradingDaysSpec.AutoMethod getAutomatic() {
        return spec().getAutomaticMethod();
    }

    public void setAutomatic(TradingDaysSpec.AutoMethod value) {
        TradingDaysSpec td = spec();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getAutomaticMethod())) {
            return;
        }
        if (value.equals(TradingDaysSpec.AutoMethod.UNUSED)) {
            switch (getOption()) {
                case Default:
                    root().update(TradingDaysSpec.td(TradingDaysType.TD2, td.getLengthOfPeriodType(), true, td.isAutoAdjust()));
                    break;
                case Holidays:
                    root().update(TradingDaysSpec.holidays(td.getHolidays(), TradingDaysType.TD2, td.getLengthOfPeriodType(), true, td.isAutoAdjust()));
                    break;
            }
        } else {
            double pr = td.getProbabilityForFTest();
            switch (getOption()) {
                case Default:
                    root().update(TradingDaysSpec.automatic(td.getLengthOfPeriodType(), value,
                            pr != 0 ? pr : TradingDaysSpec.DEF_PFTD, td.isAutoAdjust()));
                    break;
                case Holidays:
                    root().update(TradingDaysSpec.automaticHolidays(td.getHolidays(), td.getLengthOfPeriodType(), value,
                            pr != 0 ? pr : TradingDaysSpec.DEF_PFTD, td.isAutoAdjust()));
                    break;
            }
        }
    }


/////////////////////////////////////////////////////////
    private static final int AUTO_ID = 0, PFTD_ID = 10, OPTION_ID = 20, STOCK_ID = 30, HOLIDAYS_ID = 40, USER_ID = 50, TEST_ID = 1, COEFF_ID = 100;

    @Messages({"regular.tradingDaysSpecUI.optionDesc.name=option",
        "regular.tradingDaysSpecUI.optionDesc.desc=Specifies the type of a calendar being assigned to the series (Default – default calendar without country-specific holidays; Stock – day-of-week effects for inventories and other stock reported for the w-th day of the month; Holidays – the calendar variables based on user-defined calendar possibly with country specific holidays; UserDefined – calendar variables specified by the user) or excludes calendar variables from the regression model (None)."
    })
    private EnhancedPropertyDescriptor optionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("option", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_optionDesc_name());
            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_optionDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root().isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"regular.tradingDaysSpecUI.automaticDesc.name=automatic",
        "regular.tradingDaysSpecUI.automaticDesc.desc= The calendar effects can be added to the model manually, through the Option, tradingDays and LeapYear parameters (Unused ); or automatically, where the  choice of the number of calendar variables is based on  F Test or Wald test.  In both cases for an automatic choice the model with higher F value is chosen, provided that it is higher than Pftd."
    })
    private EnhancedPropertyDescriptor autoDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("automatic", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTO_ID);
            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_automaticDesc_name());
            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_automaticDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root().isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"regular.tradingDaysSpecUI.holidaysDesc.name=holidays",
        "regular.tradingDaysSpecUI.holidaysDesc.desc=holidays"
    })
    private EnhancedPropertyDescriptor holidaysDesc() {
        if (spec().getHolidays() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("holidays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HOLIDAYS_ID);
            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_holidaysDesc_name());
            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_holidaysDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root().isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

//    @Messages({"regular.tradingDaysSpecUI.pftdDesc.name=Pftd",
//        "regular.tradingDaysSpecUI.pftdDesc.desc=P-Value applied in the test specified by the automatic parameter to assess the significance of the pre-tested calendar effect and to decide if calendar effects are included into the TRAMO model."
//    })
//    private EnhancedPropertyDescriptor pftdDesc() {
//        if (!spec().isAutomatic()) {
//            return null;
//        }
//        try {
//            PropertyDescriptor desc = new PropertyDescriptor("pftd", this.getClass());
//            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PFTD_ID);
//            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_pftdDesc_name());
//            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_pftdDesc_desc());
//            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
//            edesc.setReadOnly(isRo() || hasFixedCoefficients());
//            return edesc;
//        } catch (IntrospectionException ex) {
//            return null;
//        }
//    }

    @Messages({"regular.tradingDaysSpecUI.userDesc.name=User Variable",
        "regular.tradingDaysSpecUI.userDesc.desc="
    })
    private EnhancedPropertyDescriptor userDesc() {
        if (spec().getUserVariables() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("userVariables", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, USER_ID);
            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_userDesc_name());
            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_userDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root().isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"regular.tradingDaysSpecUI.testDesc.name=RegressionTestType",
        "regular.tradingDaysSpecUI.testDesc.desc="
    })
    private EnhancedPropertyDescriptor testDesc() {
        if (getOption() == TradingDaysSpecType.None) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_testDesc_name());
            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_testDesc_desc());
            edesc.setReadOnly(root().isRo() || spec().isAutomatic() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"regular.tradingDaysSpecUI.tdDesc.name=tradingDays",
        "regular.tradingDaysSpecUI.tdDesc.desc="
    })
    private EnhancedPropertyDescriptor tdDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("tradingDays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_tdDesc_name());
            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_tdDesc_desc());
            edesc.setReadOnly(root().isRo() || spec().isAutomatic() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"regular.tradingDaysSpecUI.stdDesc.name=w",
        "regular.tradingDaysSpecUI.stdDesc.desc=Position of the day in the month. 31 for last day."
    })
    private EnhancedPropertyDescriptor stdDesc() {
        if (getOption() != TradingDaysSpecType.Stock) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("w", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, STOCK_ID);
            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_stdDesc_name());
            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_stdDesc_desc());
            edesc.setReadOnly(root().isRo() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({"regular.tradingDaysSpecUI.leapyearDesc.name=Leap year",
        "regular.tradingDaysSpecUI.leapyearDesc.desc=Enables/disables for a leap-year correction."
    })
    private EnhancedPropertyDescriptor lpDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        boolean adjust = root().transform().getAdjust() != LengthOfPeriodType.None;
        if (adjust) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("leapYear", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_leapyearDesc_name());
            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_leapyearDesc_desc());
            edesc.setReadOnly(root().isRo()|| root().transform().getAdjust() != LengthOfPeriodType.None
                    || isAutoAdjust() || hasFixedCoefficients());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.tradingDaysSpecUI.autoadjustDesc.name=autoAdjust",
        "regular.tradingDaysSpecUI.autoadjustDesc.desc="
    })
    private EnhancedPropertyDescriptor autoadjustDesc() {
        if (!spec().isDefaultTradingDays() && !spec().isHolidays()) {
            return null;
        }
        boolean auto = root().transform().getFunction() != TransformationType.None;
        if (!auto) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("autoAdjust", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTO_ID);
            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_autoadjustDesc_name());
            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_autoadjustDesc_desc());
            edesc.setReadOnly(root().isRo() || hasFixedCoefficients() || spec().getLengthOfPeriodType() == LengthOfPeriodType.None);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "regular.tradingDaysSpecUI.coeffDesc.name=Coefficients",
        "regular.tradingDaysSpecUI.coeffDesc.desc=Coefficients"
    })
    private EnhancedPropertyDescriptor coeffDesc() {
        if (!spec().isDefined()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("coefficients", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, COEFF_ID);
            desc.setDisplayName(Bundle.regular_tradingDaysSpecUI_coeffDesc_name());
            desc.setShortDescription(Bundle.regular_tradingDaysSpecUI_coeffDesc_desc());
            edesc.setReadOnly(root().isRo() || root().transform().getFunction() == TransformationType.Auto);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

}
