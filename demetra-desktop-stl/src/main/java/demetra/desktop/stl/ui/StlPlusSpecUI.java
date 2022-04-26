/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.stl.ui;

import demetra.data.WeightFunction;
import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.descriptors.IObjectDescriptor;
import demetra.stl.SeasonalSpecification;
import demetra.stl.StlPlusSpecification;
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
public class StlPlusSpecUI implements IObjectDescriptor<StlPlusSpecification> {

    private final StlPlusSpecRoot root;

    @Override
    public StlPlusSpecification getCore() {
        return root.core;
    }

    public StlPlusSpecUI(StlPlusSpecification spec, boolean ro) {
        root = new StlPlusSpecRoot(spec, ro);
    }

    public StlPlusSpecUI(StlPlusSpecRoot root) {
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
        "stlPlusSpecUI.trendDesc.name=TREND",
        "stlPlusSpecUI.trendDesc.desc=Trend specification."
    })
    private EnhancedPropertyDescriptor trendDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("TrendFilter", this.getClass(), "getTrendFilter", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, TREND_ID);
            desc.setDisplayName(Bundle.stlPlusSpecUI_trendDesc_name());
            desc.setShortDescription(Bundle.stlPlusSpecUI_trendDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @NbBundle.Messages({
        "stlPlusSpecUI.seasDesc.name=SEASONALS",
        "stlPlusSpecUI.seasDesc.desc=Seasonal specifications."
    })
    private EnhancedPropertyDescriptor seasDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SeasonalFilters", this.getClass(), "getSeasonalFilters", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SEAS_ID);
            desc.setDisplayName(Bundle.stlPlusSpecUI_seasDesc_name());
            desc.setShortDescription(Bundle.stlPlusSpecUI_seasDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

   @NbBundle.Messages({
        "stlPlusSpecUI.algDesc.name=ALGORITHM",
        "stlPlusSpecUI.algDesc.desc=STL+ Algorithm"
    })
    private EnhancedPropertyDescriptor algDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("Algorithm", this.getClass(), "getAlgorithm", null);
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, ALG_ID);
            desc.setDisplayName(Bundle.stlPlusSpecUI_algDesc_name());
            desc.setShortDescription(Bundle.stlPlusSpecUI_algDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    @Messages("stlPlusSpecUI.getDisplayName=STL+")
    @Override
    public String getDisplayName() {
        return Bundle.stlPlusSpecUI_getDisplayName();
    }

}
