/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.highfreq.DecompositionSpec;
import demetra.highfreq.ExtendedAirlineSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class DecompositionSpecUI extends BaseFractionalAirlineDecompositionSpecUI {

    @Override
    public String toString() {
        return "";
    }

    private DecompositionSpec inner() {
        return this.root.getDecomposition();
    }

    public DecompositionSpecUI(FractionalAirlineDecompositionSpecRoot root) {
        super(root);
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
        return inner().isBiasCorrection();
    }

    public void setBias(boolean bias) {
        if (bias != inner().isBiasCorrection()) {
            update(inner().toBuilder()
                    .biasCorrection(bias)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor biasDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Bias", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, BIAS_ID);
            desc.setDisplayName("bias");
            desc.setShortDescription("bias correction");
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isIterative() {
        return inner().isIterative();
    }

    public void setIterative(boolean iter) {
        if (iter != inner().isIterative()) {
            update(inner().toBuilder()
                    .biasCorrection(iter)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor iterativeDesc() {
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

     public boolean isYearly() {
        double[] p = inner().getPeriodicities();
        for (int i = 0; i < p.length; ++i) {
            if (p[i] == 365.25) {
                return true;
            }
        }
        return false;
    }

    public boolean isWeekly() {
        double[] p = inner().getPeriodicities();
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
                p = ExtendedAirlineSpec.NO_PERIOD;
            }
        }
        update(inner().toBuilder()
                .periodicities(p)
                .build());
    }

    private EnhancedPropertyDescriptor yDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Yearly", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, Y_ID);
            desc.setDisplayName("yearly");
            desc.setShortDescription("Yearly");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
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
                p = ExtendedAirlineSpec.NO_PERIOD;
            }
        }
        update(inner().toBuilder()
                .periodicities(p)
                .build());
    }

    private EnhancedPropertyDescriptor wDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Weekly", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, W_ID);
            desc.setDisplayName("weekly");
            desc.setShortDescription("Weekly");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isStdev() {
        return inner().isStdev();
    }

    public void setStdev(boolean stdev) {
        if (stdev != inner().isStdev()) {
            update(inner().toBuilder()
                    .stdev(stdev)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor stdevDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Stdev", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, STDEV_ID);
            desc.setDisplayName("stdev");
            desc.setShortDescription("Compute stdev");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public int getForecastsLength() {
        return inner().getForecastsCount();
    }

    public void setForecastsLength(int value) {
        update(inner().toBuilder().forecastsCount(value).build());
    }

    public int getBackcastsLength() {
        return inner().getBackcastsCount();
    }

    public void setBackcastsLength(int value) {
        update(inner().toBuilder().backcastsCount(value).build());
    }

   
    public boolean isToInt() {
        return inner().isAdjustToInt();
    }

    public void setToInt(boolean toInt) {
        if (toInt != inner().isAdjustToInt()) {
            update(inner().toBuilder()
                    .adjustToInt(toInt)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor toIntDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ToInt", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, INT_ID);
            desc.setDisplayName("to int");
            desc.setShortDescription("Adjust periods to int values");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
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
        try {
            PropertyDescriptor desc = new PropertyDescriptor("BackcastsLength", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NB_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_nbcastsDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_nbcastsDesc_desc());
            edesc.setReadOnly(isRo());
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
        try {
            PropertyDescriptor desc = new PropertyDescriptor("ForecastsLength", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, NF_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.decompositionSpecUI_nfcastsDesc_name());
            desc.setShortDescription(Bundle.decompositionSpecUI_nfcastsDesc_desc());
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final int BIAS_ID = 2, ITERATIVE_ID=3, NF_ID=5, NB_ID=6,
            Y_ID = 10, W_ID = 11, STDEV_ID = 13, INT_ID = 14;
    
    @Override
    @NbBundle.Messages("decompositionSpecUI.getDisplayName=Outliers")
    public String getDisplayName() {
        return Bundle.decompositionSpecUI_getDisplayName();
    }
    
}
