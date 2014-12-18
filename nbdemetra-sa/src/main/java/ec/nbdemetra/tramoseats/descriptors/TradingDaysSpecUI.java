/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.tramoseats.descriptors;

import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.nbdemetra.ui.properties.l2fprod.Holidays;
import ec.nbdemetra.ui.properties.l2fprod.UserVariables;
import ec.tstoolkit.modelling.TradingDaysSpecType;
import ec.tstoolkit.modelling.arima.tramo.TradingDaysSpec;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import ec.tstoolkit.timeseries.calendars.TradingDaysType;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.RegressionTestType;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pcuser
 */
public class TradingDaysSpecUI extends BaseTramoSpecUI {

    static {
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(RegressionTestType.class);
        CustomPropertyEditorRegistry.INSTANCE.registerEnumEditor(TradingDaysSpec.AutoMethod.class);
    }

    @Override
    public String toString() {
        return getOption() == TradingDaysSpecType.None ? "" : "in use";
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

    @Override
    public String getDisplayName() {
        return "Trading days";
    }

    private TradingDaysSpec inner() {
        TradingDaysSpec spec = core.getRegression().getCalendar().getTradingDays();
        return spec;
    }

    public TradingDaysSpecUI(TramoSpecification spec, boolean ro) {
        super(spec, ro);
    }

    public TradingDaysSpecType getOption() {
        TradingDaysSpec spec = inner();
        if (spec.isUsed()) {
            if (spec.isStockTradingDays()) {
                return TradingDaysSpecType.Stock;
            } else if (spec.getHolidays() != null) {
                return TradingDaysSpecType.Holidays;
            } else if (spec.getUserVariables() != null) {
                return TradingDaysSpecType.UserDefined;
            } else if (spec.isUsed()) {
                return TradingDaysSpecType.Default;
            }
        }
        return TradingDaysSpecType.None;
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
                spec.setLeapYear(true);
                spec.setTest(true);
                break;
            case Stock:
                spec.disable();
                spec.setStockTradingDays(31);
                spec.setTest(true);
                break;
            case Holidays:
                spec.disable();
                spec.setTradingDaysType(TradingDaysType.TradingDays);
                spec.setLeapYear(true);
                spec.setHolidays(GregorianCalendarManager.DEF);
                spec.setTest(true);
                break;
            case UserDefined:
                spec.disable();
                spec.setUserVariables(new String[]{});
                spec.setTest(true);
                break;

            default:
                throw new UnsupportedOperationException("Not supported yet.");

        }
    }

    public RegressionTestType getRegressionTestType() {
        return inner().getRegressionTestType();
    }

    public void setRegressionTestType(RegressionTestType value) {
        inner().setRegressionTestType(value);
    }

    public int getW() {
        return inner().getStockTradingDays();
    }

    public void setW(int w) {
        inner().setStockTradingDays(w);
    }

    public TradingDaysType getTradingDays() {
        return inner().getTradingDaysType();
    }

    public void setTradingDays(TradingDaysType value) {
        inner().setTradingDaysType(value);
    }

    public boolean getLeapYear() {
        return inner().isLeapYear();
    }

    public void setLeapYear(boolean value) {
        inner().setLeapYear(value);
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

    public TradingDaysSpec.AutoMethod getAutomatic() {
        return inner().getAutomaticMethod();
    }

    public void setAutomatic(TradingDaysSpec.AutoMethod value) {
        inner().setAutomaticMethod(value);
    }
    
   public double getPftd() {
        return inner().getProbabibilityForFTest();
    }

    public void setPftd(double value) {
        inner().setProbabibilityForFTest(value);
    }
    
/////////////////////////////////////////////////////////
    private static final int AUTO_ID = 0, PFTD_ID=10, OPTION_ID = 20, STOCK_ID = 30, HOLIDAYS_ID = 40, USER_ID = 50, TEST_ID = 1;
    private static final String PFTD_NAME="Pftd";
    private static final String PFTD_DESC="Probability for F-Tests in automatic identification of TD";

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

    private EnhancedPropertyDescriptor autoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("automatic", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTO_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro_);
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

    private EnhancedPropertyDescriptor pftdDesc() {
        if (! inner().isAutomatic()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("pftd", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PFTD_ID);
            desc.setName(PFTD_NAME);
            desc.setShortDescription(PFTD_DESC);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
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

    private EnhancedPropertyDescriptor testDesc() {
        if (getOption() == TradingDaysSpecType.None) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("RegressionTestType", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            edesc.setReadOnly(ro_ || inner().isAutomatic());
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
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            edesc.setReadOnly(ro_ || inner().isAutomatic());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor stdDesc() {
        if (getOption() != TradingDaysSpecType.Stock) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("w", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, STOCK_ID);
            desc.setShortDescription("Position of the day in the month. 31 for last day.");
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
            PropertyDescriptor desc = new PropertyDescriptor("leapYear", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, OPTION_ID);
            edesc.setReadOnly(ro_ || inner().isAutomatic());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
