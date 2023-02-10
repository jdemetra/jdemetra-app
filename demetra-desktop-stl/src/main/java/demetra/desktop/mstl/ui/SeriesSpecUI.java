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

import demetra.desktop.descriptors.EnhancedPropertyDescriptor;
import demetra.desktop.sa.descriptors.highfreq.AbstractSeriesSpecUI;
import demetra.desktop.sa.descriptors.highfreq.HighFreqSpecUI;
import demetra.modelling.highfreq.SeriesSpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.List;
import org.openide.util.NbBundle;

/**
 *
 * @author PALATEJ
 */
public class SeriesSpecUI extends AbstractSeriesSpecUI {

    private final MStlPlusSpecRoot root;

    public SeriesSpecUI(MStlPlusSpecRoot root) {
        this.root = root;
    }

    @Override
    protected SeriesSpec spec() {
        return root.getPreprocessing().getSeries();
    }

    @Override
    protected HighFreqSpecUI root() {
        return root;
    }

        public boolean isPreprocessing() {
        return root.getPreprocessing().isEnabled();
    }

    public void setPreprocessing(boolean enabled) {
        root.update(root.getPreprocessing().toBuilder().enabled(enabled).build());
    }
    
        @Override
    public List<EnhancedPropertyDescriptor> getProperties() {
        List<EnhancedPropertyDescriptor> properties = super.getProperties();
        EnhancedPropertyDescriptor desc = preprocessingDesc();
        if (desc != null) {
            properties.add(desc);
        }
        return properties;
    }


    @NbBundle.Messages({"seriesSpecUI.preprocessingDesc.name=PREPROCESSING",
        "seriesSpecUI.preprocessingDesc.desc=Reg-Arima (airline) preprocessing"
    })
    private EnhancedPropertyDescriptor preprocessingDesc() {
        try {
            PropertyDescriptor desc = new PropertyDescriptor("preprocessing", this.getClass());
            EnhancedPropertyDescriptor edesc = new EnhancedPropertyDescriptor(desc, PREPROCESSING_ID);
            desc.setDisplayName(Bundle.seriesSpecUI_preprocessingDesc_name());
            desc.setShortDescription(Bundle.seriesSpecUI_preprocessingDesc_desc());
            edesc.setRefreshMode(EnhancedPropertyDescriptor.Refresh.All);
            //edesc.setReadOnly(true);
            return edesc;
        } catch (IntrospectionException ex) {
            return null;
        }
    }

    private static final int PREPROCESSING_ID = 2;
}
