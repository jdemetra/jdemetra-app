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
import ec.tstoolkit.modelling.arima.x13.RegressionSpec;
import ec.tstoolkit.modelling.arima.x13.TradingDaysSpec;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.LengthOfPeriodType;
import ec.tstoolkit.timeseries.calendars.TradingDaysType;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

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
        desc = testDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Trading days";
    }

    private TradingDaysSpec inner() {
        return core.getRegression().getTradingDays();
    }

    public TradingDaysSpecUI(RegArimaSpecification spec, boolean ro) {
        super(spec, ro);
    }

    public TradingDaysSpecType getOption() {
        TradingDaysSpec spec = inner();
        if (spec.getHolidays() != null) {
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
        spec.disable();
        switch (value) {
            case None:
                break;
            case Default:
                spec.disable();
                spec.setTradingDaysType(TradingDaysType.TradingDays);
                spec.setLengthOfPeriod(LengthOfPeriodType.LeapYear);
                spec.setTest(RegressionTestSpec.Remove);
                spec.setAutoAdjust(true);
                break;
            case Holidays:
                spec.disable();
                spec.setTradingDaysType(TradingDaysType.TradingDays);
                spec.setLengthOfPeriod(LengthOfPeriodType.LeapYear);
                spec.setTest(RegressionTestSpec.Remove);
                spec.setAutoAdjust(true);
                spec.setHolidays(GregorianCalendarManager.DEF);
                break;
            case UserDefined:
                spec.disable();
                spec.setUserVariables(new String[]{});
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

    private EnhancedPropertyDescriptor optionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("option", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor testDesc() {
        if (getOption() == TradingDaysSpecType.None) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("test", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TEST_ID);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor autoDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("autoAdjust", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTO_ID);
            edesc.setReadOnly(ro_ || core.getTransform().getFunction() != DefaultTransformationType.Auto);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor tdDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("tradingDays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TD_ID);
            desc.setDisplayName(TD_NAME);
            desc.setShortDescription(TD_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor lpDesc() {
        if (getOption() != TradingDaysSpecType.Default && getOption() != TradingDaysSpecType.Holidays) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("lengthOfPeriod", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LP_ID);
            desc.setDisplayName(LP_NAME);
            desc.setShortDescription(LP_DESC);
            edesc.setReadOnly(ro_ || core.getTransform().getAdjust() != LengthOfPeriodType.None || isAutoAdjust());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor holidaysDesc() {
        if (inner().getHolidays() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("holidays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HOLIDAYS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    public static final String TD_NAME = "td",
            LP_NAME = "lp";
    public static final String TD_DESC = "Option for trading days",
            LP_DESC = "Option for length of period";
}
