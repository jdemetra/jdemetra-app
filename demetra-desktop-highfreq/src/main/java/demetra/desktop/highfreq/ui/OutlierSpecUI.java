/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.descriptors.DateSelectorUI;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.highfreq.OutlierSpec;
import demetra.timeseries.TimeSelector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class OutlierSpecUI extends BaseFractionalAirlineSpecUI {

    @Override
    public String toString() {
        return "";
    }

    private OutlierSpec inner() {
        return core().getOutlier();
    }

    public OutlierSpecUI(FractionalAirlineSpecRoot root) {
        super(root);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        // regression
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = spanDesc();
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
        return descs;
    }

    public DateSelectorUI getSpan() {
        return new DateSelectorUI(inner().getSpan(), isRo(), selector -> updateSpan(selector));
    }

    public void updateSpan(TimeSelector span) {
        update(inner().toBuilder().span(span).build());
    }

    @NbBundle.Messages({
        "outliersSpecUI.spanDesc.name=Detection span",
        "outliersSpecUI.spanDesc.desc=[int1, int2] Time span used for the automatic outliers detection. Encompasses the int1 and int2 parameters."
    })
    private EnhancedPropertyDescriptor spanDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("span", this.getClass(), "getSpan", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SPAN_ID);
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            desc.setDisplayName(Bundle.outliersSpecUI_spanDesc_name());
            desc.setShortDescription(Bundle.outliersSpecUI_spanDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private boolean isOutlier(String code) {
        String[] outliers = inner().getOutliers();
        for (int i = 0; i < outliers.length; ++i) {
            if (outliers[i].equals(code)) {
                return true;
            }
        }
        return false;
    }

    // code is an outlier. Should have been checked
    private String[] removeOutlier(String code) {
        String[] outliers = inner().getOutliers();
        String[] noutliers;
        if (outliers.length == 1) {
            noutliers = OutlierSpec.NO_OUTLIER;
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
        String[] outliers = inner().getOutliers();
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
        inner().getOutliers();
        return isOutlier("AO");
    }

    public void setAO(boolean ao) {
        if (ao != isOutlier("AO")) {
            if (ao) {
                update(inner().toBuilder().outliers(addOutlier("AO")).build());
            } else {
                update(inner().toBuilder().outliers(removeOutlier("AO")).build());
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
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isLS() {
        inner().getOutliers();
        return isOutlier("LS");
    }

    public void setLS(boolean ls) {
        if (ls != isOutlier("LS")) {
            if (ls) {
                update(inner().toBuilder().outliers(addOutlier("LS")).build());
            } else {
                update(inner().toBuilder().outliers(removeOutlier("LS")).build());
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
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isWO() {
        inner().getOutliers();
        return isOutlier("WO");
    }

    public void setWO(boolean wo) {
        if (wo != isOutlier("WO")) {
            if (wo) {
                update(inner().toBuilder().outliers(addOutlier("WO")).build());
            } else {
                update(inner().toBuilder().outliers(removeOutlier("WO")).build());
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
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public double getCv() {
        return inner().getCriticalValue();
    }

    public void setCv(double cv) {
        if (cv != inner().getCriticalValue()) {
            update(inner().toBuilder()
                    .criticalValue(cv)
                    .build());
        }
    }

    private EnhancedPropertyDescriptor cvDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Cv", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, CV_ID);
            desc.setDisplayName("cv");
            desc.setShortDescription("Critical value for outliers detection");
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            edesc.setReadOnly(isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final int SPAN_ID = 10, AO_ID = 20, LS_ID = 21, WO_ID = 22, CV_ID = 23;

    @Override
    @NbBundle.Messages("outlierSpecUI.getDisplayName=Outliers")
    public String getDisplayName() {
        return Bundle.outlierSpecUI_getDisplayName();
    }

}
