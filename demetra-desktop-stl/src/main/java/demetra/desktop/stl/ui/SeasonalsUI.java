/*
 * Copyright 2022 National Bank of Belgium
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
import demetra.stl.MStlSpec;
import demetra.stl.SeasonalSpec;
import demetra.stl.StlSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class SeasonalsUI extends BaseStlPlusSpecUI {
    
    @Override
    public String toString(){
        return "";
    }

    public SeasonalsUI(StlPlusSpecRoot root) {
        super(root);
    }

    @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        ArrayList<EnhancedPropertyDescriptor> descs = new ArrayList<>();
        EnhancedPropertyDescriptor desc = seasDesc();
        if (desc != null) {
            descs.add(desc);
        }
        return descs;
    }

    @NbBundle.Messages({
        "seasonalSpecUI.seasDesc.name=Seasonals",
        "seasonalSpecUI.seasDesc.desc=Seasonal specifications."
    })
    private EnhancedPropertyDescriptor seasDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("SeasonalFilters", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, SEAS_ID);
            desc.setDisplayName(Bundle.seasonalSpecUI_seasDesc_name());
            desc.setShortDescription(Bundle.seasonalSpecUI_seasDesc_desc());
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    public SeasonalSpec[] getSeasonalFilters() {
        return root.core.getSeasonalSpecs().toArray(n -> new SeasonalSpec[n]);
    }

    public void setSeasonalFilters(SeasonalSpec[] specs) {
        MStlSpec.Builder builder = root.core.toBuilder().clearSeasonalSpecs();
        for (int i = 0; i < specs.length; ++i) {
            builder.seasonalSpec(specs[i]);
        }
        root.core = builder.build();
    }

    private static final int SEAS_ID = 1;

    @Override
    @NbBundle.Messages("seasonalsUI.getDisplayName=Seasonals")
    public String getDisplayName() {
        return Bundle.seasonalsUI_getDisplayName();
    }
}
