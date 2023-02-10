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

import demetra.desktop.descriptors.IPropertyDescriptors;
import demetra.desktop.sa.descriptors.highfreq.AbstractRegressionSpecUI;
import demetra.desktop.sa.descriptors.highfreq.HighFreqSpecUI;
import demetra.modelling.highfreq.RegressionSpec;

/**
 *
 * @author PALATEJ
 */
public class RegressionSpecUI extends AbstractRegressionSpecUI {

    private final MStlPlusSpecRoot root;

    public RegressionSpecUI(MStlPlusSpecRoot root) {
        this.root = root;
    }

    @Override
    protected HighFreqSpecUI root() {
        return root;
    }

    @Override
    protected RegressionSpec spec() {
        return root.getPreprocessing().getRegression();
    }

    @Override
    public IPropertyDescriptors getCalendar() {
        return new CalendarSpecUI(root);
    }

    @Override
    public IPropertyDescriptors getEaster() {
        return new EasterSpecUI(root);
    }

}
