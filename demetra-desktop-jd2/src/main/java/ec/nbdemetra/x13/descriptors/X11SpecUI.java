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
import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle.Messages;

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
        desc = backcastDesc();
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
        desc = sigmavecDesc();
        if (desc != null) {
            descs.add(desc);
        }

        desc = excludefcstDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @Messages("x11SpecUI.getDisplayName=X11")
    @Override
    public String getDisplayName() {
        return Bundle.x11SpecUI_getDisplayName();
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

    public int getBackcastHorizon() {
        return core.getBackcastHorizon();
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

    public void setBackcastHorizon(int value) {
        core.setBackcastHorizon(value);
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
        if (calendarsigma.Select == CalendarSigma.Select && core.getSigmavec() == null) {
            this.setSigmavec(this.getSigmavec());
        };
    }

    public SigmavecOption[] getSigmavec() {
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

    public void setSigmavec(SigmavecOption[] sigmavec) {
        core.setSigmavec(sigmavec);
    }


    public void setExcludefcst(boolean value) {
        core.setExcludefcst(value);
    }

    public boolean isExcludefcst() {
        return core.isExcludefcst();
    }

    private static final int MODE_ID = 0, SEAS_ID = 1, FORECAST_ID = 2, BACKCAST_ID = 12, LSIGMA_ID = 3, USIGMA_ID = 4, AUTOTREND_ID = 5,
            TREND_ID = 6, SEASONMA_ID = 7, FULLSEASONMA_ID = 8, CALENDARSIGMA_ID = 9, SIGMAVEC_ID = 10, EXCLUDEFCST_ID = 11;

    @Messages({
        "x11SpecUI.calendarsigmaDesc.name=Calendarsigma",
        "x11SpecUI.calendarsigmaDesc.desc=[calendarsigma] Specifies if the standard errors used for extreme value detection and adjustment are computed separately for each calendar month (quarter), or separately for two complementary sets of calendar months (quarters)."
    })
    private EnhancedPropertyDescriptor calendarsigmaDesc() {
        if (!core.isSeasonal()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("CalendarSigma", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CALENDARSIGMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.x11SpecUI_calendarsigmaDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_calendarsigmaDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.excludefcstDesc.name=Excludeforecast",
        "x11SpecUI.excludefcstDesc.desc=[excludefcst] If excludefcst=yes, forecasts and backcasts from the regARIMA model are not used in the generation of extreme values in the seasonal adjustment routines. The default is excludefcst=no, which allows the full forecast and backcast extended series to be used in the extreme value process."
    })
    private EnhancedPropertyDescriptor excludefcstDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("excludefcst", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, EXCLUDEFCST_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.x11SpecUI_excludefcstDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_excludefcstDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.sigmavecDesc.name=Sigma Vector",
        "x11SpecUI.sigmavecDesc.desc=[sigmavec] Specifies the two groups of periods (month or quarters) for whose irregulars a group standard error will be calculated under the calendarsigma=select option."
    })
    private EnhancedPropertyDescriptor sigmavecDesc() {
        if (!core.isSeasonal() || !core.getCalendarSigma().equals(CalendarSigma.Select)) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Sigmavec", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SIGMAVEC_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.x11SpecUI_sigmavecDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_sigmavecDesc_desc());
            edesc.setReadOnly(ro_ || freq_ == TsFrequency.Undefined);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.modeDesc.name=Mode",
        "x11SpecUI.modeDesc.desc=[mode] Decomposition mode. Could be changed by the program, if needed."
    })
    private EnhancedPropertyDescriptor modeDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mode", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MODE_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.x11SpecUI_modeDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_modeDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.seasDesc.name=Seasonal component",
        "x11SpecUI.seasDesc.desc=Computes a seasonal component (true) or set it to 0 (additive decomposition) or 1 (multiplicative decomposition) (false)"
    })
    private EnhancedPropertyDescriptor seasDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Seasonal", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SEAS_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.x11SpecUI_seasDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_seasDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.forecastDesc.name=Forecasts horizon",
        "x11SpecUI.forecastDesc.desc=[forecast(maxlead)] Length of the forecasts generated by ARIMA. Negative figures are translated in years of forecasts"
    })
    private EnhancedPropertyDescriptor forecastDesc() {
        try {
            if (!x13_) {
                return null;
            }
            PropertyDescriptor desc = new PropertyDescriptor("ForecastHorizon", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FORECAST_ID);
            desc.setDisplayName(Bundle.x11SpecUI_forecastDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_forecastDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.backcastDesc.name=Backcasts horizon",
        "x11SpecUI.backcastDesc.desc=[backcast(maxback)] Length of the backcasts used in X11. Negative figures are translated in years of backcasts"
    })
    private EnhancedPropertyDescriptor backcastDesc() {
        try {
            if (!x13_) {
                return null;
            }
            PropertyDescriptor desc = new PropertyDescriptor("BackcastHorizon", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BACKCAST_ID);
            desc.setDisplayName(Bundle.x11SpecUI_backcastDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_backcastDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.lsigmaDesc.name=LSigma",
        "x11SpecUI.lsigmaDesc.desc=[sigmalim] Lower sigma boundary for the detection of extreme values."
    })
    private EnhancedPropertyDescriptor lsigmaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LSigma", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LSIGMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.x11SpecUI_lsigmaDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_lsigmaDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.usigmaDesc.name=USigma",
        "x11SpecUI.usigmaDesc.desc=[sigmalim] Upper sigma boundary for the detection of extreme values."
    })
    private EnhancedPropertyDescriptor usigmaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("USigma", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, USIGMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.x11SpecUI_usigmaDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_usigmaDesc_desc());
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

    @Messages({
        "x11SpecUI.seasonmaDesc.name=Seasonal filter",
        "x11SpecUI.seasonmaDesc.desc=[seasonalma] Specifies which seasonal moving average (also called seasonal filter) will be used to estimate the seasonal factors."
    })
    private EnhancedPropertyDescriptor seasonmaDesc() {
        if (!core.isSeasonal() || hasSeasDetails()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SeasonalMA", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SEASONMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.x11SpecUI_seasonmaDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_seasonmaDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.fullseasonmaDesc.name=Details on seasonal filters",
        "x11SpecUI.fullseasonmaDesc.desc=[seasonalma] Details on specifc seasonalma for the different periods."
    })
    private EnhancedPropertyDescriptor fullseasonmaDesc() {
        if (!core.isSeasonal()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("FullSeasonalMA", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, FULLSEASONMA_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.x11SpecUI_fullseasonmaDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_fullseasonmaDesc_desc());
            edesc.setReadOnly(ro_ || freq_ == TsFrequency.Undefined);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.autotrendmaDesc.name=Automatic henderson filter",
        "x11SpecUI.autotrendmaDesc.desc=[trendma] The length of the henderson filter used in the estimation of the trend is detected automatically by the program."
    })
    private EnhancedPropertyDescriptor autotrendmaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AutoTrendMA", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AUTOTREND_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.x11SpecUI_autotrendmaDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_autotrendmaDesc_desc());
            edesc.setReadOnly(ro_);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages({
        "x11SpecUI.trendmaDesc.name=Henderson filter",
        "x11SpecUI.trendmaDesc.desc=[trendma] Length of the henderson filter used in the estimation of the trend. Should be an odd number in the range [1, 101]."
    })
    private EnhancedPropertyDescriptor trendmaDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TrendMA", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TREND_ID);
            edesc.setReadOnly(core.isAutoHenderson() || ro_);
            desc.setDisplayName(Bundle.x11SpecUI_trendmaDesc_name());
            desc.setShortDescription(Bundle.x11SpecUI_trendmaDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
}
