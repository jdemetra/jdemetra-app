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
package demetra.desktop.mstl.ui;

import demetra.desktop.stl.ui.LoessSpecUI;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.stl.MStlSpec;
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
public class MStlPlusSpecUI implements IObjectDescriptor<MStlSpec> {

    private final MStlPlusSpecRoot root;

    @Override
    public MStlSpec getCore() {
        return root.core;
    }

    public MStlPlusSpecUI(MStlSpec spec, boolean ro) {
        root = new MStlPlusSpecRoot(spec, ro);
    }

    public MStlPlusSpecUI(MStlPlusSpecRoot root) {
        this.root = root;
    }

    public LoessSpecUI getTrendFilter() {
        return new LoessSpecUI(root.core.getTrendSpec(), root.ro, spec -> {
            root.core = root.core.toBuilder().trendSpec(spec).build();
        });
    }

    public AlgorithmUI getAlgorithm() {
        return new AlgorithmUI(root);
    }

    public SeasonalsUI getSeasonalFilters() {
        return new SeasonalsUI(root);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = algDesc();
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
    private static final int ALG_ID = 1, TREND_ID = 2, SEAS_ID = 3;
//
    @NbBundle.Messages({
        "mstlPlusSpecUI.trendDesc.name=TREND",
        "mstlPlusSpecUI.trendDesc.desc=Trend specification."
    })
    private EnhancedPropertyDescriptor trendDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TrendFilter", this.getClass(), "getTrendFilter", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TREND_ID);
            desc.setDisplayName(Bundle.mstlPlusSpecUI_trendDesc_name());
            desc.setShortDescription(Bundle.mstlPlusSpecUI_trendDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "mstlPlusSpecUI.seasDesc.name=SEASONALS",
        "mstlPlusSpecUI.seasDesc.desc=Seasonal specifications."
    })
    private EnhancedPropertyDescriptor seasDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SeasonalFilters", this.getClass(), "getSeasonalFilters", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SEAS_ID);
            desc.setDisplayName(Bundle.mstlPlusSpecUI_seasDesc_name());
            desc.setShortDescription(Bundle.mstlPlusSpecUI_seasDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

   @NbBundle.Messages({
        "mstlPlusSpecUI.algDesc.name=ALGORITHM",
        "mstlPlusSpecUI.algDesc.desc=STL+ Algorithm"
    })
    private EnhancedPropertyDescriptor algDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Algorithm", this.getClass(), "getAlgorithm", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ALG_ID);
            desc.setDisplayName(Bundle.mstlPlusSpecUI_algDesc_name());
            desc.setShortDescription(Bundle.mstlPlusSpecUI_algDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages("mstlPlusSpecUI.getDisplayName=STL+")
    @Override
    public String getDisplayName() {
        return Bundle.mstlPlusSpecUI_getDisplayName();
    }

}
