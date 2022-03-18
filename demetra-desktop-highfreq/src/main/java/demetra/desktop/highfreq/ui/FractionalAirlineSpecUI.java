/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.desktop.ui.properties.l2fprod.Holidays;
import demetra.highfreq.FractionalAirlineSpec;
import demetra.timeseries.calendars.HolidaysOption;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean Palate
 */
public class FractionalAirlineSpecUI implements IObjectDescriptor<FractionalAirlineSpec> {

    private FractionalAirlineSpec root;
    private final boolean ro;

    @Override
    public FractionalAirlineSpec getCore() {
        return root;
    }

    public FractionalAirlineSpecUI(FractionalAirlineSpec spec, boolean ro) {
        root = spec;
        this.ro = ro;
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = logDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = meanDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = calendarDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = hoptionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = singleDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = yDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = wDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = arDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = diffDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = toIntDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = aoDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = lsDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = woDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = cvDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = precisionDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = approximateHessianDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    ///////////////////////////////////////////////////////////////////////////
    private static final int LOG_ID = 1, MEAN_ID = 2,
            CALENDAR_ID = 3, HOPTION_ID=4, SINGLE_ID = 5,
            Y_ID = 10, W_ID = 11, DIFF_ID = 12, AR_ID = 13, INT_ID = 14,
            AO_ID = 20, LS_ID = 21, WO_ID = 22, CV_ID = 23,
            PRECISION_ID = 30,
            APPHESSIAN_ID = 31;

    public boolean isLog() {
        return root.isLog();
    }

    public void setLog(boolean log) {
        if (log != root.isLog()) {
            root = root.toBuilder()
                    .log(log)
                    .build();
        }
    }

    private EnhancedPropertyDescriptor logDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Log", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LOG_ID);
            desc.setDisplayName("log");
            desc.setShortDescription("log transformation");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isMean() {
        return root.isMeanCorrection();
    }

    public void setMean(boolean mean) {
        if (mean != root.isMeanCorrection()) {
            root = root.toBuilder()
                    .meanCorrection(mean)
                    .build();
        }
    }

    private EnhancedPropertyDescriptor meanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mean", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MEAN_ID);
            desc.setDisplayName("mean");
            desc.setShortDescription("mean correction");
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public Holidays getHolidays() {
        return new Holidays(root.getCalendar());
    }

    public void setHolidays(Holidays holidays) {
        root = root.toBuilder().calendar(holidays.getName()).build();
    }

    private EnhancedPropertyDescriptor calendarDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("holidays", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CALENDAR_ID);
            desc.setDisplayName("calendar");
            desc.setShortDescription("Calendar");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public HolidaysOption getHolidaysOption() {
        return root.getHolidaysOption();
    }

    public void setHolidaysOption(HolidaysOption option) {
        if (option != root.getHolidaysOption()) {
            root = root.toBuilder()
                    .holidaysOption(option)
                    .build();
        }
    }

    private EnhancedPropertyDescriptor hoptionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("HolidaysOption", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, HOPTION_ID);
            desc.setDisplayName("option");
            desc.setShortDescription("Holiday option");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    public boolean isSingle() {
        return root.isSingle();
    }

    public void setSingle(boolean single) {
        if (single != root.isSingle()) {
            root = root.toBuilder()
                    .single(single)
                    .build();
        }
    }

    private EnhancedPropertyDescriptor singleDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Single", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SINGLE_ID);
            desc.setDisplayName("single");
            desc.setShortDescription("single holiday variable");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isYearly() {
        double[] p = root.getPeriodicities();
        for (int i = 0; i < p.length; ++i) {
            if (p[i] == 365.25) {
                return true;
            }
        }
        return false;
    }

    public boolean isWeekly() {
        double[] p = root.getPeriodicities();
        for (int i = 0; i < p.length; ++i) {
            if (p[i] == 7) {
                return true;
            }
        }
        return false;
    }

    public void setYearly(boolean y) {
        double[] p;
        if (y) {
            if (isWeekly()) {
                p = new double[]{7, 365.25};
            } else {
                p = new double[]{365.25};
            }
        } else {
            if (isWeekly()) {
                p = new double[]{7};
            } else {
                p = FractionalAirlineSpec.NO_PERIOD;
            }
        }
        root = root.toBuilder()
                .periodicities(p)
                .build();
    }

    private EnhancedPropertyDescriptor yDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Yearly", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, Y_ID);
            desc.setDisplayName("yearly");
            desc.setShortDescription("Yearly");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public void setWeekly(boolean w) {
        double[] p;
        if (w) {
            if (isYearly()) {
                p = new double[]{7, 365.25};
            } else {
                p = new double[]{7};
            }
        } else {
            if (isYearly()) {
                p = new double[]{365.25};
            } else {
                p = FractionalAirlineSpec.NO_PERIOD;
            }
        }
        root = root.toBuilder()
                .periodicities(p)
                .build();
    }

    private EnhancedPropertyDescriptor wDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Weekly", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, W_ID);
            desc.setDisplayName("weekly");
            desc.setShortDescription("Weekly");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isAr() {
        return root.isAr();
    }

    public void setAr(boolean ar) {
        if (ar != root.isAr()) {
            root = root.toBuilder()
                    .ar(ar)
                    .build();
        }
    }

    private EnhancedPropertyDescriptor arDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ar", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AR_ID);
            desc.setDisplayName("ar");
            desc.setShortDescription("Auto-regressive parameter");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public int getDifferencing() {
        return root.getDifferencingOrder();
    }

    public void setDifferencing(int diff) {
        if (diff != root.getDifferencingOrder()) {
            root = root.toBuilder()
                    .differencingOrder(diff)
                    .build();
        }
    }

    private EnhancedPropertyDescriptor diffDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Differencing", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DIFF_ID);
            desc.setDisplayName("differencing");
            desc.setShortDescription("Differencing order (-1 to default differencing) ");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }
    
    private boolean isOutlier(String code) {
        String[] outliers = root.getOutliers();
        for (int i = 0; i < outliers.length; ++i) {
            if (outliers[i].equals(code)) {
                return true;
            }
        }
        return false;
    }

    // code is an outlier. Should have been checked
    private String[] removeOutlier(String code) {
        String[] outliers = root.getOutliers();
        String[] noutliers;
        if (outliers.length == 1) {
            noutliers = FractionalAirlineSpec.NO_OUTLIER;
        } else {
            noutliers = new String[outliers.length - 1];
            for (int i = 0, j = 0; i < outliers.length; ++i) {
                if (!outliers[i].equals(code)) {
                    noutliers[j++] = outliers[i];
                }
            }
        }
        return noutliers;
    }

    private String[] addOutlier(String code) {
        String[] outliers = root.getOutliers();
        String[] noutliers = new String[outliers.length + 1];
        int i = 0;
        while (i < outliers.length) {
            noutliers[i] = outliers[i];
            ++i;
        }
        noutliers[i] = code;
        return noutliers;
    }

    public boolean isAO() {
        root.getOutliers();
        return isOutlier("AO");
    }

    public void setAO(boolean ao) {
        if (ao != isOutlier("AO")) {
            if (ao) {
                root = root.toBuilder().outliers(addOutlier("AO")).build();
            } else {
                root = root.toBuilder().outliers(removeOutlier("AO")).build();
            }
        }
    }

    private EnhancedPropertyDescriptor aoDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("AO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AO_ID);
            desc.setDisplayName("ao");
            desc.setShortDescription("Additive outliers");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isLS() {
        root.getOutliers();
        return isOutlier("LS");
    }

    public void setLS(boolean ls) {
        if (ls != isOutlier("LS")) {
            if (ls) {
                root = root.toBuilder().outliers(addOutlier("LS")).build();
            } else {
                root = root.toBuilder().outliers(removeOutlier("LS")).build();
            }
        }
    }

    private EnhancedPropertyDescriptor lsDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("LS", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, LS_ID);
            desc.setDisplayName("ls");
            desc.setShortDescription("Level shifts");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isWO() {
        root.getOutliers();
        return isOutlier("WO");
    }

    public void setWO(boolean wo) {
        if (wo != isOutlier("WO")) {
            if (wo) {
                root = root.toBuilder().outliers(addOutlier("WO")).build();
            } else {
                root = root.toBuilder().outliers(removeOutlier("WO")).build();
            }
        }
    }

    private EnhancedPropertyDescriptor woDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("WO", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, WO_ID);
            desc.setDisplayName("wo");
            desc.setShortDescription("Switch outliers");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public double getCv() {
        return root.getCriticalValue();
    }

    public void setCv(double cv) {
        if (cv != root.getCriticalValue()) {
            root = root.toBuilder()
                    .criticalValue(cv)
                    .build();
        }
    }

    private EnhancedPropertyDescriptor cvDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Cv", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CV_ID);
            desc.setDisplayName("cv");
            desc.setShortDescription("Critical value for outliers detection");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isToInt() {
        return root.isAdjustToInt();
    }

    public void setToInt(boolean toInt) {
        if (toInt != root.isAdjustToInt()) {
            root = root.toBuilder()
                    .adjustToInt(toInt)
                    .build();
        }
    }

    private EnhancedPropertyDescriptor toIntDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ToInt", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, INT_ID);
            desc.setDisplayName("to int");
            desc.setShortDescription("Adjust periods to int values");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public double getPrecision() {
        return root.getPrecision();
    }

    public void setPrecision(double eps) {
        if (eps != root.getPrecision()) {
            root = root.toBuilder()
                    .precision(eps)
                    .build();
        }
    }

    private EnhancedPropertyDescriptor precisionDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Precision", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PRECISION_ID);
            desc.setDisplayName("precision");
            desc.setShortDescription("Precision in the likelihood optimization");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isApproximateHessian() {
        return root.isApproximateHessian();
    }

    public void setApproximateHessian(boolean ah) {
        if (ah != root.isApproximateHessian()) {
            root = root.toBuilder()
                    .approximateHessian(ah)
                    .build();
        }
    }

    private EnhancedPropertyDescriptor approximateHessianDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ApproximateHessian", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, APPHESSIAN_ID);
            desc.setDisplayName("approximate hessian");
            desc.setShortDescription("Use approximate hessian to comput stderr of the parameters");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(ro);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "fractional airline specification";
    }
}
