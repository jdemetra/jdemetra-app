/*
 * Copyright 2023 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.highfreq.DecompositionSpec;
import demetra.highfreq.ExtendedAirlineSpec;
import demetra.math.Constants;
import demetra.timeseries.TsUnit;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class DecompositionSpecUI implements IPropertyDescriptors {

    @Override
    public String toString() {
        return "";
    }

    private final ExtendedAirlineDecompositionSpecRoot root;

    private DecompositionSpec spec() {
        return this.root.getDecomposition();
    }

    public DecompositionSpecUI(ExtendedAirlineDecompositionSpecRoot root) {
        this.root = root;
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = iterativeDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = nfcastsDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = nbcastsDesc();
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
        desc = toIntDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = biasDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = stdevDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    public boolean isBias() {
        return spec().isBiasCorrection();
    }

    public void setBias(boolean bias) {
        if (bias != spec().isBiasCorrection()) {
            root.update(spec().toBuilder()
                    .biasCorrection(bias)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor biasDesc() {
        if (spec() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Bias", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BIAS_ID);
            desc.setDisplayName("bias");
            desc.setShortDescription("bias correction");
            edesc.setReadOnly(root.isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isIterative() {
        return spec().isIterative();
    }

    public void setIterative(boolean iter) {
        if (iter != spec().isIterative()) {
            root.update(spec().toBuilder()
                    .biasCorrection(iter)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor iterativeDesc() {
        if (spec() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Iterative", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ITERATIVE_ID);
            desc.setDisplayName("iterative");
            desc.setShortDescription("iterative processing");
            edesc.setReadOnly(true); // TODO
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final double P_DAY = 365.25, P_WEEK = P_DAY / 7;

    private double annualPeriod() {
        TsUnit period = root.getCore().getPreprocessing().getPeriod();
        int ip = period.getAnnualFrequency();
        if (ip > 0) {
            return ip;
        }
        if (period.equals(TsUnit.WEEK)) {
            return P_WEEK;
        } else if (period.equals(TsUnit.DAY)) {
            return P_DAY;
        } else {
            return 0;
        }
    }

    private double weeklyPeriod() {
        TsUnit period = root.getCore().getPreprocessing().getPeriod();
        if (period.equals(TsUnit.DAY)) {
            return 7;
        } else {
            return 0;
        }
    }

    public boolean isYearly() {
        double ap = annualPeriod();
        if (ap <= 0) {
            return false;
        }
        double[] p = spec().getPeriodicities();
        for (int i = 0; i < p.length; ++i) {
            if (p[i] == ap) {
                return true;
            }
        }
        return false;
    }

    public boolean isWeekly() {
        double wp = weeklyPeriod();
        if (wp <= 0) {
            return false;
        }
        double[] p = spec().getPeriodicities();
        for (int i = 0; i < p.length; ++i) {
            if (p[i] == wp) {
                return true;
            }
        }
        return false;
    }

    public void setYearly(boolean y) {
        double ap = annualPeriod(), wp = weeklyPeriod();
        double[] p;
        if (y) {
            if (isWeekly()) {
                p = new double[]{wp, ap};
            } else {
                p = new double[]{ap};
            }
        } else {
            if (isWeekly()) {
                p = new double[]{wp};
            } else {
                p = ExtendedAirlineSpec.NO_PERIOD;
            }
        }
        root.update(spec().toBuilder()
                .periodicities(p)
                .build());
    }

    public void setWeekly(boolean w) {
        double ap = annualPeriod(), wp = weeklyPeriod();
        double[] p;
        if (w) {
            if (isYearly()) {
                p = new double[]{wp, ap};
            } else {
                p = new double[]{wp};
            }
        } else {
            if (isYearly()) {
                p = new double[]{ap};
            } else {
                p = ExtendedAirlineSpec.NO_PERIOD;
            }
        }
        root.update(spec().toBuilder()
                .periodicities(p)
                .build());
    }

    public boolean hasFractionalPeriod() {
        double[] p = spec().getPeriodicities();
        for (int i = 0; i < p.length; ++i) {
            long cur = Math.round(p[i]);
            if (Math.abs(cur - p[i]) > Constants.getEpsilon()) {
                return true;
            }
        }
        return false;
    }

    private EnhancedPropertyDescriptor yDesc() {
        if (spec() == null || annualPeriod() <= 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Yearly", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, Y_ID);
            desc.setDisplayName("yearly");
            desc.setShortDescription("Yearly");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root.isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private EnhancedPropertyDescriptor wDesc() {
        if (spec() == null || weeklyPeriod() <= 0) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Weekly", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, W_ID);
            desc.setDisplayName("weekly");
            desc.setShortDescription("Weekly");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root.isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isStdev() {
        return spec().isStdev();
    }

    public void setStdev(boolean stdev) {
        if (stdev != spec().isStdev()) {
            root.update(spec().toBuilder()
                    .stdev(stdev)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor stdevDesc() {
        if (spec() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Stdev", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, STDEV_ID);
            desc.setDisplayName("stdev");
            desc.setShortDescription("Compute stdev");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root.isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public int getForecastsLength() {
        return spec().getForecastsCount();
    }

    public void setForecastsLength(int value) {
        root.update(spec().toBuilder().forecastsCount(value).build());
    }

    public int getBackcastsLength() {
        return spec().getBackcastsCount();
    }

    public void setBackcastsLength(int value) {
        root.update(spec().toBuilder().backcastsCount(value).build());
    }

    public boolean isToInt() {
        return spec().isAdjustToInt();
    }

    public void setToInt(boolean toInt) {
        if (toInt != spec().isAdjustToInt()) {
            root.update(spec().toBuilder()
                    .adjustToInt(toInt)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor toIntDesc() {
        if (spec() == null || !hasFractionalPeriod()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ToInt", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, INT_ID);
            desc.setDisplayName("to int");
            desc.setShortDescription("Adjust periods to int values");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(root.isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "decompositionSpecUI.nbcastsDesc.name=Backcasts length",
        "decompositionSpecUI.nbcastsDesc.desc=[npred] Number of backcasts used in the decomposition."
    })
    private EnhancedPropertyDescriptor nbcastsDesc() {
        if (spec() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BackcastsLength", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NB_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_nbcastsDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_nbcastsDesc_desc());
            edesc.setReadOnly(root.isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "decompositionSpecUI.nfcastsDesc.name=Forecasts length",
        "decompositionSpecUI.nfcastsDesc.desc=[npred] Number of forecasts used in the decomposition."
    })
    private EnhancedPropertyDescriptor nfcastsDesc() {
        if (spec() == null) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ForecastsLength", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NF_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_nfcastsDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_nfcastsDesc_desc());
            edesc.setReadOnly(root.isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final int BIAS_ID = 2, ITERATIVE_ID = 3, NF_ID = 5, NB_ID = 6,
            Y_ID = 10, W_ID = 11, STDEV_ID = 13, INT_ID = 14;

    @Override
    @NbBundle.Messages("decompositionSpecUI.getDisplayName=Outliers")
    public String getDisplayName() {
        return Bundle.decompositionSpecUI_getDisplayName();
    }

}
