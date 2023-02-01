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
package demetra.desktop.stl.ui;

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.desktop.ui.properties.l2fprod.UserInterfaceContext;
import demetra.stl.StlSpec;
import demetra.timeseries.TsDomain;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Jean Palate
 */
public class StlSpecUI implements IPropertyDescriptors {

    private final StlPlusSpecRoot root;

    public StlSpecUI(StlPlusSpecRoot root) {
        this.root = root;
    }

    public StlSpec spec() {
        return root.getStl();
    }

    public boolean isDefault() {
        return spec() == null;
    }

    public void setDefault(boolean def) {
        if (isDefault() == def) {
            return;
        }
        StlSpec spec = null;
        if (!def) {
            TsDomain domain = UserInterfaceContext.INSTANCE.getDomain();
            if (domain != null) {
                spec = StlSpec.createDefault(domain.getAnnualFrequency(), true, true);
            }
        }
        root.update(spec);
    }

    public LoessSpecUI getTrendFilter() {
        StlSpec spec = spec();
        if (spec == null) {
            return null;
        }
        return new LoessSpecUI(spec.getTrendSpec(), root.ro, tspec -> {
            StlSpec nspec = spec.toBuilder().trendSpec(tspec).build();
            root.update(nspec);
        });
    }

    public AlgorithmUI getAlgorithm() {
        return new AlgorithmUI(root);
    }

    public SeasonalSpecUI getSeasonalFilter() {
        StlSpec spec = spec();
        if (spec == null) {
            return null;
        }
        return new SeasonalSpecUI(spec.getSeasonalSpec(), root.ro, sspec -> {
            StlSpec nspec = spec.toBuilder().seasonalSpec(sspec).build();
            root.update(nspec);
        });
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = defDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = algDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = trendDesc();
        if (desc != null) {
            descs.add(desc);
        }
        desc = seasDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }
//    ///////////////////////////////////////////////////////////////////////////
    private static final int DEF_ID = 0, ALG_ID = 1, TREND_ID = 2, SEAS_ID = 3;
//

    @NbBundle.Messages({
        "stlSpecUI.defDesc.name=Default",
        "stlSpecUI.defDesc.desc=Is Default?"
    })
    private EnhancedPropertyDescriptor defDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Default", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TREND_ID);
            desc.setDisplayName(Bundle.stlSpecUI_defDesc_name());
            desc.setShortDescription(Bundle.stlSpecUI_defDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "stlSpecUI.trendDesc.name=TREND",
        "stlSpecUI.trendDesc.desc=Trend specification."
    })
    private EnhancedPropertyDescriptor trendDesc() {
        if (isDefault()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TrendFilter", this.getClass(), "getTrendFilter", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TREND_ID);
            desc.setDisplayName(Bundle.stlSpecUI_trendDesc_name());
            desc.setShortDescription(Bundle.stlSpecUI_trendDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "stlSpecUI.seasDesc.name=SEASONAL",
        "stlSpecUI.seasDesc.desc=Seasonal specification."
    })
    private EnhancedPropertyDescriptor seasDesc() {
        if (isDefault()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SeasonalFilter", this.getClass(), "getSeasonalFilters", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SEAS_ID);
            desc.setDisplayName(Bundle.stlSpecUI_seasDesc_name());
            desc.setShortDescription(Bundle.stlSpecUI_seasDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "stlSpecUI.algDesc.name=ALGORITHM",
        "stlSpecUI.algDesc.desc=STL+ Algorithm"
    })
    private EnhancedPropertyDescriptor algDesc() {
        if (isDefault()) {
            return null;
        }
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Algorithm", this.getClass(), "getAlgorithm", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ALG_ID);
            desc.setDisplayName(Bundle.stlSpecUI_algDesc_name());
            desc.setShortDescription(Bundle.stlSpecUI_algDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages("stlSpecUI.getDisplayName=STL decomposition")
    @Override
    public String getDisplayName() {
        return Bundle.stlSpecUI_getDisplayName();
    }

}
