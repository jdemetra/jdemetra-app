/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.data.Parameter;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
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
public class StochasticSpecUI extends BaseFractionalAirlineSpecUI {

    @Override
    public String toString() {
        return "";
    }

    private ExtendedAirlineSpec inner() {
        return core().getStochastic();
    }

    public StochasticSpecUI(FractionalAirlineSpecRoot root) {
        super(root);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = meanDesc();
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
        desc = diffDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = arDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
    
    public boolean isMean() {
        return inner().isMean();
    }

    public void setMean(boolean mean) {
        if (mean != inner().isMean()) {
            update(inner().toBuilder()
                    .mean(mean)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor meanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Mean", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, MEAN_ID);
            desc.setDisplayName("mean");
            desc.setShortDescription("mean correction");
            edesc.setReadOnly(isRo());
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

    public boolean isAr() {
        return inner().hasAr();
    }

    public void setAr(boolean ar) {
        if (ar != inner().hasAr()) {
            update(inner().toBuilder()
                    .phi(ar ? Parameter.undefined() : null )
                    .theta(ar ? null : Parameter.undefined())
                    .build());
        }
    }

    private EnhancedPropertyDescriptor arDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Ar", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, AR_ID);
            desc.setDisplayName("ar");
            desc.setShortDescription("Auto-regressive parameter");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public int getDifferencing() {
        return inner().getDifferencingOrder();
    }

    public void setDifferencing(int diff) {
        if (diff != inner().getDifferencingOrder()) {
            update(inner().toBuilder()
                    .differencingOrder(diff)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor diffDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Differencing", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, DIFF_ID);
            desc.setDisplayName("differencing");
            desc.setShortDescription("Differencing order (-1 to default differencing) ");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
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

    private static final int MEAN_ID = 2,
            Y_ID = 10, W_ID = 11, DIFF_ID = 12, AR_ID = 13, INT_ID = 14;
    
    @Override
    @NbBundle.Messages("stochasticSpecUI.getDisplayName=Outliers")
    public String getDisplayName() {
        return Bundle.stochasticSpecUI_getDisplayName();
    }
    
}
