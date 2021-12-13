/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.regarima.descriptors;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.ui.properties.l2fprod.Holidays;
import demetra.desktop.ui.properties.l2fprod.UserVariables;
import demetra.desktop.modelling.util.TradingDaysSpecType;
import demetra.modelling.TransformationType;
import demetra.regarima.RegressionTestSpec;
import demetra.regarima.TradingDaysSpec;
import demetra.timeseries.calendars.CalendarManager;
import demetra.timeseries.calendars.LengthOfPeriodType;
import demetra.timeseries.calendars.TradingDaysType;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class TradingDaysSpecUI extends BaseRegArimaSpecUI {

    @Override
    public String toString() {
        return  inner().isUsed() ? "in use" : "";
    }
    
    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = optionDesc();
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
    public boolean isRo(){
        return super.isRo() || inner().hasFixedCoefficients();
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
        LengthOfPeriodType adjust = core().getTransform().getAdjust();
        TransformationType function = core().getTransform().getFunction();
        boolean auto = function == TransformationType.Auto;
        boolean lp=adjust==LengthOfPeriodType.None;
        switch (value) {
            case None:
                update(TradingDaysSpec.none());
                break;
            case Default:
                update(TradingDaysSpec.td(TradingDaysType.TradingDays, 
                        lp ? LengthOfPeriodType.LeapYear : LengthOfPeriodType.None, 
                        RegressionTestSpec.Remove,
                        auto));
                break;
            case Holidays:
                update(TradingDaysSpec.holidays(CalendarManager.DEF, 
                        TradingDaysType.TradingDays, 
                        lp ? LengthOfPeriodType.LeapYear : LengthOfPeriodType.None, 
                        RegressionTestSpec.Remove,
                        auto));
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

    public RegressionTestSpec getRegressionTest() {
        return inner().getRegressionTestType();
    }

    public void setRegressionTest(RegressionTestSpec value) {
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getRegressionTestType()))
            return;
        switch (getOption()){
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
        if (w == td.getStockTradingDays())
            return;
        update(TradingDaysSpec.stockTradingDays(w, td.getRegressionTestType()));
        
    }

    public TradingDaysType getTradingDays() {
        return inner().getTradingDaysType();
    }

    public void setTradingDays(TradingDaysType value) {
        if (value.equals(TradingDaysType.None)){
            setOption(TradingDaysSpecType.None);
            return;
        }
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getTradingDaysType()))
            return;
        switch (getOption()){
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
        switch (getOption()){
            case Default:
                update(TradingDaysSpec.td(td.getTradingDaysType(), td.getLengthOfPeriodType(), 
                        td.getRegressionTestType(), value));
                break;
            case Holidays:
                update(TradingDaysSpec.holidays(td.getHolidays(), td.getTradingDaysType(), 
                        td.getLengthOfPeriodType(), td.getRegressionTestType(),
                        value));
                break;
        }
    }

    public LengthOfPeriodType getLeapYear() {
        return inner().getLengthOfPeriodType();
    }

    public void setLeapYear(LengthOfPeriodType value) {
        TradingDaysSpec td = inner();
        // No fixed coefficient (otherwise, read only)
        if (value.equals(td.getLengthOfPeriodType()))
            return;
        switch (getOption()){
            case Default:
                update(TradingDaysSpec.td(td.getTradingDaysType(), value, 
                        td.getRegressionTestType(), td.isAutoAdjust()));
                break;
            case Holidays:
                update(TradingDaysSpec.holidays(td.getHolidays(), 
                        td.getTradingDaysType(), value, 
                        td.getRegressionTestType(), td.isAutoAdjust()
                ));
                break;
        }
    }

    public Holidays getHolidays() {
        return new Holidays(inner().getHolidays());
    }

    public void setHolidays(Holidays holidays) {
        TradingDaysSpec td = inner();
        update(TradingDaysSpec.holidays(holidays.getName(), td.getTradingDaysType(), 
                td.getLengthOfPeriodType(), td.getRegressionTestType(), td.isAutoAdjust()));
    }

    public UserVariables getUserVariables() {
        return new UserVariables(inner().getUserVariables());
    }

    public void setUserVariables(UserVariables vars) {
        TradingDaysSpec td = inner();
        update(TradingDaysSpec.userDefined(vars.getNames(), td.getRegressionTestType()));
    }

   
    
/////////////////////////////////////////////////////////
     private static final int OPTION_ID = 1, STOCK_ID = 2, HOLIDAYS_ID = 3, USER_ID = 4, TD_ID = 5, LP_ID = 6, AUTO_ID = 7, TEST_ID = 10;

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
            edesc.setReadOnly(isRo());
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
        if (! inner().isStockTradingDays()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("w", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, STOCK_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_stdDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_stdDesc_desc());
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
        if (!inner().isDefaultTradingDays() && ! inner().isHolidays()) {
            return null;
       }
        boolean auto = core().getTransform().getFunction()== TransformationType.Auto;
        if (! auto)
            return null;
        boolean lp = inner().getLengthOfPeriodType() != LengthOfPeriodType.None;
        if (! lp)
            return null;
         try {
             PropertyDescriptor desc = new PropertyDescriptor("autoAdjust", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTO_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_autoDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_autoDesc_desc());
            edesc.setReadOnly(isRo());
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
            edesc.setReadOnly(isRo());
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
        if (!inner().isDefaultTradingDays() && ! inner().isHolidays()) {
            return null;
        }
        boolean adjust = core().getTransform().getAdjust() != LengthOfPeriodType.None;
        if (adjust)
            return null;
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LeapYear", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LP_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_lpDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_lpDesc_desc());
            edesc.setReadOnly(isRo() || core().getTransform().getAdjust() != LengthOfPeriodType.None || isAutoAdjust());
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
        if (! inner().isHolidays()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("holidays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HOLIDAYS_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_holidaysDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_holidaysDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
