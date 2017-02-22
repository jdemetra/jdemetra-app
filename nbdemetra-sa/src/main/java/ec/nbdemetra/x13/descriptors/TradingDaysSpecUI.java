/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.nbdemetra.ui.properties.l2fprod.Holidays;
import ec.nbdemetra.ui.properties.l2fprod.UserVariables;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.RegressionTestSpec;
import ec.tstoolkit.modelling.TradingDaysSpecType;
import static ec.tstoolkit.modelling.TradingDaysSpecType.UserDefined;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import ec.tstoolkit.modelling.arima.x13.TradingDaysSpec;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.LengthOfPeriodType;
import ec.tstoolkit.timeseries.calendars.TradingDaysType;
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
        return getOption() == TradingDaysSpecType.None ? "" : "in use";
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
        return core.getRegression().getTradingDays();
    }

    public TradingDaysSpecUI(RegArimaSpecification spec, boolean ro) {
        super(spec, ro);
    }

    public TradingDaysSpecType getOption() {
        TradingDaysSpec spec = inner();
        if (spec.isStockTradingDays()) {
            return TradingDaysSpecType.Stock;
        } else if (spec.getHolidays() != null) {
            return TradingDaysSpecType.Holidays;
        } else if (spec.getUserVariables() != null) {
            return TradingDaysSpecType.UserDefined;
        } else if (spec.getTradingDaysType() != TradingDaysType.None) {
            return TradingDaysSpecType.Default;
        } else {
            return TradingDaysSpecType.None;
        }
    }

    public void setOption(TradingDaysSpecType value) {
        TradingDaysSpec spec = inner();
        switch (value) {
            case None:
        spec.disable();
                break;
            case Default:
                spec.setTradingDaysType(TradingDaysType.TradingDays);
                spec.setLengthOfPeriod(LengthOfPeriodType.LeapYear);
                spec.setTest(RegressionTestSpec.Remove);
                spec.setAutoAdjust(true);
                spec.setHolidays(null);
                break;
            case Holidays:
                spec.setTradingDaysType(TradingDaysType.TradingDays);
                spec.setLengthOfPeriod(LengthOfPeriodType.LeapYear);
                spec.setTest(RegressionTestSpec.Remove);
                spec.setAutoAdjust(true);
                spec.setHolidays(GregorianCalendarManager.DEF);
                break;
            case UserDefined:
                spec.setUserVariables(new String[]{});
                spec.setTest(RegressionTestSpec.Remove);
                break;
            case Stock:
                spec.disable();
                spec.setStockTradingDays(31);
                spec.setTest(RegressionTestSpec.Remove);
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");

        }
    }

    public RegressionTestSpec getTest() {
        return inner().getTest();
    }

    public void setTest(RegressionTestSpec value) {
        inner().setTest(value);
    }

    public boolean isAutoAdjust() {
        return inner().isAutoAdjust();
    }

    public void setAutoAdjust(boolean value) {
        inner().setAutoAdjust(value);
    }

    public TradingDaysType getTradingDays() {

        return inner().getTradingDaysType();
    }

    public void setTradingDays(TradingDaysType value) {
        inner().setTradingDaysType(value);
    }

    public LengthOfPeriodType getLengthOfPeriod() {
        return inner().getLengthOfPeriod();
    }

    public void setLengthOfPeriod(LengthOfPeriodType value) {
        inner().setLengthOfPeriod(value);
    }

    public Holidays getHolidays() {
        return new Holidays(inner().getHolidays());
    }

    public void setHolidays(Holidays holidays) {
        inner().setHolidays(holidays.getName());
    }

    public UserVariables getUserVariables() {
        return new UserVariables(inner().getUserVariables());
    }

    public void setUserVariables(UserVariables vars) {
        inner().setUserVariables(vars.getNames());
    }

    public int getW() {
        return inner().getStockTradingDays();
    }

    public void setW(int w) {
        inner().setStockTradingDays(w);
    }

    @Messages({
        "tradingDaysSpecUI.stdDesc.name=W",
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
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor userDesc() {
        if (inner().getUserVariables() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("userVariables", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, USER_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
/////////////////////////////////////////////////////////
    private static final int TYPE_ID = 0, OPTION_ID = 1, STOCK_ID = 2, HOLIDAYS_ID = 3, USER_ID = 4, TD_ID = 5, LP_ID = 6, AUTO_ID = 7, TEST_ID = 10;

    @Messages({
        "tradingDaysSpecUI.optionDesc.name=option",
        "tradingDaysSpecUI.optionDesc.desc="
    })
    private EnhancedPropertyDescriptor optionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("option", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_optionDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_optionDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
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
        if (getOption() == TradingDaysSpecType.None) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_testDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_testDesc_desc());
            edesc.setReadOnly(ro_);
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
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("autoAdjust", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTO_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_autoDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_autoDesc_desc());
            edesc.setReadOnly(ro_ || core.getTransform().getFunction() != DefaultTransformationType.Auto);
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
            edesc.setReadOnly(ro_);
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
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("lengthOfPeriod", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LP_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_lpDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_lpDesc_desc());
            edesc.setReadOnly(ro_ || core.getTransform().getAdjust() != LengthOfPeriodType.None || isAutoAdjust());
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
        if (inner().getHolidays() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("holidays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HOLIDAYS_ID);
            desc.setDisplayName(Bundle.tradingDaysSpecUI_holidaysDesc_name());
            desc.setShortDescription(Bundle.tradingDaysSpecUI_holidaysDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
