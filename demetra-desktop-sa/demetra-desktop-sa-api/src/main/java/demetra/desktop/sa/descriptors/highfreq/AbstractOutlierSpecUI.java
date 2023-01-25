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
package demetra.desktop.sa.descriptors.highfreq;

import demetra.desktop.descriptors.DateSelectorUI;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.modelling.highfreq.OutlierSpec;
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
public abstract class AbstractOutlierSpecUI implements IPropertyDescriptors  {

    @Override
    public String toString() {
        return "";
    }

    protected abstract HighFreqSpecUI root();
    
    protected abstract OutlierSpec spec();

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
        return new DateSelectorUI(spec().getSpan(), root().isRo(), selector -> updateSpan(selector));
    }

    public void updateSpan(TimeSelector span) {
        root().update(spec().toBuilder().span(span).build());
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
        String[] outliers = spec().getOutliers();
        for (int i = 0; i < outliers.length; ++i) {
            if (outliers[i].equals(code)) {
                return true;
            }
        }
        return false;
    }

    // code is an outlier. Should have been checked
    private String[] removeOutlier(String code) {
        String[] outliers = spec().getOutliers();
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
        String[] outliers = spec().getOutliers();
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
        spec().getOutliers();
        return isOutlier("AO");
    }

    public void setAO(boolean ao) {
        if (ao != isOutlier("AO")) {
            if (ao) {
                root().update(spec().toBuilder().outliers(addOutlier("AO")).build());
            } else {
                root().update(spec().toBuilder().outliers(removeOutlier("AO")).build());
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
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isLS() {
        spec().getOutliers();
        return isOutlier("LS");
    }

    public void setLS(boolean ls) {
        if (ls != isOutlier("LS")) {
            if (ls) {
                root().update(spec().toBuilder().outliers(addOutlier("LS")).build());
            } else {
                root().update(spec().toBuilder().outliers(removeOutlier("LS")).build());
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
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public boolean isWO() {
        spec().getOutliers();
        return isOutlier("WO");
    }

    public void setWO(boolean wo) {
        if (wo != isOutlier("WO")) {
            if (wo) {
                root().update(spec().toBuilder().outliers(addOutlier("WO")).build());
            } else {
                root().update(spec().toBuilder().outliers(removeOutlier("WO")).build());
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
            edesc.setReadOnly(root().isRo());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public double getCv() {
        return spec().getCriticalValue();
    }

    public void setCv(double cv) {
        if (cv != spec().getCriticalValue()) {
            root().update(spec().toBuilder()
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
            edesc.setReadOnly(root().isRo());
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
