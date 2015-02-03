/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.x13.descriptors;

import ec.satoolkit.DecompositionMode;
import ec.satoolkit.x11.CalendarSigma;
import ec.satoolkit.x11.SeasonalFilterOption;
import ec.satoolkit.x11.SigmavecOption;
import ec.satoolkit.x11.X11Exception;
import ec.satoolkit.x11.X11Specification;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kristof Bayens
 */
public class X11SpecUI extends BaseX11SpecUI {

    private final TsFrequency freq_;
    private final boolean x13_;

    public X11SpecUI(X11Specification spec, TsFrequency freq, boolean x13, boolean ro) {
        super(spec, ro);
        freq_ = freq;
        x13_ = x13;
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
     
        EnhancedPropertyDescriptor desc = modeDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = seasDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = forecastDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lsigmaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = usigmaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = seasonmaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = fullseasonmaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = autotrendmaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = trendmaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = calendarsigmaDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc=sigmavecDesc();
          if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Override
    public String getDisplayName() {
        return "X11";
    }

    public DecompositionMode getMode() {
        return core.getMode();
    }

    public void setMode(DecompositionMode value) {
        core.setMode(value);
    }

//    public boolean isUseForecast() {
//        return core.getForecastHorizon() != 0;
//    }
    public int getForecastHorizon() {
        return core.getForecastHorizon();
    }

    public boolean isSeasonal() {
        return core.isSeasonal();
    }

    public void setSeasonal(boolean value) {
        core.setSeasonal(value);
    }

    public void setForecastHorizon(int value) {
        core.setForecastHorizon(value);
    }

//    public void setUseForecast(boolean value) {
//        if (value) {
//            core.setForecastHorizon(-1);
//        } else {
//            core.setForecastHorizon(0);
//        }
//    }
    public double getLSigma() {
        return core.getLowerSigma();
    }

    public void setLSigma(double value) {
        core.setLowerSigma(value);
    }

    public double getUSigma() {
        return core.getUpperSigma();
    }

    public void setUSigma(double value) {
        core.setUpperSigma(value);
    }

    public SeasonalFilterOption getSeasonalMA() {
//        if (hasSeasDetails()) {
//            return null;
//        }
//        else 
        if (core.getSeasonalFilters() == null) {
            return SeasonalFilterOption.Msr;
        } else {
            return core.getSeasonalFilters()[0];
        }
    }

    public void setSeasonalMA(SeasonalFilterOption value) {
        core.setSeasonalFilter(value);
    }

    public SeasonalFilterOption[] getFullSeasonalMA() {
        SeasonalFilterOption[] filters = core.getSeasonalFilters();
        int len = freq_.intValue();
        if (filters != null && filters.length == len) {
            return filters;
        }
        SeasonalFilterOption option = filters == null ? SeasonalFilterOption.Msr : filters[0];
        filters = new SeasonalFilterOption[len];
        for (int i = 0; i < len; ++i) {
            filters[i] = option;
        }
        return filters;

    }

    public void setFullSeasonalMA(SeasonalFilterOption[] value) {
        core.setSeasonalFilters(value);
    }

    public boolean isAutoTrendMA() {
        return core.isAutoHenderson();
    }

    public void setAutoTrendMA(boolean value) {
        if (value) {
            core.setHendersonFilterLength(0);
        } else {
            core.setHendersonFilterLength(13);
        }
    }

    public int getTrendMA() {
        return core.getHendersonFilterLength() == 0 ? 13 : core.getHendersonFilterLength();
    }

    public void setTrendMA(int value) {
        if (value <= 1 || value > 101 || value % 2 == 0) {
            throw new X11Exception("Invalid value for henderson filter");
        } else {
            core.setHendersonFilterLength(value);
        }
    }

    public CalendarSigma getCalendarSigma() {
        return core.getCalendarSigma();
    }
    public void setCalendarSigma(CalendarSigma calendarsigma) {
        core.setCalendarSigma(calendarsigma);
    }

   public SigmavecOption[] getSigmavec(){
      SigmavecOption[] groups = core.getSigmavec();
        int len = freq_.intValue();
        if (groups != null && groups.length == len) {
            return groups;
        }
        //Sigmavec option = groups == null ? Sigmavec.group1 : groups[0];
     //   Sigmavec option = Sigmavec.group1;
        groups = new SigmavecOption[len];
        for (int i = 0; i < len; ++i) {
            groups[i] = SigmavecOption.Group1;
        }
        return groups;

   }
   
   public void setSigmavec(SigmavecOption[] sigmavec){
       core.setSigmavec(sigmavec);
   }
   
    private static final int MODE_ID = 0, SEAS_ID = 1, FORECAST_ID = 2, LSIGMA_ID = 3, USIGMA_ID = 4, AUTOTREND_ID = 5,
            TREND_ID = 6, SEASONMA_ID = 7, FULLSEASONMA_ID = 8, CALENDARSIGMA_ID = 9, SIGMAVEC_ID=10;

        private EnhancedPropertyDescriptor calendarsigmaDesc() {
        if (!core.isSeasonal()) {
            return null;
        }
            try {
            PropertyDescriptor desc = new PropertyDescriptor("CalendarSigma", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CALENDARSIGMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(CALENDARSIGMA_NAME);
            desc.setShortDescription(CALENDARSIGMA_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    private EnhancedPropertyDescriptor sigmavecDesc() {
       if (!core.isSeasonal()) {
            return null;
        }
       if(!core.getCalendarSigma().equals(CalendarSigma.None)) {
          return null;
       }
       
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Sigmavec", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SIGMAVEC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(SIGMAVEC_NAME);
            desc.setShortDescription(SIGMAVEC_DESC);
            edesc.setReadOnly(ro_|| freq_ == TsFrequency.Undefined);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor modeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mode", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MODE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(MODE_NAME);
            desc.setShortDescription(MODE_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor seasDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Seasonal", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SEAS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(SEAS_NAME);
            desc.setShortDescription(SEAS_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor forecastDesc() {
        try {
            if (!x13_) {
                return null;
            }
            PropertyDescriptor desc = new PropertyDescriptor("ForecastHorizon", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FORECAST_ID);
            desc.setDisplayName(FORECAST_NAME);
            desc.setShortDescription(FORECAST_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor lsigmaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LSigma", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LSIGMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(LSIGMA_NAME);
            desc.setShortDescription(LSIGMA_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor usigmaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("USigma", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, USIGMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(USIGMA_NAME);
            desc.setShortDescription(USIGMA_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private boolean hasSeasDetails() {
        SeasonalFilterOption[] s = core.getSeasonalFilters();
        if (s == null
                || s.length <= 1) {
            return false;
        }
        SeasonalFilterOption opt = s[0];
        for (int i = 1; i < s.length; ++i) {
            if (s[i] != opt) {
                return true;
            }
        }
        return false;
    }

    private EnhancedPropertyDescriptor seasonmaDesc() {
        if (!core.isSeasonal() || hasSeasDetails()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SeasonalMA", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SEASONMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(SEASONMA_NAME);
            desc.setShortDescription(SEASONMA_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor fullseasonmaDesc() {
        if (!core.isSeasonal()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("FullSeasonalMA", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FULLSEASONMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(FULLSEASONMA_NAME);
            desc.setShortDescription(FULLSEASONMA_DESC);
            edesc.setReadOnly(ro_ || freq_ == TsFrequency.Undefined);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor autotrendmaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AutoTrendMA", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTOTREND_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(AUTOTREND_NAME);
            desc.setShortDescription(AUTOTREND_DESC);
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor trendmaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TrendMA", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TREND_ID);
            edesc.setReadOnly(core.isAutoHenderson() || ro_);
            desc.setDisplayName(TREND_NAME);
            desc.setShortDescription(TREND_DESC);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    private static final String MODE_NAME = "Mode",
            SEAS_NAME = "Seasonal component",
            FORECAST_NAME = "Forecasts horizon",
            LSIGMA_NAME = "LSigma",
            USIGMA_NAME = "USigma",
            AUTOTREND_NAME = "Automatic henderson filter",
            TREND_NAME = "Henderson filter",
            SEASONMA_NAME = "Seasonal filter",
            FULLSEASONMA_NAME = "Details on seasonal filters",
            TRUE7TERM_NAME = "True 7 term",
            CALENDARSIGMA_NAME = "Calendarsigma",
            SIGMAVEC_NAME = "Sigma Vector";
    private static final String MODE_DESC = "[mode] Decomposition mode. Could be changed by the program, if needed.",
            SEAS_DESC = "Computes a seasonal component (true) or set it to 0 (additive decomposition) or 1 (multiplicative decomposition) (false)",
            FORECAST_DESC = "[forecast(maxlead)] Length of the forecasts generated by ARIMA. Negative figures are translated in years of forecasts",
            LSIGMA_DESC = "[sigmalim] Lower sigma boundary for the detection of extreme values.",
            USIGMA_DESC = "[sigmalim] Upper sigma boundary for the detection of extreme values.",
            AUTOTREND_DESC = "[trendma] The length of the henderson filter used in the estimation of the trend is detected automatically by the program.",
            TREND_DESC = "[trendma] Length of the henderson filter used in the estimation of the trend. Should be an odd number in the range [1, 101].",
            SEASONMA_DESC = "[seasonalma] Specifies which seasonal moving average (also called seasonal filter) will be used to estimate the seasonal factors.",
            FULLSEASONMA_DESC = "[seasonalma] Details on specifc seasonalma for the different periods.",
            TRUE7TERM_DESC = "[true7term] Specifies the end weights used for the seven term Henderson filter.",
            CALENDARSIGMA_DESC = "[calendarsigma] Specifies if the standard errors used for extreme value detection and adjustment are computed separately for each calendar month (quarter), or separately for two complementary sets of calendar months (quarters).",
            SIGMAVEC_DESC = "[sigmavec] Specifies the two groups of periods (month or quarters) for whose irregulars a group standard error will be calculated under the calendarsigma=select option.";
}
