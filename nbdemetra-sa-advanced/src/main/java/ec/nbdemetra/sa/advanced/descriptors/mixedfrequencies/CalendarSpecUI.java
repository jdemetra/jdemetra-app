/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ec.nbdemetra.sa.advanced.descriptors.mixedfrequencies;

import ec.tstoolkit.arima.special.mixedfrequencies.MixedFrequenciesSpecification;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.modelling.arima.tramo.EasterSpec;
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
public class CalendarSpecUI extends BaseSpecUI {

    /**
     *
     * @param spec
     */
    public CalendarSpecUI(MixedFrequenciesSpecification spec) {
        super(spec);
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = tdDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lpDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = eDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = durationDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "Calendar";
    }

    public TradingDaysType getTradingDays(){
        return core.getRegression().getTradingDays().getTradingDaysType();
    }

    public void setTradingDays(TradingDaysType value){
        core.getRegression().getTradingDays().setTradingDaysType(value);
    }

    public boolean isLeapYear(){
        return core.getRegression().getTradingDays().isLeapYear();
    }

    public void setLeapYear(boolean value){
        core.getRegression().getTradingDays().setLeapYear(value);
    }

    public EasterSpec.Type getEaster(){
        return core.getRegression().getEaster().getOption();
    }

    public void setEaster(EasterSpec.Type value){
        core.getRegression().getEaster().setOption(value);
    }

    public int getDuration(){
        return core.getRegression().getEaster().getDuration();
    }

    public void setDuration(int value){
        core.getRegression().getEaster().setDuration(value);
    }

    private static final int TD_ID = 1, LP_ID = 2, E_ID = 3, DURATION_ID = 4;

    private EnhancedPropertyDescriptor tdDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("tradingDays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TD_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(TD_NAME);
            desc.setShortDescription(TD_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor lpDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("leapYear", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LP_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(LP_NAME);
            desc.setShortDescription(LP_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor eDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("easter", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, E_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(E_NAME);
            desc.setShortDescription(E_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor durationDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("duration", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DURATION_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(DURATION_NAME);
            desc.setShortDescription(DURATION_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final String
            TD_NAME = "Trading days",
            LP_NAME = "Leap year",
            E_NAME = "Easter",
            DURATION_NAME = "Pre-test";

    private static final String
            TD_DESC = "Trading days effect",
            LP_DESC = "Leap year effect",
            E_DESC = "Easter effect",
            DURATION_DESC = "Test for the presence of calendar effects";
}
